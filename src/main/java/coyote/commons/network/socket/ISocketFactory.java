/*
 * $Id: ISocketFactory.java,v 1.2 2005/03/01 20:44:04 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.network.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Interface ISocketFactory
 */
public interface ISocketFactory {

  /**
   * Returns a socket connected to a ServerSocket at the specified network
   * address and port.
   *
   * @param hostaddress - the InetAddress to which we want to connect
   * @param port - the int representing the port on the given address
   *
   * @return Socket connected to the specified host and address
   *
   * @throws IOException if a connection could not be made
   */
  public abstract Socket createSocket( InetAddress hostaddress, int port ) throws IOException;




  /**
   * Returns a server socket which uses all network interfaces on this host, is
   * bound to a the specified port, and uses the specified connection backlog.
   *
   * @param port - the port to which we listen
   * @param backlog - how many connections are queued
   *
   * @return Socket on which we can listen()
   *
   * @throws IOException if the socket could not be created
   */
  public abstract ServerSocket createServerSocket( int port, int backlog ) throws IOException;




  /**
   * Returns a server socket which uses the given network interface on this
   * host, is bound to a the specified port, and uses the specified connection
   * backlog.
   *
   * @param port - the port to which we listen
   * @param backlog - how many connections are queued
   * @param addr - the InetAddress to which the socket is bound
   *
   * @return Socket on which we can listen()
   *
   * @throws IOException if the socket could not be created
   */
  public ServerSocket createServerSocket( int port, int backlog, InetAddress addr ) throws IOException;




  /**
   * Returns a socket (SSL, etc.) layered over an existing socket to a
   * ServerSocket on the named host, at the given port.
   *
   * <p>The host and port refer to the logical destination server. This socket
   * is configured using the socket options established for the implementing
   * factory.</p>
   *
   * @param socket - the socket connection to wrap within the SSL socket
   * @param host
   * @param port - the port on the given address
   * @param autoclose
   *
   * @return Socket connected to the specified host and address using SSL
   *
   * @throws IOException if a connection could not be made
   */
  public abstract Socket createSocket( Socket socket, String host, int port, boolean autoclose ) throws IOException;
}