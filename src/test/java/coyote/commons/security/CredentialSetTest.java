/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.commons.security;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import coyote.commons.ByteUtil;


/**
 * 
 */
public class CredentialSetTest {

  /**
   * Test method for {@link coyote.commons.security.CredentialSet#CredentialSet()}.
   */
  @Test
  public void testCredentialSet() {
    CredentialSet creds = new CredentialSet();
    assertNotNull(creds);
  }




  /**
   * Test method for {@link coyote.commons.security.CredentialSet#CredentialSet(int)}.
   */
  @Test
  public void testCredentialSetInt() {
    CredentialSet creds = new CredentialSet(1);
    assertNotNull(creds);
    assertTrue(creds.getRounds() == 1);
  }




  /**
   * Test method for {@link coyote.commons.security.CredentialSet#getRounds()}.
   */
  @Test
  public void testGetRounds() {
    CredentialSet creds = new CredentialSet();
    assertNotNull(creds);
    assertTrue(creds.getRounds() == 0);
  }




  /**
   * Test method for {@link coyote.commons.security.CredentialSet#add(java.lang.String, byte[])}.
   */
  @Test
  public void testAdd() {
    CredentialSet creds = new CredentialSet();
    assertNotNull(creds);
    creds.add("test", "data".getBytes());
  }




  /**
   * Test method for {@link coyote.commons.security.CredentialSet#CredentialSet(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testCredentialSetStringString() {
    CredentialSet creds = new CredentialSet(CredentialSet.PASSWORD, "p45Sw0rD");
    assertNotNull(creds);
  }




  /**
   * Test method for {@link coyote.commons.security.CredentialSet#contains(java.lang.String)}.
   */
  @Test
  public void testContains() {
    CredentialSet creds = new CredentialSet();
    assertNotNull(creds);
    creds.add("test", "data".getBytes());
    assertTrue(creds.contains("test"));
    creds = new CredentialSet("password", "123abc");
    assertTrue(creds.contains("password"));

  }




  /**
   * Tests the ability to extract the credential values from the set, both 
   * clear-text and MD5 hash.
   * 
   * @throws Exception if the system does not support UTF8 string encoding
   */
  @Test
  public void testDataAccess() throws Exception {
    String PASSWORD = "123abc{$&";
    String EXPECTED_HASH = "2CF492AB948FD5941451172BD23FE9D9";
    CredentialSet creds = new CredentialSet(CredentialSet.PASSWORD, PASSWORD);
    byte[] value = creds.getValue(CredentialSet.PASSWORD);
    assertNotNull(value);
    String newString = new String(value, "UTF8");
    assertTrue(PASSWORD.equals(newString));

    // store credentials in memory as MD5 hashed values, 1 round of calculations
    creds = new CredentialSet(CredentialSet.PASSWORD, PASSWORD, 1);
    value = creds.getValue(CredentialSet.PASSWORD);
    assertNotNull(value);

    // Make sure that the stored credential is NOT cleartext
    newString = new String(value, "UTF-8");
    assertFalse(PASSWORD.equals(newString));

    // convert the MD5 hash to a hex string, no delimiters between hex values
    String dbValue = ByteUtil.bytesToHex(value, null);
    assertTrue(EXPECTED_HASH.equals(dbValue));
  }




  @Test
  public void chaining() throws Exception {
    String PRIVATEKEY = "4nxoiwamnrf95jnfks8wjd9rfk";
    String PASSWORD = "123abc{$&";
    CredentialSet creds = new CredentialSet() //
        .add(CredentialSet.PRIVATEKEY, PRIVATEKEY) //
        .add(CredentialSet.PASSWORD, PASSWORD);
  }

}
