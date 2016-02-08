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

import java.io.InputStream;
import java.util.Map;

import coyote.commons.network.http.HTTPD;
import coyote.commons.network.http.IHTTPSession;
import coyote.commons.network.http.IStatus;
import coyote.commons.network.http.Response;


/**
 * General nugget to subclass when you provide text or html data. Only fixed 
 * size responses will be generated.
 */
public abstract class DefaultHandler extends DefaultStreamHandler {

  @Override
  public Response get( final UriResource uriResource, final Map<String, String> urlParams, final IHTTPSession session ) {
    return HTTPD.newFixedLengthResponse( getStatus(), getMimeType(), getText() );
  }




  @Override
  public InputStream getData() {
    throw new IllegalStateException( "This method should not be called in a text based nugget" );
  }




  @Override
  public abstract IStatus getStatus();




  public abstract String getText();
}