package coyote.commons.network.http.nugget;

import java.util.logging.Logger;

import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.Response;


/**
 * This is a HTTPD which routes requests to request handlers (a.k.a. nuggets) 
 * based on the request URI.
 * 
 * <p>This allows the server to implement a pluggable approach to handling 
 * requests. For example, it is possible to implement micro services with 
 * simple classes. 
 */
public class HTTPDRouter extends HTTPD {


  private final UriRouter router;



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
   * default routings, they are over writable.
   * 
   * <pre>
   * router.setNotFoundHandler(GeneralHandler.class);
   * </pre>
   */

  public void addMappings() {
    router.setNotImplemented( NotImplementedHandler.class );
    router.setNotFoundHandler( Error404UriHandler.class );
    router.addRoute( "/", Integer.MAX_VALUE / 2, IndexHandler.class );
    router.addRoute( "/index.html", Integer.MAX_VALUE / 2, IndexHandler.class );
  }




  public void addRoute( final String url, final Class<?> handler, final Object... initParameter ) {
    router.addRoute( url, 100, handler, initParameter );
  }




  public void removeRoute( final String url ) {
    router.removeRoute( url );
  }




  @Override
  public Response serve( final IHTTPSession session ) {
    // Try to find match
    return router.process( session );
  }
}
