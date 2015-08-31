/*
 * Copyright (c) 2005 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons;

/**
 * Interface INamable
 */
public interface INamable extends INamed {

  /**
   *Set the name of this component instance.
   *
   * @param name the name of the component
   */
  public abstract void setName( String name );

}
