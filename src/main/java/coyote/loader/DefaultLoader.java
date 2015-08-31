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

import java.util.Iterator;

import coyote.loader.cfg.Config;
import coyote.loader.log.Log;
import coyote.loader.log.LogMsg;
import coyote.loader.thread.Daemon;


/**
 * 
 */
public class DefaultLoader extends AbstractLoader implements Loader {

  /** Tag used in various class identifying locations. */
  public static final String CLASS = "Loader";

  public static final String DEFAULT_DESCRIPTION = "Default component loader";




  public DefaultLoader() {

  }




  /**
   * Start the components running.
   * 
   * @see java.lang.Runnable#run()
   */
  public void run() {
    // only run once, this is not foolproof as the active flag is set only when 
    // the watchdog loop is entered
    if ( isActive() ) {
      return;
    }

    // Save the name of the thread that is running this class
    final String oldName = Thread.currentThread().getName();

    // Rename this thread to the name of this class
    Thread.currentThread().setName( CLASS );

    // Parse through the configuration and start setting up the component(s)
    initComponents();

    Log.info( "Components initialized" );

    // By this time all loggers (including the catch-all logger) should be open
    final StringBuffer b = new StringBuffer( CLASS );
    //      b.append( PowerSG.VERSION.toString() );
    b.append( " initialized - Runtime: " );
    b.append( System.getProperty( "java.version" ) );
    b.append( " (" );
    b.append( System.getProperty( "java.vendor" ) );
    b.append( ")" );
    b.append( " - Platform: " );
    b.append( System.getProperty( "os.arch" ) );
    b.append( " OS: " );
    b.append( System.getProperty( "os.name" ) );
    b.append( " (" );
    b.append( System.getProperty( "os.version" ) );
    b.append( ")" );
    Log.info( b );

    // enter a loop performing watchdog and maintenance functions
    watchdog();

    // The watchdog loop has exited, so we are done processing

    // Rename the thread back to what it was called before we were being run
    Thread.currentThread().setName( oldName );

  }




  /**
   * 
   */
  private void initComponents() {
    // TODO Auto-generated method stub

  }




  /**
   * The main execution loop.
   * 
   * <p>This is where the thread spends its time monitoring components it has 
   * loaded and performing housekeeping operations.</p>
   * 
   * <p>While it is called a watchdog, this does not detect when a component is 
   * hung nor restart it. The exact API for components to "pet the dog" is 
   * still in the works.</p>
   */
  private void watchdog() {
    setActiveFlag( true );

    Log.info( LogMsg.createMsg( "Loader.operational" ) );

    while ( !isShutdown() ) {

      // Make sure that all this loaders are active, otherwise remove the
      // reference to them and allow GC to remove them from memory
      for ( final Iterator it = components.keySet().iterator(); it.hasNext(); ) {
        final Object cmpnt = it.next();
        if ( cmpnt instanceof Daemon ) {
          if ( !( (Daemon)cmpnt ).isActive() ) {
            Log.info( LogMsg.createMsg( "Loader.removing_inactive_cmpnt", new Object[] { cmpnt.toString() } ) );

            // get a reference to the components configuration
            final Config config = (Config)components.get( cmpnt );

            // try to shut it down properly
            safeShutdown( (Daemon)cmpnt );

            // remove the component
            it.remove();

            // re-load the component
            loadComponent( config );
          }
        }
      }

      // If we have no components which are active, there is not need for this
      // loader to remain running
      if ( components.size() == 0 ) {
        Log.warn( LogMsg.createMsg( "Loader.no_components" ) );
        this.shutdown();
      }

      // Yield to other threads and sleep(wait) for a time
      park( parkTime );

    }

    if ( Log.isLogging( Log.DEBUG_EVENTS ) ) {
      Log.debug( LogMsg.createMsg( "Loader.terminating" ) );
    }

    terminate();

    setActiveFlag( false );
  }




  /**
   * @param config
   */
  private void loadComponent( Config config ) {
    // TODO Auto-generated method stub

  }

}
