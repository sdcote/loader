/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.network.http;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;


/**
 * This models a set of name-value pairs which can be associated to a HTTP
 * Session through its identifier.
 *
 * <p>This identifier is normally associated to the session through a cookie
 * set in the HTTP headers by the {@link SessionProfileManager}.
 */
public class SessionProfile implements Serializable {
  private static final long serialVersionUID = -2260496918545261708L;
  private final HashMap<String, Serializable> data = new HashMap<>();
  private final String identifier;
  private long expiry = 0;




  /**
   * Create a new session profile with the given session identifier.
   *
   * @param id the identifier of the session.
   */
  public SessionProfile(final String id) {
    identifier = id;
  }




  /**
   * Get the value of the named object from this session profile.
   *
   * @param key the name of the object to retrieve.
   *
   * @return the data value of the named object or null if it does not exist.
   */
  public Serializable get(final String key) {
    return data.get(key);
  }




  /**
   * @return the expiration time im milliseconds
   */
  public long getExpiry() {
    return expiry;
  }




  /**
   * @return the identifier for this session profile.
   */
  public String getIdentifier() {
    return identifier;
  }




  /**
   * Determine if this session has expired.
   *
   * @return true if the expiry is greather than 0 and less than the current
   *         time.
   */
  public boolean isExpired() {
    return expiry > 0 && expiry < System.currentTimeMillis();
  }




  /**
   * Set a named value in this session profile.
   *
   * @param key the name of the value to set
   * @param value the data value to set
   */
  public void set(final String key, final Serializable value) {
    data.put(key, value);
  }




  /**
   * @param expiry the epoch time in milliseconds when this profile expires. A
   *        value of zero means the profile will never expire.
   */
  public void setExpiry(final long expiry) {
    this.expiry = expiry;
  }




  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder(getClass().getSimpleName());
    b.append(":");
    b.append(hashCode());
    b.append(getIdentifier());
    b.append(" expires ");
    if (getExpiry() == 0) {
      b.append("never");
    } else {
      b.append(new Date(getExpiry()));
    }
    return b.toString();
  }

}