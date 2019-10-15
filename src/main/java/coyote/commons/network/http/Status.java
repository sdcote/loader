/*
 * Copyright (c) 2002 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */

package coyote.commons.network.http;

/**
 * Some HTTP response status codes
 */
public enum Status {
  SWITCH_PROTOCOL(101, "Switching Protocols"), //
  OK(200, "OK"), //
  CREATED(201, "Created"), // 
  ACCEPTED(202, "Accepted"), //
  NO_CONTENT(204, "No Content"), // 
  PARTIAL_CONTENT(206, "Partial Content"), // 
  MULTI_STATUS(207, "Multi-Status"), //
  ALREADY_REPORTED(208, "Already Reported"), //
  IM_USED(226, "IM Used"), //
  MULTIPLE_CHOICES(300, "Multiple Choices"), //
  REDIRECT(301, "Moved Permanently"), //
  REDIRECT_TEMPORARY(302, "Found"), //
  REDIRECT_SEE_OTHER(303, "See Other"), // 
  NOT_MODIFIED(304, "Not Modified"), //
  USE_PROXY(305, "Use Proxy"), //
  SWITCH_PROXY(306, "Switch Proxy"), //
  TEMPORARY_REDIRECT(307, "Temporary Redirect"), //
  PERMANENT_REDIRECT(308, "Permanent Redirect"), //
  BAD_REQUEST(400, "Bad Request"), //
  UNAUTHORIZED(401, "Unauthorized"), //
  FORBIDDEN(403, "Forbidden"), //
  NOT_FOUND(404, "Not Found"), //
  METHOD_NOT_ALLOWED(405, "Method Not Allowed"), // 
  NOT_ACCEPTABLE(406, "Not Acceptable"), //
  PROXY_AUTH_REQUIRED(407, "Proxy Authentication Required"), //
  REQUEST_TIMEOUT(408, "Request Timeout"), //
  CONFLICT(409, "Conflict"), //
  GONE(410, "Gone"), //
  LENGTH_REQUIRED(411, "Length Required"), //
  PRECONDITION_FAILED(412, "Precondition Failed"), //
  PAYLOAD_TOO_LARGE(413, "Payload Too Large"), //
  URI_TOO_LONG(414, "URI Too Long"), //
  UNSUPPORTED_MEDIA(415, "Unsupported Media Type"), //
  RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"), // 
  EXPECTATION_FAILED(417, "Expectation Failed"), //
  TEAPOT(418, "I'm a teapot"), //
  MISDIRECTED(421, "Misdirected Request"), //
  UNPROCESSABLE(422, "Unprocessable Entity"), //
  LOCKED(423, "Locked"), //
  FAILED_DEPENDENCY(424, "Failed Dependency"), //
  UPGRADE_REQUIRED(426, "Upgrade Required"), //
  PRECONDITION_REQUIRED(428, "Precondition Required"), //
  TOO_MANY_REQUESTS(429, "Too Many Requests"), //
  HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"), //
  LEGALLY_UNAVAIAL(451, "Unavailable For Legal Reasons"), //  
  INTERNAL_ERROR(500, "Internal Server Error"), //
  NOT_IMPLEMENTED(501, "Not Implemented"), //
  BAD_GATEWAY(502, "Bad Gateway"), //
  UNAVAILABLE(503, "Service Unavailable"), // 
  GATEWAY_TIMEOUT(504, "Gateway Timeout"), //
  UNSUPPORTED_HTTP_VERSION(505, "HTTP Version Not Supported"), //
  VARIANT_NEGOTIATES(506, "Variant Also Negotiates"), //
  INSUFFICIENT_STORAGE(507, "Insufficient Storage"), //
  LOOP_DETECTED(508, "Loop Detected"), //
  NOT_EXTENDED(510, "Not Extended"), //
  NETWORK_AUTH_REQUIRED(511, "Network Authentication Required"); //

  private final int requestStatus;

  private final String description;




  Status(final int requestStatus, final String description) {
    this.requestStatus = requestStatus;
    this.description = description;
  }




  /**
   * Get the status description for the given status code.
   * 
   * @param code the code to look up
   * 
   * @return a description of the given status code or null if the code was 
   *         not found.
   */
  public static String getStatus(int code) {
    String retval = null;
    for (Status status : Status.values()) {
      if (status.requestStatus == code) {
        retval = status.description;
        break;
      }
    }

    return retval;
  }




  public String getDescription() {
    return description;
  }




  public int getRequestStatus() {
    return requestStatus;
  }




  @Override
  public String toString() {
    return "" + requestStatus + " " + description;
  }

}