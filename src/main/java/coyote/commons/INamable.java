/*
 * $Id: INamable.java,v 1.2 2003/11/03 20:46:59 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons;

/**
 * Interface INamable
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.2 $
 */
public interface INamable extends INamed {

  /**
   * Method setName
   *
   * @param name
   */
  public abstract void setName( String name );

}
