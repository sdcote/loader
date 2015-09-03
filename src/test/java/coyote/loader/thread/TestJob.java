/*
 * $Id:$
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.loader.thread;

/**
 * Class TestJob
 */
public class TestJob extends ThreadJob {
  private String display = ".";
  private int count = 0;




  /**
   * @param text
   */
  public TestJob( String text ) {
    // Always a good practice
    super();

    if ( text != null ) {
      display = text;
    }
  }




  /**
   * Method initialize
   */
  public void initialize() {
    // Always!
    super.initialize();

    if ( display.startsWith( "SlowStarter" ) ) {
      try {
        System.out.println( "Slow Starting " + current_thread.getName() );
        Thread.sleep( 2500 );
      } catch ( Exception ex ) {}
    }
  }




  /**
   *
   */
  public void terminate() {
    super.terminate(); // habit
    System.out.println( display + " Ran for " + ( System.currentTimeMillis() - started_time ) + " milliseconds" );
  }




  /**
   *
   */
  public void doWork() {
    // System.out.print(display);
    try {
      sleep( 100 );
    } catch ( InterruptedException x ) {}

    if ( count++ > 50 ) {
      shutdown();
    }
  }
}