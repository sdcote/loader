/*
 * $Id: LogicComponent.java,v 1.8 2007/03/21 21:21:56 scote Exp $
 *
 * Copyright Stephan D. Cote' 2008 - All rights reserved.
 */
package coyote.loader.component;

import coyote.dataframe.DataFrame;
import coyote.loader.cfg.Config;


public interface LogicComponent extends Component
{
  /** Tag used in various class identifying locations */
  public static final String CLASS_TAG = "LogicComponent";
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
   * Get the reference to the actual configuration object.
   * 
   * <p>This is useful when a managing component wants to access configuration
   * attributes within the component or wants to make a comparison with a newly
   * received configuration and the current configuration to determine if a 
   * restart is necessary.</p> 
   * 
   * @return The configuration object currently set in the component.
   */
  public Config getConfiguration();




  /**
   * Return a Config that can be used as a template for defining instances
   * of this component.
   *
   * @return a object that can be used as a configuration template.
   */
  public Config getTemplate();




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
   * Perform work related to communicating with the physical device and any 
   * other house keeping required.
   */
  public void doWork();




  /**
   * Inform the component to enter a quiescent state, possibly saving 
   * operational state information as the component will be restarted 
   * presently.
   */
  public void quiesce();




  /**
   * Signal the component to stop processing;
   */
  public void shutdown();




  /**
   * Indicate if the component is currently enabled.
   * 
   * @return True if the component is eligible for processing, False if 
   *         disabled.
   */
  public boolean isEnabled();




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
   * @return The identifier of this component used in monitoring and management.
   */
  public String getId();




  /**
   * @return the name of the component.
   */
  public String getName();




  /**
   * @return The operational state of this component instance as an abstract 
   *         data type.
   */
  public DataFrame getStatus();




  /**
   * Determines if the component requires a license to operate.
   * 
   * @return True if the component requires a license to operate, false if the
   *         component is unrestricted.
   */
  public boolean isLicensed();




  /**
   * Access to when the component was started.
   * 
   * @return The time when the component was started, 0 if the component is not
   *         yet started.
   */
  public long getStartTime();




  /**
   * Allows the framework to set the time the component was started.
   *
   * @param millis Epoch time in milliseconds as is reported by 
   *        System.currentTimeMillis()
   */
  public void setStartTime( long millis );

}
