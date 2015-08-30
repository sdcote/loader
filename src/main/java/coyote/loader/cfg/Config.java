/*
 * $Id:$
 *
 * Copyright Stephan D. Cote' 2008 - All rights reserved.
 */
package coyote.loader.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import coyote.dataframe.DataFrame;


/**
 * The Config class models a component that is used to make file-based 
 * configuration of components easier than using property files.
 * 
 * <p>The primary goal of this class is to allow hierarchical configurations to
 * be specified using different notations (such as JSON) as a formatting 
 * strategy. Basic File and network protocol I/O is supported in a simple 
 * interface.</p>
 */
public class Config extends DataFrame implements Cloneable, Serializable {

  public static final String CLASS = "Config";

  // Common configuration tags
  public static final String CLASS_TAG = "Class";
  public static final String NAME_TAG = "Name";
  public static final String ID_TAG = "ID";

  /** Serialization identifier */
  private static final long serialVersionUID = -6020161245846637528L;

  /**
   * A collection of ConfigSlots we use to optionally validate the completeness 
   * of the Config object or to provide default configurations.
   */
  private HashMap<String, ConfigSlot> slots = null;




  public Config() {
    super.put( NAME_TAG, CLASS );
  }




  /**
   * 
   * @param file
   * 
   * @return TODO Complete Documentation
   * 
   * @throws IOException
   * @throws ConfigException
   */
  public static Config read( final File file ) throws IOException, ConfigException {
    return Config.read( new FileInputStream( file ) );
  }




  /**
   * 
   * @param configStream
   * 
   * @return TODO Complete Documentation
   * 
   * @throws ConfigException
   */
  public static Config read( final InputStream configStream ) throws ConfigException {
    final Config retval = new Config();

    return retval;
  }




  /**
   * 
   * @param filename
   * 
   * @return TODO Complete Documentation
   * 
   * @throws IOException
   * @throws ConfigException
   */
  public static Config read( final String filename ) throws IOException, ConfigException {
    return Config.read( new FileInputStream( filename ) );
  }




  /**
   * 
   * @param uri
   * 
   * @return TODO Complete Documentation
   * 
   * @throws IOException
   * @throws ConfigException
   */
  public static Config read( final URI uri ) throws IOException, ConfigException {
    if ( uri.getScheme().toLowerCase().startsWith( "file" ) ) {
      return Config.read( new FileInputStream( uri.getAuthority() ) );
    } else {
      return Config.read( uri.toURL().openStream() );
    }
  }




  /**
   * Add the referenced ConfigSlot.
   *
   * @param slot the reference to the ConfigSlot to add.
   */
  public void addConfigSlot( final ConfigSlot slot ) {
    if ( slots == null ) {
      slots = new HashMap();
    }

    if ( slot != null ) {
      slots.put( slot.getName(), slot );
    }
  }




  /**
   * Return an Iterator over all the ConfigSlots
   *
   * @return an Iterator over all the ConfigSlot, never returns null;
   */
  public Iterator<ConfigSlot> configSlotIterator() {
    if ( slots != null ) {
      return slots.values().iterator();
    } else {
      return new Vector().iterator();
    }
  }




  /**
   * Retrieve a named ConfigSlot from the configuration
   *
   * @param name String which represents the name of the slot to retrieve
   *
   * @return value ConfigSlot object with the given name or null if it does
   *         not exist
   */
  public ConfigSlot getConfigSlot( final String name ) {
    if ( slots != null ) {
      synchronized( slots ) {
        return (ConfigSlot)slots.get( name );
      }
    } else {
      return null;
    }
  }




  /**
   * Access the current number of elements set in this configuration.
   * 
   * @return number of named values in this configuration
   */
  public int getElementCount() {
    return fields.size();
  }




  /**
   * @return the id
   */
  public String getId() {
    return getAsString( ID_TAG );
  }




  /**
   * @param id the id to set
   */
  public void setId( final String id ) {
    this.put( ID_TAG, id );
  }




  /**
   * @return the name
   */
  public String getName() {
    return getAsString( NAME_TAG );
  }




  /**
   * @param name the name to set
   */
  public void setName( final String name ) {
    this.put( NAME_TAG, name );
  }




  /**
   * @return the class
   */
  public String getClassName() {
    return getAsString( CLASS_TAG );
  }




  /**
   * @param name the class name to set
   */
  public void setClassName( final String name ) {
    put( Config.CLASS_TAG, name );
  }




  /**
   * Remove the referenced ConfigSlot
   *
   * @param slot The reference to the ConfigSlot to remove.
   */
  public void removeConfigSlot( final ConfigSlot slot ) {
    if ( slots == null ) {
      return;
    } else {
      synchronized( slots ) {
        slots.remove( slot );
      }
    }
  }




  /**
   * Use the set configuration slots and prime the configuration with those 
   * defaults.
   * 
   * <p>This allows the caller to set a configuration object to the defaults. 
   * This is useful a a starting point for configurable components when a 
   * configuration has not been provided.</p>
   */
  public void setDefaults() {
    final Iterator<ConfigSlot> it = configSlotIterator();

    while ( it.hasNext() ) {
      final ConfigSlot slot = (ConfigSlot)it.next();

      if ( slot != null ) {
        final String defaultValue = slot.getDefaultValue();

        if ( defaultValue != null ) {
          put( slot.getName(), defaultValue );
        }
      }
    }

  }

}
