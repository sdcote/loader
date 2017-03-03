/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.network.http.auth;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.TestResponse;
import coyote.commons.network.http.TestRouter;


/**
 * 
 */
public class AuthAnnotationTest {

  private static TestRouter server = null;
  private static final int PORT = 3233;




  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    server = new TestRouter( PORT );
    server.addMappings();

    try {
      server.start( HTTPD.SOCKET_READ_TIMEOUT, true );
    } catch ( IOException ioe ) {
      System.err.println( "Couldn't start server:\n" + ioe );
      System.exit( -1 );
    }
  }




  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    server.stop();
  }




  @Test
  public void test() {
    // setup a handler we can call which will post results into the response
    server.addRoute( "/", Integer.MAX_VALUE, ProtectedHandler.class );

    try {
      TestResponse response = sendGet( "http://localhost:" + PORT );
      System.out.println( response.getStatus() + ":" + response.getData() );
      assertTrue( response.getStatus() == 200 );

      response = sendGet( "http://localhost:" + PORT + "/" );
      System.out.println( response.getStatus() + ":" + response.getData() );
      assertTrue( response.getStatus() == 200 );

    } catch ( Exception e ) {
      fail( e.getMessage() );
    }

  }




  protected TestResponse sendGet( String url ) throws Exception {
    TestResponse testResponse = new TestResponse( url );

    URL obj = new URL( url );
    HttpURLConnection con = (HttpURLConnection)obj.openConnection();
    con.setRequestMethod( "GET" );

    int responseCode = con.getResponseCode();
    testResponse.setStatus( responseCode );
    if ( responseCode < 300 ) {
      BufferedReader in = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ( ( inputLine = in.readLine() ) != null ) {
        response.append( inputLine );
      }
      in.close();

      testResponse.setData( response.toString() );
    }
    return testResponse;
  }

}
