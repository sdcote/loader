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
import java.util.TreeSet;


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
 * minute 0-59
 * hour 0-23
 * day 1-31
 * month 1-12
 * day of week 0-6
 * 
 * see https://en.wikipedia.org/wiki/Cron#CRON_expression
 * 
 * <p>Note: TreeSet is used only for assisting in development and debugging 
 * when using the {@code dump()} method. It can be safely replaced with 
 * HashSet.</p>
 */
public class CronEntry {
  private static final String ANY = "*";
  private static final String SUN = "Sun"; // 0
  private static final String MON = "Mon"; // 1
  private static final String TUE = "Tue"; // 2
  private static final String WED = "Wed"; // 3
  private static final String THU = "Thu"; // 4
  private static final String FRI = "Fri"; // 5
  private static final String SAT = "Sat"; // 6

  private static final String WEEKDAYS = "W"; // 1-5

  private static final String JAN = "Jan"; // 1
  private static final String FEB = "Feb"; // 2
  private static final String MAR = "MAR"; // 3
  private static final String APR = "Apr"; // 4
  private static final String MAY = "May"; // 5
  private static final String JUN = "Jun"; // 6
  private static final String JUL = "Jul"; // 7
  private static final String AUG = "Aug"; // 8
  private static final String SEP = "Sep"; // 9
  private static final String OCT = "Oct"; // 10
  private static final String NOV = "Nov"; // 11
  private static final String DEC = "Dec"; // 12

  static final protected int MAX_MINUTES_IN_HOUR = 59;
  static final protected int MAX_HOURS_IN_DAY = 23;
  static final protected int MAX_DAYS_IN_WEEK = 6;
  static final protected int MAX_MONTHS_IN_YEAR = 12;
  static final protected int MAX_DAYS_IN_MONTH = 31;

  private TreeSet<String> minutes = new TreeSet<String>();
  private TreeSet<String> hours = new TreeSet<String>();
  private TreeSet<String> day = new TreeSet<String>();
  private TreeSet<String> month = new TreeSet<String>();
  private TreeSet<String> weekday = new TreeSet<String>();
  private String configLine = "";

  private String minutePattern = ANY;
  private String hourPattern = ANY;
  private String dayPattern = ANY;
  private String monthPattern = ANY;
  private String dayOfWeekPattern = ANY;




  /**
   * Create a CronEntry which allows all times (i.e. "{@code * * * * *}")
   */
  public CronEntry() {
    setMinutePattern( ANY );
    setHourPattern( ANY );
    setDayPattern( ANY );
    setMonthPattern( ANY );
    setDayOfWeekPattern( ANY );
  }




  /**
   * Parse the given crontab pattern into a CronEntry.
   * 
   * <p>Parsing is from left to right using the traditional ordering:<ol>
   * <li>minutes</li>
   * <li>hours</li>
   * <li>day</li>
   * <li>month</li>
   * <li>day of week</li></ol>
   * Any missing fields will be defaulted to ANY (i.e."*").</p>
   * 
   * <p>Only simple syntax is supported:
   * <li>* - any value</li>
   * <li>? - any value</li>
   * <li># - scalar value</li>
   * <li>#,#, - a list of scalars including intervals</li>
   * <li>#-# - a range of numbers</li>
   * <li>/# - intervals</li></p>
   * 
   * @param pattern The pattern to parse
   * 
   * @return A CronEntry representing the given pattern
   * 
   * @throws ParseException if the pattern is invalid
   */
  public static CronEntry parse( String pattern ) throws ParseException {
    CronEntry retval = new CronEntry();

    String[] tokens = new String[0];

    // Handle null and empty arguments
    if ( pattern != null ) {
      retval.configLine = pattern.trim();
      if ( retval.configLine.length() > 0 ) {
        tokens = retval.configLine.split( " " );
      }
    }

    if ( tokens.length > 0 ) {
      retval.setMinutePattern( tokens[0] );
      if ( tokens.length > 1 ) {
        retval.setHourPattern( tokens[1] );
        if ( tokens.length > 2 ) {
          retval.setDayPattern( tokens[2] );
          if ( tokens.length > 3 ) {
            retval.setMonthPattern( tokens[3] );
            if ( tokens.length > 4 ) {
              retval.setDayOfWeekPattern( tokens[4] );
            } else {
              retval.setDayOfWeekPattern( ANY );
            }
          } else {
            retval.setMonthPattern( ANY );
            retval.setDayOfWeekPattern( ANY );
          }
        } else {
          retval.setDayPattern( ANY );
          retval.setMonthPattern( ANY );
          retval.setDayOfWeekPattern( ANY );
        }
      } else {
        retval.setHourPattern( ANY );
        retval.setDayPattern( ANY );
        retval.setMonthPattern( ANY );
        retval.setDayOfWeekPattern( ANY );
      }
    } else {
      retval.setMinutePattern( ANY );
      retval.setHourPattern( ANY );
      retval.setDayPattern( ANY );
      retval.setMonthPattern( ANY );
      retval.setDayOfWeekPattern( ANY );
    }

    return retval;
  }




  /**
   * Parse the given token and fill a hash map (a.k.a. time map) with valid 
   * values indicated by the token.
   * 
   * @param token a range the token to parse specifying a range
   * @param maximum the maximum value for the range
   * @param start the first value in the range to populate
   * 
   * @return a hash map filled with values specified by the string; will not be null
   */
  private static TreeSet<String> parseRangeParam( String token, int maximum, int start ) {
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
        rangeitems.append( "0," ); // 0 is implied
        for ( int a = 1; a <= maximum; a++ ) {
          if ( a % Integer.parseInt( paramarray[i].substring( paramarray[i].indexOf( "/" ) + 1 ) ) == 0 ) {
            if ( a == maximum ) {
              rangeitems.append( start + "," );
            } else {
              rangeitems.append( a + "," );
            }
          }
        }
      } else {
        if ( paramarray[i].equals( "*" ) || paramarray[i].equals( "?" ) ) {
          rangeitems.append( fillRange( start + "-" + maximum ) );
        } else {
          // TODO: check for valid values!
          rangeitems.append( fillRange( paramarray[i] ) );
        }
      }
    }
    String[] values = rangeitems.toString().split( "," );
    TreeSet<String> result = new TreeSet<String>();
    for ( int i = 0; i < values.length; i++ ) {
      result.add( values[i] );
    }

    return result;

  }




  /**
   * Convert the #-# pattern to a list (#,#,#,#,...) pattern.
   * 
   * @param range The range pattern in the format of "#-#"
   *  
   * @return a list of values represented by that range
   */
  private static String fillRange( String range ) {
    // if no delimiter, just return the range as a value
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
   * Check the current calendar object to see if it is included in the 
   * currently sent time pattern.
   * 
   * @param cal the calendar to check
   * 
   * @return true if the date represented by the argument can run according to this cron entry, false otherwise.
   */
  public boolean mayRunAt( Calendar cal ) {
    int monthOfYear = cal.get( Calendar.MONTH ) + 1;
    int dayOfMonth = cal.get( Calendar.DAY_OF_MONTH );
    int dayOfWeek = cal.get( Calendar.DAY_OF_WEEK ) - 1;
    int hourOfDay = cal.get( Calendar.HOUR_OF_DAY );
    int minuteOfHour = cal.get( Calendar.MINUTE );

    if ( minutes.contains( Integer.toString( minuteOfHour ) ) ) {
      if ( hours.contains( Integer.toString( hourOfDay ) ) ) {
        if ( day.contains( Integer.toString( dayOfMonth ) ) ) {
          if ( month.contains( Integer.toString( monthOfYear ) ) ) {
            if ( weekday.contains( Integer.toString( dayOfWeek ) ) ) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }




  /**
   * @return true if the current system time can run according to this cron entry.
   */
  public boolean mayRunNow() {
    return mayRunAt( new GregorianCalendar() );
  }




  public long getNextTime() {
    return getNextTime( new GregorianCalendar() );
  }




  // Return the next time specified by this cron entry 
  public long getNextTime( GregorianCalendar cal ) {
    long retval = -1;
    int next = 0;

    int monthOfYear, dayOfMonth, dayOfWeek, hourOfDay, minuteOfHour;

    while ( retval < 0 ) {
      monthOfYear = cal.get( Calendar.MONTH ) + 1;
      dayOfMonth = cal.get( Calendar.DAY_OF_MONTH );
      dayOfWeek = cal.get( Calendar.DAY_OF_WEEK ) - 1;
      hourOfDay = cal.get( Calendar.HOUR_OF_DAY );
      minuteOfHour = cal.get( Calendar.MINUTE );

      if ( monthPasses( monthOfYear ) ) {

        if ( weekDayPasses( dayOfWeek ) && dayPasses( dayOfMonth ) ) {

          if ( hourPasses( hourOfDay ) ) {

            if ( minutePasses( minuteOfHour ) ) {
              /// we got it
              retval = cal.getTimeInMillis();
            } else {
              // find the next allowable minute
              next = getNext( minutes, minuteOfHour, MAX_MINUTES_IN_HOUR );

              // if next < where we started, increment hour
              if ( next <= minuteOfHour ) {
                cal.set( Calendar.HOUR_OF_DAY, getNext( hours, hourOfDay, MAX_HOURS_IN_DAY ) );
              } else {
                cal.set( Calendar.MINUTE, next );
              }
            }
          } else {
            //find the next allowable hour
            next = getNext( hours, hourOfDay, MAX_HOURS_IN_DAY );
            if ( next < hourOfDay ) {
              // go to next allowable day
              int dom = getNext( day, dayOfMonth, MAX_DAYS_IN_MONTH );

              // have to do a little check to make sure we don't set Feb31 here
              if ( dom > cal.getActualMaximum( Calendar.DAY_OF_MONTH ) ) {
                cal.add( Calendar.MONTH, 1 ); // go to the next month
                dom = getNext( day, 0, MAX_DAYS_IN_MONTH );// get next day in new month
              }
              cal.set( Calendar.DAY_OF_MONTH, dom );

            }
            cal.set( Calendar.HOUR_OF_DAY, next );

          }
        } else {
          // find the next allowable day
          next = getNext( day, dayOfMonth, MAX_DAYS_IN_MONTH );

          if ( next < dayOfMonth || next > cal.getActualMaximum( Calendar.DAY_OF_MONTH ) ) {
            cal.add( Calendar.MONTH, 1 );
          }

          cal.set( Calendar.DAY_OF_MONTH, next );

        }
      } else {
        cal.add( Calendar.MONTH, 1 );
      }
    }

//    StringBuffer b = new StringBuffer( "Returning: '" );
//    b.append( cal.get( Calendar.MINUTE ) );
//    b.append( " " );
//    b.append( cal.get( Calendar.HOUR_OF_DAY ) );
//    b.append( " " );
//    b.append( cal.get( Calendar.DAY_OF_MONTH ) );
//    b.append( " " );
//    b.append( cal.get( Calendar.MONTH ) + 1 );
//    b.append( " " );
//    b.append( cal.get( Calendar.DAY_OF_WEEK ) - 1 );
//    b.append( "' " );
//    b.append( cal.getTime() );
//    System.out.println( b );

    return retval;
  }




  /**
   * Returns the next acceptable value in the given time map starting after the 
   * given value.
   * 
   * @param timemap The time map to search
   * @param start the starting point
   * @param max the max value expected in the map
   * 
   * @return the next valid value, or 0 if the end of the map was reached 
   *         without finding the next value.
   */
  int getNext( TreeSet<String> timemap, int start, int max ) {
    if ( timemap == null || timemap.size() == 0 ) {
      throw new IllegalArgumentException( "Time map cannot be null or empty" );
    }

    // increment the return value to the max value
    for ( int indx = start + 1; indx <= max; indx++ ) {
      // go through each element of the set
      for ( int x = 0; x <= timemap.size(); x++ ) {
        // if there is an entry for the new index value, return it
        if ( timemap.contains( Integer.toString( indx ) ) ) {
          return indx;
        }
      }
    }

    // wrap around the list, since it is possible the next available value is behind us
    for ( int indx = 0; indx <= start; indx++ ) {
      for ( int x = 0; x <= timemap.size(); x++ ) {
        if ( timemap.contains( Integer.toString( indx ) ) ) {
          return indx;
        }
      }
    }

    System.err.println( "GetNext failed to get the next value starting from " + start + " in this time map: " + timemap );
    return -1;
  }




  /**
   * @return the interval of milliseconds from the current time to the next time allowed by the cron entry.
   */
  public long getNextInterval() {
    return getNextTime() - new GregorianCalendar().getTimeInMillis();
  }




  private boolean weekDayPasses( int val ) {
    return ( weekday.contains( Integer.toString( val ) ) );
  }




  private boolean monthPasses( int val ) {
    return ( month.contains( Integer.toString( val ) ) );
  }




  private boolean dayPasses( int val ) {
    return ( day.contains( Integer.toString( val ) ) );
  }




  private boolean hourPasses( int val ) {
    return ( hours.contains( Integer.toString( val ) ) );
  }




  private boolean minutePasses( int val ) {
    return ( minutes.contains( Integer.toString( val ) ) );
  }




  /**
   * @return the minutePattern
   */
  public String getMinutePattern() {
    return minutePattern;
  }




  /**
   * @param pattern the minutePattern to set
   */
  public void setMinutePattern( String pattern ) {
    minutes = parseRangeParam( pattern, MAX_MINUTES_IN_HOUR, 0 );
    minutePattern = pattern;
  }




  /**
   * @return the hourPattern
   */
  public String getHourPattern() {
    return hourPattern;
  }




  /**
   * @param pattern the hourPattern to set
   */
  public void setHourPattern( String pattern ) {
    hours = parseRangeParam( pattern, MAX_HOURS_IN_DAY, 0 );
    hourPattern = pattern;
  }




  /**
   * @return the dayPattern
   */
  public String getDayPattern() {
    return dayPattern;
  }




  /**
   * @param pattern the dayPattern to set
   */
  public void setDayPattern( String pattern ) {
    day = parseRangeParam( pattern, MAX_DAYS_IN_MONTH, 1 );
    dayPattern = pattern;
  }




  /**
   * @return the monthPattern
   */
  public String getMonthPattern() {
    return monthPattern;
  }




  /**
   * @param pattern the monthPattern to set
   */
  public void setMonthPattern( String pattern ) {
    month = parseRangeParam( pattern, MAX_MONTHS_IN_YEAR, 1 );
    monthPattern = pattern;
  }




  /**
   * @return the dayOfWeekPattern
   */
  public String getDayOfWeekPattern() {
    return dayOfWeekPattern;
  }




  /**
   * @param pattern the dayOfWeekPattern to set
   */
  public void setDayOfWeekPattern( String pattern ) {
    weekday = parseRangeParam( pattern, MAX_DAYS_IN_WEEK, 0 );
    dayOfWeekPattern = pattern;
  }




  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append( minutePattern );
    b.append( " " );
    b.append( hourPattern );
    b.append( " " );
    b.append( dayPattern );
    b.append( " " );
    b.append( monthPattern );
    b.append( " " );
    b.append( dayOfWeekPattern );
    return b.toString();
  }




  String dump() {
    StringBuffer b = new StringBuffer( "Cron Entry Pattern: " );
    b.append( toString() );
    b.append( "\r\nminutes(" );
    b.append( minutes.size() );
    b.append( "):" );
    b.append( minutes );
    b.append( "\r\nhours(" );
    b.append( hours.size() );
    b.append( "):" );
    b.append( hours );
    b.append( "\r\ndays(" );
    b.append( day.size() );
    b.append( "):" );
    b.append( day );
    b.append( "\r\nmonths(" );
    b.append( month.size() );
    b.append( "):" );
    b.append( month );
    b.append( "\r\nweekday(" );
    b.append( weekday.size() );
    b.append( "):" );
    b.append( weekday );
    return b.toString();
  }

}
