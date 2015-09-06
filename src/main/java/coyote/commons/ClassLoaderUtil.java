/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;


/**
 * 
 */
public class ClassLoaderUtil {

  private static ClassLoader classLoader;




  /**
   * Method getClassLoader
   *
   * @return
   */
  public static ClassLoader getClassLoader() {
    return classLoader;
  }




  /**
   * Method setClassLoader
   *
   * @param classloader
   */
  public static void setClassLoader( ClassLoader classloader ) {
    classLoader = classloader;
  }




  /**
   * Method loadClass
   *
   * @param s
   *
   * @return
   *
   * @throws ClassNotFoundException
   */
  public static Class loadClass( String s ) throws ClassNotFoundException {
    if ( s.startsWith( "[" ) ) {
      return Array.newInstance( loadClass( s.substring( 1 ) ), 0 ).getClass();
    }

    if ( s.endsWith( ";" ) ) {
      return loadClass( s.substring( 1, s.length() - 1 ) );
    } else {
      return ( classLoader == null ) ? Class.forName( s ) : classLoader.loadClass( s );
    }
  }




  /**
   * Method loadResource
   *
   * @param s
   *
   * @return
   *
   * @throws IOException
   */
  public static byte[] loadResource( String s ) throws IOException {
    InputStream inputstream = null;

    if ( s.startsWith( "file:/" ) ) {
      String s1 = s.substring( 6 );

      if ( classLoader != null ) {
        if ( s1.startsWith( "/" ) ) {
          s1 = s1.substring( 1 );
        }

        inputstream = classLoader.getResourceAsStream( s1 );
      } else {
        inputstream = ( coyote.commons.ClassLoaderUtil.class ).getResourceAsStream( s1 );
      }
    }

    if ( inputstream == null ) {
      inputstream = ( new URL( s ) ).openStream();
    }

    byte[] classdata = StreamUtil.readFully( inputstream );
    inputstream.close();

    return classdata;
  }

}
