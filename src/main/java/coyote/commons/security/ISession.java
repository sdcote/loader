/*
 * $Id: ISession.java,v 1.2 2004/01/02 15:10:14 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.security;

import java.util.Iterator;


/**
 * A session is an instance of a login that has been validated through 
 * credentials.
 */
public interface ISession {

  /**
   * Method expired
   *
   * @return
   */
  public abstract boolean expired();




  /**
   * Method invalidate
   *
   * @throws IllegalStateException
   */
  public abstract void invalidate() throws IllegalStateException;




  /**
   * Method isNew
   *
   * @return
   *
   * @throws IllegalStateException
   */
  public abstract boolean isNew() throws IllegalStateException;




  /**
   * Method getCreationTime
   *
   * @return
   *
   * @throws IllegalStateException
   */
  public abstract long getCreationTime() throws IllegalStateException;




  /**
   * Method getLastAccessedTime
   *
   * @return
   */
  public abstract long getLastAccessedTime();




  /**
   * Method accessed
   */
  public abstract void accessed();




  /**
   * Method getId
   *
   * @return
   */
  public abstract String getId();




  /**
   * Method setTimeout
   *
   * @param i
   */
  public abstract void setTimeout( int i );




  /**
   * Method getTimeout
   *
   * @return
   */
  public abstract int getTimeout();




  /**
   * Method getAttribute
   *
   * @param name
   *
   * @return
   *
   * @throws IllegalStateException
   */
  public abstract Object getAttributeValue( String name ) throws IllegalStateException;




  /**
   * Method getAttributeNames
   *
   * @return
   *
   * @throws IllegalStateException
   */
  public abstract Iterator getAttributeNameIterator() throws IllegalStateException;




  /**
   * Method setAttribute
   *
   * @param name
   * @param value
   *
   * @throws IllegalStateException
   */
  public abstract void setAttribute( String name, Object value ) throws IllegalStateException;




  /**
   * Method removeAttribute
   *
   * @param name
   *
   * @return
   *
   * @throws IllegalStateException
   */
  public abstract Object removeAttribute( String name ) throws IllegalStateException;

}