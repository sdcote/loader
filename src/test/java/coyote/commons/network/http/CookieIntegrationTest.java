package coyote.commons.network.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.junit.Test;


public class CookieIntegrationTest extends IntegrationTestBase<CookieIntegrationTest.CookieTestServer> {

  public static class CookieTestServer extends HTTPD {

    List<Cookie> cookiesReceived = new ArrayList<Cookie>();

    List<Cookie> cookiesToSend = new ArrayList<Cookie>();




    public CookieTestServer() {
      super( 8192 );
    }




    @Override
    public Response serve( final IHTTPSession session ) {
      final CookieHandler cookies = session.getCookies();
      for ( final String cookieName : cookies ) {
        cookiesReceived.add( new Cookie( cookieName, cookies.read( cookieName ) ) );
      }
      for ( final Cookie c : cookiesToSend ) {
        cookies.set( c );
      }
      return Response.createFixedLengthResponse( "Cookies!" );
    }
  }




  @Override
  public CookieTestServer createTestServer() {
    return new CookieTestServer();
  }




  @Test
  public void testCookieSentBackToClient() throws Exception {
    testServer.cookiesToSend.add( new Cookie( "name", "value", 30 ) );
    final HttpGet httpget = new HttpGet( "http://localhost:8192/" );
    final ResponseHandler<String> responseHandler = new BasicResponseHandler();
    httpclient.execute( httpget, responseHandler );

    final CookieStore cookies = httpclient.getCookieStore();
    assertEquals( 1, cookies.getCookies().size() );
    assertEquals( "name", cookies.getCookies().get( 0 ).getName() );
    assertEquals( "value", cookies.getCookies().get( 0 ).getValue() );
  }




  @Test
  public void testNoCookies() throws Exception {
    final HttpGet httpget = new HttpGet( "http://localhost:8192/" );
    final ResponseHandler<String> responseHandler = new BasicResponseHandler();
    httpclient.execute( httpget, responseHandler );

    final CookieStore cookies = httpclient.getCookieStore();
    assertEquals( 0, cookies.getCookies().size() );
  }




  @Test
  public void testServerReceivesCookiesSentFromClient() throws Exception {
    final BasicClientCookie clientCookie = new BasicClientCookie( "name", "value" );
    final Calendar calendar = Calendar.getInstance();
    calendar.add( Calendar.DAY_OF_YEAR, 100 );
    clientCookie.setExpiryDate( calendar.getTime() );
    clientCookie.setDomain( "localhost" );
    httpclient.getCookieStore().addCookie( clientCookie );
    final HttpGet httpget = new HttpGet( "http://localhost:8192/" );
    final ResponseHandler<String> responseHandler = new BasicResponseHandler();
    httpclient.execute( httpget, responseHandler );

    assertEquals( 1, testServer.cookiesReceived.size() );
    assertTrue( testServer.cookiesReceived.get( 0 ).getHTTPHeader().contains( "name=value" ) );
  }
}
