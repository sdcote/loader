/*
 * Copyright (c) 2007 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.loader.component;

import coyote.dataframe.DataFrame;
import coyote.loader.WatchDog;
import coyote.loader.cfg.Config;


public interface ManagedComponent extends Component {

  /** Name used in various class identifying locations */
  public static final String CLASS = "LogicComponent";

  // Tags are the name of configuration elements
  public static final String LOG_TAG = "Log";
  public static final String ENABLED_TAG = "Enabled";
  public static final String DESCRIPTION_TAG = "Description";
  public static final String ACTIVATION_TOKEN_TAG = "ActivationToken";




  /**
   * Configure the component with the given configuration.
   * 
   * @param config The object containing the configuration attributes.
   */
  public void configure( Config config );




  /**
   * Set the watchdog for this component.
   * 
   * @param watchdog the watchdog to set.
   */
  public void setWatchDog( WatchDog watchdog );




  /**
   * Perform work related to communicating with the physical device and any 
   * other house keeping required.
   */
  public void doWork();




  /**
   * Gives the component instance a change to prepare before doing work.
   * 
   * <p>This method is called after <code>configure(Config)</code> and
   * before <code>doWork()</code> is called.</p>
   * 
   * <p>A component will have its <code>initialize()</code> method called to 
   * perform initialization even if the component is not enabled. This is 
   * because it is possible that a component may be disabled and re-enabled 
   * during its operational lifecycle.</p>
   */
  public void initialize();




  /**
   * Inform the component to enter a quiescent state, possibly saving 
   * operational state information as the component will be restarted 
   * presently.
   */
  public void quiesce();




  /**
   * Sets the enabled status of the component.
   * 
   * @param flag True to enable the component for processing, false to disable 
   *        the component.
   */
  public void setEnabled( boolean flag );




  /**
   * Allows and identifier to be set so the component instance can be addressed 
   * in monitoring and management operations.
   * 
   * @param id The identifier to set in the component.
   */
  public void setId( String id );




  /**
   * Allows the framework to set the time the component was started.
   *
   * @param millis Epoch time in milliseconds as is reported by 
   *        System.currentTimeMillis()
   */
  public void setStartTime( long millis );




  /**
   * Signal the component to stop processing;
   * 
   * <p>Shut this component down using the given DataFrame as a set of parameters.</p>
   * 
   * @param params
   */
  public void shutdown( final DataFrame params );

}
