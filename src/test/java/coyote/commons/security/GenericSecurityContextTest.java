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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * 
 */
public class GenericSecurityContextTest {

  private static SecurityContext context = null;
  private static final String ADMIN_ROLE = "admin";




  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    // Create a generic security context
    context = new GenericSecurityContext( "Demo" );

    // Add some roles to the context
    Role role = new Role( ADMIN_ROLE );

    // specify the permission for this role
    role.addPermission( new Permission( "ticket", Permission.CREATE ) );

    // add the role to the context
    context.add( role );

    // Add some logins to the context
    Login login = new Login( new GenericSecurityPrincipal( "ID:12345", "user1" ), new CredentialSet( CredentialSet.PASSWORD, "SeCr3t" ) );

    // add a role to the login
    login.addRole( role );

    // Add the login to the context
    context.add( login );
  }




  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    //context.terminate();
  }




  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {}




  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}




  /**
   * 
   */
  @Test
  public void testBasic() {

    // Make sure we have a role
    Role newRole = context.getRole( ADMIN_ROLE );
    assertNotNull( "admin role cold not be retrieved", newRole );

    // Now some login tests...

    Login login = null;

    // Now try to get a login with some invalid credentials
    login = context.getLogin( "user5", new CredentialSet( CredentialSet.PASSWORD, "MyPa55w04d" ) );
    assertNull( "Should not be able to get a login for user5", login );

    // try a valid username but slightly different password
    login = context.getLogin( "user1", new CredentialSet( CredentialSet.PASSWORD, "SeCr3T" ) );
    assertNull( "Should not be able to get a login for user1", login );

    // This should work
    login = context.getLogin( "user1", new CredentialSet( CredentialSet.PASSWORD, "SeCr3t" ) );
    assertNotNull( "user1 could not be validated", login );

    // Make sure we can get a security principal associated to this login
    SecurityPrincipal principal = login.getPrincipal();
    assertNotNull( principal );
    assertTrue( "ID:12345".equals( principal.getId() ) );
    assertTrue( "user1".equals( principal.getName() ) );

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // - -
    // Now see if the login is allowed to create a ticket
    assertTrue( "user1 should be allowed to create a ticket", context.allows( login, Permission.CREATE, "ticket" ) );

    // Now make sure the login is not allowed to delete an order
    assertFalse( "user1 should not be allowed to delete an order", context.allows( login, Permission.DELETE, "order" ) );

    // Since we have a valid login, let's create a session
    Session session = context.createSession( login );
    assertNotNull( "Could not create a session for user1", session );

    String sessionId = session.getId();

    // Now let's try to retrieve the login, by the session identifier
    Session userSession = context.getSession( sessionId );
    assertNotNull( "Could not retrieve a session for by its identifier", userSession );

    // Try to retrieve the login from an identified session
    Login userLogin = context.getLoginBySession( sessionId );
    assertNotNull( "Could not retrieve a login for by its session identifier", userLogin );

    // Try to retrieve the session for the login
    Session loginSession = context.getSession( login );
    assertNotNull( "Could not retrieve a session for by its login", loginSession );
  }

}
