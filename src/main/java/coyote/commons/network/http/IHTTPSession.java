/*
 * Copyright (c) 2003 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 */
package coyote.commons.network.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Handles one session, i.e. parses the HTTP request and returns the
 * response.
 */
public interface IHTTPSession {

  void execute() throws IOException;




  CookieHandler getCookies();




  Map<String, String> getHeaders();




  InputStream getInputStream();




  Method getMethod();




  Map<String, String> getParms();




  String getQueryParameterString();




  /**
   * Get the remote hostname of the requester.
   * 
   * @return the hostname.
   */
  String getRemoteHostName();




  /**
   * Get the remote ip address of the requester.
   * 
   * @return the IP address.
   */
  String getRemoteIpAddress();




  /**
   * @return the path part of the URL.
   */
  String getUri();




  /**
   * Adds the files in the request body to the files map.
   * 
   * @param files
   *            map to modify
   */
  void parseBody( Map<String, String> files ) throws IOException, ResponseException;
}