/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.loader.log;

import java.util.StringTokenizer;

import coyote.commons.ExceptionUtil;
import coyote.commons.StringUtil;


/**
 * StringAppender is an implementation of Logger that extends AbstractLogger 
 * and defines event() to write the event to a Writer.
 */
public class StringAppender extends AbstractLogger {
  StringBuffer buffer = new StringBuffer();




  /**
   * Constructor StringAppender
   */
  public StringAppender() {
    this(0);
  }




  /**
   * Construct appender with an initial mask value.
   *
   * @param mask The initial mask value.
   */
  public StringAppender(final long mask) {
    super(mask);
    formatter = new ConsoleFormatter();
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
  public void append(final String category, final Object event, final Throwable cause) {
    try {
      buffer.append(formatter.format(event, category, cause));
    } catch (final Exception e) {
      System.err.println(this.getClass().getName() + " formatting error: " + e + ":" + e.getMessage() + StringUtil.LINE_FEED + ExceptionUtil.stackTrace(e));
    }
  }




  /**
   * Initialize the logger.
   */
  public void initialize() {
    // we don't call super.initialize() because we don't need the file based initialization

    // determine the categories to log
    if (config != null && config.getString(Logger.CATEGORY_TAG) != null) {
      for (final StringTokenizer st = new StringTokenizer(config.getString(Logger.CATEGORY_TAG), Logger.CATEGORY_DELIMS); st.hasMoreTokens(); startLogging(st.nextToken()));
    }

    // determine if this logger is disabled, if so set mask to 0
    if (config != null && config.getString(Logger.ENABLED_TAG) != null) {
      String str = config.getString(Logger.ENABLED_TAG).toLowerCase();
      if ("false".equals(str) || "0".equals(str) || "no".equals(str)) {
        disable(); // set the mask to 0
      }
    }

  }




  /**
   * @return the buffer
   */
  public StringBuffer getBuffer() {
    return buffer;
  }




  /**
   * Terminates the logger.
   */
  public void terminate() {
    // nothing to do here
  }




  public void clear() {
    buffer.delete(0, buffer.length());
  }




  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return buffer.toString();
  }
}