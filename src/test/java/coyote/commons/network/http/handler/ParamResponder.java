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
package coyote.commons.network.http.handler;

import java.util.Map;

import coyote.commons.network.MimeType;
import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.IStatus;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;


/**
 * 
 */
public class ParamResponder implements UriResponder {
  private String responseText = "Boom";




  /**
   * @see coyote.commons.network.http.handler.UriResponder#delete(coyote.commons.network.http.handler.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response delete( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session ) {
    responseText = urlParams.get( "name" );
    return Response.createFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  /**
   * @see coyote.commons.network.http.handler.UriResponder#get(coyote.commons.network.http.handler.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response get( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session ) {
    responseText = urlParams.get( "name" );
    return Response.createFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  /**
   * @see coyote.commons.network.http.handler.UriResponder#other(java.lang.String, coyote.commons.network.http.handler.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response other( String method, UriResource uriResource, Map<String, String> urlParams, IHTTPSession session ) {
    responseText = urlParams.get( "name" );
    return Response.createFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  /**
   * @see coyote.commons.network.http.handler.UriResponder#post(coyote.commons.network.http.handler.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response post( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session ) {
    responseText = urlParams.get( "name" );
    return Response.createFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  /**
   * @see coyote.commons.network.http.handler.UriResponder#put(coyote.commons.network.http.handler.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response put( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session ) {
    responseText = urlParams.get( "name" );
    return Response.createFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  /**
   * @return
   */
  private String getText() {
    return responseText;
  }




  /**
   * @return
   */
  private String getMimeType() {
    return MimeType.TEXT.getType();
  }




  /**
   * @return
   */
  private IStatus getStatus() {
    return Status.OK;
  }
}
