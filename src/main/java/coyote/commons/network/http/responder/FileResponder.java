/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote
 */
package coyote.commons.network.http.responder;

import coyote.commons.network.MimeType;
import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.loader.cfg.Config;
import coyote.loader.log.Log;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;


/**
 * Generic handler to retrieve the requested page from a file root of a file system.
 *
 * <p>This allows for serving from the file system like a regular web server. Contrast this with the ResourceResponder
 * which serves content from the classpath.
 */
public class FileResponder extends DefaultResponder {

  public static final String ROOT_TAG = "Root";
  private static final String DEFAULT_ROOT = "content";
  public static final String LIST_FILES_TAG = "listFiles";

  public static final List<String> INDEX_FILE_NAMES = new ArrayList<String>() {
    {
      add("index.html");
      add("index.htm");
    }
  };


  protected BufferedInputStream fileToInputStream(final File fileOrdirectory) throws IOException {
    return new BufferedInputStream(new FileInputStream(fileOrdirectory));
  }


  @Override
  public Response get(final Resource resource, final Map<String, String> urlParams, final HTTPSession session) {
    // WebServer loader = resource.initParameter( 0, WebServer.class ); // Not needed for this responder
    Config config = resource.initParameter(1, Config.class);

    // Retrieve the base directory from which to serve files
    String root = config.getString(ROOT_TAG);
    File docroot = new File(root);

    // Determine the real URI into our content
    final String baseUri = resource.getUri();
    String realUri = HTTPDRouter.normalizeUri(session.getUri());
    for (int index = 0; index < Math.min(baseUri.length(), realUri.length()); index++) {
      if (baseUri.charAt(index) != realUri.charAt(index)) {
        realUri = HTTPDRouter.normalizeUri(realUri.substring(index));
        break;
      }
    }

    // The requested file is the document root plus the real request URI
    File requestedFile = new File(docroot, realUri);

    // if they asked for a directory, 
    if (requestedFile.isDirectory()) {
      // look for an index file
      String indexFile = findIndexFileInDirectory(requestedFile);
      if (indexFile == null) {
        // No index file, list the directory if it is readable and we are configured to list files
        if (requestedFile.canRead() && this.isListingFiles(config)) {
          if (!realUri.startsWith("/")) {
            realUri = "/" + realUri;
          }

          return Response.createFixedLengthResponse(Status.OK, MimeType.HTML.getType(), listDirectory(realUri, requestedFile));
        } else {
          return Response.createFixedLengthResponse(Status.FORBIDDEN, MimeType.TEXT.getType(), "Directory listing not permitted.");
        }
      } else {
        // return the found index file
        requestedFile = new File(requestedFile, indexFile);
        try {
          return Response.createChunkedResponse(getStatus(), HTTPD.getMimeTypeForFile(requestedFile.getName()), fileToInputStream(requestedFile));
        } catch (final IOException ioe) {
          return Response.createFixedLengthResponse(Status.REQUEST_TIMEOUT, MimeType.TEXT.getType(), null);
        }
      }

    } else {
      // if the file does not exist or is not a file...
      if (!requestedFile.exists() || !requestedFile.isFile()) {
        Log.append(HTTPD.EVENT, "404 NOT FOUND - '" + realUri + "' LOCAL: " + requestedFile.getAbsolutePath());
        return new Error404Responder().get(resource, urlParams, session);
      } else {
        try {
          return Response.createChunkedResponse(getStatus(), HTTPD.getMimeTypeForFile(requestedFile.getName()), fileToInputStream(requestedFile));
        } catch (final IOException ioe) {
          return Response.createFixedLengthResponse(Status.REQUEST_TIMEOUT, MimeType.TEXT.getType(), null);
        }
      }
    }
  }


  /**
   * @return
   */
  private boolean isListingFiles(Config config) {
    if (config != null && config.containsIgnoreCase(LIST_FILES_TAG)) {
      return config.getBoolean(LIST_FILES_TAG);
    }
    return false;
  }


  protected String listDirectory(String uri, File currDir) {
    String heading = "Directory " + uri;
    StringBuilder msg = new StringBuilder("<!DOCTYPE html>\n<html>\n<head>\n  <title>");
    msg.append(heading);
    msg.append("</title>\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n  <style>\n    body { background: #fff; }\n  </style>\n</head>\n<body>\n  <header>\n    <h1>");
    msg.append(heading);
    msg.append("</h1>\n  </header>\n  <hr/>\n  <main>\n    <pre id=\"contents\">\n");

    String up = null;
    if (uri.length() > 1) {
      String u = uri.substring(0, uri.length() - 1);
      int slash = u.lastIndexOf('/');
      if (slash >= 0 && slash < u.length()) {
        up = uri.substring(0, slash + 1);
      }
    }

    List<String> files = Arrays.asList(currDir.list(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return new File(dir, name).isFile();
      }
    }));
    Collections.sort(files);
    List<String> directories = Arrays.asList(currDir.list(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return new File(dir, name).isDirectory();
      }
    }));
    Collections.sort(directories);
    if (up != null || directories.size() + files.size() > 0) {
      if (up != null || directories.size() > 0) {
        if (up != null) {
          msg.append("<a href=\"").append(up).append("\">..</a>\n");
        }
        for (String directory : directories) {
          String dir = directory + "/";
          msg.append("<a href=\"").append(encodeUri(dir)).append("\" title=\"").append(dir).append("\">").append(dir).append("</a>\n");
        }
      }
      if (files.size() > 0) {
        for (String file : files) {
          msg.append("<a href=\"").append(encodeUri(file)).append("\" title=\"").append(file).append("\">").append(file).append("</a>");
          File curFile = new File(currDir, file);
          long len = curFile.length();
          msg.append("&nbsp;(");
          if (len < 1024) {
            msg.append(len).append(" bytes");
          } else if (len < 1024 * 1024) {
            msg.append(len / 1024).append(".").append(len % 1024 / 10 % 100).append(" KB");
          } else {
            msg.append(len / (1024 * 1024)).append(".").append(len % (1024 * 1024) / 10000 % 100).append(" MB");
          }
          msg.append(")\n");
        }
      }
    }
    msg.append("    </pre>\n  </main>\n  <hr/>\n</body>\n</html>");
    return msg.toString();
  }


  /**
   * URL-encodes everything between "/"-characters. Encodes spaces as '%20' instead of '+'.
   */
  private String encodeUri(String uri) {
    String newUri = "";
    StringTokenizer st = new StringTokenizer(uri, "/ ", true);
    while (st.hasMoreTokens()) {
      String tok = st.nextToken();
      if ("/".equals(tok)) {
        newUri += "/";
      } else if (" ".equals(tok)) {
        newUri += "%20";
      } else {
        try {
          newUri += URLEncoder.encode(tok, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
      }
    }
    return newUri;
  }


  private String findIndexFileInDirectory(File directory) {
    for (String fileName : FileResponder.INDEX_FILE_NAMES) {
      File indexFile = new File(directory, fileName);
      if (indexFile.isFile()) {
        return fileName;
      }
    }
    return null;
  }


  @Override
  public String getMimeType() {
    throw new IllegalStateException("This method should not be called");
  }


  @Override
  public Status getStatus() {
    return Status.OK;
  }


  @Override
  public String getText() {
    throw new IllegalStateException("This method should not be called");
  }

}