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
 * minute 0-59
 * hour 0-23
 * day 1-31
 * month 1-12
 * day of week 0-6
 * 
 * see https://en.wikipedia.org/wiki/Cron#CRON_expression
 */
public class CronEntry {
  private static final String ANY = "*";
  private static final String MON = "Mon";
  private static final String TUE = "Tue";
  private static final String WED = "Wed";
  private static final String THU = "Thu";
  private static final String FRI = "Fri";
  private static final String SAT = "Sat";
  private static final String SUN = "Sun";
  private static final String WEEKDAYS = "W";
  private static final String JAN = "Jan";
  private static final String FEB = "Feb";
  private static final String MAR = "MAR";
  private static final String APR = "Apr";
  private static final String MAY = "May";
  private static final String JUN = "Jun";
  private static final String JUL = "Jul";
  private static final String AUG = "Aug";
  private static final String SEP = "Sep";
  private static final String OCT = "Oct";
  private static final String NOV = "Nov";
  private static final String DEC = "Dec";

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

  private String minutePattern = ANY;
  private String hourPattern = ANY;
  private String dayPattern = ANY;
  private String monthPattern = ANY;
  private String dayOfWeekPattern = ANY;




  private CronEntry() {

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
   * <li>#,#, - a list of scalars</li>
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
   * 
   * @param token a range
   * @param timelength  range of values
   * 
   * @return
   */
  private static HashMap<String, String> parseRangeParam( String token, int timelength, int minlength ) {
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
          // TODO: check for valid values!
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
   * @param cal the calendar to check
   * @return true if the date represented by the argument can run according to this cron entry, false otherwise.
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




  /**
   * @return true if the current system time can run according to this cron entry.
   */
  public boolean mayRunNow() {
    return mayRunAt( new GregorianCalendar() );
  }




  // Return the next time specified by this cron entry 
  public long getNextTime() {
    long retval = -1;
    int next = 0;
    Calendar cal = new GregorianCalendar();

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
              next = getNext( minutes, minuteOfHour );
              if ( next == 0 ) {
                cal.set( Calendar.HOUR_OF_DAY, getNext( hours, hourOfDay ) );
                cal.set( Calendar.MINUTE, getNext( minutes, 0 ) );
              } else {
                cal.set( Calendar.MINUTE, next );
              }
            }
          } else {
            //find the next allowable hour
            next = getNext( hours, hourOfDay );
            if ( next == 0 ) {
              int dom = getNext( day, dayOfMonth );

              // have to do a little check to make sure we dont set Feb31 here
              if ( dom > cal.getActualMaximum( Calendar.DAY_OF_MONTH ) ) {
                cal.add( Calendar.MONTH, 1 ); // go to the next month
                dom = getNext( day, 0 );// get next day in new month
              }

              cal.set( Calendar.DAY_OF_MONTH, dom );
              cal.set( Calendar.HOUR_OF_DAY, getNext( hours, 0 ) );
            } else {
              cal.set( Calendar.HOUR_OF_DAY, next );
            }
          }
        } else {
          // find the next allowable day
          next = getNext( day, dayOfMonth );

          if ( next > cal.getActualMaximum( Calendar.DAY_OF_MONTH ) ) {
            cal.add( Calendar.MONTH, 1 ); // go to the next month
            next = getNext( day, 0 );// get next day in new month
          } else if ( next == 0 ) {
            cal.set( Calendar.MONTH, getNext( month, monthOfYear ) );
            // TODO do we set everything else to zero as well?????  
          }

          //

          // TODO Start Here

          //

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
   * Returns the next acceptable value in the given time map starting after the 
   * given value.
   * 
   * @param timemap The time map to search
   * 
   * @param start the starting point
   * 
   * @return the next valid value, or 0 if the end of the map was reached 
   *         without finding the next value.
   */
  private int getNext( HashMap<String, String> timemap, int start ) {
    // start searching at the next higest value
    int indx = start + 1;

    if ( timemap == null || timemap.size() == 0 ) {
      throw new IllegalArgumentException( "Time map cannot be null or empty" );
    }

    // cycle through the map, but only for as long as there are entries
    for ( int x = 0; x < timemap.size(); x++ ) {
      // if there is an entry for the new index value, return it
      if ( timemap.containsKey( Integer.toString( indx ) ) ) {
        return indx;
      }
      // otherwise increment the value
      indx++;
    }

    // we apparently went through the entire array(and then some) without 
    // finding a matching value; return zero indicating where to start next
    return 0;
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




  /**
   * @return the minutePattern
   */
  protected String getMinutePattern() {
    return minutePattern;
  }




  /**
   * @param pattern the minutePattern to set
   */
  protected void setMinutePattern( String pattern ) {
    minutes = parseRangeParam( pattern, MINUTESPERHOUR, 0 );
    minutePattern = pattern;
  }




  /**
   * @return the hourPattern
   */
  protected String getHourPattern() {
    return hourPattern;
  }




  /**
   * @param pattern the hourPattern to set
   */
  protected void setHourPattern( String pattern ) {
    hours = parseRangeParam( pattern, HOURESPERDAY, 0 );
    hourPattern = pattern;
  }




  /**
   * @return the dayPattern
   */
  protected String getDayPattern() {
    return dayPattern;
  }




  /**
   * @param pattern the dayPattern to set
   */
  protected void setDayPattern( String pattern ) {
    day = parseRangeParam( pattern, DAYSPERMONTH, 1 );
    dayPattern = pattern;
  }




  /**
   * @return the monthPattern
   */
  protected String getMonthPattern() {
    return monthPattern;
  }




  /**
   * @param pattern the monthPattern to set
   */
  protected void setMonthPattern( String pattern ) {
    month = parseRangeParam( pattern, MONTHSPERYEAR, 1 );
    monthPattern = pattern;
  }




  /**
   * @return the dayOfWeekPattern
   */
  protected String getDayOfWeekPattern() {
    return dayOfWeekPattern;
  }




  /**
   * @param pattern the dayOfWeekPattern to set
   */
  protected void setDayOfWeekPattern( String pattern ) {
    weekday = parseRangeParam( pattern, DAYSPERWEEK, 0 );
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

}
