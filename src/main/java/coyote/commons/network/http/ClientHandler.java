/*
 * Copyright (c) 2003 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 */
package coyote.commons.network.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

import coyote.loader.log.Log;


/**
 * The runnable that will be used for every new client connection.
 */
public class ClientHandler implements Runnable {

  /**
   * 
   */
  private final HTTPD httpd;

  private final InputStream inputStream;

  private final Socket acceptSocket;




  ClientHandler( final HTTPD daemon, final InputStream input, final Socket acptSocket ) {
    httpd = daemon;
    inputStream = input;
    acceptSocket = acptSocket;
  }




  public void close() {
    HTTPD.safeClose( inputStream );
    HTTPD.safeClose( acceptSocket );
  }




  @Override
  public void run() {
    OutputStream outputStream = null;
    try {
      outputStream = acceptSocket.getOutputStream();
      final TempFileManager tempFileManager = httpd.tempFileManagerFactory.create();
      final HTTPSession session = new HTTPSession( httpd, tempFileManager, inputStream, outputStream, acceptSocket.getInetAddress() );
      while ( !acceptSocket.isClosed() ) {
        session.execute();
      }
    } catch ( final Exception e ) {
      // When the socket is closed by the client, we throw our own 
      // SocketException to break the "keep alive" loop above. If the exception 
      // was anything other than the expected SocketException OR a 
      // SocketTimeoutException, print the stacktrace
      if ( !( ( e instanceof SocketException ) && "HTTPD Shutdown".equals( e.getMessage() ) ) && !( e instanceof SocketTimeoutException ) ) {
        Log.append( HTTPD.EVENT, "ERROR: Communication with the client broken, or an bug in the handler code", e );
      }
    }
    finally {
      HTTPD.safeClose( outputStream );
      HTTPD.safeClose( inputStream );
      HTTPD.safeClose( acceptSocket );
      httpd.asyncRunner.closed( this );
    }
  }

}