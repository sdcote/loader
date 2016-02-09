/*
 * Copyright (c) 2006 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 */
package coyote.commons;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;


/**
 * Parses basic cron entries and provides some functions for scheduling.
 * 
 * <p>This class creates maps of valid values for each of the 5 time ranges: 
 * minutes, hours, days, months and weekdays. The maps can them be checked for
 * the existence of the argument values in those arrays. For example; if the 
 * current minute is 15 and the minutes map contains 15, then the current time 
 * passes the minute check. The rest of the date/time values can be checked 
 * accordingly.</p>
 * 
 * see https://en.wikipedia.org/wiki/Cron#CRON_expression
 */
public class CronEntry {

  static final protected int MINUTESPERHOUR = 60;
  static final protected int HOURESPERDAY = 24;
  static final protected int DAYSPERWEEK = 7;
  static final protected int MONTHSPERYEAR = 12;
  static final protected int DAYSPERMONTH = 31;

  private HashMap<String, String> minutes = new HashMap<String, String>();
  private HashMap<String, String> hours = new HashMap<String, String>();
  private HashMap<String, String> day = new HashMap<String, String>();
  private HashMap<String, String> month = new HashMap<String, String>();
  private HashMap<String, String> weekday = new HashMap<String, String>();
  private String configLine = "";




  private CronEntry() {}




  /**
   * Parse the given crontab pattern into a CronEntry
   * 
   * <p>Only simple syntax is supported:
   * <li>* - any value</li>
   * <li>? - any value</li>
   * <li># - scalar value</li>
   * <li>#,#, - a list of scalars</li>
   * <li>#-# - a range of numbers</li>
   * <li>/# - intervals</li>
   * 
   * @param pattern The pattern to parse
   * 
   * @return A CronEntry representing the given pattern
   * 
   * @throws ParseException if the pattern is invalid
   */
  public static CronEntry parse( String pattern ) throws ParseException {
    CronEntry retval = new CronEntry();

    retval.configLine = pattern;
    String[] tokens = retval.configLine.split( " " );
    if ( tokens.length > 0 ) {
      retval.minutes = parseRangeParam( tokens[0], MINUTESPERHOUR, 0 );
      if ( tokens.length > 1 ) {
        retval.hours = parseRangeParam( tokens[1], HOURESPERDAY, 0 );
        if ( tokens.length > 2 ) {
          retval.day = parseRangeParam( tokens[2], DAYSPERMONTH, 1 );
          if ( tokens.length > 3 ) {
            retval.month = parseRangeParam( tokens[3], MONTHSPERYEAR, 1 );
            if ( tokens.length > 4 ) {
              retval.weekday = parseRangeParam( tokens[4], DAYSPERWEEK, 0 );
            } else {
              retval.weekday = parseRangeParam( "*", DAYSPERWEEK, 0 );
            }
          } else {
            retval.month = parseRangeParam( "*", MONTHSPERYEAR, 1 );
            retval.weekday = parseRangeParam( "*", DAYSPERWEEK, 0 );
          }
        } else {
          retval.day = parseRangeParam( "*", DAYSPERMONTH, 1 );
          retval.month = parseRangeParam( "*", MONTHSPERYEAR, 1 );
          retval.weekday = parseRangeParam( "*", DAYSPERWEEK, 0 );
        }
      } else {
        retval.hours = parseRangeParam( "*", HOURESPERDAY, 0 );
        retval.day = parseRangeParam( "*", DAYSPERMONTH, 1 );
        retval.month = parseRangeParam( "*", MONTHSPERYEAR, 1 );
        retval.weekday = parseRangeParam( "*", DAYSPERWEEK, 0 );
      }
    } else {
      retval.minutes = parseRangeParam( "*", MINUTESPERHOUR, 0 );
      retval.hours = parseRangeParam( "*", HOURESPERDAY, 0 );
      retval.day = parseRangeParam( "*", DAYSPERMONTH, 1 );
      retval.month = parseRangeParam( "*", MONTHSPERYEAR, 1 );
      retval.weekday = parseRangeParam( "*", DAYSPERWEEK, 0 );
    }
    return retval;
  }




  /**
   * 
   * @param token a range
   * @param timelength  range of values
   * 
   * @return
   */
  private static HashMap<String, String> parseRangeParam( String token, int timelength, int minlength ) {
    // split by ","

    // System.out.println(param + ":");

    String[] paramarray;
    if ( token.indexOf( "," ) != -1 ) {
      paramarray = token.split( "," );
    } else {
      paramarray = new String[] { token };
    }
    StringBuffer rangeitems = new StringBuffer();
    for ( int i = 0; i < paramarray.length; i++ ) {
      // you may mix */# syntax with other range syntax
      if ( paramarray[i].indexOf( "/" ) != -1 ) {
        // handle */# syntax
        for ( int a = 1; a <= timelength; a++ ) {
          if ( a % Integer.parseInt( paramarray[i].substring( paramarray[i].indexOf( "/" ) + 1 ) ) == 0 ) {
            if ( a == timelength ) {
              rangeitems.append( minlength + "," );
            } else {
              rangeitems.append( a + "," );
            }
          }
        }
      } else {
        if ( paramarray[i].equals( "*" ) || paramarray[i].equals( "?" ) ) {
          rangeitems.append( fillRange( minlength + "-" + timelength ) );
        } else {
          rangeitems.append( fillRange( paramarray[i] ) );
        }
      }
    }
    String[] values = rangeitems.toString().split( "," );
    HashMap<String, String> result = new HashMap<String, String>();
    for ( int i = 0; i < values.length; i++ ) {
      result.put( values[i], values[i] );
    }

    return result;

  }




  /**
   * 
   * @param range
   * @return
   */
  private static String fillRange( String range ) {
    // split by "-"

    if ( range.indexOf( "-" ) == -1 ) {
      return range + ",";
    }

    String[] rangearray = range.split( "-" );
    StringBuffer result = new StringBuffer();
    for ( int i = Integer.parseInt( rangearray[0] ); i <= Integer.parseInt( rangearray[1] ); i++ ) {
      result.append( i + "," );
    }
    return result.toString();
  }




  /**
   * @param cal
   * @return
   */
  public boolean mayRunAt( Calendar cal ) {
    int monthOfYear = cal.get( Calendar.MONTH ) + 1;
    int dayOfMonth = cal.get( Calendar.DAY_OF_MONTH );
    int dayOfWeek = cal.get( Calendar.DAY_OF_WEEK ) - 1;
    int hourOfDay = cal.get( Calendar.HOUR_OF_DAY );
    int minuteOfHour = cal.get( Calendar.MINUTE );

    if ( minutes.get( Integer.toString( minuteOfHour ) ) != null ) {
      if ( hours.get( Integer.toString( hourOfDay ) ) != null ) {
        if ( day.get( Integer.toString( dayOfMonth ) ) != null ) {
          if ( month.get( Integer.toString( monthOfYear ) ) != null ) {
            if ( weekday.get( Integer.toString( dayOfWeek ) ) != null ) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }




  public boolean mayRunNow() {
    return mayRunAt( new GregorianCalendar() );
  }




  // Return the next time specified by this cron entry 
  public long getNextTime() {
    long retval = -1;

    Calendar cal = new GregorianCalendar();

    int monthOfYear = cal.get( Calendar.MONTH ) + 1;
    int dayOfMonth = cal.get( Calendar.DAY_OF_MONTH );
    int dayOfWeek = cal.get( Calendar.DAY_OF_WEEK ) - 1;
    int hourOfDay = cal.get( Calendar.HOUR_OF_DAY );
    int minuteOfHour = cal.get( Calendar.MINUTE );

    while ( retval < 0 ) {
      if ( monthPasses( monthOfYear ) ) {
        if ( weekDayPasses( dayOfWeek ) && dayPasses( dayOfMonth ) ) {
          if ( hourPasses( hourOfDay ) ) {
            if ( minutePasses( minuteOfHour ) ) {
              /// we got it
              retval = cal.getTimeInMillis();
            } else {
              cal.add( Calendar.MINUTE, +1 );
            }
          } else {
            // Nudge to hour of day (0)
            cal.add( Calendar.HOUR, 1 );
            // set to first minute of hour (0)
            cal.set( Calendar.MINUTE, 0 );
          }
        } else {
          // Nudge to next day
          cal.add( Calendar.DAY_OF_MONTH, 1 );
          // set to first hour of day (0)
          cal.set( Calendar.HOUR_OF_DAY, 0 );
          // set to first minute of hour (0)
          cal.set( Calendar.MINUTE, 0 );
        }
      } else {
        // Nudge to next month
        cal.add( Calendar.MONTH, 1 );
        // set to first day of month
        cal.set( Calendar.DAY_OF_MONTH, 1 );
        // set to first hour of day (0)
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        // set to first minute of hour (0)
        cal.set( Calendar.MINUTE, 0 );
      }
    }

    return retval;
  }




  /**
   * @return the interval of milliseconds from the current time to the next time allowed by the cron entry.
   */
  public long getNextInterval() {
    return getNextTime() - new GregorianCalendar().getTimeInMillis();
  }




  private boolean weekDayPasses( int val ) {
    return ( weekday.get( Integer.toString( val ) ) != null );
  }




  private boolean monthPasses( int val ) {
    return ( month.get( Integer.toString( val ) ) != null );
  }




  private boolean dayPasses( int val ) {
    return ( day.get( Integer.toString( val ) ) != null );
  }




  private boolean hourPasses( int val ) {
    return ( hours.get( Integer.toString( val ) ) != null );
  }




  private boolean minutePasses( int val ) {
    return ( minutes.get( Integer.toString( val ) ) != null );
  }

}
