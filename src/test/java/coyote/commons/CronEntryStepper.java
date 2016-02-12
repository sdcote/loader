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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * 
 */
public class CronEntryStepper {
  private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

  static SimpleDateFormat DATEFORMAT = new SimpleDateFormat( DEFAULT_DATE_FORMAT );




  /**
   * @param args
   */
  public static void main( String[] args ) {
    CronEntry subject = new CronEntry();
    long millis;
    Calendar now = new GregorianCalendar();
    //now.add( Calendar.MINUTE, -15 );
    System.out.println( "NOW:      " + DATEFORMAT.format( now.getTime() ) + " - " + CronEntry.toPattern( now ) );
    System.out.println();

    long nowmillis = now.getTimeInMillis();

    Calendar cal = new GregorianCalendar();

    // set the pattern to one hour in the future
    subject.setHourPattern( Integer.toString( cal.get( Calendar.HOUR_OF_DAY ) + 1 ) ); // adjustment
    System.out.println( subject.dump() );

    millis = subject.getNextTime( now );
    Date result = new Date( millis );

    System.out.println();
    System.out.println( "RESULT:   " + DATEFORMAT.format( result ) );
    System.out.println( "INTERVAL: " + millis + " - " + CronEntryTest.formatElapsed( millis - nowmillis ) );
  }

}
