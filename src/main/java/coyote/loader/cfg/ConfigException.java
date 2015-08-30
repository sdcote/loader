/*
 * $Id:$
 *
 * Copyright Stephan D. Cote' 2008 - All rights reserved.
 */
package coyote.loader.cfg;

/**
 * The ConfigException class models...
 * 
 * @author Stephan D. Cote'
 * @version $Revision:$
 */
public class ConfigException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 229675499440617423L;




  /**
   * 
   */
  public ConfigException() {
    super();
  }




  /**
   * @param message
   */
  public ConfigException( final String message ) {
    super( message );
  }




  /**
   * @param message
   * @param newNested
   */
  public ConfigException( final String message, final Throwable newNested ) {
    super( message, newNested );
  }




  /**
   * @param newNested
   */
  public ConfigException( final Throwable newNested ) {
    super( newNested );
  }

}
