/*
 * Copyright (c) 2007 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.loader.log;

import java.io.Writer;


/**
 * An appender that does nothing.
 * 
 * <p>See Martin Fowler's refactoring books (Refactoring to Patterns) or 
 * (Refactoring: Improving the Design of Existing Code) for details on using 
 * Null Objects in software.</p>
 */
public class NullAppender extends AbstractLogger {

  /**
   * Construct a WriterLogger that writes to the specified writer with an
   * initial mask value.
   *
   * @param writer The writer.
   * @param mask The initial mask value.
   */
  public NullAppender(final Writer writer, final long mask) {
    super(mask);
  }




  /**
   * @param mask The initial mask value.
   */
  public NullAppender(long mask) {
    this(null, mask);
  }




  /**
   * Initialize the logger.
   */
  public void initialize() {
    // no-op implementation
  }




  /**
   * Terminates the logger.
   */
  public void terminate() {
    // no-op implementation
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
    // no-op implementation
  }

}