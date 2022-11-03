/*
 * Copyright (c) 2016 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.commons.network;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetAddress;


/**
 * 
 */
public class IpAclTest {

  /**
   * @throws java.lang.Exception on error
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}




  /**
   * @throws java.lang.Exception on error
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {}




  @Test
  public void testConstrucor() {
    try {
      IpAcl acl = new IpAcl();
      acl.add("192.168/16", true);
      acl.add("10/8", false);
    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }
  }




  @Test
  public void testBasic() {
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);
      acl.add("172/8", true);
      acl.add("10/8", true);
      acl.add("192.168/16", true);

      String arg = "172.17.0.1";
      assertTrue("Should allow '" + arg + "'", acl.allows(arg));

      InetAddress address = InetAddress.getByName(arg);
      System.out.println(address);
      assertTrue("Should allow '" + address + "'", acl.allows(address));

    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }

  }


  @Test
  public void testDenySpecific() {
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);

      // Order is important! specific addresses should be specified first and broader scopes later since the first
      // network rule matching the argument is returned. This is by design to keep the  matching logic fast.
      acl.add("172.17.0.2/32", false); // deny this specific address in the allowed range
      acl.add("172/8", true); // allow the rest of the network
      System.out.println(acl);

      String arg = "172.17.0.1";
      assertTrue("Should allow '" + arg + "'", acl.allows(arg));

      InetAddress address = InetAddress.getByName(arg);
      System.out.println(address);
      assertTrue("Should allow '" + address + "'", acl.allows(address));

      arg = "172.17.0.2";
      assertFalse("Should deny '" + arg + "'", acl.allows(arg));

      address = InetAddress.getByName(arg);
      assertFalse("Should deny '" + address + "'", acl.allows(address));

      arg = "172.17.0.3";
      assertTrue("Should allow '" + arg + "'", acl.allows(arg));

      address = InetAddress.getByName(arg);
      System.out.println(address);
      assertTrue("Should allow '" + address + "'", acl.allows(address));


    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }

  }




  @Test
  public void testAllows() {
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);
      acl.add("192.168/16", true);
      acl.add("10/8", false);

      String arg = "192.168.1.100";
      assertTrue("Should allow '" + arg + "'", acl.allows(arg));

      arg = "10.8.107.12";

      assertFalse("Should NOT allow '" + arg + "'", acl.allows(arg));

      // if( acl.allows( arg ) )
      // {
      // System.out.println( "Error: ACL allows '" + arg + "'" );
      // }
      // else
      // {
      // System.out.println( "ACL denies '" + arg + "'" );
      // }
    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }

    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);

      // Only allow this one IP address
      acl.add("192.168.1.100/32", IpAcl.ALLOW);

      // This should pass
      String arg = "192.168.1.100";
      assertTrue("Should allow '" + arg + "'", acl.allows(arg));

      // These should not pass
      arg = "10.8.107.12";

      assertFalse("Should NOT allow '" + arg + "'", acl.allows(arg));

      arg = "192.168.1.101";

      assertFalse("Should NOT allow '" + arg + "'", acl.allows(arg));
    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }

    // Test the ordering, 192.168.100 subnet is denied, but the rest of 192.168 
    // is allowed
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);
      acl.add("192.168.100/24", false);
      acl.add("192.168/16", true);

      String arg = "192.168.100.23";
      assertFalse("Should NOT allow '" + arg + "'", acl.allows(arg));

      arg = "192.168.23.100";
      assertTrue("Should allow '" + arg + "'", acl.allows(arg));

      arg = "10.8.107.12";
      assertFalse("Should NOT allow '" + arg + "'", acl.allows(arg));

    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }

  }




  @Test
  public void testAllowAll() {
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);
      acl.add("255.255.255.255/0", true);
      String arg = "192.168.1.100";
      assertTrue("Should allow '" + arg + "'", acl.allows(arg));
    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }
    try {
      IpAcl acl = new IpAcl(IpAcl.DENY);
      acl.add("0/0", true);
      String arg = "192.168.1.100";
      assertTrue("Should allow '" + arg + "'", acl.allows(arg));
    } catch (Exception ex) {
      fail("Could not construct: " + ex.getMessage());
    }
  }

}