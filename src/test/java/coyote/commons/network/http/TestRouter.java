package coyote.commons.network.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import coyote.commons.network.http.handler.DefaultHandler;
import coyote.commons.network.http.handler.DefaultStreamHandler;
import coyote.commons.network.http.handler.GeneralHandler;
import coyote.commons.network.http.handler.HTTPDRouter;
import coyote.commons.network.http.handler.StaticPageHandler;
import coyote.commons.network.http.handler.UriResource;
import coyote.commons.network.http.handler.UriResponder;


public class TestRouter extends HTTPDRouter {
  private static final int PORT = 9090;

  public static class StaticPageTestHandler extends StaticPageHandler {

    @Override
    protected BufferedInputStream fileToInputStream( final File fileOrdirectory ) throws IOException {
      if ( "exception.html".equals( fileOrdirectory.getName() ) ) {
        throw new IOException( "trigger something wrong" );
      }
      return super.fileToInputStream( fileOrdirectory );
    }
  }

  static public class StreamUrl extends DefaultStreamHandler {

    @Override
    public InputStream getData() {
      return new ByteArrayInputStream( "a stream of data ;-)".getBytes() );
    }




    @Override
    public String getMimeType() {
      return "text/plain";
    }




    @Override
    public IStatus getStatus() {
      return Status.OK;
    }

  }

  public static class UserHandler extends DefaultHandler {

    @Override
    public Response get( final UriResource uriResource, final Map<String, String> urlParams, final IHTTPSession session ) {
      final String text = getText( urlParams, session );
      final ByteArrayInputStream inp = new ByteArrayInputStream( text.getBytes() );
      final int size = text.getBytes().length;
      return Response.createFixedLengthResponse( getStatus(), getMimeType(), inp, size );
    }




    @Override
    public String getMimeType() {
      return "text/html";
    }




    @Override
    public IStatus getStatus() {
      return Status.OK;
    }




    @Override
    public String getText() {
      return "not implemented";
    }




    public String getText( final Map<String, String> urlParams, final IHTTPSession session ) {
      String text = "<html><body>User handler. Method: " + session.getMethod().toString() + "<br>";
      text += "<h1>Uri parameters:</h1>";
      for ( final Map.Entry<String, String> entry : urlParams.entrySet() ) {
        final String key = entry.getKey();
        final String value = entry.getValue();
        text += "<div> Param: " + key + "&nbsp;Value: " + value + "</div>";
      }
      text += "<h1>Query parameters:</h1>";
      for ( final Map.Entry<String, String> entry : session.getParms().entrySet() ) {
        final String key = entry.getKey();
        final String value = entry.getValue();
        text += "<div> Query Param: " + key + "&nbsp;Value: " + value + "</div>";
      }
      text += "</body></html>";

      return text;
    }

  }





  /**
   * Main entry point
   *
   * @param args
   */
  public static void main( final String[] args ) {
    ServerRunner.run( TestRouter.class );
  }




  /**
   * Create the server instance
   */
  public TestRouter() throws IOException {
    super( PORT );
    addDefaultRoutes();
    //System.out.println( "\nRunning! Point your browers to http://localhost:" + PORT + "/ \n" );
  }




  /**
   * Create the server instance at a particular port
   */
  public TestRouter( final int port ) throws IOException {
    super( port );
    addDefaultRoutes();
    //System.out.println( "\nRunning! Point your browers to http://localhost:" + port + "/ \n" );
  }




  /**
   * Add the routes Every route is an absolute path Parameters starts with ":"
   * Handler class should implement @UriResponder interface If the handler not
   * implement UriResponder interface - toString() is used
   */
  @Override
  public void addDefaultRoutes() {
    super.addDefaultRoutes();
    addRoute( "/user", UserHandler.class );
    addRoute( "/user/:id", UserHandler.class );
    addRoute( "/user/help", GeneralHandler.class );
    addRoute( "/general/:param1/:param2", GeneralHandler.class );
    addRoute( "/photos/:customer_id/:photo_id", null );
    addRoute( "/test", String.class );
    addRoute( "/interface", UriResponder.class ); // this will cause an error
                                                  // when called
    addRoute( "/toBeDeleted", String.class );
    removeRoute( "/toBeDeleted" );
    addRoute( "/stream", StreamUrl.class );
    addRoute( "/browse/(.)+", StaticPageTestHandler.class, new File( "src/test/resources" ).getAbsoluteFile() );
  }
}
