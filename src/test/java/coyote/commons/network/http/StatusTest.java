/*
 * Copyright (c) 2017 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.commons.network.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import coyote.commons.StringUtil;


/**
 * 
 */
public class StatusTest {

  @Test
  public void test() {
    String status = Status.getStatus(429);
    assertTrue(StringUtil.isNotBlank(status));
    assertEquals(Status.TOO_MANY_REQUESTS.getDescription(), status);
  }

}
