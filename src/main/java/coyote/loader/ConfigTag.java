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

/**
 * 
 */
public class ConfigTag {

  // System Properties

  /** Name ({@value}) of the system property containing the configuration directory name. */
  public static final String CONFIG_DIR = "cfg.dir";

  /** Name ({@value}) of the system property containing the name of the cipher to use. */
  public static final String CIPHER_NAME = "cipher.name";

  /** Name ({@value}) of the system property containing the Base64 cipher key value. */
  public static final String CIPHER_KEY = "cipher.key";

  // Configuration tags

  /** Name ({@value}) of the Logging configuration sections. */
  public static final String LOGGER = "Logger";

  /** Name ({@value}) of the configuration attribute specifying a class. */
  public static final String CLASS = "Class";

  /** Name ({@value}) of the configuration attribute specifying a name. */
  public static final String NAME = "Name";

  /** Name ({@value}) of the configuration attribute specifying an identifier. */
  public static final String ID = "ID";

  /** Name ({@value}) of the configuration attribute specifying the components to load. */
  public static final String COMPONENTS = "Components";

  /** Name ({@value}) of the configuration attribute specifying the component to load. */
  public static final String COMPONENT = "Component";

}
