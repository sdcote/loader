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
package coyote.commons.network.http.nugget;

//import static org.junit.Assert.*;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import coyote.commons.network.http.HTTPD;


/**
 * 
 */
public class ResourceHandlerTest {

  private static TestRouter server = null;
  private static final int PORT = 3232;




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
    server.addRoute( "/", Integer.MAX_VALUE, ResourceHandler.class, "content" );
    server.addRoute( "/(.)+", Integer.MAX_VALUE, ResourceHandler.class, "content" );

    try {
      String data = sendGet( "http://localhost:" + PORT );
      System.out.println( data );
      // TODO: perfom some checks
    } catch ( Exception e ) {
      e.printStackTrace();
      fail( e.getMessage() );
    }

    //pass( "Not yet implemented" ); // TODO
  }




  //HTTP GET request
  private String sendGet( String url ) throws Exception {

    URL obj = new URL( url );
    HttpURLConnection con = (HttpURLConnection)obj.openConnection();
    con.setRequestMethod( "GET" );
    con.setRequestProperty( "User-Agent", "Mozilla/5.0" );

    int responseCode = con.getResponseCode();
    System.out.println( "\nSending 'GET' request to URL : " + url );
    System.out.println( "Response Code : " + responseCode );

    BufferedReader in = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ( ( inputLine = in.readLine() ) != null ) {
      response.append( inputLine );
    }
    in.close();

    return response.toString();

  }
}
