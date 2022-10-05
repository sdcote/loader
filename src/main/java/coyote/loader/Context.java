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
package coyote.loader;

import java.util.List;
import java.util.Set;

import coyote.commons.template.SymbolTable;


/**
 * Represent a shared operational context multiple components in the runtime 
 * can use to share information.
 * 
 * <p><em>Note:</em> it is common practice for components to enter a {@code 
 * wait()} on a context while waiting for other components to populate 
 * references in the context. The populating components then issue a (@code 
 * notifyAll()} on the context and allow other waiting threads to re-check the 
 * context for the shared references they need. This is not required but simply 
 * common practice which can be considered in your designs.</p>
 */
public interface Context {

  void addListener(ContextListener listener);




  /**
   * Perform a check to see if the context contains an object mapped to the 
   * given key, case sensitive.
   * 
   * @param key The key of the object to check
   * 
   * @return true if there is an object mapped to this key, false otherwise 
   */
  boolean contains(String key);




  /**
   * @return a dump of all the properties and symbols in this context.
   */
  String dump();




  /**
   * Set the end time to now.
   */
  void end();




  /**
   * Return the object from this context with the given (case sensitive) key.
   * 
   * @param key the name of the object to return
   * 
   * @return the object with that name or null if the named object is not found
   */
  Object get(String key);




  /**
   * Performs a search for a property with the given name taking case into 
   * account.
   * 
   * @param key the key for which to search
   * @param usecase true to indicate case sensitive, false to perform a case 
   *        insensitive search.
   * 
   * @return the value of the property, or null if the property was not found 
   *         with the given key of if the key was null or blank.
   */
  Object get(String key, boolean usecase);




  /**
   * Return the string representation of the object from this context with the 
   * given key.
   * 
   * <p>This is a convenience method for calling {@code toString()} on the 
   * returned value after having checked for null.</p>
   * 
   * @param key the name of the object to return
   * 
   * @return the string representation of the object with that name or null if 
   *         the named object is not found
   */
  String getAsString(String key);




  /**
   * Return the string representation of the object from this context with the 
   * given key taking case into account.
   * 
   * <p>This is a convenience method for calling {@code toString()} on the 
   * returned value after having checked for null.</p>
   * 
   * @param key the name of the object to return
   * @param usecase true to perform a case sensitive search, false to perform a 
   *        case insensitive search.
   * 
   * @return the string representation of the object with that name or null if 
   *         the named object is not found
   */
  String getAsString(String key, boolean usecase);




  /**
   * @return the elapsed time in milliseconds from start to end (or now) or 0 if not started. 
   */
  long getElapsed();




  /**
   * @return the endTime
   */
  long getEndTime();




  /**
   * @return the message
   */
  String getMessage();




  /**
   * @return the parent context, may be null.
   */
  AbstractContext getParent();




  /**
   * @return the startTime
   */
  long getStartTime();




  /**
   * @return the status
   */
  String getStatus();




  /**
   * @return the symbolTable for this context
   */
  SymbolTable getSymbols();




  boolean isInError();




  /**
   * Accessor to check if the context is not in error (i.e. fine).
   * 
   * <p>This is a convenience method to make code more readable</p>
   * 
   * @return true if the context is fine (without error) false if there is a problem.
   */
  boolean isNotInError();




  /**
   * Place an object in this context with the given key
   * 
   * @param key the name of the object to place
   * @param value the object to place (null results in the object being removed)
   */
  void set(String key, Object value);




  /**
   * @param endTime the endTime to set
   */
  void setEndTime(long endTime);




  void setError(boolean flag);




  /**
   * Set the context in an error state with the given message.
   * 
   * @param msg The message to place in the context.
   */
  void setError(String msg);




  void setListeners(List<ContextListener> listeners);




  /**
   * @param message the message to set
   */
  void setMessage(String message);




  /**
   * @param context the parent context to set
   */
  void setParent(AbstractContext context);




  /**
   * @param startTime the startTime to set
   */
  void setStartTime(long startTime);




  /**
   * @param status the status to set
   */
  void setStatus(String status);




  /**
   * @param symbols the symbols to set in this context
   */
  void setSymbols(SymbolTable symbols);




  /**
   * Set the start time to now.
   */
  void start();

  
  

  /**
   * Access this context key set
   * 
   * @return a set of keys to access the contents of this context.
   */
  Set<String> getKeys();


  /**
   * @return a list of listeners for this context.
   */
  List<ContextListener> getListeners();
}
