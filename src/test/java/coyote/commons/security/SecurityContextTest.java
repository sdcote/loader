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
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * 
 */
public class SecurityContextTest {

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
   * 
   */
  @Test
  public void testGenericContext() {

    Login login = null;

    // the most common use case: username with a set of credentials
    login = context.getLogin( "user1", new CredentialSet( CredentialSet.PASSWORD, "SeCr3t" ) );
    System.out.println( login );
  }

}
