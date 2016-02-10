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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

    try {
      subject = CronEntry.parse( null );
      //System.out.println(subject);
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    String pattern = "* * * * *";
    try {
      subject = CronEntry.parse( pattern );
      //System.out.println(subject);
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "? ? ? ? ?";
    try {
      subject = CronEntry.parse( pattern );
      //System.out.println(subject);
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "/15 3 * * ?";
    try {
      subject = CronEntry.parse( pattern );
      //System.out.println(subject);
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "*/15 3 */2 * 1-6";
    try {
      subject = CronEntry.parse( pattern );
      //System.out.println(subject);
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "B A D * *";
    try {
      subject = CronEntry.parse( pattern );
      //System.out.println(subject);
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "";
    try {
      subject = CronEntry.parse( pattern );
      //System.out.println(subject);
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

    pattern = "* * * * * * * * * * * * * *";
    try {
      subject = CronEntry.parse( pattern );
      //System.out.println(subject);
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Test method for {@link coyote.commons.CronEntry#mayRunAt(java.util.Calendar)}.
   */
  @Test
  public void testMayRunAt() {
    StringBuffer b = new StringBuffer();
    Calendar cal = new GregorianCalendar();

    CronEntry subject = null;
    try {
      subject = CronEntry.parse( null );

      // set the minute pattern to the current minute
      subject.setMinutePattern( Integer.toString( cal.get( Calendar.MINUTE ) ) );
      subject.setHourPattern( Integer.toString( cal.get( Calendar.HOUR_OF_DAY ) ) );
      subject.setDayPattern( Integer.toString( cal.get( Calendar.DAY_OF_MONTH ) ) );
      subject.setMonthPattern( Integer.toString( cal.get( Calendar.MONTH ) + 1 ) );
      subject.setDayOfWeekPattern( Integer.toString( cal.get( Calendar.DAY_OF_WEEK ) - 1 ) );

      //System.out.println( subject );
      assertTrue( subject.mayRunAt( cal ) );
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }

  }




  /**
   * Test method for {@link coyote.commons.CronEntry#mayRunNow()}.
   */
  @Test
  public void testMayRunNow() {
    String pattern = "* * * * *";
    CronEntry subject = null;
    try {
      subject = CronEntry.parse( pattern );
      assertTrue( subject.mayRunNow() );

      subject = CronEntry.parse( null );
      Calendar cal = new GregorianCalendar();
      subject.setMinutePattern( Integer.toString( cal.get( Calendar.MINUTE ) ) );
      subject.setHourPattern( Integer.toString( cal.get( Calendar.HOUR_OF_DAY ) ) );
      subject.setDayPattern( Integer.toString( cal.get( Calendar.DAY_OF_MONTH ) ) );
      subject.setMonthPattern( Integer.toString( cal.get( Calendar.MONTH ) + 1 ) );
      subject.setDayOfWeekPattern( Integer.toString( cal.get( Calendar.DAY_OF_WEEK ) - 1 ) );
      assertTrue( subject.mayRunNow() );

      //System.out.println( subject );      
    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Test method for {@link coyote.commons.CronEntry#getNextTime()}.
   */
  @Test
  public void testGetNextTime() {
    CronEntry subject = null;
    try {
      subject = CronEntry.parse( null );
      Calendar cal = new GregorianCalendar();
      subject.setMinutePattern( Integer.toString( cal.get( Calendar.MINUTE ) ) );
      subject.setHourPattern( Integer.toString( cal.get( Calendar.HOUR_OF_DAY ) + 1 ) );
      subject.setDayPattern( Integer.toString( cal.get( Calendar.DAY_OF_MONTH ) ) );
      subject.setMonthPattern( Integer.toString( cal.get( Calendar.MONTH ) + 1 ) );
      subject.setDayOfWeekPattern( Integer.toString( cal.get( Calendar.DAY_OF_WEEK ) - 1 ) );
      assertFalse( subject.mayRunNow() );

      //long millis = subject.getNextTime();
      // System.out.println( millis );

    } catch ( ParseException e ) {
      fail( e.getMessage() );
    }
  }




  /**
   * Test method for {@link coyote.commons.CronEntry#getNextInterval()}.
   */
  @Test
  public void testGetNextInterval() {
    //fail( "Not yet implemented" );
  }

}
