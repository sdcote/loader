/*
 * Copyright Stephan D. Cote' 2008 - All rights reserved.
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.loader.cfg;

import coyote.dataframe.DataField;


/**
 * Class ConfigSlot
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision$
 */
public class ConfigSlot {
  protected String name = null;
  protected String description = null;
  protected int type = 0;
  protected Object defaultValue = null;
  protected String message = null;




  /**
   * Constructor ConfigSlot
   */
  public ConfigSlot() {}




  /**
   * Constructor ConfigSlot
   *
   * @param name The name of the attribute slot
   * @param description A string of descriptive text for the use/meaning of this attribute
   * @param dflt the default object value of this attribute
   */
  public ConfigSlot( final String name, final String description, final Object dflt ) {
    if ( name != null ) {
      this.setName( name );
      this.setDescription( description );
      try {
        new DataField( dflt );
        this.setDefaultValue( dflt );
      } catch ( Exception e ) {
        throw new IllegalArgumentException( "Unsupported default value type: " + e.getMessage() );
      }
    } else {
      throw new IllegalArgumentException( "ConfigSlot name is null" );
    }
  }




  /**
   * Constructor ConfigSlot
   *
   * @param slot
   */
  public ConfigSlot( final ConfigSlot slot ) {
    this.name = slot.name;
    this.description = slot.description;
    this.type = slot.type;
    this.defaultValue = slot.defaultValue;
  }




  /**
   * Method getName
   *
   * @return TODO Complete Documentation
   */
  public String getName() {
    return name;
  }




  /**
   * Method setName
   *
   * @param name
   */
  public void setName( final String name ) {
    if ( name != null ) {
      this.name = name;
    } else {
      throw new IllegalArgumentException( "ConfigSlot name is null" );
    }
  }




  /**
   * Method getDescription
   *
   * @return TODO Complete Documentation
   */
  public String getDescription() {
    return description;
  }




  /**
   * Method setDescription
   *
   * @param description
   */
  public void setDescription( final String description ) {
    this.description = description;
  }




  /**
   * Method getDefaultValue
   *
   * @return TODO Complete Documentation
   */
  public Object getDefaultValue() {
    return defaultValue;
  }




  /**
   * Method setDefaultValue
   *
   * @param val
   */
  public void setDefaultValue( final Object val ) {
    this.defaultValue = val;
  }




  /**
   * Get the user-defined message for this slot.
   *
   * @return TODO Complete Documentation
   */
  public String getMessage() {
    return message;
  }




  /**
   * Set a user-defined message for this slot.
   *
   * <p>Many times, the ConfigSlot is used to represent a mutable Attribute
   * instance, as in GUIs, where using an Attribute instance can be prohibitive
   * in its type checking. In such cases, it is useful to be able to pass an
   * ConfigSlot instead and then create an Attribute after all edits are
   * completed. In such cases, the ability to pass a user-defined message field
   * is useful as in the case where value failed some validity check and the
   * ConfigSlot is passed back to the GUI with the invalid value in the
   * defaultValue field and an error message in the Message field.</p>
   *
   * @param message
   */
  public void setMessage( final String message ) {
    this.message = message;
  }
}
