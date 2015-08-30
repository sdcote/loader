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
 * The Component class models...
 */
public interface Component {

  /**
   * @return the identifier of the application to which this component belongs.
   */
  public String getApplicationId();




  /**
   * Determine the classification of this component.
   * 
   * <p>This method will return a string that represents this components 
   * category for the purposes of reporting. It is expected that the category
   * will follow some standard naming convention useful to the framework.</p>
   * 
   * <p>One such convention may include the identification of applications and
   * infrastructure components. This may include the designation of agents and
   * other specialized components.</p>
   * 
   * @return The category of this component.
   */
  public String getCategory();




  /**
   * 
   * @return a description of this component
   */
  public String getDescription();




  /**
   * @return the unique identifier of this component
   */
  public String getId();




  /**
   * @return the name of this component within this loader.
   */
  public String getName();




  /**
   * Access a brief informational profile for this component instance.
   * 
   * <p>This method is called as a part of the component "query" operation to 
   * ensure the component instance is operational.</p>
   * 
   * @return Small set of attributes describing this component.
   */
  public DataFrame getProfile();




  /**
   * Access a detailed status of this component.
   * 
   * <p>This method is called as a part of a Loaders status reporting, the 
   * result of which will be included in the Loaders response.</p>
   * 
   * <p>Care must be taken when calling this method as it may cause the 
   * component to spend significant resources in collecting data to represent 
   * its current operation status.</p>
   * 
   * @return Detailed set of attributes describing the operational details of
   *         this component.
   */
  public DataFrame getStatus();




  /**
   * 
   * @return The identifier of the system to which this component belongs.
   */
  public String getSystemId();




  /**
   * Shut this component down using the given DataFrame as a set of parameters.
   * 
   * @param params
   */
  public void shutdown( final DataFrame params );

}
