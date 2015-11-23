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
package coyote.loader.log;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;


/**
 * Log defines static methods for maintaining a collection of Loggers and
 * sending an event to all of them.
 * 
 * <p>This is an implementation of a category based logger, where all messages 
 * of a category are logged. There is no concepts of levels in this logger.  
 * This means if you turn on the logging of one category, all the other 
 * categories remain unaffected. This allows your to turn on and off all 
 * "security" messages for example while the "protocol" messages remain 
 * unaffected.</p>
 *
 * <p>The collection is initialized to a single default logger that logs INFO,
 * WARN, ERROR and FATAL events to System.out through the ConsoleAppender
 * logging class.</p>
 */
public final class Log {

  /** Map of all the known logging category codes keyed by their name. */
  static final Hashtable<String, Long> stringToCode = new Hashtable<String, Long>();

  /** Map of all the category names keyed by their category code. */
  static final Hashtable<Long, String> codeToString = new Hashtable<Long, String>();

  /** Map of all the loggers in the fixture keyed by their name. */
  static final Hashtable<String, Logger> nameToLogger = new Hashtable<String, Logger>();

  /**
   * The name of the category of events where an expected event has occurred 
   * and additional (verbose) information will be displayed including the 
   * location in the code where the event occurred.
   */
  public static final String TRACE = "TRACE";

  /**
   * The name of the category of events where an expected event has occurred 
   * and additional (verbose) information will be displayed.
   */
  public static final String DEBUG = "DEBUG";

  /** 
   * The name of the category of events where an expected event has occurred. 
   */
  public static final String INFO = "INFO";

  /**
   * The name of the category of events where an unexpected event has occurred 
   * but execution can continue. The code can compensate for the event and the 
   * occurrence may even be acceptable.
   */
  public static final String WARN = "WARN";

  /** 
   * The name of the category of events where an unexpected event has occurred 
   * but execution can continue while operations may not produce the expected 
   * results.
   */
  public static final String ERROR = "ERROR";

  /** 
   * The name of the category of events where an unexpected event has occurred 
   * and all or part of the thread of execution can not continue.
   */
  public static final String FATAL = "FATAL";

  /** The category mask for the TRACE category. */
  public static final long TRACE_EVENTS = Log.getCode( Log.TRACE );
  /** The category mask for the DEBUG category. */
  public static final long DEBUG_EVENTS = Log.getCode( Log.DEBUG );
  /** The category mask for the INFO category. */
  public static final long INFO_EVENTS = Log.getCode( Log.INFO );
  /** The category mask for the WARN category. */
  public static final long WARN_EVENTS = Log.getCode( Log.WARN );
  /** The category mask for the ERROR category. */
  public static final long ERROR_EVENTS = Log.getCode( Log.ERROR );
  /** The category mask for the FATAL category. */
  public static final long FATAL_EVENTS = Log.getCode( Log.FATAL );

  static long masks; // union of masks of all loggers
  static final long started = System.currentTimeMillis();

  /** 
   * The name of the default logger, or the name of the logger created and 
   * enabled by the logging subsystem when first accessed and initialized. 
   */
  public static final String DEFAULT_LOGGER_NAME = "default";

  /**
   * If a loggers name appears in this list, it will NOT be removed from the
   * logger collection.
   */
  private static final HashSet<String> permanentLoggers = new HashSet<String>();

  static {
    try {
      Runtime.getRuntime().addShutdownHook( new Thread( "LogShutdown" ) {
        public void run() {
          for ( final Enumeration<Logger> en = Log.nameToLogger.elements(); en.hasMoreElements(); ) {
            en.nextElement().terminate();
          }
        }
      } );
    } catch ( final Throwable ignore ) {}
    // are the only logging framework
    Log.addLogger( Log.DEFAULT_LOGGER_NAME, new NullAppender( Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS ) );
  } // static initializer




  /**
   * Checks if a string is not null, empty ("") and not only whitespace.
   * 
   * @param str the String to check, may be null
   * 
   * @return <code>true</code> if the String is not empty and not null and not
   *         whitespace
   */
  public static boolean isBlank( String str ) {
    int strLen;
    if ( str == null || ( strLen = str.length() ) == 0 ) {
      return true;
    }
    for ( int i = 0; i < strLen; i++ ) {
      if ( ( Character.isWhitespace( str.charAt( i ) ) == false ) ) {
        return false;
      }
    }
    return true;
  }




  /**
   * Add the specified logger to the static collection of loggers.
   *
   * @param name The name of the logger.
   * @param logger The logger to add.
   */
  public synchronized static void addLogger( final String name, final Logger logger ) {
    if ( isBlank( name ) )
      throw new IllegalArgumentException( "Logger name cannot be blank" );

    if ( logger != null ) {
      Log.nameToLogger.put( name, logger );
      logger.initialize();
      Log.recalcMasks();
    }
  }




  /**
   * Log the object using the info category
   *
   * @param category The category of the desired log operation 
   * @param event The event to log.
   */
  public static void append( final long category, final Object event ) {
    Log.append( category, event, null );
  }




  /**
   * Send append( category, message ) to each logger that is logging the
   * specified category.
   *
   * @param code The category code.
   * @param event The event to log.
   * @param cause The cause of the event.
   */
  public synchronized static void append( final long code, final Object event, final Throwable cause ) {
    final String category = Log.getCategory( code );

    for ( final Enumeration<Logger> en = Log.nameToLogger.elements(); en.hasMoreElements(); ) {
      final Logger logger = en.nextElement();

      if ( ( logger.getMask() & code ) != 0 ) {
        logger.append( category, event, cause );
      }
    }
  }




  /**
   * Log the object using the info category.
   *
   * @param event The event to log.
   */
  public static void append( final Object event ) {
    Log.append( Log.INFO_EVENTS, event, null );
  }




  /**
   * Send append( category, message ) to each logger that is logging the
   * specified category.
   *
   * @param category The category of the desired log operation 
   * @param event The event to log.
   */
  public synchronized static void append( final String category, final Object event ) {
    Log.append( category, event, null );
  }




  /**
   * Send append( category, message ) to each logger that is logging the
   * specified category.
   *
   * @param category The category.
   * @param event The event to log.
   * @param cause The exception that caused the log entry. Can be null.
   */
  public synchronized static void append( final String category, final Object event, final Throwable cause ) {
    Log.append( Log.getCode( category ), event, null );
  }




  /**
   * Log the event with category "DEBUG".
   *
   * <p>This is equivalent to <tt>log( DEBUG_EVENTS, event );</tt></p>
   *
   * @param event The event to log
   */
  public static void debug( final Object event ) {
    if ( ( Log.masks & Log.DEBUG_EVENTS ) != 0 ) {
      Log.append( Log.DEBUG_EVENTS, event, null );
    }
  }




  /**
   * Log the event with category "DEBUG".
   *
   * <p>This is equivalent to <tt>log( DEBUG_EVENTS, event, cause );</tt></p>
   *
   * @param event The event to log
   * @param cause The cause of the event.
   */
  public static void debug( final Object event, final Throwable cause ) {
    if ( ( Log.masks & Log.DEBUG_EVENTS ) != 0 ) {
      Log.append( Log.DEBUG_EVENTS, event, cause );
    }
  }




  /**
   * Disable the specified logger.
   * 
   * <p>The named logger will save a mask of all the categories it is currently 
   * logging and set the current category mask to zero effectively turning the 
   * logger off.</p>
   *
   * @param name The name of the logger to temporally disable.
   * 
   * @see #enableLogger(String)
   */
  public static synchronized void disableLogger( final String name ) {
    if ( !Log.permanentLoggers.contains( name ) ) {
      final Logger ilogger = Log.nameToLogger.get( name );

      if ( ilogger != null ) {
        ilogger.disable();
      }
    }
  }




  /**
   * Re-enable the specified logger.
   * 
   * <p>The named logger will begin logging the categories it was previously
   * logging when it was disabled.</p>
   *
   * @param name The name of the logger to re-enable.
   * 
   * @see #disableLogger(String)
   */
  public static synchronized void enableLogger( final String name ) {
    final Logger ilogger = Log.nameToLogger.get( name );

    if ( ilogger != null ) {
      ilogger.enable();
    }
  }




  /**
   * Log the event with category "ERROR".
   *
   * <p>This is equivalent to <tt>log( ERROR_EVENTS, event );</tt></p>
   *
   * @param event
   */
  public static void error( final Object event ) {
    if ( ( Log.masks & Log.ERROR_EVENTS ) != 0 ) {
      Log.append( Log.ERROR_EVENTS, event, null );
    }
  }




  /**
   * Log the event with category "ERROR".
   *
   * <p>This is equivalent to <tt>log( ERROR_EVENTS, event, cause );</tt></p>
   *
   * @param event The event to log
   * @param cause The cause of the event.
   */
  public static void error( final Object event, final Throwable cause ) {
    if ( ( Log.masks & Log.ERROR_EVENTS ) != 0 ) {
      Log.append( Log.ERROR_EVENTS, event, cause );
    }
  }




  /**
   * Log the event with category "FATAL".
   *
   * <p>This is equivalent to <tt>log( FATAL_EVENTS, event );</tt></p>
   *
   * @param event
   */
  public static void fatal( final Object event ) {
    if ( ( Log.masks & Log.FATAL_EVENTS ) != 0 ) {
      Log.append( Log.FATAL_EVENTS, event, null );
    }
  }




  /**
   * Log the event with category "FATAL".
   *
   * <p>This is equivalent to <tt>log( FATAL_EVENTS, event );</tt></p>
   *
   * @param event The event to log
   * @param cause The cause of the event.
   */
  public static void fatal( final Object event, final Throwable cause ) {
    if ( ( Log.masks & Log.FATAL_EVENTS ) != 0 ) {
      Log.append( Log.FATAL_EVENTS, event, cause );
    }
  }




  /**
   * Access the name of the given category code.
   * 
   * @return The category associated with the specified code.
   */
  public synchronized static String getCategory( final long code ) {
    return Log.codeToString.get( new Long( code ) );
  }




  /**
   * Access all the named categories currently registered with the logging
   * subsystem.
   * 
   * <p>This is a good way to discover what categories have been registered by
   * components &quot;behind the scenes&quot;. When used in development trouble 
   * shooting activities, a developer may discover logging categories to enable 
   * and give new insight into the operation of the application.</p>
   *
   * @return An array of category names.
   * 
   * @see #getCode(String)
   */
  public synchronized static String[] getCategoryNames() {
    final String[] retval = new String[Log.stringToCode.size()];

    int i = 0;

    for ( final Enumeration<String> en = Log.stringToCode.keys(); en.hasMoreElements(); i++ ) {
      retval[i] = en.nextElement();
    }

    return retval;
  }




  /**
   * Return the code for the specified category.
   *
   * <p>This allows us to specify 58 user-defined categories to the existing 6
   * before an over-run occurs.</p>
   *
   * @param category The category name.
   *
   * @return The code for the given category.
   */
  public static synchronized long getCode( final String category ) {
    if ( Log.stringToCode.size() < 64 ) {
      Long code = Log.stringToCode.get( category );

      if ( code == null ) {
        code = new Long( 1L << Log.stringToCode.size() );

        Log.stringToCode.put( category, code );
        Log.codeToString.put( code, category );
      }

      return code.longValue();
    }

    throw new IllegalStateException( "Maximum number of categories (" + Log.stringToCode.size() + ") has been reached" );
  }




  /**
   * Access the default logger, that is, the logger with the name of "default".
   * 
   * @return The default logger, or null if there is no logger named "default".
   */
  public synchronized static Logger getDefaultLogger() {
    return Log.nameToLogger.get( Log.DEFAULT_LOGGER_NAME );
  }




  /**
   * Return the number of milliseconds since logging began.
   *
   * <p>This is useful for a single point of reference between log entries.</p>
   *
   * @return The number of milliseconds since logging began.
   */
  public static long getInterval() {
    return System.currentTimeMillis() - Log.started;
  }




  /**
   * Return the Logger object with the given name.
   *
   * @param name The name of the logger to retrieve.
   *
   * @return The reference to the Logger object with the given name.
   */
  public synchronized static Logger getLogger( final String name ) {
    if ( name != null ) {
      return Log.nameToLogger.get( name );
    } else {
      return null;
    }
  }




  /**
   * Access the number of loggers currently in the static collection.
   *
   * @return the number of logger in the static collection.
   */
  public synchronized static int getLoggerCount() {
    return Log.nameToLogger.size();
  }




  /**
   * Return an enumeration over all the current logger names.
   *
   * @return TODO Complete Documentation
   */
  public synchronized static Enumeration<String> getLoggerNames() {
    final Vector<String> names = new Vector<String>();

    for ( final Enumeration<String> en = Log.nameToLogger.keys(); en.hasMoreElements(); ) {
      names.addElement( en.nextElement() );
    }

    return names.elements();
  }




  /**
   * Return an enumeration over all the current loggers.
   *
   * @return an enumeration over all the current loggers.
   */
  public synchronized static Enumeration<Logger> getLoggers() {
    final Vector<Logger> loggers = new Vector<Logger>();

    for ( final Enumeration<Logger> en = Log.nameToLogger.elements(); en.hasMoreElements(); ) {
      loggers.addElement( en.nextElement() );
    }

    return loggers.elements();
  }




  /**
   * Log the event with category "INFO".
   *
   * <p>This is equivalent to <tt>log( INFO_EVENTS, event );</tt></p>
   *
   * @param event
   */
  public static void info( final Object event ) {
    if ( ( Log.masks & Log.INFO_EVENTS ) != 0 ) {
      Log.append( Log.INFO_EVENTS, event, null );
    }
  }




  /**
   * Log the event with category "INFO".
   *
   * <p>This is equivalent to <tt>log( INFO_EVENTS, event, cause );</tt></p>
   *
   * @param event The event to log
   * @param cause The cause of the event.
   */
  public static void info( final Object event, final Throwable cause ) {
    if ( ( Log.masks & Log.INFO_EVENTS ) != 0 ) {
      Log.append( Log.INFO_EVENTS, event, cause );
    }
  }




  /**
   * Return true if at least one of the loggers is logging a category defined
   * by the mask.
   * 
   * <p>This is the fastest way to determine if it is worth the time and effort 
   * to construct a message before the append method is called.</p> 
   *
   * @param mask The mask.
   *
   * @return TODO Complete Documentation
   */
  public static boolean isLogging( final long mask ) {
    return ( ( Log.masks & mask ) != 0 );
  }




  /**
   * Return true if at least one of the loggers is logging the specified
   * category.
   *
   * @param category The category.
   *
   * @return TODO Complete Documentation
   */
  public static boolean isLogging( final String category ) {
    return Log.isLogging( Log.getCode( category ) );
  }




  /**
   * Check to see if a named logger is permanent.
   * 
   * @param name The name of the logger to check.
   * 
   * @return True if the logger is tagged as permanent, false otherwise.
   */
  public static boolean isPermanent( final String name ) {
    return Log.permanentLoggers.contains( name );
  }




  /**
   * Make the logger with the given name permanent.
   * 
   * <p>The logger with the given name will not be removed from the logging 
   * system once made permanent. There is no way to undo this operation for the 
   * life of the runtime.</p>
   * 
   * <p><strong>NOTE:</strong> It is possible to make a named logger permanent 
   * even before it is added to the system. Once a string is passed to this 
   * method, the logging system will forever ignore requests to remove it.</p>
   * 
   * @param name The name to ignore on any remove request.
   */
  public static void makeLoggerPermanent( final String name ) {
    Log.permanentLoggers.add( name );
  }




  /**
   * Recalculate the master mask value.
   */
  static synchronized void recalcMasks() {
    Log.masks = 0L;

    for ( final Enumeration<Logger> enumeration = Log.nameToLogger.elements(); enumeration.hasMoreElements(); ) {
      Log.masks |= enumeration.nextElement().getMask();
    }

  }




  /**
   * Remove the default logger from the logging fixture.
   * 
   * <p>Removing the logger from the static collection will result in the 
   * default logger not being included in collective <tt>append()</tt> 
   * operations.</p>
   */
  public synchronized static void removeDefaultLogger() {
    Log.removeLogger( Log.DEFAULT_LOGGER_NAME );
  }




  /**
   * Remove the specified logger from the static collection of loggers.
   * 
   * <p>If the name of the logger appears on the permanent logger list, it will
   * NOT be removed. The operation will be silently ignored. This includes the
   * default logger name.</p>
   *
   * @param name The name of the logger.
   */
  public static synchronized void removeLogger( final String name ) {
    if ( !Log.permanentLoggers.contains( name ) ) {
      final Logger logger = Log.nameToLogger.get( name );

      if ( logger != null ) {
        logger.terminate();
      }

      Log.nameToLogger.remove( name );
      Log.recalcMasks();
    }
  }




  /**
   * Removes all logers from the system - including permanent loggers.
   */
  public static synchronized void removeAllLoggers() {

    // terminate each of the loggers
    for ( final Enumeration<Logger> en = Log.nameToLogger.elements(); en.hasMoreElements(); ) {
      en.nextElement().terminate();
    }
    
    // clear the logger table
    Log.nameToLogger.clear();
    
    // recalc masks to 0
    Log.recalcMasks();
  }




  /**
   * Set the mask of all the loggers.
   *
   * @param mask The mask to set to all loggers in the collection.
   */
  public static synchronized void setMask( final long mask ) {
    Logger lgr = null;
    for ( final Enumeration<Logger> en = Log.nameToLogger.elements(); en.hasMoreElements(); ) {
      lgr = en.nextElement();
      if ( !lgr.isLocked() ) {
        lgr.setMask( mask );
      }
    }
  }




  /**
   * Instruct all loggers to start logging events of the specified category.
   *
   * @param category The category.
   */
  public synchronized static void startLogging( final String category ) {
    Logger lgr = null;
    for ( final Enumeration<Logger> en = Log.nameToLogger.elements(); en.hasMoreElements(); ) {
      lgr = en.nextElement();
      if ( !lgr.isLocked() ) {
        lgr.startLogging( category );
      }
    }
  }




  /**
   * Instruct all loggers to stop logging events of the specified category.
   *
   * @param category The category.
   */
  public synchronized static void stopLogging( final String category ) {
    Logger lgr = null;
    for ( final Enumeration<Logger> en = Log.nameToLogger.elements(); en.hasMoreElements(); ) {
      lgr = en.nextElement();
      if ( !lgr.isLocked() ) {
        lgr.stopLogging( category );
      }
    }
  }




  /**
   * Log the event with category "TRACE".
   *
   * <p>This is equivalent to <tt>log( TRACE_EVENTS, event );</tt></p>
   *
   * @param event
   */
  public static void trace( final Object event ) {
    Log.append( Log.TRACE_EVENTS, event, null );
  }




  /**
   * Log the event with category "TRACE".
   *
   * <p>This is equivalent to <tt>log( TRACE_EVENTS, event, cause );</tt></p>
   *
   * @param event The event to log
   * @param cause The cause of the event.
   */
  public static void trace( final Object event, final Throwable cause ) {
    if ( ( Log.masks & Log.TRACE_EVENTS ) != 0 ) {
      Log.append( Log.TRACE_EVENTS, event, cause );
    }
  }




  /**
   * Log the event with category "WARN".
   *
   * <p>This is equivalent to <tt>log( WARN_EVENTS, event );</tt></p>
   *
   * @param event
   */
  public static void warn( final Object event ) {
    if ( ( Log.masks & Log.WARN_EVENTS ) != 0 ) {
      Log.append( Log.WARN_EVENTS, event, null );
    }
  }




  /**
   * Log the event with category "WARN".
   *
   * <p>This is equivalent to <tt>log( WARN_EVENTS, event, cause );</tt></p>
   *
   * @param event The event to log
   * @param cause The cause of the event.
   */
  public static void warn( final Object event, final Throwable cause ) {
    if ( ( Log.masks & Log.WARN_EVENTS ) != 0 ) {
      Log.append( Log.WARN_EVENTS, event, cause );
    }
  }




  /**
   * Private constructor to keep instances of this class from being created.
   */
  private Log() {}

}
