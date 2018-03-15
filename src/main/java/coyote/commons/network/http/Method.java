/*
 * Copyright (c) 2002 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */

package coyote.commons.network.http;

/**
 * HTTP Request methods, with the ability to decode a <code>String</code>
 * back to its enum value.
 */
public enum Method {
  GET("GET"), POST("POST"), HEAD("HEAD"), OPTIONS("OPTIONS"), PUT("PUT"), DELETE("DELETE"), TRACE("TRACE"), CONNECT("CONNECT"), PATCH("PATCH"), PROPFIND("PROPFIND"), PROPPATCH("PROPPATCH"), MKCOL("MKCOL"), MOVE("MOVE"), COPY("COPY"), LOCK("LOCK"), UNLOCK("UNLOCK");

  private String name;




  static Method lookup(final String method) {
    if (method == null) {
      return null;
    }

    try {
      return valueOf(method);
    } catch (final IllegalArgumentException e) {
      // TODO: Log it?
      return null;
    }
  }




  private Method(String s) {
    name = s;
  }




  /**
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return name;
  }




  public static Method getMethodByName(String name) {
    if (name != null) {
      for (Method method : Method.values()) {
        if (name.equalsIgnoreCase(method.toString())) {
          return method;
        }
      }
    }
    return null;
  }

}