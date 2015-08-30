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

import coyote.commons.GUID;
import coyote.dataframe.DataFrame;
import coyote.loader.cfg.Config;
import coyote.loader.log.Log;
import coyote.loader.log.Logger;
import coyote.loader.log.NullLogger;
import coyote.loader.thread.ThreadJob;


/**
 * The LogicComponentBase class models a base clas of logic components which 
 * are created and managed by the Loader.
 */
public abstract class LogicComponentBase extends ThreadJob implements LogicComponent {

  private static final String CLASS = "Component";
  private static final String UNKNOWN = "Unknown";
  
  protected coyote.loader.cfg.Config configuration = null;
  protected volatile boolean logging = false;
  protected volatile boolean enabled = true;
  protected volatile boolean licensed = false;
  protected long startTime;
  protected String identifier = new GUID().toString();
  protected String componentName = LogicComponentBase.CLASS_TAG;
  protected Logger lcb_logr = new NullLogger();




  /**
   * 
   */
  public LogicComponentBase() {
    super();
  }




  /**
   * Configure the component with the given configuration object.
   * 
   * @param config The object containing the configuration attributes.
   */
  public void configure( final Config config ) {
    configuration = config;

//    if ( configuration != null && configuration.getId() != null && configuration.getId().trim().length() > 0 ) {
//      identifier = configuration.getId().trim();
//      componentName = componentName + "." + identifier;
//    }

    // TODO good place to setup a platform logger
  }




  public String getApplicationId() {
    return UNKNOWN;
  }




  /**
   * @see net.smartforge.oam.Component#getCategory()
   */
  public String getCategory() {
    return UNKNOWN;
  }




  /**
   * @see net.smartforge.LogicComponent#getConfiguration()
   */
  public Config getConfiguration() {
    return configuration;
  }




  /**
   * @see net.smartforge.oam.Component#getDescription()
   */
  public String getDescription() {
    return null;
  }




  /**
   * @see net.smartforge.oam.Component#getId()
   */
  public String getId() {
    return identifier;
  }




  /**
   * @see net.smartforge.oam.Component#getName()
   */
  public String getName() {
    return componentName;
  }




  /**
   * @return The platform logger assigned to this component. Will never return null.
   */
  public Logger getPlatformLogger() {
    if ( lcb_logr == null ) {
      lcb_logr = new NullLogger();
    }

    return lcb_logr;
  }




  /**
   * @see net.smartforge.oam.Component#getProfile()
   */
  public DataFrame getProfile() {
    final DataFrame retval = new DataFrame();
    retval.put(CLASS_TAG, CLASS );
    retval.put( "ID", identifier );

    return retval;
  }




  /**
   * @return the startTime
   */
  public long getStartTime() {
    return startTime;
  }




  public DataFrame getStatus() {
    return getProfile();
  }




  /**
   * @see net.smartforge.oam.Component#getSystemId()
   */
  public String getSystemId() {
    return LogicComponent.CLASS_TAG;
  }




  /**
   * Return a Config that can be used as a template for defining instances
   * of this component.
   *
   * @return a configuration that can be used as a template
   */
  public Config getTemplate() {
    final Config template = new Config();

    try {
      template.setName( LogicComponent.CLASS_TAG );

      // define the slots
      // template.addConfigSlot( new ConfigSlot( LogicComponent.ENABLED_TAG, "Flag indicating the component is enabled to run.", new Boolean( true ) ).toString() );
    } catch ( final Exception ex ) {
      // should always work
    }

    return template;
  }




  /**
   * @see net.smartforge.Driver#isEnabled()
   */
  public boolean isEnabled() {
    return enabled;
  }




  /**
   * @return the licensed
   */
  public boolean isLicensed() {
    return licensed;
  }




  /**
   * Return true if our personal platform logger is logging a category defined 
   * by the given mask.
   * 
   * <p>This is the fastest way to determine if it is worth the time and effort 
   * to construct a message before the append method is called.</p> 
   *
   * @param mask The mask.
   *
   * @return TODO Complete Documentation
   */
  public boolean isLogging( final long mask ) {
    return ( ( lcb_logr.getMask() & mask ) != 0 );
  }




  protected void logCustom( final String category, final Object entry ) {
    lcb_logr.append( category, entry, null );
  }




  protected void logCustom( final String category, final Object entry, final Throwable cause ) {
    lcb_logr.append( category, entry, cause );
  }




  protected void logDebug( final Object entry ) {
    lcb_logr.append( Log.DEBUG, entry, null );
  }




  protected void logDebug( final Object entry, final Throwable cause ) {
    lcb_logr.append( Log.DEBUG, entry, cause );
  }




  protected void logError( final Object entry ) {
    lcb_logr.append( Log.ERROR, entry, null );
  }




  protected void logError( final Object entry, final Throwable cause ) {
    lcb_logr.append( Log.ERROR, entry, cause );
  }




  protected void logFatal( final Object entry ) {
    lcb_logr.append( Log.FATAL, entry, null );
  }




  protected void logFatal( final Object entry, final Throwable cause ) {
    lcb_logr.append( Log.FATAL, entry, cause );
  }




  protected void logInfo( final Object entry ) {
    lcb_logr.append( Log.INFO, entry, null );
  }




  protected void logInfo( final Object entry, final Throwable cause ) {
    lcb_logr.append( Log.INFO, entry, cause );
  }




  protected void logTrace( final Object entry ) {
    lcb_logr.append( Log.TRACE, entry, null );
  }




  protected void logTrace( final Object entry, final Throwable cause ) {
    lcb_logr.append( Log.TRACE, entry, cause );
  }




  protected void logWarn( final Object entry ) {
    lcb_logr.append( Log.WARN, entry, null );
  }




  protected void logWarn( final Object entry, final Throwable cause ) {
    lcb_logr.append( Log.WARN, entry, cause );
  }




  /**
   * @see net.smartforge.Driver#setEnabled(boolean)
   */
  public void setEnabled( final boolean flag ) {
    enabled = flag;
  }




  /**
   * @see net.smartforge.Driver#setId(java.lang.String)
   */
  public void setId( final String id ) {
    if ( ( id != null ) && ( id.length() > 0 ) ) {
      identifier = id;
    }
  }




  /**
   * @param lgr the platform logger to set in this component.
   */
  public void setPlatformLogger( final Logger lgr ) {
    if ( lgr == null ) {
      lcb_logr = new NullLogger();
    } else {
      this.lcb_logr = lgr;
    }
  }




  /**
   * @see net.smartforge.LogicComponent#setStartTime(long)
   */
  public void setStartTime( final long millis ) {
    startTime = millis;
  }

}
