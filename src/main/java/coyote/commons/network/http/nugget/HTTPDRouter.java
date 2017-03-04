package coyote.commons.network.http.nugget;

import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.SecurityResponseException;


/**
 * This is a HTTPD which routes requests to request handlers (a.k.a. nuggets) 
 * based on the request URI.
 * 
 * <p>This allows the server to implement a pluggable approach to handling 
 * requests. For example, it is possible to implement microservices with 
 * simple classes. </p>
 */
public class HTTPDRouter extends HTTPD {

  private final UriRouter router;




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
  }




  /**
   * Default routings, they are over writable.
   * 
   * <pre>router.setNotFoundHandler(GeneralHandler.class);</pre>
   */
  public void addMappings() {
    router.setNotImplemented( NotImplementedHandler.class );
    router.setNotFoundHandler( Error404UriHandler.class );
    router.addRoute( "/", Integer.MAX_VALUE / 2, BlankPage.class );
    router.addRoute( "/index.html", Integer.MAX_VALUE / 2, BlankPage.class );
  }




  /**
   * Add a handler for the given URL pattern.
   * 
   * @param urlPattern RegEx pattern describing the URL to match
   * @param handler The class to be instantiated to handle the connection
   * @param initParams the array of objects to pass to the handler upon in
   */
  public void addRoute( final String urlPattern, final Class<?> handler, final Object... initParams ) {
    router.addRoute( urlPattern, 100, handler, initParams );
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
    router.addRoute( urlPattern, priority, handler, initParams );
  }




  public void removeRoute( final String url ) {
    router.removeRoute( url );
  }




  /**
   * @throws SecurityResponseException if processing the request generated a security exception
   * @see coyote.commons.network.http.HTTPD#serve(coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response serve( final IHTTPSession session ) throws SecurityResponseException {
    // Try to find match
    return router.process( session );
  }
}
