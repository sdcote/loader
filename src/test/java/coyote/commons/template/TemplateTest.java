/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.template;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * 
 */
public class TemplateTest {
  private static final SymbolTable symbols = new SymbolTable();




  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    symbols.put( "One", 1.02 );
    symbols.put( "Today", new Date() );

  }




  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {}




  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {}




  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}




  @Test
  public void testNumber() {
    String text = "[#$One|###,###.00#]";
    String formattedText = Template.resolve( text, symbols );
    //System.out.println(formattedText);
    assertEquals( "1.02", formattedText );
  }




  @Test
  public void testDate() {
    String text = "[#$Today|YYYYMMdd#]";
    String formattedText = Template.resolve( text, symbols );
    //System.out.println(formattedText);
    assertTrue( formattedText.length() == 8 );
  }




  @Test
  public void testObjects() {

    // Create a new object to place in the template
    Thing thing = new Thing();
    
    // Place it in the template with the name of "Thing"
    Template.put( "Thing", thing );

    // Create a template which call a method on the object
    String text = "[#Thing.hello()#] World!";

    // Resolve the text
    String formattedText = Template.resolve( text, symbols );
    System.out.println( formattedText );
  }

  
  /**
   * The objects which can be placed in templates have few limitations. 
   * Only methods which take strings as arguments are called.
   * A is limit of 8 arguments in such methods.
   */
  class Thing {
    Thing() {

    }




    String hello() {
      return "Hello";
    }




    String tupper( String text ) {
      if ( text != null ) {
        return text.toUpperCase();
      } else {
        return "";
      }
    }

  }
}
