/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * 
 */
public class RoleTest {

  private static Role subject = null;

  private static final String SYSADM = "SysAdm";
  private static final String DESC = "System Administrator";
  private static final String TICKET = "Ticket";
  private static final String USER = "User";




  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // Add some roles to the context
    subject = new Role(SYSADM, DESC);

    // specify the permissions for this role
    subject.addPermission(new Permission(TICKET, Permission.CREATE));
    subject.addPermission(new Permission(TICKET, Permission.READ));
    subject.addPermission(new Permission(TICKET, Permission.ASSIGN));
    subject.addPermission(new Permission(TICKET, Permission.CLOSE));

    subject.addPermission(new Permission(USER, Permission.CREATE));
    subject.addPermission(new Permission(USER, Permission.READ));
  }




  /**
   * Test method for {@link coyote.commons.security.Role#Role(java.lang.String)}.
   */
  @Test
  public void testRoleString() {
    Role role = new Role("user");
    assertNotNull(role);
  }




  /**
   * Test method for {@link coyote.commons.security.Role#Role(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testRoleStringString() {
    Role role = new Role("user", "Generic user role");
    assertNotNull(role);
  }




  /**
   * Test method for {@link coyote.commons.security.Role#getName()}.
   */
  @Test
  public void testGetName() {
    String name = subject.getName();
    assertNotNull(name);
    assertEquals(SYSADM, name);
  }




  /**
   * Test method for {@link coyote.commons.security.Role#getDescription()}.
   */
  @Test
  public void testGetDescription() {
    String desc = subject.getDescription();
    assertNotNull(desc);
    assertEquals(DESC, desc);
  }




  /**
   * Test method for {@link coyote.commons.security.Role#setDescription(java.lang.String)}.
   */
  @Test
  public void testSetDescription() {
    Role role = new Role("user", "Generic user role");
    String desc = role.getDescription();
    assertNotNull(desc);
    assertEquals(desc, "Generic user role");
  }




  /**
   * Test method for {@link coyote.commons.security.PermissionEnabledSubject#addPermission(java.lang.String, long)}.
   */
  @Test
  public void testAddPermissionStringLong() {
    Role role = new Role("user", "Generic user role");
    role.addPermission("ticket", Permission.CREATE);
    assertTrue(role.getPermissions().size() == 1);
  }




  /**
   * Test method for {@link coyote.commons.security.PermissionEnabledSubject#addPermission(coyote.commons.security.Permission)}.
   */
  @Test
  public void testAddPermissionPermission() {
    Role role = new Role("user", "Generic user role");
    role.addPermission(new Permission("ticket", Permission.CREATE));
    assertTrue(role.getPermissions().size() == 1);
  }




  /**
   * Test method for {@link coyote.commons.security.PermissionEnabledSubject#allows(java.lang.String, long)}.
   */
  @Test
  public void testAllows() {
    assertTrue(subject.allows(TICKET, Permission.CREATE));
  }




  /**
   * Test method for {@link coyote.commons.security.PermissionEnabledSubject#getPermissions()}.
   */
  @Test
  public void testGetPermissions() {
    List<Permission> perms = subject.getPermissions();
    assertNotNull(perms);
    assertTrue(perms.size() == 2);

    assertTrue(subject.allows(USER, Permission.CREATE));
    assertTrue(subject.allows(USER, Permission.READ));

  }

}
