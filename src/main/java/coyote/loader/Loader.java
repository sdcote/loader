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

import coyote.commons.security.BlowfishCipher;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;
import coyote.loader.thread.Scheduler;
import coyote.loader.thread.ThreadPool;


/**
 * 
 */
public interface Loader extends WatchDog {

  public static final String CFG_URI_PROPERTY = "cfg.uri";
  public static final String CFG_DIR_PROPERTY = "cfg.dir";
  public static final String ENCRYPT = "encrypt";
  public static final String CIPHER_KEY = "CoyoteLoader";
  public static final String CIPHER_NAME = BlowfishCipher.CIPHER_NAME;




  /**
   * Configure this loader with the given Config data.
   * 
   * @param cfg the configuration to apply to this loader.
   * 
   * @throws ConfigurationException if there were problems configuring the loader.
   */
  public void configure( Config cfg ) throws ConfigurationException;




  /**
   * Start the loader running.
   * 
   * <p>This is a blocking call. The thread will remain in this method until 
   * the loader terminates or an exception is thrown. Keep in mind that some 
   * loaders will daemonize and this call will return immediately. In such 
   * cases, the loader will terminate when the JVM terminates.</p>
   */
  public void start();




  /**
   * This allows the component to access the watchdog thread which runs 
   * continually to keep components running and detect when components become 
   * hung.
   * 
   * @return Return the watchdog component.
   */
  public WatchDog getWatchdog();




  /**
   * This allows components to access the scheduler which runs job on a 
   * re-occurring schedule; something like cron but limited to the runtime 
   * instance.
   * 
   * @return the Scheduler for this loader.
   */
  public Scheduler getScheduler();




  /**
   * This allows components to run simple components in a pool of threads.
   * 
   * @return The ThreadPool object used by this loader.
   */
  public ThreadPool getThreadPool();




  /**
   * Called by the shutdown hook when the JVM terminates.
   */
  public void shutdown();




  /**
   * Set the command line arguments read in from the bootstrap loader
   * 
   * @param args command line arguments
   */
  public void setCommandLineArguments( String[] args );

}
