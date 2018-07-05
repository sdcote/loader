/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import coyote.commons.network.IpAddress;
import coyote.commons.network.IpNetwork;
import coyote.commons.network.MimeType;
import coyote.commons.network.http.HTTP;
import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.SecurityResponseException;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.auth.AuthProvider;
import coyote.commons.network.http.auth.GenericAuthProvider;
import coyote.commons.network.http.responder.Error404Responder;
import coyote.commons.network.http.responder.HTTPDRouter;
import coyote.commons.network.http.responder.NotImplementedResponder;
import coyote.commons.template.Template;
import coyote.dataframe.DataField;
import coyote.dataframe.DataFrame;
import coyote.dataframe.DataFrameException;
import coyote.loader.AbstractLoader;
import coyote.loader.ConfigTag;
import coyote.loader.Loader;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;
import coyote.loader.component.AbstractManagedComponent;
import coyote.loader.component.ManagedComponent;
import coyote.loader.log.Log;
import coyote.loader.log.LogMsg;


/**
 * This starts a configurable web server.
 * 
 * <p>This is a specialization of a Loader which loads a HTTP server and keeps
 * it running in memory.
 * 
 * <p>As an extension of the AbstractLoader, this also supports the loading of 
 * components, all of which will have a reference to this loader/webserver so 
 * it can use this as a coordination point for operations if necessary.
 * 
 * <p>All routes and handlers are specified in the configuration. This does 
 * not serve anything by default.
 */
public class WebServer extends AbstractLoader implements Loader {

  /** Tag used in various class identifying locations. */
  public static final String NAME = WebServer.class.getSimpleName();

  /** The default port on which this listens */
  protected static final int DEFAULT_PORT = 80;

  /** Prefix for command line arguments in the symbol table */
  public static final String COMMAND_LINE_ARG_PREFIX = "cmd.arg.";

  /** Prefix for environment variables in the symbol table */
  public static final String ENVIRONMENT_VAR_PREFIX = "env.";

  /** The port on which we should bind as specified from the command line - overrides all, even configuration file */
  protected int bindPort = -1;

  /** Our main server */
  protected HTTPDRouter server = null;

  /** Server on a normal port which sends a redirect to our main server. (E.g., any http: requests are redirected to https:) */
  private HTTPD redirectServer = null;

  /** The port on which this server listens, defaults to 80 */
  protected static final String PORT = "Port";

  /** Perform a redirect for all requests to this port to the port on which we are listening. Normally set to 80 when the port is 443 */
  protected static final String REDIRECT_PORT = "RedirectPort";

  /** Indicates SSL should be enabled; automatically enable when port=443 */
  protected static final String SECURE_SERVER = "SecureServer";

  protected static final String ENABLE_ARM = "EnableARM";
  protected static final String ENABLE_GAUGES = "EnableGauges";
  protected static final String ENABLE_TIMING = "EnableTiming";

  // Endpoint attributes
  protected static final String ENDPOINTS = "Endpoints";
  protected static final String CLASS = "Class";
  protected static final String PRIORITY = "Priority";

  protected static final String RESOURCES = "Resources";

  /** command line argument for the port on which we should bind. */
  protected static final String PORT_ARG = "-p";




  /**
   * @see coyote.loader.AbstractLoader#configure(coyote.loader.cfg.Config)
   */
  @Override
  public void configure(Config cfg) throws ConfigurationException {
    super.configure(cfg);

    // store the command line arguments in the symbol table of the engine
    for (int x = 0; x < commandLineArguments.length; x++) {
      symbols.put(COMMAND_LINE_ARG_PREFIX + x, commandLineArguments[x]);
    }

    // store environment variables in the symbol table
    Map<String, String> env = System.getenv();
    for (String envName : env.keySet()) {
      symbols.put(ENVIRONMENT_VAR_PREFIX + envName, env.get(envName));
    }

    // command line argument override all other configuration settings
    parseArgs(getCommandLineArguments());

    int redirectport = 0;

    // we need to get the port first as part of the server constructor
    int port = DEFAULT_PORT; // set default

    if (cfg != null) {

      if (cfg.containsIgnoreCase(PORT)) {
        try {
          port = Integer.parseInt(Template.resolve(cfg.getString(PORT), symbols));
          port = NetUtil.validatePort(port);
          if (port == 0) {
            Log.error("Configured port of " + port + " is not a valid port (out of range) - ignoring");
            port = DEFAULT_PORT;
          }
        } catch (NumberFormatException e) {
          port = DEFAULT_PORT;
          Log.error("Port configuration option was not a valid integer - ignoring");
        }
      }

      if (cfg.containsIgnoreCase(REDIRECT_PORT)) {
        try {
          redirectport = Integer.parseInt(Template.resolve(cfg.getString(REDIRECT_PORT), symbols));
          redirectport = NetUtil.validatePort(port);
          if (redirectport == 0) {
            Log.error("Configured port of " + redirectport + " is not a valid port (out of range) - ignoring");
            redirectport = 0;
          }
        } catch (NumberFormatException e) {
          redirectport = 0;
          Log.error("RedirectPort configuration option was not a valid integer - ignoring");

        }

      }

      boolean secureServer;
      try {
        secureServer = cfg.getAsBoolean(SECURE_SERVER);
      } catch (DataFrameException e1) {
        secureServer = false;
      }

      // If we have a valid bind port from the command line arguments, use it instead of configured port
      if (bindPort > 0 && bindPort != port) {
        Log.warn("Command line argument of port " + bindPort + " overrides configuration port of " + port);
        port = bindPort;
      }

      // create a server with the default endpoints
      server = new HTTPDRouter(port);

      if (port == 443 || secureServer) {
        try {
          server.makeSecure(HTTPD.makeSSLSocketFactory("/keystore.jks", "password".toCharArray()), null);
        } catch (IOException e) {
          Log.error("Could not make the server secure: " + e.getMessage());
        }
      }

      if (cfg != null) {
        Config sectn = cfg.getSection(GenericAuthProvider.AUTH_SECTION);
        if (sectn != null) {
          if (sectn.containsIgnoreCase(CLASS)) {
            String classname = sectn.getString(CLASS);
            if (StringUtil.isNotBlank(classname)) {
              Object authobj = loadComponent(sectn);
              if (authobj instanceof AuthProvider) {
                server.setAuthProvider((AuthProvider) authobj);
              } else {
                Log.error("Configured auth proficer class '" + classname + "' is not a valid AuthProvider instance");
              }
            } else {
              Log.error("No auth class specified, using default");
              server.setAuthProvider(new GenericAuthProvider(sectn));
            }
          } else {
            server.setAuthProvider(new GenericAuthProvider(sectn));
          }
        }

        // configure the IPACL with any found configuration data; 
        // localhost only access if no configuration data is found
        server.configIpACL(cfg.getSection(ConfigTag.IPACL));

        // Configure Denial of Service frequency tables
        server.configDosTables(cfg.getSection(ConfigTag.FREQUENCY));

        // Add the required responders to ensure basic operation
        server.setNotImplementedResponder(NotImplementedResponder.class);
        server.setNotFoundResponder(Error404Responder.class);

        // Load the resources
        List<Config> resources = cfg.getSections(RESOURCES);
        for (Config section : resources) {
          for (DataField field : section.getFields()) {
            if (field.getName() != null && field.isFrame()) {
              loadResource(field.getName(), new Config((DataFrame)field.getObjectValue()));
            }
          }
        }

        List<Config> endpoints = cfg.getSections(ENDPOINTS);
        for (Config section : endpoints) {
          for (DataField field : section.getFields()) {
            if (field.getName() != null && field.isFrame()) {
              loadEndpoint(field.getName(), new Config((DataFrame)field.getObjectValue()));
            }
          }
        }

        // configure the server to use our statistics board
        server.setStatBoard(getStats());

        // Configure the statistics board to enable collecting metrics
        try {
          getStats().enableArm(cfg.getAsBoolean(ENABLE_ARM));
        } catch (DataFrameException e) {
          getStats().enableArm(false);
        }
        try {
          getStats().enableGauges(cfg.getAsBoolean(ENABLE_GAUGES));
        } catch (DataFrameException e) {
          getStats().enableGauges(false);
        }
        try {
          getStats().enableTiming(cfg.getAsBoolean(ENABLE_TIMING));
        } catch (DataFrameException e) {
          getStats().enableTiming(false);
        }

        // Set our version in the stats board
        getStats().setVersion(NAME, Loader.API_VERSION);

        if (redirectport > 0) {
          redirectServer = new RedirectServer(redirectport);
        }

        Log.info("Configured server with " + server.getMappings().size() + " endpoints");
      }
    } else {
      Log.fatal("No configuration passed to server");
    } // if there is a cfg

  }




  /**
   * @param args
   */
  private void parseArgs(String[] args) {
    if (args != null && args.length > 0) {
      for (int x = 0; x < args.length; x++) {
        if (PORT_ARG.equalsIgnoreCase(args[x])) {
          try {
            bindPort = Integer.parseInt(args[x + 1]);
            Log.info("Binding to port " + bindPort + " as specified on the command line");
            bindPort = NetUtil.validatePort(bindPort);
            if (bindPort == 0) {
              Log.error("Command line port argument '" + args[x + 1] + "' is not a valid port (out of range) - ignoring");
            }
          } catch (NumberFormatException e) {
            Log.error("Command line port argument '" + args[x + 1] + "' is not a valid integer - ignoring");
          }
        }
      }
    }
  }




  /**
   * Load the endpoint represented in the given configuration into the server.
   * 
   * <p>Init Parameter:<ol><li>this server<li>configuration
   * 
   * @param route the route regex to map in the router
   * @param config the configuration of the route handler with at least a class attribute
   */
  private void loadEndpoint(String route, Config config) {
    // if we have a route
    if (StringUtil.isNotEmpty(route)) {
      // pull out the class name
      String className = config.getString(CLASS);

      // get the priority of the routing
      int priority = 0;
      if (config.contains(PRIORITY)) {
        try {
          priority = config.getAsInt(PRIORITY);
        } catch (DataFrameException e) {
          Log.warn("Problems parsing endpoint priority into a numeric value for route '" + route + "' using default");
        }
      }

      // If we found a class to map to the route
      if (StringUtil.isNotBlank(className)) {
        try {
          // load the class
          Class<?> clazz = Class.forName(className);
          Log.info("Loading " + className + " to handle requests for '" + route + "'");
          if (priority > 0) {
            server.addRoute(route, priority, clazz, this, config);
          } else {
            server.addRoute(route, clazz, this, config);
          }
        } catch (Exception e) {
          Log.warn("Problems adding endpoint route '" + route + "', handler: " + className + " Reason:" + e.getClass().getSimpleName());
        }
      } else {
        Log.warn("No class defined in endpoint for '" + route + "' - " + config);
      }
    } else {
      Log.warn("No route specified in endpoint for " + config);
    }
  }


  /**
   * Add the named configuration as a web server resource.
   * @param name name of the resource for binding to the loader context
   * @param config the configuration for the resource.
   */
  private void loadResource(String name, Config config) {
    if (StringUtil.isNotBlank(name)) {
      String className = config.getString(ConfigTag.CLASS);
      if (StringUtil.isNotBlank(className)) {
        try {
          Class<?> clazz = Class.forName(className);
          Constructor<?> ctor = clazz.getConstructor();
          Object object = ctor.newInstance();
          if (object instanceof ManagedComponent) {
            ManagedComponent component = (ManagedComponent) object;
            component.setContext(getContext());
            component.setLoader(this);
            component.setConfiguration(config);
            getContext().set(name,component);
          } else {
            Log.error("Resource is not a managed component");
          }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          Log.error("Could not create an instance of resource '" + className + "' - " + e.getClass().getName() + ": " + e.getMessage());
        }
      } else {
        Log.error("No resource class specified: " + config.toString());
      }
    } else {
      Log.error("Cannot add unnamed resource");
    }
  }


  /**
   * Start the components running.
   * 
   * @see coyote.loader.AbstractLoader#start()
   */
  @Override
  public void start() {
    // only start once, this is not foolproof as the active flag is set only
    // when the watchdog loop is entered
    if (!isActive()) {
      Log.info("Listening on port: " + server.getPort());
      Log.info("Access Control List: " + server.getIpAcl());
      Log.info("Running server with " + server.getMappings().size() + " endpoints");
      try {
        server.start(HTTPD.SOCKET_READ_TIMEOUT, false);
      } catch (IOException ioe) {
        Log.append(HTTPD.EVENT, "ERROR: Could not start server on port '" + server.getPort() + "' - " + ioe.getMessage());
        System.err.println("Couldn't start server:\n" + ioe);
        System.exit(1);
      }

      if (redirectServer != null) {
        try {
          redirectServer.start(HTTPD.SOCKET_READ_TIMEOUT, false);
        } catch (IOException ioe) {
          Log.append(HTTPD.EVENT, "ERROR: Could not start redirection server on port '" + redirectServer.getPort() + "' - " + ioe.getMessage());
          System.err.println("Couldn't start redirection server:\n" + ioe);
        }
      }

      // Save the name of the thread that is running this class
      final String oldName = Thread.currentThread().getName();

      // Rename this thread to the name of this class
      Thread.currentThread().setName(NAME);

      // very important to get park(millis) to operate
      current_thread = Thread.currentThread();

      // Parse through the configuration and initialize all the components
      initComponents();

      // if we have no components defined, install a wedge to keep the server open
      if (components.size() == 0) {
        Wedge wedge = new Wedge();
        wedge.setLoader(this);
        components.put(wedge, getConfig());
        activate(wedge, getConfig());
      }

      Log.trace(LogMsg.createMsg(MSG, "Loader.components_initialized"));

      final StringBuffer b = new StringBuffer(NAME);
      b.append(" v");
      b.append(Loader.API_VERSION);
      b.append(" initialized - Runtime: ");
      b.append(System.getProperty("java.version"));
      b.append(" (");
      b.append(System.getProperty("java.vendor"));
      b.append(")");
      b.append(" - Platform: ");
      b.append(System.getProperty("os.arch"));
      b.append(" OS: ");
      b.append(System.getProperty("os.name"));
      b.append(" (");
      b.append(System.getProperty("os.version"));
      b.append(")");
      Log.info(b);

      watchdog(); // thread loops here while server is operational

      terminateComponents();

      Log.info("Webserver terminated.");

      Thread.currentThread().setName(oldName);
    }
  }




  /**
   * Shut everything down when the JRE terminates.
   * 
   * <p>There is a shutdown hook registered with the JRE when this Service is
   * loaded. The shutdown hook will call this method when the JRE is 
   * terminating so that the Service can terminate any long-running processes.
   * 
   * <p>Note: this is different from {@code close()} but {@code shutdown()} 
   * will normally result in {@code close()} being invoked at some point.
   * 
   * @see coyote.loader.thread.ThreadJob#shutdown()
   */
  @Override
  public void shutdown() {
    // call the threadjob shutdown to exit the watchdog routine
    super.shutdown();

    // shutdown the servers
    if (server != null) {
      server.stop();
    }
    if (redirectServer != null) {
      redirectServer.stop();
    }
  }




  /**
   * Add the given IP address to the server blacklist.
   * 
   * <p>This results in any TCP connection from this address being dropped by 
   * the HTTPD server before any HTTP processing.
   * 
   * @param address The address to ban from this server
   */
  public synchronized void blacklist(IpAddress address) {
    blacklist(new IpNetwork(address, IpNetwork.HOSTMASK));
  }




  /**
   * Add the given IP network to the server blacklist.
   * 
   * <p>This results in any TCP connection from any address in this network 
   * being dropped by the HTTPD server before any HTTP processing.
   * 
   * @param network The address to ban from this server
   */
  public synchronized void blacklist(IpNetwork network) {
    server.addToACL(network, false);
    if (Log.isLogging(Log.DEBUG_EVENTS)) {
      Log.append(HTTPD.EVENT, "Blacklisted " + network.toString());
      Log.append(HTTPD.EVENT, "ACL: " + server.getIpAcl().toString());
    }
  }

  //

  //

  //

  /**
   * Keep the server watchdog busy if there are no components to run.
   * 
   * <p>BTW, This is an example of the simplest runnable component a Loader 
   * can manage. Initialize it, continually calling doWork() while the loader
   * is running then call terminate() when the loader shuts down.
   */
  protected class Wedge extends AbstractManagedComponent implements ManagedComponent {

    @Override
    public void initialize() {
      setIdleWait(5000);
      setIdle(true);
    }




    @Override
    public void doWork() {
      // no-op method
    }




    @Override
    public void terminate() {
      // no-op method
    }

  }

  /**
   * Listens on a particular port and sends a redirect for the same URL to the 
   * secure port.
   */
  protected class RedirectServer extends HTTPD {
    private static final String HTTP_SCHEME = "http://";
    private static final String HTTPS_SCHEME = "https://";




    public RedirectServer(int port) {
      super(port);
    }




    /**
     * Perform a case insensitive search for a header with a given name and 
     * return its value if found.
     * 
     * @param name the name of the request header to query
     * @param session the session containing the request headers
     * 
     * @return the value in the header or null if that header was not found in 
     *         the session.
     */
    private String findRequestHeaderValue(String name, HTTPSession session) {
      if (StringUtil.isNotBlank(name) && session != null && session.getRequestHeaders() != null) {
        final Set<Map.Entry<String, String>> entries = session.getRequestHeaders().entrySet();
        for (Map.Entry<String, String> header : entries) {
          if (name.equalsIgnoreCase(header.getKey()))
            return header.getValue();
        }
      }
      return null;
    }




    /**
     * Take what ever URI was requested and send a 301 (moved permanently) 
     * response with the new url.
     *  
     * @see coyote.commons.network.http.HTTPD#serve(coyote.commons.network.http.HTTPSession)
     */
    @Override
    public Response serve(HTTPSession session) throws SecurityResponseException {
      String host = findRequestHeaderValue(HTTP.HDR_HOST, session);
      if (StringUtil.isNotBlank(host)) {
        String uri;
        if (server.getPort() == 443) {
          uri = HTTPS_SCHEME + host + session.getUri();
        } else {
          uri = HTTP_SCHEME + host + ":" + server.getPort() + session.getUri();
        }
        Log.append(HTTPD.EVENT, "Redirecting to " + uri);
        Response response = Response.createFixedLengthResponse(Status.REDIRECT, MimeType.HTML.getType(), "<html><body>Moved: <a href=\"" + uri + "\">" + uri + "</a></body></html>");
        response.addHeader(HTTP.HDR_LOCATION, uri);
        return response;
      }
      return super.serve(session);
    }

  }




  /**
   * @return the port on which this server is listening or 0 if the server is not running.
   */
  public int getPort() {
    if (server != null)
      return server.getPort();
    else
      return 0;
  }




  /**
   * Add a handler at the given route.
   * 
   * <p>This is intended for the programmatic or embedded use of the server in 
   * code. 
   * 
   * @param route the route regular expression
   * @param handler the handler class
   * @param initParams initialization parameters
   */
  void addHandler(final String route, final Class<?> handler, final Object... initParams) {
    Object[] params;
    if (initParams != null) {
      params = new Object[initParams.length + 2];
      params[0] = this;
      params[1] = new Config();
      for (int x = 0; x < initParams.length; x++) {
        params[x + 2] = initParams[x];
      }
    } else {
      params = new Object[]{this, new Config()};
    }

    server.addRoute(route, 100, handler, params);
  }




  /**
   * Run the server in a separate thread.
   * 
   * @return the thread in which the server is running
   */
  public Thread execute() {
    shutdown = false;
    Thread serverThread = new Thread(new Runnable() {
      @Override
      public void run() {
        start();
      }
    });

    // start the thread running, calling this server start()
    serverThread.start();
    try {
      Thread.yield();
      Thread.sleep(200);
    } catch (InterruptedException e) {}
    return serverThread;
  }

}
