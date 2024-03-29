/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.commons.template;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Date;

import coyote.commons.UriUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import coyote.commons.CipherUtil;


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
    symbols.put("One", 1.02);
    symbols.put("Today", new Date());
    symbols.put("Greeting", "Hello World!");
    symbols.readSystemProperties();
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
    String formattedText = Template.resolve(text, symbols);
    //System.out.println(formattedText);
    assertEquals("1.02", formattedText);
  }




  @Test
  public void testDate() {
    String text = "[#$Today|YYYYMMdd#]";
    String formattedText = Template.resolve(text, symbols);
    //System.out.println(formattedText);
    assertTrue(formattedText.length() == 8);
  }




  @Test
  public void testUriFormat() {
    String text = "[#$user.dir|toURI#]";
    String formattedText = Template.resolve(text, symbols);
    System.out.println(formattedText);
    URI uri = URI.create(formattedText);
    assertTrue(UriUtil.isFile(uri));
  }




  @Test
  public void testStaticObjects() {

    // Create a new object to place in the template
    Thing thing = new Thing();

    // Place it in the static template cache with the name of "Thing"
    Template.putStatic("Thing", thing);

    // Create a template which call a method on the object
    String text = "[#Thing.hello()#] World!";

    // Resolve the text
    String formattedText = Template.resolve(text, symbols);
    assertEquals("Hello World!", formattedText);

    text = ">[#Thing.tupper(\"\")#]<-uppered";
    formattedText = Template.resolve(text, symbols);
    assertEquals("><-uppered", formattedText);

    text = ">[#Thing.tupper(\"Boo\")#]<-uppered";
    formattedText = Template.resolve(text, symbols);
    assertEquals(">BOO<-uppered", formattedText);

    text = ">[#Thing.tupper(hello, something)#]<-wrong parameter count";
    formattedText = Template.resolve(text, symbols);
    assertEquals("><-wrong parameter count", formattedText);

    text = ">[#Thing.tlower(hello)#]<-unknown method";
    formattedText = Template.resolve(text, symbols);
    assertEquals("><-unknown method", formattedText);

    text = ">[#NoThing.tupper(hello)#]<-unknown object";
    formattedText = Template.resolve(text, symbols);
    assertEquals("><-unknown object", formattedText);

    Template template = new Template("[#Thing2.hello()#] World!");
    template.put("Thing2",new Thing2());
    System.out.println( Template.resolve(text, symbols));
  }



  @Test
  public void testCachedObjects() {
    Thing thing = new Thing();
    Template.putStatic("Thing", thing);

    String text = "[#Thing2.hello()#] World!";
    Template template = new Template(text);
    template.put("Thing2",new Thing2());
    String formattedText = template.convertToString();
    assertEquals("Hello2 World!", formattedText);

    // when resolving from the static instance, the class cache is not available
    formattedText = Template.resolve(text, symbols);
    assertEquals(" World!", formattedText);

  }




  /**
   * This tests the case where the last dot in the toke might be an argument name (e.g. "user.dir")
   */
  @Test
  public void testDottedArgument() {
    String text = "[#Thing2.hello(my.friend)#] World!";
    Template template = new Template(text);
    template.put("Thing2",new Thing2());
    String formattedText = template.convertToString();
    assertEquals("Hello2-my.friend World!", formattedText);
  }




  @Test
  public void testEncrypt() {
    String text = "Biff the WonderDog";
    String secret = CipherUtil.encryptString(text);
    String symbol = SymbolTable.ENCRYPT_PREFIX + "mySecret";
    symbols.put(symbol, secret);
    String template = "[#$" + symbol + "#] saves the day!";
    String formattedText = Template.resolve(template, symbols);
    assertEquals("Biff the WonderDog saves the day!", formattedText);
  }




  @Test
  public void preProcess() {
    String text = "\"[#$Greeting#]\" is the [#$type#] example.";
    String formattedText = Template.resolve(text, symbols);
    //System.out.println( formattedText );
    assertEquals("\"Hello World!\" is the  example.", formattedText);

    String preProcessedText = Template.preProcess(text, symbols);
    //System.out.println( preProcessedText );
    assertEquals("\"Hello World!\" is the [#$type#] example.", preProcessedText);
    symbols.put("type", "standard");
    preProcessedText = Template.preProcess(text, symbols);
    //System.out.println( preProcessedText );
    assertEquals("\"Hello World!\" is the standard example.", preProcessedText);

    // exists, but null
    symbols.put("type", null);
    preProcessedText = Template.preProcess(text, symbols);
    //System.out.println( preProcessedText );
    assertEquals("\"Hello World!\" is the  example.", preProcessedText);

    // does not exist as before
    symbols.remove("type");
    preProcessedText = Template.preProcess(text, symbols);
    //System.out.println( preProcessedText );
    assertEquals("\"Hello World!\" is the [#$type#] example.", preProcessedText);

    text = "[#Thing.hello()#] World!";
    preProcessedText = Template.preProcess(text, symbols);
    //System.out.println( preProcessedText );
    assertEquals("Hello World!", preProcessedText);

    text = "[#Thang.hello()#] World!";
    preProcessedText = Template.preProcess(text, symbols);
    //System.out.println( preProcessedText );
    assertEquals(text, preProcessedText);
  }




  @Test
  public void appearsToBeATemplate() {
    String text = "\"[#$Greeting#]\" is the [#$type#] example.";
    assertTrue(Template.appearsToBeATemplate(text));

    text = "\"[#$Greeting#]\".";
    assertTrue(Template.appearsToBeATemplate(text));

    text = "[#$cmd.arg.1#]";
    assertTrue(Template.appearsToBeATemplate(text));

    // technically a template and happens when some template generators don't 
    // have a value to fill in that position.
    text = "[##]";
    assertTrue(Template.appearsToBeATemplate(text));

    text = "[# #]";
    assertTrue(Template.appearsToBeATemplate(text));

    text = "[#";
    assertFalse(Template.appearsToBeATemplate(text));

    text = "[#]";
    assertFalse(Template.appearsToBeATemplate(text));

    text = "#]";
    assertFalse(Template.appearsToBeATemplate(text));

    text = "#][#";
    assertFalse(Template.appearsToBeATemplate(text));

  }

  /**
   * The objects which can be placed in templates have few limitations. 
   * Only methods which take strings as arguments are called.
   * A is limit of 8 arguments in such methods.
   */
  class Thing {
    Thing() {}




    public String hello() {
      return "Hello";
    }




    public String tupper(String text) {
      if (text != null) {
        return text.toUpperCase();
      } else {
        return "";
      }
    }

  }

  class Thing2 {
    Thing2() {}
    public String hello() {
      return "Hello2";
    }
    public String hello(String text) {
      return "Hello2-"+text;
    }
  }

}
