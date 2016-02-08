/*
 * Copyright (c) 2002 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.network.http;

import java.io.OutputStream;

/**
 * A temporary file.
 *
 * <p>Temp files are responsible for managing the actual temporary storage and
 * cleaning themselves up when no longer needed.</p>
 */
public interface TempFile {

  public void delete() throws Exception;




  public String getName();




  public OutputStream open() throws Exception;
}