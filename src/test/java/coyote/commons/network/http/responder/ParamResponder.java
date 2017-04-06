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
package coyote.commons.network.http.responder;

import java.util.Map;

import coyote.commons.network.MimeType;
import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.IStatus;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.responder.UriResource;
import coyote.commons.network.http.responder.Responder;


/**
 * 
 */
public class ParamResponder implements Responder {
  private String responseText = "Boom";




  /**
   * @see coyote.commons.network.http.responder.Responder#delete(coyote.commons.network.http.responder.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response delete( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session ) {
    responseText = urlParams.get( "name" );
    return Response.createFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  /**
   * @see coyote.commons.network.http.responder.Responder#get(coyote.commons.network.http.responder.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response get( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session ) {
    responseText = urlParams.get( "name" );
    return Response.createFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  /**
   * @see coyote.commons.network.http.responder.Responder#other(java.lang.String, coyote.commons.network.http.responder.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response other( String method, UriResource uriResource, Map<String, String> urlParams, IHTTPSession session ) {
    responseText = urlParams.get( "name" );
    return Response.createFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  /**
   * @see coyote.commons.network.http.responder.Responder#post(coyote.commons.network.http.responder.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  public Response post( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session ) {
    responseText = urlParams.get( "name" );
    return Response.createFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  /**
   * @see coyote.commons.network.http.responder.Responder#put(coyote.commons.network.http.responder.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
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
