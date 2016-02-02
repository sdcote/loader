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
package coyote.loader.log;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class LogStepper {
  /** The logger this class uses */
  private static final Logger LOG = LoggerFactory.getLogger( LogStepper.class );




  /**
   * @param args
   */
  public static void main( String[] args ) {

    // Add a logger that will send log messages to the console 
    Log.addLogger( "Loader", new ConsoleAppender( Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS ) );

    FileAppender appender = new FileAppender( new File("debug.log"));
    appender.startLogging( Log.DEBUG );
    Log.addLogger( "Debugfile",appender );

    //Log.startLogging( Log.DEBUG );
    //Log.startLogging( Log.TRACE );

    LOG.trace( "trace" );
    LOG.debug( "debug" );
    LOG.info( "info" );
    LOG.warn( "warn" );
    LOG.error( "error" );
    System.out.println( "============================" );
    Log.startLogging( Log.TRACE );

    LOG.trace( "trace" );
    LOG.debug( "debug" );
    LOG.info( "info" );
    LOG.warn( "warn" );
    LOG.error( "error" );

  }

}
