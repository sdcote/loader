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
package coyote.loader;

import java.util.HashMap;

import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;
import coyote.loader.thread.Daemon;
import coyote.loader.thread.ThreadJob;


/**
 * 
 */
public abstract class AbstractLoader extends ThreadJob implements Loader, Runnable {

  /** A map of all the component configurations keyed by their instance */
  protected final HashMap components = new HashMap();

  /** The time to pause (sleep) between idle loop cycles (dflt=3000ms) */
  protected long parkTime = 3000;

  /** Our configuration */
  protected Config configuration = new Config();




  /**
   * @see coyote.loader.Loader#configure(coyote.loader.cfg.Config)
   */
  @Override
  public void configure( Config cfg ) throws ConfigurationException {
    configuration = cfg;
  }




  /**
   * @return the parkTime
   */
  public long getParkTime() {
    return parkTime;
  }




  /**
   * @param parkTime the parkTime to set
   */
  public void setParkTime( long parkTime ) {
    this.parkTime = parkTime;
  }




  /**
   * Try to shut the component down in a separate thread.
   * 
   * <p>This is a way to ensure that the calling thread does not get hung in a
   * deadlocked component while trying to shutdown a component.</p>
   * @param cmpnt
   */
  protected Thread safeShutdown( final Daemon cmpnt ) {
    final Thread closer = new Thread( new Runnable() {
      public void run() {
        cmpnt.shutdown();
      }
    } );

    closer.start();

    // give the component a chance to wake up and terminate
    Thread.yield();

    return closer;
  }

}
