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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import coyote.loader.log.Log;


/**
 * The runnable that will be used for the main listening thread.
 */
public class ServerRunnable implements Runnable {

  private final HTTPD httpd;

  private final int timeout;

  IOException bindException;

  boolean hasBinded = false;




  ServerRunnable( HTTPD httpd, final int timeout ) {
    this.httpd = httpd;
    this.timeout = timeout;
  }




  @Override
  public void run() {
    try {
      this.httpd.myServerSocket.bind( this.httpd.hostname != null ? new InetSocketAddress( this.httpd.hostname, this.httpd.myPort ) : new InetSocketAddress( this.httpd.myPort ) );
      hasBinded = true;
    } catch ( final IOException e ) {
      bindException = e;
      return;
    }
    do {
      try {
        final Socket finalAccept = this.httpd.myServerSocket.accept();
        if ( timeout > 0 ) {
          finalAccept.setSoTimeout( timeout );
        }
        final InputStream inputStream = finalAccept.getInputStream();
        this.httpd.asyncRunner.exec( this.httpd.createClientHandler( finalAccept, inputStream ) );
      } catch ( final IOException e ) {
        Log.append( HTTPD.EVENT, "WARNING: Communication with the client broken", e );
      }
    }
    while ( !this.httpd.myServerSocket.isClosed() );
  }
}