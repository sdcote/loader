/*
 * Copyright (c) 2003 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 */
package coyote.commons.network.http;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Cookie {

  public static String getHTTPTime( final int days ) {
    final Calendar calendar = Calendar.getInstance();
    final SimpleDateFormat dateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss z", Locale.US );
    dateFormat.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
    calendar.add( Calendar.DAY_OF_MONTH, days );
    return dateFormat.format( calendar.getTime() );
  }

  private final String n, v, e;




  public Cookie( final String name, final String value ) {
    this( name, value, 30 );
  }




  public Cookie( final String name, final String value, final int numDays ) {
    n = name;
    v = value;
    e = getHTTPTime( numDays );
  }




  public Cookie( final String name, final String value, final String expires ) {
    n = name;
    v = value;
    e = expires;
  }




  public String getHTTPHeader() {
    final String fmt = "%s=%s; expires=%s";
    return String.format( fmt, n, v, e );
  }
}