package coyote.commons.network.http.nugget;

import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.Response;


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

  static {
    // make sure we load the MIME type mappings from properties
    mimeTypes();
  }




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
    router.addRoute( "/", Integer.MAX_VALUE / 2, IndexHandler.class );
    router.addRoute( "/index.html", Integer.MAX_VALUE / 2, IndexHandler.class );
  }




  /**
   * Add
   * @param url
   * @param handler
   * @param initParameter
   */
  public void addRoute( final String url, final Class<?> handler, final Object... initParameter ) {
    router.addRoute( url, 100, handler, initParameter );
  }




  public void removeRoute( final String url ) {
    router.removeRoute( url );
  }




  /**
   * @see coyote.commons.network.http.HTTPD#serve(coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response serve( final IHTTPSession session ) {
    // Try to find match
    return router.process( session );
  }
}
