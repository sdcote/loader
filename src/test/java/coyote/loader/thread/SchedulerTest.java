/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.loader.thread;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class SchedulerTest {

  private static Scheduler scheduler = null;




  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    scheduler = new Scheduler();
  }




  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    scheduler.shutdown();
  }




  //@Test
  public void testOne() {
    long startTime = System.currentTimeMillis() + 3000;
    // long startTime = S;

    ScheduledJob task0 = new ScheduledTest( "Hello0" );
    task0.setExecutionTime( startTime );

    ScheduledJob task1 = new ScheduledTest( "Hello1" );
    task1.setExecutionTime( startTime + 1000 );

    ScheduledJob task2 = new ScheduledTest( "Hello2" );
    task2.setExecutionTime( startTime + 2000 );

    ScheduledJob task3 = new ScheduledTest( "Hello3" );
    task3.setExecutionTime( startTime + 3000 );

    ScheduledJob task4 = new ScheduledTest( "Hello4" );
    task4.setExecutionTime( startTime + 4000 );

    // Place them in a different order than should be executed
    scheduler.schedule( task3 );
    scheduler.schedule( task4 );
    scheduler.schedule( task1 );
    scheduler.schedule( task2 );
    scheduler.schedule( task0 );

    scheduler.daemonize();

    try {
      Thread.sleep( 2500 );
      assertTrue( scheduler.getJobCount() == 5 );
    } catch ( Exception ex ) {}

    try {
      Thread.sleep( 1000 );
      assertTrue( scheduler.getJobCount() == 4 );
    } catch ( Exception ex ) {}

    try {
      Thread.sleep( 5000 );
      assertTrue( scheduler.getJobCount() == 0 );
    } catch ( Exception ex ) {}

  }




  //@Test
  public void testRepeat1() {
    // long startTime = System.currentTimeMillis() + 3000;
    long startTime = System.currentTimeMillis();

    ScheduledJob task0 = new ScheduledTest( "Repeater" );
    task0.setExecutionTime( startTime );
    task0.setRepeatable( true );
    task0.setExecutionInterval( 1000 );
    task0.setExecutionLimit( 3 );

    scheduler.schedule( task0 );
    System.out.println( "Scheduler has " + scheduler.getJobCount() + " jobs scheduled" );

    scheduler.daemonize();

    try {
      Thread.sleep( 3500 );
    } catch ( Exception ex ) {}

    System.out.println( "Scheduler has " + scheduler.getJobCount() + " jobs scheduled" );

    assertTrue( task0.getExecutionCount() == 3 );
    assertTrue( scheduler.getJobCount() == 0 );
  }

}
