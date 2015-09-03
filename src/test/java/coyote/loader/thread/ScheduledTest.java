/*
 * $Id:$
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.loader.thread;

/**
 * Class TestJob
 * 
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision:$
 */
public class ScheduledTest extends ScheduledJob {
  private String display = ".";
  private int count = 0;




  /**
   * @param text
   */
  public ScheduledTest( String text ) {
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
    // Always initialize the super class first
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
    super.terminate();
  }




  /**
   *
   */
  public void doWork() {
    System.out.println( display );

    try {
      sleep( 100 );
    } catch ( InterruptedException x ) {}

    shutdown();
  }
}