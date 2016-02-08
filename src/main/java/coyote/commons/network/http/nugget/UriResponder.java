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
 * <p>All nuggets should implement this interface to support requests.</p>
 */
public interface UriResponder {

  public Response delete( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session );




  public Response get( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session );




  public Response other( String method, UriResource uriResource, Map<String, String> urlParams, IHTTPSession session );




  public Response post( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session );




  public Response put( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session );
}