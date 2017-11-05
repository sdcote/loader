/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */

package coyote.commons.network.http.auth;

import java.util.Map;

import coyote.commons.network.http.IHTTPSession;


/**
 * Interface to a component capable of providing authentication (AuthN) and
 * authorization (AuthZ) processing.
 * 
 * <p>The process normally involves checking if the connection is secure (e.g. 
 * SSL) if the resource is annotated to only require secure connections. Next 
 * the provider is called to check authentication and establish the username 
 * and groups for the request. Finally, authorization is performed by checking 
 * if the session is a member of one of the given set of groups.   
 */
public interface AuthProvider {
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";




  /**
   * Perform authentication of the identity represented in the given session.
   *
   * <p>It is also generally expected that the authentication operation will
   * populate the session with user name and groups so as to perform
   * subsequent role based access control (RBAC) on the group names
   * associated with the use in this session. Also, if the Auth annotation
   * specifies that Authentication is not required for the resource, the
   * existence of a user name is probably expected to indicate the session
   * represents a logged-in user and that additional processing may be enabled.
   *
   * @param session the session to authenticate
   *
   * @return true if the data in the given session represents an
   *         authenticated user in the system, false if otherwise.
   */
  boolean isAuthenticated(IHTTPSession session);




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
  boolean isAuthorized(IHTTPSession session, String groups);




  /**
   * Check to see if the session is using SSL/TLS.
   *
   * @param session the session to validate
   *
   * @return true if the session is using secure, encrypted connection, false
   *         otherwise.
   */
  boolean isSecureConnection(IHTTPSession session);




  /**
   * This is a generic utility method to authenticate a set of credentials and
   * populate the given session with the results (username and group list).
   * 
   * <p>This is useful for other components (like login responders) to perform 
   * authentication with credentials gathered external to the HTTP request.
   *  
   * @param session the HTTP session to authenticate
   * @param credentials the map of credentials (e.g. username and password) to 
   *        authenticate.
   * 
   * @return true if the authentication was successful, false otherwise.
   */
  boolean authenticate(IHTTPSession session, Map<String, String> credentials);

}
