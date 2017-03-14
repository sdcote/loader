/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.commons.network.http.auth;

import java.util.Map;

import coyote.commons.network.MimeType;
import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.IStatus;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.handler.DefaultHandler;
import coyote.commons.network.http.handler.UriResource;


/**
 * 
 */
public class ProtectedHandler extends DefaultHandler {

  /**
   * @see coyote.commons.network.http.handler.DefaultStreamHandler#post(coyote.commons.network.http.handler.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  @Auth(groups = "sysop", requireSSL = true)
  public Response post( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session ) {
    return Response.createFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  /**
   * @see coyote.commons.network.http.handler.DefaultHandler#get(coyote.commons.network.http.handler.UriResource, java.util.Map, coyote.commons.network.http.IHTTPSession)
   */
  @Override
  @Auth(groups = "devop", requireSSL = false)
  public Response get( UriResource uriResource, Map<String, String> urlParams, IHTTPSession session ) {
    return Response.createFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  /**
   * @see coyote.commons.network.http.handler.DefaultHandler#getStatus()
   */
  @Override
  public IStatus getStatus() {
    return Status.OK;
  }




  /**
   * @see coyote.commons.network.http.handler.DefaultHandler#getText()
   */
  @Override
  public String getText() {
    return "";
  }




  /**
   * @see coyote.commons.network.http.handler.DefaultStreamHandler#getMimeType()
   */
  @Override
  public String getMimeType() {
    return MimeType.JSON.getType();
  }

}
