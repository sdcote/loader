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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.StringTokenizer;

import coyote.commons.ExceptionUtil;
import coyote.commons.StringUtil;


/**
 * ConsoleAppender is an implementation of Logger that extends LoggerBase and
 * defines event() to write the event to a Writer.
 */
public class ConsoleAppender extends AbstractLogger {

  /** Field log_writer */
  Writer log_writer;




  /**
   * Constructor ConsoleAppender
   */
  public ConsoleAppender() {
    this( new OutputStreamWriter( System.out ), 0 );
  }




  /**
   * Construct a WriterLogger that writes to System.out with an initial mask
   * value.
   *
   * @param mask The initial mask value.
   */
  public ConsoleAppender( final long mask ) {
    this( new OutputStreamWriter( System.out ), mask );
  }




  /**
   * Construct a WriterLogger that writes to the specified writer with
   * an initial mask value of zero (i.e. does not log any events).
   *
   * @param writer The writer.
   */
  public ConsoleAppender( final Writer writer ) {
    this( writer, 0 );
  }




  /**
   * Construct a WriterLogger that writes to the specified writer with an
   * initial mask value.
   *
   * @param writer The writer.
   * @param mask The initial mask value.
   */
  public ConsoleAppender( final Writer writer, final long mask ) {
    super( mask );

    log_writer = writer;
  }




  /**
   * If enabled, log an event of the specified category to the underlying
   * Writer.
   *
   * <p>In order to remain thread-safe, a new formatter is created each call.</p>
   *
   * @param category The category.
   * @param event The event.
   * @param cause The exception that caused the log entry. Can be null.
   */
  public void append( final String category, final Object event, final Throwable cause ) {
    try {
      log_writer.write( formatter.format( event, category, cause ) );
      log_writer.flush();
    } catch ( final IOException ioe ) {
      // normal during shutdown sequences - but what about other times?
      // maybe we should consider refactoring this
    } catch ( final Exception e ) {
      System.err.println( this.getClass().getName() + " formatting error: " + e + ":" + e.getMessage() + StringUtil.LINE_FEED + ExceptionUtil.stackTrace( e ) );
    }
  }




  /**
   * Return the writer.
   *
   * @return TODO Complete Documentation
   */
  public Writer getWriter() {
    return log_writer;
  }




  /**
   * Initialize the logger.
   */
  public void initialize() {
    if ( config.contains( Logger.CATEGORY_TAG ) ) {
      for ( final StringTokenizer st = new StringTokenizer( config.getAsString( Logger.CATEGORY_TAG ), Logger.CATEGORY_DELIMS ); st.hasMoreTokens(); startLogging( st.nextToken() ) );
    }
    // System.out is already initialized
  }




  /**
   * Set the writer.
   *
   * @param writer The new writer.
   */
  public void setWriter( final Writer writer ) {
    log_writer = writer;
  }




  /**
   * Terminates the logger.
   */
  public void terminate() {
    // System.out should not be closed
  }
}