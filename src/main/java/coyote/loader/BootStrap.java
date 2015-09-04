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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Date;

import coyote.commons.ExceptionUtil;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.dataframe.DataField;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;
import coyote.loader.log.Log;
import coyote.loader.log.LogMsg;
import coyote.loader.log.LogMsg.BundleBaseName;


/**
 * This is a boot strap loader which uses the configuration file to determine 
 * which loader to use.
 * 
 * <p>Since there are several different loaders from which to choose, this 
 * loader reads in a configuration, like other loaders, then uses the 
 * configuration to determine which loader to use.</p>
 */
public class BootStrap extends AbstractLoader {

  private static Config configuration = null;
  private static URI cfgUri = null;

  private static final String CFG_PROPERTY = "cfg.uri";

  static {
    LogMsg.setBundleBaseNameDefault( new BundleBaseName( "LoaderMsg" ) );
  }




  /**
   * Use the first command line argument as the URI to the configuration file
   * 
   * @param args The command line arguments passed to the main method
   */
  private static void parseArgs( String[] args ) {
    String cfgLoc = null;

    // Get the URI to our configuration from either the command line or the system properties
    if ( args != null && args.length > 0 ) {
      cfgLoc = args[0];
    } else {
      cfgLoc = System.getProperties().getProperty( CFG_PROPERTY );
    }

    // Parse the argument value into a URI
    if ( StringUtil.isNotBlank( cfgLoc ) ) {
      cfgUri = UriUtil.parse( cfgLoc );
    }

  }




  /**
   * Generate a configuration from the file read-in.
   */
  private static void readConfig() {
    try {
      configuration = Config.read( cfgUri );
    } catch ( IOException | ConfigurationException e ) {
      System.err.println( LogMsg.createMsg( "Loader.error_reading_configuration", cfgUri, e.getLocalizedMessage(), ExceptionUtil.stackTrace( e ) ) );
      System.exit( 7 );
    }
  }




  /**
   * Determine the loader to use from the given configuration and create an 
   * instance of it.
   * 
   * <p>Once created, the loader will be passed the configuration resulting in 
   * a configured loader</p>
   *  
   * @return a configured loader or null if there was not "CLASS" attribute in 
   *         the root of the configuration indicating was not found.
   */
  private static Loader buildLoader() {
    //System.out.println(JSONMarshaler.toFormattedString( configuration ));
    Loader retval = null;

    // Look for the class to load
    for ( DataField field : configuration.getFields() ) {
      if ( ConfigTag.CLASS.equalsIgnoreCase( field.getName() ) ) {
        String className = field.getStringValue();
        if ( className != null && StringUtil.countOccurrencesOf( className, "." ) < 1 ) {
          className = BootStrap.class.getPackage().getName() + "." + className;
        }

        try {
          Class<?> clazz = Class.forName( className );
          Constructor<?> ctor = clazz.getConstructor();
          Object object = ctor.newInstance();

          if ( object instanceof Loader ) {
            retval = (Loader)object;
            try {
              retval.configure( configuration );
            } catch ( ConfigurationException e ) {
              System.err.println( LogMsg.createMsg( "Loader.could_not_config_loader", object.getClass().getName(), e.getClass().getSimpleName(), e.getMessage() ) );
              System.exit( 6 );
            }
          } else {
            System.err.println( LogMsg.createMsg( "Loader.class_is_not_loader", className ) );
            System.exit( 5 );
          }
        } catch ( ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
          System.err.println( LogMsg.createMsg( "Loader.instantiation_error", className, e.getClass().getName(), e.getMessage() ) );
          System.exit( 4 );
        }
      }
    }

    return retval;
  }




  /**
   * Add a shutdown hook into the JVM to help us shut everything down nicely.
   * 
   * @param loader The loader to terminate
   */
  private static void registerShutdownHook( final Loader loader ) {
    try {
      Runtime.getRuntime().addShutdownHook( new Thread( "LoaderHook" ) {
        public void run() {
          System.out.println( LogMsg.createMsg( "Loader.runtime_terminating", new Date() ) );
          Log.info( LogMsg.createMsg( "Loader.runtime_terminating", new Date() ) );

          if ( loader != null ) {
            loader.shutdown();
          }

          Log.info( LogMsg.createMsg( "Loader.runtime_terminated", new Date() ) );
          System.out.println( LogMsg.createMsg( "Loader.runtime_terminated", new Date() ) );
          System.out.flush();
        }
      } );
    } catch ( java.lang.NoSuchMethodError nsme ) {
      // Ignore
    } catch ( Throwable e ) {
      // Ignore
    }
  }




  /**
   * Use the first argument in the command line (or the 'cfg.uri' system 
   * property) to specify a URI of a configuration file to load.
   * 
   * @param args command line arguments.
   */
  public static void main( String[] args ) {

    // Parse the command line arguments
    parseArgs( args );

    if ( cfgUri != null ) {

      // Read in the configuration
      readConfig();

      // Create a loader from the configuration
      Loader loader = buildLoader();

      // If we have a loader
      if ( loader != null ) {

        // Register a shutdown method to terminate cleanly when the JVM exit
        registerShutdownHook( loader );

        // Start the loader running in the current thread
        try {
          loader.start();
        } catch ( Throwable t ) {
          System.err.println( LogMsg.createMsg( "Loader.logic_error_from_loader", t.getLocalizedMessage(), ExceptionUtil.stackTrace( t ) ) );
          System.exit( 3 );
        }
      } else {
        System.err.println( LogMsg.createMsg( "Loader.no_loader_configured" ) );
        System.exit( 2 );
      }
    } else {
      System.err.println( LogMsg.createMsg( "Loader.no_config_uri_defined" ) );
      System.exit( 1 );
    }

    // Normal termination
    System.exit( 0 );

  }

}
