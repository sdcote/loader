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
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * 
 */
public class PermissionTest {

  /**
   *
   */
  @Test
  public void testDemo() {

    Permission perm = new Permission( "test", Permission.UPDATE );
    System.out.println( "Basic permission: " + perm );
    assertTrue( perm.allows( Permission.UPDATE ) );
    assertFalse( perm.allows( Permission.READ ) );
    assertFalse( perm.allows( Permission.EXECUTE ) );

    // Create a permission that allows reading and updating on the "test" object
    perm = new Permission( "test", Permission.READ | Permission.UPDATE );
    System.out.println( "OR'd permission: " + perm );

    assertTrue( perm.allows( Permission.UPDATE ) );
    assertTrue( perm.allows( Permission.READ ) );
    assertFalse( perm.allows( Permission.EXECUTE ) );

    // Use the add to effectively OR the permission
    perm.addAction( Permission.EXECUTE );
    System.out.println( "added permission: " + perm );
    assertTrue( perm.allows( Permission.UPDATE ) );
    assertTrue( perm.allows( Permission.READ ) );
    assertTrue( perm.allows( Permission.EXECUTE ) );
  }




  @Test
  public void testRevoke() {

    Permission perm = new Permission( "test", Permission.UPDATE );
    perm = new Permission( "test", Permission.READ | Permission.UPDATE );
    perm.addAction( Permission.EXECUTE );

    assertTrue( perm.allows( Permission.UPDATE ) );
    assertTrue( perm.allows( Permission.READ ) );
    assertTrue( perm.allows( Permission.EXECUTE ) );

    perm.revokeAction( Permission.READ );
    assertTrue( perm.allows( Permission.UPDATE ) );
    assertFalse( perm.allows( Permission.READ ) );
    assertTrue( perm.allows( Permission.EXECUTE ) );

  }

}
