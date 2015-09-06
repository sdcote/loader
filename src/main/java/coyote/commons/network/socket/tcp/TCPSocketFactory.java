/*
 * $Id: TCPSocketFactory.java,v 1.2 2005/03/01 20:44:08 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.network.socket.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;

import coyote.commons.network.socket.ISocketFactory;


/**
 * Simple Socket factory.
 *
 * <p>Uses the javax.net.ServerSocketFactory and SocketFactory to do most (if
 * not all) of the work.</p>
 *
 * @todo Add more configuration options
 */
public class TCPSocketFactory implements ISocketFactory {

  /** The socketfactory we use to get our basic sockets */
  private SocketFactory socketFactory;

  /** The ServerSocketFactory we use to get our ServerSockets */
  private ServerSocketFactory serverSocketFactory;




  /**
   * Constructor TCPSocketFactory
   */
  public TCPSocketFactory() {
    // Get a reference to the JVM's socket factory
    socketFactory = SocketFactory.getDefault();

    // get a reference to the JVM's ServerSocket factory
    serverSocketFactory = ServerSocketFactory.getDefault();
  }




  /**
   * Returns null since there is nothing in which to wrap the socket.
   *
   * @param socket The socket connection to wrap
   * @param host
   * @param port The port on the given address
   * @param autoclose
   *
   * @return null
   *
   * @throws IOException if a connection could not be made
   */
  public Socket createSocket( Socket socket, String host, int port, boolean autoclose ) throws IOException {
    return null;
  }




  /**
   * Returns a TCP socket connected to a ServerSocket at the specified network
   * address and port.
   *
   * <p>This socket is configured using the socket options established for the
   * default factory.</p>
   *
   * @param hostaddress
   * @param port The port on the given address
   *
   * @return Socket connected to the specified host and address
   *
   * @throws IOException if a connection could not be made
   */
  public Socket createSocket( InetAddress hostaddress, int port ) throws IOException {
    return socketFactory.createSocket( hostaddress, port );
  }




  /**
   * Returns a server socket which uses all network interfaces on this host, is
   * bound to a the specified port, and uses the specified connection backlog.
   *
   * <p>The socket is configured with the default socket options (such as accept
   * timeout)</p>
   *
   * @param port The port to which we listen
   * @param backlog How many connections are queued
   *
   * @return Socket on which we can listen()
   *
   * @throws IOException if the socket could not be created
   */
  public ServerSocket createServerSocket( int port, int backlog ) throws IOException {
    return serverSocketFactory.createServerSocket( port, backlog );
  }




  /**
   * Returns a server socket which uses the given network interface on this
   * host, is bound to a the specified port, and uses the specified connection
   * backlog.
   *
   * <p>The socket is configured with the default socket options (such as accept
   * timeout)</p>
   *
   * @param port The port to which we listen
   * @param backlog How many connections are queued
   * @param addr The InetAddress to which the socket is bound
   *
   * @return Socket on which we can listen()
   *
   * @throws IOException if the socket could not be created
   */
  public ServerSocket createServerSocket( int port, int backlog, InetAddress addr ) throws IOException {
    return serverSocketFactory.createServerSocket( port, backlog, addr );
  }
}