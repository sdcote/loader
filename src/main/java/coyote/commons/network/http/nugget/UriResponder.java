/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 * 
 * Contributors:
 *   Stephan D. Cote 
 */
package coyote.commons.network.http.nugget;

import java.util.Map;

import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.Response;


/**
 * This represents a class which responnds to a URI requested from the HTTP 
 * server.
 * 
 * <p>The UriResource can contain important data for the operation of the 
 * handler. This data is set when the routing was added to the server and can 
 * be retrieved through the {@code initParameter} attribute. The UriResponder 
 * must know beforehand the type of data placed in the initialization 
 * attribute:<pre>
 * File baseDirectory = uriResource.initParameter( File.class );</pre></p>
 * 
 * <p>The {@code initParameter} attribute is actually an array of objects which 
 * the UriResponder can retrieve via index:<pre>
 * File baseDirectory = uriResource.initParameter( 0, File.class );</pre></p>
 * 
 * <p>All nuggets should implement this interface to support requests.</p>
 */
public interface UriResponder {

  /**
   * Handle the HTTP "delete" method requests.
   * 
   * @param uriResource the instance of the UriResource which contains our initialization parameters
   * @param urlParams parameters to process
   * @param session the session established with the HTTP server
   * 
   * @return The response based on this method's processing
   */
  public Response delete( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session );




  /**
   * Handle the HTTP "get" method requests.
   * 
   * @param uriResource the instance of the UriResource which contains our initialization parameters
   * @param urlParams parameters to process
   * @param session the session established with the HTTP server
   * 
   * @return The response based on this method's processing
   */
  public Response get( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session );




  /**
   * Handle the HTTP method requests which do not map to get, put, post or delete.
   * 
   * @param uriResource the instance of the UriResource which contains our initialization parameters
   * @param urlParams parameters to process
   * @param session the session established with the HTTP server
   * 
   * @return The response based on this method's processing
   */
  public Response other( String method, UriResource uriResource, Map<String, String> urlParams, IHTTPSession session );




  /**
   * Handle the HTTP "post" method requests.
   * 
   * @param uriResource the instance of the UriResource which contains our initialization parameters
   * @param urlParams parameters to process
   * @param session the session established with the HTTP server
   * 
   * @return The response based on this method's processing
   */
  public Response post( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session );




  /**
   * Handle the HTTP "put" method requests.
   * 
   * @param uriResource the instance of the UriResource which contains our initialization parameters
   * @param urlParams parameters to process
   * @param session the session established with the HTTP server
   * 
   * @return The response based on this method's processing
   */
  public Response put( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session );

}