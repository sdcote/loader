package coyote.commons.network.http.handler;

import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.SecurityResponseException;
import coyote.i13n.ArmTransaction;
import coyote.i13n.StatBoard;
import coyote.i13n.StatBoardImpl;


/**
 * This is a HTTPD which routes requests to request handlers based on the 
 * request URI.
 * 
 * <p>This allows the server to implement a pluggable approach to handling 
 * requests. For example, it is possible to implement microservices with 
 * simple classes.
 */
public class HTTPDRouter extends HTTPD {

  private final UriRouter router;

  /** the component responsible for tracking operational statistics for this router */
  StatBoard stats;




  /**
   * Remove any leading and trailing slashes (/) from the URI
   * 
   * @param value the URI value to normalize
   *  
   * @return the URI with no leading or trailing slashes
   */
  public static String normalizeUri( String value ) {
    if ( value == null ) {
      return value;
    }
    if ( value.startsWith( "/" ) ) {
      value = value.substring( 1 );
    }
    if ( value.endsWith( "/" ) ) {
      value = value.substring( 0, value.length() - 1 );
    }
    return value;

  }




  public HTTPDRouter( final int port ) {
    super( port );
    router = new UriRouter();
    stats = new StatBoardImpl();
  }




  /**
   * @return the StatBoard for this router.
   */
  public StatBoard getStatBoard() {
    return stats;
  }




  /**
   * Set the statsBoard instance this router uses to track its operational 
   * statistics.
   * 
   * @param instance = the instance of the statistics board to use
   */
  public void setStatBoard( StatBoard instance ) {
    if ( instance != null ) {
      stats = instance;
    }
  }




  /**
   * Default routings, they are over writable.
   * 
   * <pre>router.setNotFoundHandler(GeneralHandler.class);</pre>
   */
  public void addDefaultRoutes() {
    router.setNotImplemented( NotImplementedHandler.class );
    router.setNotFoundHandler( Error404UriHandler.class );
    router.addRoute( "/", Integer.MAX_VALUE / 2, BlankPageHandler.class, authProvider );
    router.addRoute( "/index.html", Integer.MAX_VALUE / 2, BlankPageHandler.class, authProvider );
  }




  /**
   * Add a handler for the given URL pattern.
   * 
   * @param urlPattern RegEx pattern describing the URL to match
   * @param handler The class to be instantiated to handle the connection
   * @param initParams the array of objects to pass to the handler upon in
   */
  public void addRoute( final String urlPattern, final Class<?> handler, final Object... initParams ) {
    router.addRoute( urlPattern, 100, handler, authProvider, initParams );
  }




  /**
   * Add a handler for the given URL pattern.
   * 
   * @param urlPattern RegEx pattern describing the URL to match
   * @param priority The evaluation priority to all the other routes
   * @param handler The class to be instantiated to handle the connection
   * @param initParams the array of objects to pass to the handler upon in
   */
  public void addRoute( final String urlPattern, int priority, final Class<?> handler, final Object... initParams ) {
    router.addRoute( urlPattern, priority, handler, authProvider, initParams );
  }




  public void removeRoute( final String url ) {
    router.removeRoute( url );
  }




  /**
   * @throws SecurityResponseException if processing the request generated a security exception
   * 
   * @see coyote.commons.network.http.HTTPD#serve(coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response serve( final IHTTPSession session ) throws SecurityResponseException {
    ArmTransaction arm = stats.startArm( session.getUri() == null ? "" : session.getUri() );
    try {
      return router.process( session );
    }
    finally {
      arm.stop();
    }
  }
}