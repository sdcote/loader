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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import coyote.loader.log.Log;

/**
 * Default strategy for creating and cleaning up temporary files.
 * 
 * <p>This class stores its files in the standard location (that is, wherever
 * {@code java.io.tmpdir} points to). Files are added to an internal list, and 
 * deleted when no longer needed; when {@code clear()} is invoked at the end of 
 * processing a request).</p>
 */
public class DefaultTempFileManager implements TempFileManager {

  private final File tmpdir;

  private final List<TempFile> tempFiles;




  public DefaultTempFileManager() {
    tmpdir = new File( System.getProperty( "java.io.tmpdir" ) );
    if ( !tmpdir.exists() ) {
      tmpdir.mkdirs();
    }
    tempFiles = new ArrayList<TempFile>();
  }




  @Override
  public void clear() {
    for ( final TempFile file : tempFiles ) {
      try {
        file.delete();
      } catch ( final Exception ignored ) {
        Log.append( HTTPD.EVENT, "WARNING: Could not delete file ", ignored );
      }
    }
    tempFiles.clear();
  }




  @Override
  public TempFile createTempFile( final String filename_hint ) throws Exception {
    final DefaultTempFile tempFile = new DefaultTempFile( tmpdir );
    tempFiles.add( tempFile );
    return tempFile;
  }
  
}