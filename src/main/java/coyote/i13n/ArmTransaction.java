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

/**
 * The ArmTransaction interface models an Application Response Measurement
 */
public interface ArmTransaction {
  /** 
   * The logging category for ARM transactions. It can be used to start and 
   * stop logging of ARM transactions via <pre>
   * <code>Log.startLogging( ArmTransaction.LOG_CATEGORY );</code></pre>
   */
  public static final String LOG_CATEGORY = "ARM";

  public static final short NEW = 0;
  public static final short RUNNING = 1;
  public static final short COMPLETE = 2;
  public static final short TIMEOUT = 3;
  public static final short UNDESIRABLE = 4;




  public void start();




  public long stop();




  public long stop( short status );




  /**
   * Increment the value with the given name.
   * 
   * <p>This method retrieves the counter with the given name or creates one by 
   * that name if it does not yet exist. The retrieved counter is then 
   * increased by one (1). 
   * 
   * @param name The name of the counter to increment.
   * 
   * @return The final value of the counter after the operation.
   */
  public long increment( String name );




  /**
   * Decrement the value with the given name.
   * 
   * <p>This method retrieves the counter with the given name or creates one by 
   * that name if it does not yet exist. The retrieved counter is then 
   * decreased by one (1). 
   * 
   * @param name The name of the counter to decrement.
   * 
   * @return The final value of the counter after the operation.
   */
  public long decrement( String name );




  /**
   * Increase the value with the given name by the given amount.
   * 
   * <p>This method retrieves the counter with the given name or creates one by 
   * that name if it does not yet exist. The retrieved counter is then 
   * increased by the given amount. 
   * 
   * @param name The name of the counter to increase.
   * 
   * @return The final value of the counter after the operation.
   */
  public long increase( String name, long value );




  /**
   * Decrease the value with the given name by the given amount.
   * 
   * <p>This method retrieves the counter with the given name or creates one by 
   * that name if it does not yet exist. The retrieved counter is then 
   * decreased by the given amount. 
   * 
   * @param name The name of the counter to decrease.
   * 
   * @return The final value of the counter after the operation.
   */
  public long decrease( String name, long value );




  public void update( String name, Object value );




  public void destroy();




  /**
   * @param crid
   */
  public void setCRID( String crid );




  /**
   * @return a correlation identifier
   */
  public String getCRID();




  public long getTotalTime();




  public ArmMaster getMaster();




  public ArmTransaction startArm( String tag );




  public ArmTransaction startArm( String tag, String crid );




  public long getStartTime();




  public long getStopTime();




  public long getWaitTime();




  public long getOverheadTime();




  public short getStatus();




  public String getName();

}
