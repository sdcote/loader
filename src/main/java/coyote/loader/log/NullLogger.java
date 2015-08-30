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

import java.net.URI;
import java.net.URISyntaxException;

import coyote.loader.cfg.Config;


/**
 * The NullLogger class models a logger that does nothing.
 * 
 * <p>This allows calls to a logger to do nothing. It is helpful in creating an 
 * instance of this class as opposed to setting an object attribute to null so 
 * the caller does not have to always check for a null reference after calling 
 * an accessor method to get a logger.</p>
 * 
 * <p>See Martin Fowler's refactoring books (Refactoring to Patterns) or 
 * (Refactoring: Improving the Design of Existing Code) for details on using 
 * Null Objects in software.</p>
 * 
 * @author Stephan D. Cote'
 */
public class NullLogger implements Logger {

  private static final String NULL_URI = "null:///";




  /**
   * @see net.smartforge.log.Logger#append(java.lang.String, java.lang.Object, java.lang.Throwable)
   */
  public void append( final String category, final Object event, final Throwable cause ) {}




  /**
   * @see net.smartforge.log.Logger#disable()
   */
  public void disable() {}




  /**
   * @see net.smartforge.log.Logger#enable()
   */
  public void enable() {}




  /**
   * @see net.smartforge.log.Logger#getConfig()
   */
  public Config getConfig() {
    return null;
  }




  /**
   * @see net.smartforge.log.Logger#getMask()
   */
  public long getMask() {
    return 0;
  }




  /**
   * @see net.smartforge.log.Logger#getTarget()
   */
  public URI getTarget() {
    try {
      return new URI( NULL_URI );
    } catch ( final URISyntaxException e ) {
      System.out.println( e.getMessage() );
    }
    return null;
  }




  /**
   * @see net.smartforge.log.Logger#initialize()
   */
  public void initialize() {}




  /**
   * @see net.smartforge.log.Logger#isLocked()
   */
  public boolean isLocked() {
    return false;
  }




  /**
   * @see net.smartforge.log.Logger#setConfig(net.smartforge.util.Config)
   */
  public void setConfig( final Config cfg ) {}




  /**
   * @see net.smartforge.log.Logger#setLocked(boolean)
   */
  public void setLocked( final boolean flag ) {}




  /**
   * @see net.smartforge.log.Logger#setMask(long)
   */
  public void setMask( final long mask ) {}




  /**
   * @see net.smartforge.log.Logger#setTarget(java.net.URI)
   */
  public void setTarget( final URI uri ) {}




  /**
   * @see net.smartforge.log.Logger#startLogging(java.lang.String)
   */
  public void startLogging( final String category ) {}




  /**
   * @see net.smartforge.log.Logger#stopLogging(java.lang.String)
   */
  public void stopLogging( final String category ) {}




  /**
   * @see net.smartforge.log.Logger#terminate()
   */
  public void terminate() {}

}
