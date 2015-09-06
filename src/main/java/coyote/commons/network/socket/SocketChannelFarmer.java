/*
 * $Id: SocketChannelFarmer.java,v 1.3 2004/01/02 15:10:22 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.network.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

import coyote.commons.UriUtil;
import coyote.loader.log.Log;


/**
 * Represents an entity that does all the work of creating SocketChannels in a
 * way that allows the caller to time-out if a connection can not be made.
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.3 $
 */
public class SocketChannelFarmer implements Runnable {

  /** Socket connection to remote host */
  private volatile SocketChannel channel = null;

  /** Host to which we want to connect */
  private String host = null;

  /** Port on the host to which we want to connect */
  private int port = -1;

  /**
   * The protocol the socket channel should use. (May be http, smtp, tcp, udp,
   * etc.)
   */
  private String protocol = null;

  /** IOException in the event a connection error occurs */
  private volatile IOException xcptn = null;

  /** Polling delay for socket checks (in milliseconds) */
  private static final int POLL_DELAY = 250;




  /**
   * Constructor SocketChannelFarmer
   *
   * @param uri
   */
  public SocketChannelFarmer( URI uri ) {
    protocol = uri.getScheme();
    host = uri.getHost();
    port = uri.getPort();

    if ( ( host == null ) || ( host.length() == 0 ) ) {
      throw new IllegalArgumentException( "No host defined in URI" );
    }

    // If no protocol was specified, default to generic TCP
    if ( ( protocol == null ) || ( protocol.length() == 0 ) ) {
      protocol = "tcp";
    }

    // If the port is missing, try to determine a well-known port from UriUtil
    if ( port < 1 ) {
      port = UriUtil.getPort( protocol );
    }

    // If we still do not have a port throw an exception
    if ( port < 1 ) {
      throw new IllegalArgumentException( "Could not determine port for URI" );
    }
  }




 /**
  *
  * @param address
  *
  * @return
  */
  public static InetAddress resolveAddress( String address ) {
    try {
      return InetAddress.getByName( address );
    } catch ( UnknownHostException e ) {
      // System.err.println( "NetUtil.resolveAddress(String) Could not resolve \"" + address + "\":\n" );
    }
    return null;
  }




  /**
   * Method run
   */
  public void run() {
    SocketChannel conn = null;

    try {
      // We do this ourselves so the connection time reflects only the actual
      // socket connection time and not all the URI parsing, factory lookup and
      // DNS activities which usually takes 20ms by themselves.
      InetAddress addr = resolveAddress( host );

      // Mark the time we started opening the socket
      long started = System.currentTimeMillis();

      // make sure we have an address
      if ( addr == null ) {
        Log.error( "IP address for '" + host + "' was NULL: could not resolve host" );
      }

      // Open a socket - Here is where we block for whatever length of time!
      Socket socket = new Socket( addr, port );

      // Mark the time we ended opening the socket;
      long ended = System.currentTimeMillis();

      // Create the SocketChannel with the given socket and protocol scheme
      conn = new SocketChannel( socket, protocol );

      // Set the time the connection was established in the return value
      conn.connectedTime = ended;

      // Set how long it took to make the connection
      conn.connectionTime = ended - started;

      // Assign the fruits of our labor to the object attribute
      channel = conn;
    } catch ( IOException ioe ) {
      // Assign to our exception object attribute
      xcptn = ioe;
    }

    // Whew! We are done
  }




  /**
   * Return if the socket is connected
   *
   * @return
   */
  public boolean isConnected() {
    if ( channel == null ) {
      return false;
    } else {
      return true;
    }
  }




  /**
   * Return if there was an error in the connection process
   *
   * @return
   */
  public boolean isError() {
    if ( xcptn == null ) {
      return false;
    } else {
      return true;
    }
  }




  /**
   * Get a SocketChannel
   *
   * @param timeout
   *
   * @return the SocketChannel if a connection could be made within the time-out
   *         period, null if there is no connection in that time
   *
   * @throws IOException
   */
  public SocketChannel getSocketChannel( long timeout ) throws IOException {
    // Allows the option to wait practically forever!
    if ( timeout < 1 ) {
      timeout = Long.MAX_VALUE;
    }

    long timer = 0;

    // continually check until we time-out
    for ( ;; ) {
      // Check to see if a connection is established
      if ( isConnected() ) {
        return getSocketChannel();
      } else {
        // Check to see if an error occurred
        if ( isError() ) {
          // No connection could be established
          throw ( getException() );
        }

        try {
          // Sleep for a short period of time
          Thread.sleep( POLL_DELAY );
        } catch ( InterruptedException ie ) {
          // ignore normal interrutption
        }

        // Increment timer
        timer += POLL_DELAY;

        // Check to see if time limit exceeded
        if ( timer > timeout ) {
          return null;
        }
      }
    }

  }




  /**
   * Method getSocketChannel
   *
   * @return
   */
  private SocketChannel getSocketChannel() {
    synchronized( channel ) {
      return channel;
    }
  }




  /**
   * Get exception
   *
   * @return
   */
  public IOException getException() {
    return xcptn;
  }
}