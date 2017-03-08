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

import java.util.Iterator;


/**
 * A scorecard hold all the collected metrics.
 */
public interface Scorecard
{

  /**
   * Return the identifier the card is using to differentiate itself from other 
   * cards on this host.
   * 
   * @return The identifier for this scorecard.
   */
  public String getId();




  /**
   * Assign a unique identifier to this scorecard.
   * 
   * @param id the unique identifier to set
   */
  public void setId( String id );




  /**
   * The Scorecard records the time it was instantiated and serves as a timer,
   * of sorts, for the system as a whole.
   * 
   * <p>Assuming the scorecard is created when the system loads, it can be used 
   * to determine how long the system has been operational.</p>
   * 
   * @return The epoch time in milliseconds this scorecard was started.
   */
  public long getStartedTime();




  /**
   * Return how long the scorecard has been active in a format using only the 
   * significant time measurements.
   * 
   * <p>Significant measurements means if the number of seconds extend past 24 
   * hours, then only report the days and hours skipping the minutes and 
   * seconds. Examples include <tt>4m 23s</tt> or <tt>22d 4h</tt>. The format 
   * is designed to make reporting scorecard uptime more polished.</p>
   * 
   * @return the time the scorecard has been active in a print-ready format.
   */
  public String getUptimeString();




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
  public Timer startTimer( String tag );




  /**
   * Enable the timer with the given name.
   * 
   * <p>If a timer is enabled that has not already been created, a new 
   * timer will be created in memory.</p>
   * 
   * @param tag The name of the timer to enable.
   */
  public void enableTimer( String tag );




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
  public void disableTimer( String tag );




  /**
   * Enable fully-functional timers from this point forward.
   * 
   * <p>When timing is enabled, functional timers are returned and their 
   * metrics are collected for later reporting. when timing is disabled, null 
   * timers are be returned each time a timer is requested. This keeps all code 
   * operational regardless of the runtime status of timing.</p>
   */
  public void enableTiming( boolean flag );




  /**
   * Removes all timers from the scorecard and frees them up for garbage 
   * collection.
   */
  public void resetTimers();




  /**
   * Get an iterator over all the Master Timers in the scorecard.
   */
  public Iterator<TimingMaster> getTimerIterator();




  /**
   * Get the master timer with the given name.
   *  
   * @param name The name of the master timer to retrieve.
   * 
   * @return The master timer with the given name or null if that timer 
   *         does not exist.
   */
  public TimingMaster getTimerMaster( final String tag );




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
  public Counter getCounter( String name );




  /**
   * @return The number of counters in the scorecard at the present time.
   */
  public int getCounterCount();




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
  public Iterator<Counter> getCounterIterator();




  /**
   * Remove the counter with the given name.
   * 
   * @param name Name of the counter to remove.
   * 
   * @return The removed counter.
   */
  public Counter removeCounter( String name );




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
  public Counter resetCounter( String name );




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
  public long decrease( String tag, long value );




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
  public long decrement( String tag );




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
  public long increase( String tag, long value );




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
  public long increment( String tag );




  /**
   * Deactivate a particular class of Application Response Measurement calls 
   * from this point on.
   */
  public void disableArmClass( final String name );




  /**
   * Activate all Application Response Measurement calls from this point on.
   */
  public void enableArm( boolean flag );




  /**
   * Activate a particular class of Application Response Measurement calls from 
   * this point on.
   */
  public void enableArmClass( String name );




  /**
   * Get an iterator over all the ARM Masters in the scorecard.
   */
  public Iterator<ArmMaster> getArmIterator();




  /**
   * Start an Application Response Measurement transaction using a particular
   * correlation identifier.
   * 
   * @param tag Grouping tag.
   * @param crid correlation identifier
   * 
   * @return A transaction to collect ARM data.
   */
  public ArmTransaction startArm( String tag, String crid );




  /**
   * Start an Application Response Measurement transaction.
   * 
   * @param tag Grouping tag.
   * 
   * @return A transaction to collect ARM data.
   */
  public ArmTransaction startArm( String tag );




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
  public State getState( String name );




  /**
   * @return The number of states in the scorecard at the present time.
   */
  public int getStateCount();




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
  public Iterator<State> getStateIterator();




  /**
   * Remove the state with the given name.
   * 
   * @param name Name of the state to remove.
   * 
   * @return The removed state.
   */
  public State removeState( String name );




  /**
   * Set the named state to the given value.
   * 
   * Use this to set double and float values (with decimal places)
   * 
   * @param name The name of the state to set.
   * 
   * @param value The value to set in the state.
   */
  public void setState( String name, double value );




  /**
   * Set the named state to the given value.
   * 
   * Use this to set values without decimal places.
   * 
   * @param name The name of the state to set.
   * 
   * @param value The value to set in the state.
   */
  public void setState( String name, long value );




  /**
   * Set the named state to the given value.
   * 
   * @param name The name of the state to set.
   * 
   * @param value The value to set in the state.
   */
  public void setState( String name, String value );




  /**
   * Activate all gauges calls from this point on.
   */
  public void enableGauges( boolean flag );




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
  public Gauge getGauge( String name );




  /**
   * Get an iterator over all the gauges in the scorecard.
   */
  public Iterator<Gauge> getGaugeIterator();




  /**
   * Remove the gauge with the given name.
   * 
   * @param name Name of the gauge to remove.
   * 
   * @return The removed gauge.
   */
  public Gauge removeGauge( String name );




  /**
   * Reset and clear-out the named gauge.
   * 
   * @param name The name of the gauge to clear out.
   */
  public void resetGauge( String name );




  /**
   * Update the named gauge with the given value.
   *  
   * @param name The name of the gauge to update.
   * @param value The value with which to update the gauge.
   */
  public void updateGauge( String name, long value );




  /**
   * @return The number of gauges in the scorecard at the present time.
   */
  public int getGaugeCount();

}
