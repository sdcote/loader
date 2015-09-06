/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial API and implementation
 */
package coyote.commons.network;

import java.util.StringTokenizer;


/**
 * Class Ip4Address
 */
public class Ip4Address extends IpAddress {

  /**
   * Tests a string to see if it represents a valid IPv4 address. 
   * <p>
   * For a string to be considered valid, it must contain four numbers with 
   * values between 0 and 255, separated by dots - i.e. standard IPv4 dotted-
   * decimal notation.
   * </p>
   * <p>
   * For instance, if <code>addr</code> contains the value "202.122.3.1",
   * <code>isValidIP(addr)</code> will return 'true'.
   * </p>
   * 
   * @param addr A string that may contain an IPv4 address in dotted-decimal
   *          notation.
   * @return true if addr is an IPv4 address.
   */
  public static boolean isValidIPv4( String addr ) {
    return ( isIPv4DottedDecimal( addr ) && ( IPv4octetCount( addr ) == 4 ) );
  }




  /**
   * Tests a string to see if it represents a dotted-decimal address which may
   * be all or part of a valid IPv4 address. 
   * <p>
   * For instance if <code>addr</code> contains the value "202.122.3", 
   * <code>isIPv4DottedDecimal(addr)</code> will return 'true'.
   * </p>
   * 
   * @param addr A string that may contain an IPv4 addresss in dotted-decimal
   *          notation.
   * @return true if addr is all or part of an IPv4 address.
   */
  public static boolean isIPv4DottedDecimal( String addr ) {
    StringTokenizer labels = new StringTokenizer( addr, "." );
    String thisLabel;
    int labelCount = 0, octet;

    while ( labels.hasMoreTokens() ) {
      thisLabel = labels.nextToken();

      try {
        octet = Integer.parseInt( thisLabel );

        if ( ( octet < 0 ) || ( octet > 255 ) ) {
          return false;
        } else {
          labelCount++;
        }
      } catch ( NumberFormatException ex ) {
        return false;
      }
    }

    return ( ( labelCount >= 1 ) && ( labelCount <= 4 ) );
  }




  /**
   * Counts the number of octets in a string which is assumed to contain a
   * dotted-decimal address representing an IPv4 address.
   * 
   * @param addr The candidate dotted-decimal representation of an IPv4 address.
   * @return The number of octets contained in the string.
   */
  public static int IPv4octetCount( String addr ) {
    StringTokenizer labels = new StringTokenizer( addr, "." );
    String thisLabel;
    int labelCount = 0, octet;

    while ( labels.hasMoreTokens() ) {
      thisLabel = labels.nextToken();

      try {
        octet = Integer.parseInt( thisLabel );

        if ( ( octet < 0 ) || ( octet > 255 ) ) {
          return 0;
        } else {
          labelCount++;
        }
      } catch ( NumberFormatException ex ) {
        return 0; // *** Not sure this is right...
      }
    }

    return labelCount;
  }




  /**
   * Create a query string that is appropriate for use as the target of a PTR
   * query for an IPv4 address. 
   * <p>
   * For instance if <code>addr</code> contains the value "202.122.3.1", 
   * <code>IPv4ptrQueryString(addr)</code> will return
   * '1.3.122.202.in-addr.arpa'.
   * </p>
   * 
   * @param addr The IPv4 address or address fragment that you which to find an
   *          PTR record for.
   * @return A string representing a reverse lookup of the address passed in.
   */
  public static String IPv4ptrQueryString( String addr ) {
    if ( isIPv4DottedDecimal( addr ) ) {
      StringTokenizer labels = new StringTokenizer( addr, "." );
      String thisLabel;
      String retnValue = "in-addr.arpa";

      while ( labels.hasMoreTokens() ) {
        thisLabel = labels.nextToken();
        retnValue = thisLabel + "." + retnValue;
      }

      return ( retnValue );
    } else {
      return "";
    }
  }
}