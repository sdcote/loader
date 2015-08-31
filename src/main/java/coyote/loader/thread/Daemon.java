/*
 * Copyright (c) 2006 Stephan D. Cote' - All rights reserved.
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

import coyote.loader.cfg.Config;


/**
 * The Daemon class models a component which runs in the background.
 */
public interface Daemon {
  /** Token used in various class identifying locations */
  public static final String CLASS = "Daemon";
  public static final String INTERVAL_TAG = "CycleInterval";




  /**
   * Configure the Daemon
   * 
   * @param cap The configuration attributes representing the configuration
   */
  public void configure( Config cap );




  /**
   * Creates a thread, runs this job in that thread and exits leaving that
   * thread (and the JVM) running in memory.
   *
   * @return the thread in which this daemon is running.
   */
  public Thread daemonize();




  /**
   * Get the configuration of this daemon as a data capsule
   */
  public Config getConfiguration();




  /**
   * Return a Config that can be used as a template for defining instances
   * of this daemon.
   *
   * @return a capsule that can be used as a configuration template
   */
  public Config getTemplate();




  /**
   * Determine if the Daemon is active.
   * 
   * @return True if the daemon is active and ready for processing, False if it 
   *         has failed or is otherwise inoperable.
   */
  public boolean isActive();




  /**
   * Inform the daemon to enter a quiescent state, possibly saving operational 
   * state information as the daemon will be restarted presently.
   */
  public void quiesce();




  /**
   * Request this object to shutdown.
   */
  public void shutdown();




  /**
   * Wait for the Daemon to go active.
   *
   * @param timeout The number of milliseconds to wait for the main run loop to
   *          be entered.
   */
  public void waitForActive( long timeout );

}
