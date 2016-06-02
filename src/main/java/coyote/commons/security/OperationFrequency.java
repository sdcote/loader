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
package coyote.commons.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import coyote.commons.network.IpAddress;
import coyote.commons.network.IpNetwork;


/**
 * This provides a basic check for too frequent requests by IP address.
 * 
 * <p>This class sets up a map of check times mapped by IpAddress. For each 
 * entry in the table, a circular array contains times, if a check matches an 
 * entry, it is then checked to see if the next element in the array is less 
 * than the allowable interval, if so false is returned indicating the check 
 * happened too soon and that too may checks are being performed too often.</p>
 * 
 * <p>This class is designed to provide a simple way to check for a Denial of 
 * Service attack by allowing the setting a limit of the number of requests by
 * IP Address.</p> 
 */
public class OperationFrequency {
  private static final short DEFAULT_LIMIT = 24;
  private static final long DEFAULT_DURATION = 500;

  private Map<IpNetwork, RequestTable> networks = new HashMap<IpNetwork, RequestTable>();
  private Map<IpAddress, RequestTable> addresses = new HashMap<IpAddress, RequestTable>();
  private short limit = DEFAULT_LIMIT;
  private long duration = DEFAULT_DURATION;




  public synchronized RequestTable addNetwork( IpNetwork addr, short limit, long duration ) {
    RequestTable retval = new RequestTable( limit, duration );
    networks.put( addr, retval );
    return retval;
  }




  public synchronized RequestTable addAddress( IpAddress addr, short limit, long duration ) {
    RequestTable retval = new RequestTable( limit, duration );
    addresses.put( addr, retval );
    return retval;
  }




  public synchronized boolean check( IpAddress addr ) {
    RequestTable table = null;

    // look for a network match
    for ( IpNetwork net : networks.keySet() ) {
      if ( net.contains( addr ) ) {
        table = networks.get( net );
        break;
      }
    }

    // else look for an address match
    if ( table == null ) {
      for ( IpAddress adr : addresses.keySet() ) {
        if ( adr.equals( addr ) ) {
          table = addresses.get( adr );
          break;
        }
      }
    }

    // else add a new address
    if ( table == null ) {
      table = addAddress( addr, limit, duration );
    }

    return table.check( System.currentTimeMillis() );
  }




  public int getNetworkCount() {
    return networks.size();
  }




  public int getAddressCount() {
    return addresses.size();
  }




  /**
   * @return the limit
   */
  public short getLimit() {
    return limit;
  }




  /**
   * @param limit the limit to set
   */
  public void setLimit( short limit ) {
    this.limit = limit;
  }




  /**
   * @return the duration
   */
  public long getDuration() {
    return duration;
  }




  /**
   * @param duration the duration to set
   */
  public void setDuration( long duration ) {
    this.duration = duration;
  }




  /**
   * Clear out all old address mappings by their last check time.
   * 
   * <p>Network Mappings are not touched</p>
   * 
   * @param age any tables with last check times older than this number of milliseconds will be removed from the mappings
   */
  public synchronized void expire( long age ) {
    long time = System.currentTimeMillis();

    Iterator<Map.Entry<IpAddress, RequestTable>> it = addresses.entrySet().iterator();
    while ( it.hasNext() ) {
      Map.Entry<IpAddress, RequestTable> entry = it.next();

      if ( time - entry.getValue().getLastCheck() > age ) {
        it.remove();
      }
    }

  }

  private class RequestTable {
    private final long[] times;
    private final long interval;
    private long count = -1;




    RequestTable( short size, long interval ) {
      times = new long[size];
      this.interval = interval;
    }




    public long getLastCheck() {
      // find the current position in the list of times
      int index = (int)( ( count < 0 ) ? 0 : count % times.length );
      return times[index];
    }




    /**
     * @param time the time in millis (Java epoch)
     * 
     * @return true if
     */
    boolean check( long time ) {
      // in Java, overflows go negative not back to zero
      if ( count < 0 ) {
        count = 0;
      } else {
        count++;
      }

      // find the next position in the list of times
      int index = (int)( count % times.length );

      try {
        if ( times[index] > 0 && time - times[index] < interval )
          return false;
        else
          return true;
      }
      finally {
        times[index] = time;
      }
    }

  } // class

}