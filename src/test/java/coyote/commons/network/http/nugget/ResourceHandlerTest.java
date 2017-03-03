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

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import coyote.commons.network.http.HTTPD;

/**
 * 
 */
public class ResourceHandlerTest {

  private static TestRouter server = null;
  
  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    server = new TestRouter(3232);
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
    
    //pass( "Not yet implemented" ); // TODO
  }

}
