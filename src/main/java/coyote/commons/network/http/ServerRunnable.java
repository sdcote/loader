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
        final Socket clientSocket = this.httpd.myServerSocket.accept();
        if ( timeout > 0 ) {
          clientSocket.setSoTimeout( timeout );
        }

        // Log.append( HTTPD.EVENT, "Connection from " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort() + " to port " + clientSocket.getLocalPort() + " of " + clientSocket.getLocalAddress() );

        // First check if the address has been calling us too frequently 
        // indicating a possible denial of service attack
        if ( this.httpd.dosTable.check( clientSocket.getInetAddress() ) ) {
          // Allow only connections from the local host or from remote hosts on 
          // our ACL
          if ( clientSocket.getLocalAddress().equals( clientSocket.getInetAddress() ) || this.httpd.acl.allows( clientSocket.getInetAddress() ) ) {
            final InputStream inputStream = clientSocket.getInputStream();
            this.httpd.asyncRunner.exec( this.httpd.createClientHandler( clientSocket, inputStream ) );
          } else {
            Log.append( HTTPD.EVENT, "Remote connection from " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort() + " refused due to ACL restrictions" );
            HTTPD.safeClose( clientSocket );
          }
        } else {
          Log.append( HTTPD.EVENT, "Remote connection from " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort() + " refused due to possible Denial of Service activity" );
          HTTPD.safeClose( clientSocket );
        }
      } catch ( final IOException e ) {
        Log.append( HTTPD.EVENT, "WARNING: Communication with the client broken", e );
      }
    }
    while ( !this.httpd.myServerSocket.isClosed() );
  }
}