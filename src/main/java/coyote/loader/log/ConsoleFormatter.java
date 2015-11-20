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

import coyote.commons.StringUtil;


/**
 * An event formatter which is suitable for console logging.
 * 
 * <p>This is a minimalistic formatter to keep the console clean.</p>
 */
public class ConsoleFormatter implements Formatter {

  /**
   *
   */
  public ConsoleFormatter() {
  }




  /**
   * Format the event into a string to be placed in the log.
   *
   * @param event the thing to be logged
   * @param category the type of log entry it is
   * @param cause The exception that caused the log entry. Can be null.
   *
   * @return a string representing the line to place in the log
   */
  public String format( final Object event, final String category, final Throwable cause ) {

    final StringBuffer buffer = new StringBuffer();

    buffer.append( category );
    buffer.append( " | " );

    if ( event != null )
      buffer.append( event.toString() );

    buffer.append( StringUtil.LINE_FEED );

    return buffer.toString();
  }




  /**
   * Create any header data.
   *
   * @return header data to be written out before any log entries.
   */
  public byte[] initialize() {
    return null;
  }




  /**
   * Create any footer data.
   *
   * @return footer data to be written after any events before the output is closed.
   */
  public byte[] terminate() {
    return null;
  }
}