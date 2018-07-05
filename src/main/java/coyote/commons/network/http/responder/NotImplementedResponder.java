/*
 * Copyright (c) 2004 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */

package coyote.commons.network.http.responder;

import coyote.commons.network.http.Status;
import coyote.commons.network.http.Status;


public class NotImplementedResponder extends DefaultResponder {

  @Override
  public String getMimeType() {
    return "text/html";
  }




  @Override
  public Status getStatus() {
    return Status.NOT_IMPLEMENTED;
  }




  @Override
  public String getText() {
    return "<html><body><h3>Not implemented</h3><p>The uri is mapped in the router, but no responder is specified.</p></body></html>";
  }
}