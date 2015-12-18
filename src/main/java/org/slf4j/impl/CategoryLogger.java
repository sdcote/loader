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
package org.slf4j.impl;

import org.slf4j.Logger;
import org.slf4j.Marker;

import coyote.loader.log.Log;


/**
 * This is a logger which intercepts logging requests via the SLF4J API and 
 * sends them to a category logger.
 */
public class CategoryLogger implements Logger {

  private final String loggerName;




  public CategoryLogger( final String name ) {
    loggerName = name;
  }




  /**
   * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String)
   */
  @Override
  public void debug( final Marker marker, final String msg ) {
    Log.debug( msg );
  }




  /**
   * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Object)
   */
  @Override
  public void debug( final Marker marker, final String format, final Object arg ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Object[])
   */
  @Override
  public void debug( final Marker marker, final String format, final Object... arguments ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public void debug( final Marker marker, final String format, final Object arg1, final Object arg2 ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
   */
  @Override
  public void debug( final Marker marker, final String msg, final Throwable t ) {
    Log.debug( msg, t );
  }




  /**
   * @see org.slf4j.Logger#debug(java.lang.String)
   */
  @Override
  public void debug( final String msg ) {
    Log.debug( msg );
  }




  /**
   * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object)
   */
  @Override
  public void debug( final String format, final Object arg ) {
    Log.debug( arg );
  }




  /**
   * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object[])
   */
  @Override
  public void debug( final String format, final Object... arguments ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public void debug( final String format, final Object arg1, final Object arg2 ) {
    Log.debug( arg1 );
  }




  /**
   * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Throwable)
   */
  @Override
  public void debug( final String msg, final Throwable t ) {
    Log.info( msg, t );
  }




  /**
   * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String)
   */
  @Override
  public void error( final Marker marker, final String msg ) {
    Log.error( msg );
  }




  /**
   * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Object)
   */
  @Override
  public void error( final Marker marker, final String format, final Object arg ) {
    Log.error( arg );
  }




  /**
   * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Object[])
   */
  @Override
  public void error( final Marker marker, final String format, final Object... arguments ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public void error( final Marker marker, final String format, final Object arg1, final Object arg2 ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
   */
  @Override
  public void error( final Marker marker, final String msg, final Throwable t ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#error(java.lang.String)
   */
  @Override
  public void error( final String msg ) {
    Log.error( msg );
  }




  /**
   * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object)
   */
  @Override
  public void error( final String format, final Object arg ) {
    Log.error( arg );
  }




  /**
   * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object[])
   */
  @Override
  public void error( final String format, final Object... arguments ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public void error( final String format, final Object arg1, final Object arg2 ) {
    Log.error( arg1 );
  }




  /**
   * @see org.slf4j.Logger#error(java.lang.String, java.lang.Throwable)
   */
  @Override
  public void error( final String msg, final Throwable t ) {
    Log.error( msg, t );
  }




  /**
   * @see org.slf4j.Logger#getName()
   */
  @Override
  public String getName() {
    return loggerName;
  }




  /**
   * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String)
   */
  @Override
  public void info( final Marker marker, final String msg ) {
    Log.info( msg );
  }




  /**
   * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Object)
   */
  @Override
  public void info( final Marker marker, final String format, final Object arg ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Object[])
   */
  @Override
  public void info( final Marker marker, final String format, final Object... arguments ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public void info( final Marker marker, final String format, final Object arg1, final Object arg2 ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
   */
  @Override
  public void info( final Marker marker, final String msg, final Throwable t ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#info(java.lang.String)
   */
  @Override
  public void info( final String msg ) {
    Log.info( msg );
  }




  /**
   * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object)
   */
  @Override
  public void info( final String format, final Object arg ) {
    Log.info( arg );
  }




  /**
   * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object[])
   */
  @Override
  public void info( final String format, final Object... arguments ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public void info( final String format, final Object arg1, final Object arg2 ) {
    Log.info( arg1 );
  }




  /**
   * @see org.slf4j.Logger#info(java.lang.String, java.lang.Throwable)
   */
  @Override
  public void info( final String msg, final Throwable t ) {
    Log.info( msg, t );
  }




  /**
   * @see org.slf4j.Logger#isDebugEnabled()
   */
  @Override
  public boolean isDebugEnabled() {
    return Log.isLogging( Log.DEBUG );
  }




  /**
   * @see org.slf4j.Logger#isDebugEnabled(org.slf4j.Marker)
   */
  @Override
  public boolean isDebugEnabled( final Marker marker ) {
    return false;
  }




  /**
   * @see org.slf4j.Logger#isErrorEnabled()
   */
  @Override
  public boolean isErrorEnabled() {
    return Log.isLogging( Log.ERROR );
  }




  /**
   * @see org.slf4j.Logger#isErrorEnabled(org.slf4j.Marker)
   */
  @Override
  public boolean isErrorEnabled( final Marker marker ) {
    return false;
  }




  /**
   * @see org.slf4j.Logger#isInfoEnabled()
   */
  @Override
  public boolean isInfoEnabled() {
    return Log.isLogging( Log.INFO );
  }




  /**
   * @see org.slf4j.Logger#isInfoEnabled(org.slf4j.Marker)
   */
  @Override
  public boolean isInfoEnabled( final Marker marker ) {
    // TODO Auto-generated method stub
    return false;
  }




  /**
   * @see org.slf4j.Logger#isTraceEnabled()
   */
  @Override
  public boolean isTraceEnabled() {
    return Log.isLogging( Log.TRACE );
  }




  /**
   * @see org.slf4j.Logger#isTraceEnabled(org.slf4j.Marker)
   */
  @Override
  public boolean isTraceEnabled( final Marker marker ) {
    return false;
  }




  /**
   * @see org.slf4j.Logger#isWarnEnabled()
   */
  @Override
  public boolean isWarnEnabled() {
    return Log.isLogging( Log.WARN );
  }




  /**
   * @see org.slf4j.Logger#isWarnEnabled(org.slf4j.Marker)
   */
  @Override
  public boolean isWarnEnabled( final Marker marker ) {
    return false;
  }




  /**
   * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String)
   */
  @Override
  public void trace( final Marker marker, final String msg ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Object)
   */
  @Override
  public void trace( final Marker marker, final String format, final Object arg ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Object[])
   */
  @Override
  public void trace( final Marker marker, final String format, final Object... argArray ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public void trace( final Marker marker, final String format, final Object arg1, final Object arg2 ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
   */
  @Override
  public void trace( final Marker marker, final String msg, final Throwable t ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#trace(java.lang.String)
   */
  @Override
  public void trace( final String msg ) {
    Log.trace( msg );
  }




  /**
   * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object)
   */
  @Override
  public void trace( final String format, final Object arg ) {
    Log.trace( arg );
  }




  /**
   * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object[])
   */
  @Override
  public void trace( final String format, final Object... arguments ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public void trace( final String format, final Object arg1, final Object arg2 ) {
    Log.trace( arg1 );
  }




  /**
   * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Throwable)
   */
  @Override
  public void trace( final String msg, final Throwable t ) {
    Log.trace( msg, t );
  }




  /**
   * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String)
   */
  @Override
  public void warn( final Marker marker, final String msg ) {
    Log.warn( msg );
  }




  /**
   * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Object)
   */
  @Override
  public void warn( final Marker marker, final String format, final Object arg ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Object[])
   */
  @Override
  public void warn( final Marker marker, final String format, final Object... arguments ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public void warn( final Marker marker, final String format, final Object arg1, final Object arg2 ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
   */
  @Override
  public void warn( final Marker marker, final String msg, final Throwable t ) {
    Log.warn( msg, t );
  }




  /**
   * @see org.slf4j.Logger#warn(java.lang.String)
   */
  @Override
  public void warn( final String msg ) {
    Log.warn( msg );
  }




  /**
   * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object)
   */
  @Override
  public void warn( final String format, final Object arg ) {
    Log.warn( arg );
  }




  /**
   * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object[])
   */
  @Override
  public void warn( final String format, final Object... arguments ) {
    // TODO Auto-generated method stub
  }




  /**
   * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object, java.lang.Object)
   */
  @Override
  public void warn( final String format, final Object arg1, final Object arg2 ) {
    Log.warn( arg1 );
  }




  /**
   * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Throwable)
   */
  @Override
  public void warn( final String msg, final Throwable t ) {
    Log.warn( msg, t );
  }

}
