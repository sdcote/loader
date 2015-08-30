/*
 * $Id: Daemon.java,v 1.6 2007/03/21 23:32:43 scote Exp $
 *
 * Copyright Stephan D. Cote' 2008 - All rights reserved.
 */
package coyote.loader.thread;

import coyote.loader.cfg.Config;

/**
 * The Daemon class models...
 * 
 * @author Stephan D. Cote'
 * @version $Revision: 1.6 $
 */
public interface Daemon {
  /** Tag used in various class identifying locations */
  public static final String CLASS_TAG = "Daemon";
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
