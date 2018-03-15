/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */

package coyote.commons.network.http.responder;

import java.io.InputStream;
import java.util.Map;

import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Status;
import coyote.commons.network.http.Response;


/**
 * General responder to subclass when you provide text or html data. Only fixed
 * size responses will be generated.
 */
public abstract class DefaultResponder extends DefaultStreamResponder {

  @Override
  public Response get(final Resource resource, final Map<String, String> urlParams, final HTTPSession session) {
    return Response.createFixedLengthResponse(getStatus(), getMimeType(), getText());
  }




  @Override
  public InputStream getData() {
    throw new IllegalStateException("This method should not be called in a text based responder");
  }




  @Override
  public abstract Status getStatus();




  public abstract String getText();
}