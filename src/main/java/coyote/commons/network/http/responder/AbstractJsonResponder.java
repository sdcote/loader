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
package coyote.commons.network.http.responder;

import coyote.commons.network.MimeType;
import coyote.commons.network.http.HTTPSession;
import coyote.commons.network.http.Response;
import coyote.commons.network.http.Status;
import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;

import java.util.Map;


/**
 * This is a common base class for handlers which implement a JSON web service pattern.
 *
 * <p>At the core is a {@code results} DataFrame used to hold the results of processing. Later, the {@link #getText()}
 * can be called to retrieve the JSON formatted data contained therein.
 */
public abstract class AbstractJsonResponder extends DefaultResponder implements Responder {

  protected final DataFrame results = new DataFrame();
  private boolean formattingJson = false;
  protected Status resultStatus = Status.OK;
  protected DataFrame METHOD_NOT_ALLOWED = new DataFrame().set("error", "Method Not Allowed");


  /**
   * @return the results DataFrame into which the results of processing is to be placed for later marshaling into JSON
   * text when the {@link #getText()} method is called.
   */
  public DataFrame getResults() {
    return results;
  }


  /**
   * @return true if we are formatting JSON, false (default) if not.
   */
  public boolean isFormattingJson() {
    return formattingJson;
  }


  /**
   * @param flag true to cause getText() to return formatted JSON, false (default) to represent it in compressed format.
   */
  public void setFormattingJson(boolean flag) {
    this.formattingJson = flag;
  }


  @Override
  public Status getStatus() {
    return resultStatus;
  }


  /**
   * @param status the response status to set
   */
  public void setStatus(Status status) {
    this.resultStatus = status;
  }


  @Override
  public String getText() {
    if (formattingJson)
      return JSONMarshaler.toFormattedString(results);
    else
      return JSONMarshaler.marshal(results);
  }


  @Override
  public String getMimeType() {
    return MimeType.JSON.getType();
  }


  @Override
  public Response get(final Resource resource, final Map<String, String> urlParams, final HTTPSession session) {
    return Response.createFixedLengthResponse(Status.METHOD_NOT_ALLOWED, getMimeType(), METHOD_NOT_ALLOWED.toString());
  }


  @Override
  public Response delete(final Resource resource, final Map<String, String> urlParams, final HTTPSession session) {
    return Response.createFixedLengthResponse(Status.METHOD_NOT_ALLOWED, getMimeType(), METHOD_NOT_ALLOWED.toString());
  }


  @Override
  public Response post(final Resource resource, final Map<String, String> urlParams, final HTTPSession session) {
    return Response.createFixedLengthResponse(Status.METHOD_NOT_ALLOWED, getMimeType(), METHOD_NOT_ALLOWED.toString());
  }


  @Override
  public Response put(final Resource resource, final Map<String, String> urlParams, final HTTPSession session) {
    return Response.createFixedLengthResponse(Status.METHOD_NOT_ALLOWED, getMimeType(), METHOD_NOT_ALLOWED.toString());
  }


  @Override
  public Response other(final String method, final Resource resource, final Map<String, String> urlParams, final HTTPSession session) {
    return Response.createFixedLengthResponse(Status.METHOD_NOT_ALLOWED, getMimeType(), METHOD_NOT_ALLOWED.toString());
  }

}
