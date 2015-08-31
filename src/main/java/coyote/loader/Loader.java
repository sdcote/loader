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
package coyote.loader;

import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigurationException;


/**
 * 
 */
public interface Loader {

  /**
   * Configure this loader with the given Config data.
   * 
   * @param cfg the configuration to apply to this loader.
   * 
   * @throws ConfigurationException if there were problems configuring the loader.
   */
  public void configure( Config cfg ) throws ConfigurationException;




  /**
   * Start the loader running.
   * 
   * <p>This is a blocking call. The thread will remain in this method until 
   * the loader terminates or an exception is thrown.</p>
   */
  void run();

}
