/*
 * Copyright (c) 2006 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.i13n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;


/**
 * This is the default implementation of a scorecard.
 * 
 * <p>Some find it useful to make this globally accessible so that many 
 * different components can use this in a coordinated manner.</p>
 */
public class ScorecardDflt implements Scorecard
{
  private String CARDID = UUID.randomUUID().toString().toLowerCase();

  /** The time this scorecard was create/started. */
  private long startedTimestamp = 0;

  /** Timing is disabled by default */
  private volatile boolean timingEnabled = false;

  /** Application Response Measurement is disabled by default */
  private volatile boolean armEnabled = false;

  /** Gauges are disabled by default */
  private volatile boolean gaugesEnabled = false;

  /** Map of master timers by their name */
  private final HashMap<String, TimingMaster> masterTimers = new HashMap<String, TimingMaster>();

  /** Map of counters by their name */
  private final HashMap<String, Counter> counters = new HashMap<String, Counter>();

  /** Map of ARM masters by their name */
  private final HashMap<String, ArmMaster> armMasters = new HashMap<String, ArmMaster>();

  /** Map of states by their name */
  private final HashMap<String, State> states = new HashMap<String, State>();

  /** Map of gauges by their name */
  private final HashMap<String, Gauge> gauges = new HashMap<String, Gauge>();

  /** Re-usable null timer to save object creation and GC'n */
  private static final Timer NULL_TIMER = new NullTimer( null );

  /** Re-usable null ARM transaction to save object creation and GC'n */
  private static final ArmTransaction NULL_ARM = new NullArm( null, null, null );

  /** Re-usable null gauge to save object creation and GC'n */
  private static final Gauge NULL_GAUGE = new NullGauge( null );




  // TODO: NullCounter? EnableCounters? Enable Counter?

  // TODO: NullState?  EnableStates? EnableState?

  // TODO: NullGauge? MasterGauges?

  public ScorecardDflt()
  {
    startedTimestamp = System.currentTimeMillis();
  }




  /**
   * Return the identifier the card is using to differentiate itself from other 
   * cards on this host.
   * 
   * @return The identifier for this scorecard.
   */
  public String getId()
  {
    return CARDID;
  }




  /**
   * Assign a unique identifier to this scorecard.
   * 
   * @param id the unique identifier to set
   */
  public void setId( String id )
  {
    CARDID = id;
  }




  /**
   * @return The epoch time in milliseconds this fixture was started.
   */
  public long getStartedTime()
  {
    return startedTimestamp;
  }




  /**
   * Return how long the fixture has been active in a format using only the 
   * significant time measurements.
   * 
   * <p>Significant measurements means if the number of seconds extend past 24 
   * hours, then only report the days and hours skipping the minutes and 
   * seconds. Examples include <tt>4m 23s</tt> or <tt>22d 4h</tt>. The format 
   * is designed to make reporting fixture up-time more polished.</p>
   * 
   * @return the time the fixture has been active in a reportable format.
   */
  public String getUptimeString()
  {
    return formatSignificantElapsedTime( ( System.currentTimeMillis() - startedTimestamp ) / 1000 );
  }




  /**
   * Start a timer with the given name.
   * 
   * <p>Use the returned Timer to stop the interval measurement.</p>
   *  
   * @param tag The name of the timer instance to start.
   * 
   * @return The timer instance that should be stopped when the interval is 
   *         completed.
   */
  public Timer startTimer( String tag )
  {
    Timer retval = null;
    if( timingEnabled )
    {
      synchronized( masterTimers )
      {
        // get an existing master timer or create a new one
        TimingMaster master = (TimingMaster)masterTimers.get( tag );
        if( master == null )
        {
          master = new TimingMaster( tag );
          masterTimers.put( tag, master );
        }

        // have the master timer return a timer instance
        retval = master.createTimer();

        //start the timer instance
        retval.start();
      }
    }
    else
    {
      // just return the do-nothing timer
      retval = NULL_TIMER;
    }

    // return the started timer
    return retval;
  }




  /**
   * Enable the timer with the given name.
   * 
   * <p>If a timer is enabled that has not already been created, a new 
   * timer will be created in memory.</p>
   * 
   * @param tag The name of the timer to enable.
   */
  public void enableTimer( String tag )
  {
    synchronized( masterTimers )
    {
      // get an existing master timer or create a new one
      TimingMaster master = (TimingMaster)masterTimers.get( tag );
      if( master == null )
      {
        master = new TimingMaster( tag );
        masterTimers.put( tag, master );
      }
      master.setEnabled( true );
    }
  }




  /**
   * Disable the timer with the given name.
   * 
   * <p>Disabling a timer will cause all new timers with the given name to 
   * skip processing reducing the amount of processing performed by the 
   * timers without losing the existing data in the timer. Any existing 
   * timers will continue to accumulate data.</p>
   * 
   * <p>If a timer is disabled that has not already been created, a disabled 
   * timer will be created in memory that can be enabled at a later time.</p>
   * 
   * @param tag The name of the timer to disable.
   */
  public void disableTimer( String tag )
  {
    synchronized( masterTimers )
    {
      // get an existing master timer or create a new one
      TimingMaster master = (TimingMaster)masterTimers.get( tag );
      if( master == null )
      {
        master = new TimingMaster( tag );
        masterTimers.put( tag, master );
      }
      master.setEnabled( false );
    }
  }




  /**
   * Enable fully-functional timers from this point forward.
   * 
   * <p>When timing is enabled, functional timers are returned and their 
   * metrics are collected for later reporting. when timing is disabled, null 
   * timers are be returned each time a timer is requested. This keeps all code 
   * operational regardless of the runtime status of timing.</p>
   */
  public void enableTiming( boolean flag )
  {
    synchronized( masterTimers )
    {
      timingEnabled = flag;
    }
  }




  /**
   * Removes all timers from the scorecard and frees them up for garbage 
   * collection.
   */
  public void resetTimers()
  {
    synchronized( masterTimers )
    {
      masterTimers.clear();
    }
  }




  /**
   * Get an iterator over all the Master Timers in the scorecard.
   */
  public Iterator<TimingMaster> getTimerIterator()
  {
    final ArrayList<TimingMaster> list = new ArrayList<TimingMaster>();
    synchronized( masterTimers )
    {
      for( final Iterator<TimingMaster> it = masterTimers.values().iterator(); it.hasNext(); list.add( it.next() ) )
      {
        ;
      }
    }
    return list.iterator();
  }




  /**
   * Get the master timer with the given name.
   *  
   * @param name The name of the master timer to retrieve.
   * 
   * @return The master timer with the given name or null if that timer 
   *         does not exist.
   */
  public TimingMaster getTimerMaster( final String tag )
  {
    synchronized( masterTimers )
    {
      return (TimingMaster)masterTimers.get( tag );
    }
  }




  /**
   * Return the counter with the given name.
   * 
   * <p>If the counter does not exist, one will be created and added to the 
   * static list of counters for later retrieval.</p>
   * 
   * @param name The name of the counter to return.
   * 
   * @return The counter with the given name.
   */
  public Counter getCounter( final String name )
  {
    Counter counter = null;
    synchronized( counters )
    {
      counter = counters.get( name );
      if( counter == null )
      {
        counter = new Counter( name );
        counters.put( name, counter );
      }
    }
    return counter;
  }




  /**
   * @return The number of counters in the scorecard at the present time.
   */
  public int getCounterCount()
  {
    return counters.size();
  }




  /**
   * Access an iterator over the counters.
   * 
   * <p>NOTE: this iterator is detached from the counters in that the remove() 
   * call on the iterator will only affect the returned iterator and not the 
   * counter collection in the scorecard. If you wish to remove a counter, you 
   * MUST call removeCounter(Counter) with the reference returned from this 
   * iterator as well.</p>
   * 
   * @return a detached iterator over the counters.
   */
  public Iterator<Counter> getCounterIterator()
  {
    final ArrayList<Counter> list = new ArrayList<Counter>();
    for( final Iterator<Counter> it = counters.values().iterator(); it.hasNext(); list.add( it.next() ) )
    {
      ;
    }
    return list.iterator();
  }




  /**
   * Remove the counter with the given name.
   * 
   * @param name Name of the counter to remove.
   * 
   * @return The removed counter.
   */
  public Counter removeCounter( final String name )
  {
    synchronized( counters )
    {
      return (Counter)counters.remove( name );
    }
  }




  /**
   * Reset the counter with the given name returning a copy of the counter 
   * before the reset occurred.
   * 
   * <p>The return value will represent a copy of the counter prior to the 
   * reset and is useful for applications that desire delta values. These delta
   * values are simply the return values of successive reset calls.</p>
   * 
   * <p>If the counter does not exist, it will be created prior to being reset.
   * The return value will reflect an empty counter with the given name.</p>
   *
   * @param name The name of the counter to reset.
   *  
   * @return a counter containing the values of the counter prior to the reset.
   */
  public Counter resetCounter( final String name )
  {
    Counter retval = null;
    synchronized( counters )
    {
      retval = getCounter( name ).reset();
    }

    return retval;
  }




  /**
   * Decrease the value with the given name by the given amount.
   * 
   * <p>This method retrieves the counter with the given name or creates one by 
   * that name if it does not yet exist. The retrieved counter is then 
   * decreased by the given amount.</p> 
   * 
   * @param tag The name of the counter to decrease.
   * 
   * @return The final value of the counter after the operation.
   */
  public long decrease( final String tag, final long value )
  {
    return getCounter( tag ).decrease( value );
  }




  /**
   * Decrement the value with the given name.
   * 
   * <p>This method retrieves the counter with the given name or creates one by 
   * that name if it does not yet exist. The retrieved counter is then 
   * decreased by one (1).</p> 
   * 
   * @param tag The name of the counter to decrement.
   * 
   * @return The final value of the counter after the operation.
   */
  public long decrement( final String tag )
  {
    return getCounter( tag ).decrement();
  }




  /**
   * Increase the value with the given name by the given amount.
   * 
   * <p>This method retrieves the counter with the given name or creates one by 
   * that name if it does not yet exist. The retrieved counter is then 
   * increased by the given amount.</p> 
   * 
   * @param tag The name of the counter to increase.
   * 
   * @return The final value of the counter after the operation.
   */
  public long increase( final String tag, final long value )
  {
    return getCounter( tag ).increase( value );
  }




  /**
   * Increment the value with the given name.
   * 
   * <p>This method retrieves the counter with the given name or creates one by 
   * that name if it does not yet exist. The retrieved counter is then 
   * increased by one (1).</p> 
   * 
   * @param tag The name of the counter to increment.
   * 
   * @return The final value of the counter after the operation.
   */
  public long increment( final String tag )
  {
    return getCounter( tag ).increment();
  }




  /**
   * Deactivate a particular class of Application Response Measurement calls 
   * from this point on.
   */
  public void disableArmClass( final String name )
  {
    synchronized( armMasters )
    {
      // get an existing master ARM or create a new one
      ArmMaster master = armMasters.get( name );
      if( master == null )
      {
        master = new ArmMaster( name );
        armMasters.put( name, master );
      }
      master.setEnabled( false );
    }
  }




  /**
   * Activate all Application Response Measurement calls from this point on.
   */
  public void enableArm( final boolean flag )
  {
    synchronized( armMasters )
    {
      armEnabled = flag;
    }
  }




  /**
   * Activate a particular class of Application Response Measurement calls from 
   * this point on.
   */
  public void enableArmClass( final String name )
  {
    synchronized( armMasters )
    {
      // get an existing master ARM or create a new one
      ArmMaster master = armMasters.get( name );
      if( master == null )
      {
        master = new ArmMaster( name );
        armMasters.put( name, master );
      }
      master.setEnabled( true );
    }
  }




  /**
   * Get an iterator over all the ARM Masters in the scorecard.
   */
  public Iterator<ArmMaster> getArmIterator()
  {
    final ArrayList<ArmMaster> list = new ArrayList<ArmMaster>();
    synchronized( armMasters )
    {
      for( final Iterator<ArmMaster> it = armMasters.values().iterator(); it.hasNext(); list.add( it.next() ) )
      {
        ;
      }
    }
    return list.iterator();
  }




  /**
   * Start an Application Response Measurement transaction using a particular
   * correlation identifier.
   * 
   * @param tag Grouping tag.
   * @param crid correlation identifier
   * 
   * @return A transaction to collect ARM data.
   */
  public ArmTransaction startArm( final String tag, final String crid )
  {
    ArmTransaction retval = null;
    if( armEnabled )
    {
      synchronized( armMasters )
      {
        // get an existing ARM master or create a new one
        ArmMaster master = armMasters.get( tag );
        if( master == null )
        {
          master = new ArmMaster( tag );
          armMasters.put( tag, master );
        }

        // have the master ARM return a transaction instance
        retval = master.createArm( tag, crid );

        //start the ARM transaction
        retval.start();
      }
    }
    else
    {
      // just return the do-nothing timer
      retval = NULL_ARM;
    }

    return retval;
  }




  /**
   * Start an Application Response Measurement transaction.
   * 
   * @param tag Grouping tag.
   * 
   * @return A transaction to collect ARM data.
   */
  public ArmTransaction startArm( final String tag )
  {
    return startArm( tag, null );
  }




  /**
   * Return the state with the given name.
   * 
   * <p>If the state does not exist, one will be created and added to the 
   * static list of states for later retrieval.</p>
   * 
   * @param name The name of the state to return.
   * 
   * @return The state with the given name.
   */
  public State getState( final String name )
  {
    State state = null;
    synchronized( states )
    {
      state = states.get( name );
      if( state == null )
      {
        state = new State( name );
        states.put( name, state );
      }
    }
    return state;
  }




  /**
   * @return The number of states in the scorecard at the present time.
   */
  public int getStateCount()
  {
    return states.size();
  }




  /**
   * Access an iterator over the states.
   * 
   * <p>NOTE: this iterator is detached from the states in that the remove() 
   * call on the iterator will only affect the returned iterator and not the 
   * state collection in the scorecard. If you wish to remove a state, you MUST 
   * call removeState(Counter) with the reference returned from this iterator 
   * as well.</p>
   * 
   * @return a detached iterator over the states.
   */
  public Iterator<State> getStateIterator()
  {
    final ArrayList<State> list = new ArrayList<State>();
    for( final Iterator<State> it = states.values().iterator(); it.hasNext(); list.add( it.next() ) )
    {
      ;
    }
    return list.iterator();
  }




  /**
   * Remove the state with the given name.
   * 
   * @param name Name of the state to remove.
   * 
   * @return The removed state.
   */
  public State removeState( final String name )
  {
    if( name == null )
    {
      return null;
    }

    synchronized( states )
    {
      return states.remove( name );
    }
  }




  /**
   * Set the named state to the given value.
   * 
   * @param name The name of the state to set.
   * 
   * @param value The value to set in the state.
   */
  public void setState( final String name, final double value )
  {
    getState( name ).set( value );
  }




  /**
   * Set the named state to the given value.
   * 
   * @param name The name of the state to set.
   * 
   * @param value The value to set in the state.
   */
  public void setState( final String name, final long value )
  {
    getState( name ).set( value );
  }




  /**
   * Set the named state to the given value.
   * 
   * @param name The name of the state to set.
   * 
   * @param value The value to set in the state.
   */
  public void setState( final String name, final String value )
  {
    if( ( name == null ) || ( name.length() == 0 ) )
    {
      return;
    }

    if( value == null )
    {
      removeState( name );
    }
    else
    {
      getState( name ).set( value );
    }
  }




  /**
   * Activate all gauges calls from this point on.
   */
  public void enableGauges( final boolean flag )
  {
    synchronized( gauges )
    {
      gaugesEnabled = flag;
    }
  }




  /**
   * Return the reference to the named gauge.
   * 
   * <p>This will always return an object; it may be a stub, or a working 
   * implementation depending upon the state of the scorecard at the time. If 
   * gauges are enabled, then a working gauge is returned, otherwise a null 
   * gauge is returned.</p>
   * 
   * <p>Because the state of gauge operation can change over the operation of 
   * the scorecard, it is not advisable to hold on to the reference between calls 
   * to the gauge. Always get the appropriate reference to the gauge
   * 
   * @param name the name of the gauge to return.
   * 
   * @return Either the 
   * 
   * @throws IllegalArgumentException if the name of the gauge is null
   */
  public Gauge getGauge( final String name )
  {
    if( name == null )
    {
      throw new IllegalArgumentException( "Gauge name is null" );
    }

    Gauge retval = null;
    if( gaugesEnabled )
    {
      synchronized( gauges )
      {
        retval = gauges.get( name );
        if( retval == null )
        {
          retval = new GaugeBase( name );
          gauges.put( name, retval );
        }
      }
    }
    else
    {
      // just return the do-nothing gauge
      retval = NULL_GAUGE;
    }

    return retval;
  }




  /**
   * Get an iterator over all the gauges in the scorecard.
   */
  public Iterator<Gauge> getGaugeIterator()
  {
    final ArrayList<Gauge> list = new ArrayList<Gauge>();
    synchronized( gauges )
    {
      for( final Iterator<Gauge> it = gauges.values().iterator(); it.hasNext(); list.add( it.next() ) )
      {
        ;
      }
    }
    return list.iterator();
  }




  /**
   * Remove the gauge with the given name.
   * 
   * @param name Name of the gauge to remove.
   * 
   * @return The removed gauge.
   */
  public Gauge removeGauge( final String name )
  {
    if( name == null )
    {
      return null;
    }

    synchronized( gauges )
    {
      return gauges.remove( name );
    }
  }




  /**
   * Reset and clear-out the named gauge.
   * 
   * @param name The name of the gauge to clear out.
   */
  public void resetGauge( final String name )
  {
    if( ( name != null ) && ( name.length() > 0 ) )
    {
      getGauge( name ).reset();
    }
  }




  /**
   * Update the named gauge with the given value.
   *  
   * @param name The name of the gauge to update.
   * @param value The value with which to update the gauge.
   */
  public void updateGauge( final String name, final long value )
  {
    if( ( name != null ) && ( name.length() > 0 ) )
    {
      getGauge( name ).update( value );
    }
  }




  /**
   * @return The number of gauges in the scorecard at the present time.
   */
  public int getGaugeCount()
  {
    return gauges.size();
  }




  /**
   * Print only the most significant portion of the time.
   *
   * <p>This is the two most significant units of time. Form will be something
   * like "3h 26m" indicating 3 hours 26 minutes and some insignificant number
   * of seconds. Formats are Xd Xh (days-hours), Xh Xm (Hours-minutes), Xm Xs
   * (minutes-seconds) and Xs (seconds).</p>
   *
   * @param seconds number of elapsed seconds NOT milliseconds.
   *
   * @return formatted string
   */
  private static String formatSignificantElapsedTime( final long seconds )
  {
    final long days = seconds / 86400;
    final StringBuffer buffer = new StringBuffer();

    if( days > 0 ) // Display days and hours
    {
      buffer.append( days );
      buffer.append( "d " );
      buffer.append( ( ( seconds / 3600 ) % 24 ) ); // hours
      buffer.append( "h" );

      return buffer.toString();
    }

    final int hours = (int)( ( seconds / 3600 ) % 24 );

    if( hours > 0 ) // Display hours and minutes
    {
      buffer.append( hours );
      buffer.append( "h " );
      buffer.append( ( ( seconds / 60 ) % 60 ) ); // minutes
      buffer.append( "m" );

      return buffer.toString();
    }

    final int minutes = (int)( ( seconds / 60 ) % 60 );

    if( minutes > 0 ) // Display minutes and seconds
    {
      buffer.append( minutes );
      buffer.append( "m " );
      buffer.append( ( seconds % 60 ) ); // seconds
      buffer.append( "s" );

      return buffer.toString();
    }

    final int secs = (int)( seconds % 60 );
    buffer.append( secs ); // seconds
    buffer.append( "s" );

    return buffer.toString();

  }

}
