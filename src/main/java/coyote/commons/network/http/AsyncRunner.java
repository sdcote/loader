/*
 * Copyright (c) 2003 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 */
package coyote.commons.network.http;


/**
 * Pluggable strategy for asynchronously executing requests.
 */
public interface AsyncRunner {

  void closeAll();




  void closed( ClientHandler clientHandler );




  void exec( ClientHandler code );
}