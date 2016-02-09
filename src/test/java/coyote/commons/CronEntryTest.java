/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons;

//import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.junit.Test;


/**
 * 
 */
public class CronEntryTest {

  /**
   * Test method for {@link coyote.commons.CronEntry#parse(java.lang.String)}.
   */
  @Test
  public void testParse() {
    CronEntry subject = null;

    String pattern = "* * * * *";
    try {
      subject = CronEntry.parse( pattern );
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "? ? ? ? ?";
    try {
      subject = CronEntry.parse( pattern );
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "/15 3 * * ?";
    try {
      subject = CronEntry.parse( pattern );
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "*/15 3 */2 * 1-6";
    try {
      subject = CronEntry.parse( pattern );
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "B A D * *";
    try {
      subject = CronEntry.parse( pattern );
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "";
    try {
      subject = CronEntry.parse( pattern );
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "* * * * * * * * * * * * * *";
    try {
      subject = CronEntry.parse( pattern );
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Test method for {@link coyote.commons.CronEntry#mayRunAt(java.util.Calendar)}.
   */
  @Test
  public void testMayRunAt() {
    //fail( "Not yet implemented" );
  }




  /**
   * Test method for {@link coyote.commons.CronEntry#mayRunNow()}.
   */
  @Test
  public void testMayRunNow() {
    //fail( "Not yet implemented" );
  }




  /**
   * Test method for {@link coyote.commons.CronEntry#getNextTime()}.
   */
  @Test
  public void testGetNextTime() {
    //fail( "Not yet implemented" );
  }




  /**
   * Test method for {@link coyote.commons.CronEntry#getNextInterval()}.
   */
  @Test
  public void testGetNextInterval() {
    //fail( "Not yet implemented" );
  }

}
