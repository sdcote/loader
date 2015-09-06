/*
 * $Id: SSLSocketFactory.java,v 1.2 2005/03/01 20:44:08 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.network.socket.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Random;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.sun.net.ssl.internal.ssl.Provider;

import coyote.commons.network.socket.ISocketFactory;


/**
 * Class SSLSocketFactory
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.2 $
 */
public class SSLSocketFactory implements ISocketFactory {

  /** Field DEFAULT_PASSWORD */
  static final String DEFAULT_PASSWORD = "bralyn";

  /** Field regularRandom */
  static final Random regularRandom = new Random();

  /** Field home */
  String home;

  /** Field trustStore */
  String trustStore;

  /** Field trustStorePassword */
  String trustStorePassword;

  /** Field socketFactory */
  javax.net.ssl.SSLSocketFactory socketFactory;

  /** Field serverSocketFactory */
  SSLServerSocketFactory serverSocketFactory;




  /**
   * Default constructor
   *
   * @throws Exception
   */
  public SSLSocketFactory() throws Exception {
    try {
      initialize();
    } catch ( Exception exception ) {
      System.out.println( "SSL startup exception" );
      System.out.println( "  java.home = ".concat( String.valueOf( home ) ) );
      System.out.println( "  javax.net.ssl.trustStore = ".concat( String.valueOf( trustStore ) ) );
      System.out.println( "  javax.net.ssl.trustStorePassword = ".concat( String.valueOf( trustStorePassword ) ) );

      throw exception;
    }
  }




  /**
   * Initialize the SSL keystore and socket factories
   *
   * @throws Exception
   */
  void initialize() throws Exception {
    trustStore = System.getProperty( "javax.net.ssl.trustStore" );
    home = System.getProperty( "java.home" );

    if ( trustStore == null ) {
      trustStore = String.valueOf( ( new StringBuffer( String.valueOf( home ) ) ).append( File.separator ).append( "lib" ).append( File.separator ).append( "security" ).append( File.separator ).append( "cacerts" ) );
    }

    trustStorePassword = System.getProperty( "javax.net.ssl.trustStorePassword" );

    if ( trustStorePassword == null ) {
      trustStorePassword = DEFAULT_PASSWORD;
    }

    char tspChars[] = trustStorePassword.toCharArray();

    Security.addProvider( new Provider() );

    SecureRandom securerandom = new SecureRandom();
    securerandom.setSeed( regularRandom.nextLong() );

    KeyManagerFactory keymanagerfactory = KeyManagerFactory.getInstance( "SunX509" );
    KeyStore keystore = KeyStore.getInstance( "JKS" );
    keystore.load( new FileInputStream( trustStore ), tspChars );
    keymanagerfactory.init( keystore, tspChars );

    SSLContext sslcontext = SSLContext.getInstance( "SSL" );
    sslcontext.init( keymanagerfactory.getKeyManagers(), null, securerandom );

    socketFactory = sslcontext.getSocketFactory();
    serverSocketFactory = sslcontext.getServerSocketFactory();
  }




  /**
   * Returns a socket layered over an existing socket to a ServerSocket on the
   * named host, at the given port.
   *
   * <p>This method can be used when tunneling SSL through a proxy.</p>
   *
   * <p>The host and port refer to the logical destination server. This socket
   * is configured using the socket options established for this factory.</p>
   *
   * <p>This socket is configured using the socket options established for the
   * default factory.</p>
   *
   * @param socket the socket connection to wrap within the SSL socket
   * @param host
   * @param port the port on the given address
   * @param autoclose
   *
   * @return Socket connected to the specified host and address using SSL
   *
   * @throws IOException if a connection could not be made
   */
  public Socket createSocket( Socket socket, String host, int port, boolean autoclose ) throws IOException {
    SSLSocket sslsocket = (SSLSocket)socketFactory.createSocket( socket, host, port, autoclose );
    sslsocket.startHandshake();

    return sslsocket;
  }




  /**
   * Returns a SSL socket connected to a ServerSocket at the specified network
   * address and port.
   *
   * <p>This socket is configured using the socket options established for the
   * default factory.</p>
   *
   * @param addr
   * @param port
   *
   * @return Socket connected to the specified host and address
   *
   * @throws IOException if a connection could not be made
   */
  public Socket createSocket( InetAddress addr, int port ) throws IOException {
    return socketFactory.createSocket( addr, port );
  }




  /**
   * Returns a SSL server socket which uses all network interfaces on this host,
   * is bound to a the specified port, and uses the specified connection backlog.
   *
   * <p>The socket is configured with the default socket options (such as accept
   * timeout)</p>
   *
   * @param port
   * @param backlog
   *
   * @return Socket on which we can listen()
   *
   * @throws IOException if the socket could not be created
   */
  public ServerSocket createServerSocket( int port, int backlog ) throws IOException {
    return serverSocketFactory.createServerSocket( port, backlog );
  }




  /**
   * Returns a SSL server socket which uses the given network interface on this
   * host, is bound to a the specified port, and uses the specified connection
   * backlog.
   *
   * <p>The socket is configured with the default socket options (such as accept
   * timeout)</p>
   *
   * @param port - the port to which we listen
   * @param backlog - how many connections are queued
   * @param addr - the InetAddress to which the socket is bound
   *
   * @return Socket on which we can listen()
   *
   * @throws IOException if the socket could not be created
   */
  public ServerSocket createServerSocket( int port, int backlog, InetAddress addr ) throws IOException {
    return serverSocketFactory.createServerSocket( port, backlog, addr );
  }
}