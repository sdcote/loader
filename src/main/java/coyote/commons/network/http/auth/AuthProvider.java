/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.network.http.auth;

import coyote.commons.network.http.IHTTPSession;


/**
 * Interface to a component capable of prodisinf authentication (AuthN) and 
 * authorization (AuthZ) processing.
 */
public interface AuthProvider {

  /**
   * Check to see if the session is using SSL/TLS.
   * 
   * @param session the session to validate
   * 
   * @return true if the session is using secure, encrypted connection, false 
   *         otherwise.
   */
  boolean isSecureConnection( IHTTPSession session );




  /**
   * Perform authentication of the identity represented in the given session.
   * 
   * @param session the session to authenticate
   * 
   * @return true if the data in the given session represents an 
   *         authenticated user in the system, false if otherwise.
   */
  boolean isAuthenticated( IHTTPSession session );




  /**
   * Perform authorization processing on the given session to determine if the 
   * associated identity is a member of the given groups.
   * 
   * @param session the session to authorize
   * @param groups a comma delimited list of groups
   * 
   * @return true if the session is authenticated and a member of one of the 
   *         specified groups, false otherwise.
   */
  boolean isAuthorized( IHTTPSession session, String groups );

}
