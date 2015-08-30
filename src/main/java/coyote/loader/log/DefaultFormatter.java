/*
 * $Id: DefaultFormatter.java,v 1.5 2007/03/16 15:41:36 scote Exp $
 */
package coyote.loader.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import coyote.commons.ExceptionUtil;
import coyote.commons.StringUtil;


/**
 * Class DefaultFormatter
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.5 $
 */
public class DefaultFormatter implements Formatter {
  private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
  private volatile long lastevent = 0;




  /**
   *
   */
  public DefaultFormatter() {
    super();
  }




  /**
   * Method format
   *
   * @param event
   * @param category
   * @param cause The exception that caused the log entry. Can be null.
   *
   * @return TODO Complete Documentation
   */
  public String format( final Object event, final String category, final Throwable cause ) {
    final long now = System.currentTimeMillis();

    if ( lastevent == 0 ) {
      lastevent = now;
    }

    final StringBuffer buffer = new StringBuffer();

    buffer.append( DefaultFormatter.DATE_FORMATTER.format( new Date( now ) ) );
    buffer.append( " | " );
    buffer.append( Thread.currentThread().getName() );
    buffer.append( " | " );
    buffer.append( category );
    buffer.append( " | " );
    buffer.append( Log.getInterval() );
    buffer.append( ":" );
    buffer.append( ( now - lastevent ) );
    buffer.append( " | " );

    // The trace category get additional location information
    if ( Log.TRACE.equals( category ) ) {
      final StackTraceElement[] stack = new Exception().fillInStackTrace().getStackTrace();

      // get the 5th element, 1=DefaultFormatter.format(), 2=ConsoleAppender.event(),
      // 3=Log.log(), 4=Log.trace(), 5=the source method call
      final StackTraceElement elem = stack[4];

      buffer.append( ExceptionUtil.getLocalJavaName( elem.getClassName() ) );
      buffer.append( "." );
      buffer.append( elem.getMethodName() );
      buffer.append( "():" );

      if ( elem.getLineNumber() < 0 ) {
        buffer.append( "Native Method" );
      } else {
        buffer.append( elem.getLineNumber() );
      }

      buffer.append( " | " );
    }

    buffer.append( event.toString() );

    Throwable ball = cause;
    if ( ( event != null ) && ( cause == null ) && ( event instanceof Throwable ) ) {
      ball = (Throwable)event;
    }

    if ( ball != null ) {
      buffer.append( StringUtil.LINE_FEED );

      final StackTraceElement[] stack = ( ball ).getStackTrace();
      StackTraceElement elem = stack[( stack.length - 1 )];

      buffer.append( event.getClass().getName() );
      buffer.append( " at " );

      // --
      buffer.append( elem.getClassName() );
      buffer.append( "." );
      buffer.append( elem.getMethodName() );
      buffer.append( "(" );

      if ( elem.getLineNumber() < 0 ) {
        buffer.append( "Native Method" );
      } else {
        buffer.append( elem.getFileName() );
        buffer.append( ":" );
        buffer.append( elem.getLineNumber() );
      }

      buffer.append( ") message=[" );
      buffer.append( ( ball ).getMessage() );
      buffer.append( "]" );

      // --

      buffer.append( " - root cause: " );

      elem = stack[0];

      // -- yes, duplicate code, but inline is still faster --
      buffer.append( elem.getClassName() );
      buffer.append( "." );
      buffer.append( elem.getMethodName() );
      buffer.append( "(" );

      if ( elem.getLineNumber() < 0 ) {
        buffer.append( "Native Method" );
      } else {
        buffer.append( elem.getFileName() );
        buffer.append( ":" );
        buffer.append( elem.getLineNumber() );
      }

      buffer.append( ")" );
      // --

    }

    buffer.append( StringUtil.LINE_FEED );

    lastevent = now;

    return buffer.toString();
  }




  /**
   * Method initialize
   *
   * @return TODO Complete Documentation
   */
  public byte[] initialize() {
    return null;
  }




  /**
   * Method terminate
   *
   * @return TODO Complete Documentation
   */
  public byte[] terminate() {
    return null;
  }
}