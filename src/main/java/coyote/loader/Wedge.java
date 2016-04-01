/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
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

import coyote.loader.cfg.Config;
import coyote.loader.component.AbstractManagedComponent;
import coyote.loader.component.ManagedComponent;
import coyote.loader.log.Log;


/**
 * This is a do-nothing component which keeps the watchdog busy as it will 
 * terminate once all the components are done processing. This "never dies". 
 */
public class Wedge extends AbstractManagedComponent implements ManagedComponent {

  public Wedge() {
    Log.debug( this.getClass().getSimpleName() + " constructed" );
  }




  /**
   * @see coyote.loader.component.AbstractManagedComponent#setConfiguration(coyote.loader.cfg.Config)
   */
  @Override
  public void setConfiguration( Config config ) {
    super.setConfiguration( config );
    Log.debug( config.toFormattedString() );
  }




  /**
   * Called just before we enter the main run loop.
   * 
   * @see coyote.loader.thread.ThreadJob#initialize()
   */
  @Override
  public void initialize() {
    // pause 5 seconds between calls to doWork()
    setIdleWait( 5000 );

    // Start out idling
    setIdle( true );
  }




  /**
   * This is the main, reentrant method that is called while in the 
   * main run loop.
   * 
   * @see coyote.loader.thread.ThreadJob#doWork()
   */
  @Override
  public void doWork() {
    // do nothing
  }




  /**
   * Called after the main run loop is exited, performs any resource clean up.
   * 
   * @see coyote.loader.thread.ThreadJob#terminate()
   */
  @Override
  public void terminate() {
    // nothing to do
  }

}
