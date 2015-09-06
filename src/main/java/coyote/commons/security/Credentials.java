/*
 * $Id: Credentials.java,v 1.2 2004/01/02 15:10:15 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.security;

/**
 * Models a set of information used in authentication operations.
 *
 * <p>It is possible for a user to have multiple account/password sets,
 * identifying them as different entities or principals with varying roles in
 * the system. This is why the term account is used in this context.</p>
 */
public class Credentials {

  /** The account name */
  private String account;

  /** The authenticating data */
  private String password;




  /**
   * Constructor Credentials
   */
  public Credentials() {}




  /**
   * Constructor Credentials
   *
   * @param act The name of the account
   * @param psswd The authenticating data
   */
  public Credentials( String act, String psswd ) {
    account = act;
    password = psswd;
  }




  /**
   * Check if the credentials matxh
   *
   * @param credentials the credentials against which the check is to be made
   *
   * @return true if the credential match, false otherwise.
   */
  public boolean equals( Object credentials ) {
    if ( credentials != null ) {
      if ( credentials instanceof Credentials ) {
        Credentials obj = (Credentials)credentials;

        if ( ( this.account != null ) || ( ( this.account == null ) && ( obj.account == null ) ) ) {
          if ( obj.account != null ) {
            if ( this.account.equals( obj.account ) ) {
              // account names match, now check passwords
              if ( this.password != null ) {
                if ( obj.password != null ) {
                  // return if the passwords match
                  return this.password.equals( obj.password );
                } else {
                  // We have a password, but the passed credential does not
                  return false;
                }
              } else {
                // we don't have a password, so the passed credential should not
                return ( obj.password == null );
              }
            } else {
              // account names do not match
              return false;
            }
          } else {
            // Our account is not null and their account IS null
            return false;
          }
        } else {
          // our account is null or both accounts were null
          return false;
        }
      }
    }

    // Null credential
    return false;
  }




  /**
   * @return the account name
   */
  public String getAccount() {
    return account;
  }




  /**
   * Set the account name
   *
   * @param account the account name to set
   */
  public void setAccount( String account ) {
    this.account = account;
  }




  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }




  /**
   * Set the password
   *
   * @param password the password to set.
   */
  public void setPassword( String password ) {
    this.password = password;
  }
}