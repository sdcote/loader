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
package coyote.loader.component;

import coyote.dataframe.DataFrame;


/**
 * The BaseComponent class models a starting point for components.
 */
public abstract class AbstractComponent implements Component {
  public static final String UNKNOWN = "Unknown";




  /**
   * @see coyote.loader.component.Component#getApplicationId()
   */
  @Override
  public String getApplicationId() {
    return AbstractComponent.UNKNOWN;
  }




  /**
   * @see coyote.loader.component.Component#getCategory()
   */
  @Override
  public String getCategory() {
    return AbstractComponent.UNKNOWN;
  }




  /**
   * @see coyote.loader.component.Component#getDescription()
   */
  @Override
  public String getDescription() {
    return AbstractComponent.UNKNOWN;
  }




  /**
   * @see coyote.loader.component.Component#getId()
   */
  @Override
  public String getId() {
    return AbstractComponent.UNKNOWN;
  }




  /**
   * @see coyote.loader.component.Component#getName()
   */
  @Override
  public String getName() {
    return AbstractComponent.UNKNOWN;
  }




  /**
   * @see coyote.loader.component.Component#getProfile()
   */
  @Override
  public DataFrame getProfile() {
    return null;
  }




  /**
   * @see coyote.loader.component.Component#getStatus()
   */
  @Override
  public DataFrame getStatus() {
    return null;
  }




  /**
   * @see coyote.loader.component.Component#getSystemId()
   */
  @Override
  public String getSystemId() {
    return AbstractComponent.UNKNOWN;
  }




  /**
   * @see coyote.loader.component.Component#shutdown(coyote.dataframe.DataFrame)
   */
  @Override
  public void shutdown( final DataFrame command ) {}

}
