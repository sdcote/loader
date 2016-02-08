/*
 * Copyright (c) 2002 Stephan D. Cote' - All rights reserved.
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
 * Temporary file manager.
 * 
 * <p>Temp file managers are created 1-to-1 with incoming requests, to create
 * and cleanup temporary files created as a result of handling the request.</p>
 */
public interface TempFileManager {

  void clear();




  public TempFile createTempFile( String filename_hint ) throws Exception;
}