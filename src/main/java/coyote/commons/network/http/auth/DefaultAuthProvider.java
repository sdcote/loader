/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */

package coyote.commons.network.http.auth;

import java.util.Map;

import coyote.commons.network.http.HTTPSession;


/**
 * The default Authentication and authorization provider for the server.
 *
 * This denies access to everything.
 */
public class DefaultAuthProvider implements AuthProvider {

  /**
   * @see coyote.commons.network.http.auth.AuthProvider#isAuthenticated(coyote.commons.network.http.HTTPSession)
   */
  @Override
  public boolean isAuthenticated(final HTTPSession session) {
    return false;
  }




  /**
   * @see coyote.commons.network.http.auth.AuthProvider#isAuthorized(coyote.commons.network.http.HTTPSession, java.lang.String)
   */
  @Override
  public boolean isAuthorized(final HTTPSession session, final String groups) {
    return false;
  }




  /**
   * @see coyote.commons.network.http.auth.AuthProvider#isSecureConnection(coyote.commons.network.http.HTTPSession)
   */
  @Override
  public boolean isSecureConnection(final HTTPSession session) {
    return false;
  }




  /**
   * @see coyote.commons.network.http.auth.AuthProvider#authenticate(coyote.commons.network.http.HTTPSession, java.util.Map)
   */
  @Override
  public boolean authenticate(HTTPSession session, Map<String, String> credentials) {
    return false;
  }

}
