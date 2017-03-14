/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 */
package coyote.commons.network.http.handler;

import coyote.commons.network.http.IStatus;
import coyote.commons.network.http.Status;


/**
 * This responds with a blank page.
 * 
 * <p>Useful to handle URLs which should respond, but not return any data.
 */
public class BlankPageHandler extends DefaultHandler {

  @Override
  public String getMimeType() {
    return "text/html";
  }




  @Override
  public IStatus getStatus() {
    return Status.OK;
  }




  @Override
  public String getText() {
    return "<html><body></body></html>";
  }

}