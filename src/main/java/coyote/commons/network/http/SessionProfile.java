/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.network.http;

import java.io.Serializable;
import java.util.HashMap;


/**
 * 
 */
public class SessionProfile implements Serializable {
  private static final long serialVersionUID = -2260496918545261708L;
  private final HashMap<String, Serializable> data = new HashMap<>();
  private final String identifier;




  /**
   * Create a new session profile with the given session identifier.
   * 
   * @param id the identifier of the session.
   */
  public SessionProfile(String id) {
    this.identifier = id;
  }




  /**
   * Get the value of the named object from this session profile.
   * 
   * @param key the name of the object to retrieve.
   * 
   * @return the data value of the named object or null if it does not exist.
   */
  public Serializable get(String key) {
    return data.get(key);
  }




  /**
   * Set a named value in this session profile.
   * 
   * @param key the name of the value to set
   * @param value the data value to set
   */
  public void set(String key, Serializable value) {
    data.put(key, value);
  }




  /**
   * @return the identifier for this session profile.
   */
  public String getIdentifier() {
    return identifier;
  }




  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + ":" + hashCode() + " " + getIdentifier();
  }

}