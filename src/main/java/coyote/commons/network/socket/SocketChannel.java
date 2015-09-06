/*
 * $Id: SocketChannel.java,v 1.4 2005/03/01 20:44:04 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.network.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import coyote.commons.ClassLoaderUtil;
import coyote.commons.ExceptionUtil;
import coyote.commons.StreamUtil;
import coyote.commons.UriUtil;
import coyote.commons.network.IChannel;
import coyote.commons.network.IChannelHandler;
import coyote.loader.log.Log;


/**
 * SocketChannel is an IChannel that represents a socket connection.
 *
 * <p>Keeps a map of SocketFactories by protocol</p>
 */
public final class SocketChannel implements Runnable, IChannel {

  /** Field DEFAULT_BACKLOG */
  public static final int DEFAULT_BACKLOG = 50;

  /** Field protocolToFactoryClass */
  static final Hashtable protocolToFactoryClass = new Hashtable();

  /** Field protocolToFactory */
  static final Hashtable protocolToFactory = new Hashtable();

  /**
   * The SocketServer that created us and probably has a reference to a
   * IChannelSink that knows how to handle the session
   */
  SocketServer server;

  /** The socket on which we do our IO */
  Socket socket;

  /** The input stream we use to read from the socket */
  BufferedInputStream input;

  /** The output stream we use to write to the socket */
  BufferedOutputStream output;

  /** The URI of our local connection (what our peer sees as the remote URI) */
  URI localURI;

  /** The URI of the peer to which we are (or was) connected */
  URI remoteURI;

  /** The time in milliseconds it took to create a connection to the peer */
  public long connectionTime;

  /** The Date in Brady Bunch format when the channel was connected */
  public long connectedTime;

  /** The optional IChannelHandler that we are to run to service this instance */
  IChannelHandler channelHandler = null;

  static {
    addFactory( "tcp", "net.bralyn.network.socket.tcp.TCPSocketFactory" );
    addFactory( "ssl", "net.bralyn.network.socket.ssl.SSLSocketFactory" );
  }




  /**
   * Constructor SocketChannel
   *
   * @param socket
   * @param protocol
   *
   * @throws IOException
   */
  public SocketChannel( Socket socket, String protocol ) throws IOException {
    this.socket = socket;
    input = new BufferedInputStream( socket.getInputStream() );
    output = new BufferedOutputStream( socket.getOutputStream() );
    localURI = asURI( protocol, socket.getLocalAddress(), socket.getLocalPort() );
    remoteURI = asURI( protocol, socket.getInetAddress(), socket.getPort() );
  }




  /**
   * Constructor SocketChannel
   *
   * @param socket
   * @param protocol
   * @param srvr
   *
   * @throws IOException
   */
  public SocketChannel( Socket socket, String protocol, SocketServer srvr ) throws IOException {
    server = srvr;
    this.socket = socket;
    input = new BufferedInputStream( socket.getInputStream() );
    output = new BufferedOutputStream( socket.getOutputStream() );
    localURI = asURI( protocol, socket.getLocalAddress(), socket.getLocalPort() );
    remoteURI = asURI( protocol, socket.getInetAddress(), socket.getPort() );
  }




  /**
   * Tests to see of the socket is open, or has been closed by the remote peer.
   *
   * <p>This is to be used in Java 1.3 VMs as there are no reliable ways to
   * test for a closed connection in a single-threaded manner. In Java 1.4,
   * there are several new methods to answer this problem.</p>
   *
   * <p>This does not work with all IP stacks as some sockets will stay open
   * locally, even though the remote end has closed the connection.</p>
   *
   * @return True if the socket is open, False if the socket was closed by the
   *         peer (or us).
   */
  public boolean isOpen() {
    int available;

    try {
      available = input.available();

      // workaround since available() does not throw IOException if socket has
      // been closed by peer
      if ( available == 0 ) {
        // save the socket timeout
        int oldTimeOut = socket.getSoTimeout();

        // Set IO timeout to 1 millisecond
        socket.setSoTimeout( 1 );

        // set our read limit to 2 octets
        input.mark( 2 );

        try {
          // Try a read to see if -1 (meaning a closed stream) is returned
          available = input.read();

          // Reset the stream
          input.reset();
        } catch ( InterruptedIOException ex ) {
          // Timeout is OK, nothing in stream
        }

        // reset the socket timeout
        socket.setSoTimeout( oldTimeOut );
      }
    } catch ( Exception e ) {
      return false;
    }

    // as long as we are not -1 we are open for business
    return ( available >= 0 );
  }




  /**
   * Method toString
   *
   * @return
   */
  public String toString() {
    return new String( "SocketChannel( " + localURI + "->" + remoteURI + " )" );
  }




  /**
   * Adds a SocketFactory for a specified protocol
   *
   * <p>For example:<br><code>addFactory(&quot;tcp&quot;,
   * &quot;net.bralyn.network.TCPSocketFactory&quot;);</code><br> will add the
   * &quot;net.bralyn.network.TCPSocketFactory&quot; class to the SocketChannel
   * and any calls to create a &quot;tcp&quot; socket will use that factory to
   * generate the socket</p>
   *
   * @param protocol
   *
   * @param classname
   */
  public static void addFactory( String protocol, String classname ) {
    protocolToFactoryClass.put( protocol, classname );
  }




  /**
   * Method addFactory
   *
   * @param protocol
   * @param factory
   */
  public static synchronized void addFactory( String protocol, ISocketFactory factory ) {
    protocolToFactory.put( protocol, factory );
  }




  /**
   * Method removeFactory
   *
   * @param protocol
   */
  public static void removeFactory( String protocol ) {
    protocolToFactory.remove( protocol );
  }




  /**
   * Method getFactory
   *
   * @param protocol
   *
   * @return
   */
  public static synchronized ISocketFactory getFactory( String protocol ) {
    ISocketFactory isocketfactory = (ISocketFactory)protocolToFactory.get( protocol );

    if ( isocketfactory != null ) {
      return isocketfactory;
    }

    // Could not find a factory in the cache, get the class name based on the
    // protocol so we can create a new factory.
    String classname = (String)protocolToFactoryClass.get( protocol );

    // If we could not find the proper factory class name
    if ( classname == null ) {
      // get the TCP socket factory as a default
      classname = (String)protocolToFactoryClass.get( "tcp" );
    }

    // Mak sure we have at least a TCP socket factory
    if ( classname == null ) {
      // Jeese! we're worthless!
      return null;
    }

    // Create an instance of the socket factory
    try {
      Class clazz = ClassLoaderUtil.loadClass( classname );
      ISocketFactory factory = (ISocketFactory)clazz.newInstance();
      addFactory( protocol, factory );

      // return it to the requestor
      return factory;
    } catch ( Exception ex ) {
      ex.printStackTrace();
    }

    return null;
  }




  /**
   * Convenience method to create a URI out of a scheme, InetAddress and port.
   *
   * @param scheme
   * @param inetaddress
   * @param port
   *
   * @return
   */
  public static URI asURI( String scheme, InetAddress inetaddress, int port ) {
    try {
      return new URI( scheme + "://" + inetaddress.getHostAddress() + ":" + port );
    } catch ( URISyntaxException e ) {
      // e.printStackTrace();
    }

    return null;
  }




  /**
   * Method getInputStream
   *
   * @return
   *
   * @throws IOException
   */
  public InputStream getInputStream() throws IOException {
    return input;
  }




  /**
   * Method getOutputStream
   *
   * @return
   *
   * @throws IOException
   */
  public OutputStream getOutputStream() throws IOException {
    return output;
  }




  /**
   * Method setTimeout
   *
   * @param i
   *
   * @throws IOException
   */
  public void setTimeout( int i ) throws IOException {
    socket.setSoTimeout( i );
  }




  /**
   * Peek into the inputstream
   *
   * @param i
   *
   * @return an array of bytes representing the next bytes the will be read from the input stream.
   *
   * @throws IOException if there is a problem with reading the channel
   */
  public byte[] peek( int i ) throws IOException {
    input.mark( i );

    byte abyte0[] = StreamUtil.readFully( input, i );
    input.reset();

    return abyte0;
  }




  /**
   * Method close
   *
   * @throws IOException
   */
  public void close() throws IOException {
    // Close the input
    try {
      input.close();
    } catch ( Exception exception ) {}
    finally {}

    // Close the output
    try {
      output.close();
    } catch ( Exception exception ) {}
    finally {}

    // Close the socket
    try {
      socket.close();
    } catch ( Exception exception ) {}
    finally {}
  }




  /**
   * Method getLocalURI
   *
   * @return
   */
  public URI getLocalURI() {
    return localURI;
  }




  /**
   * Method getRemoteURI
   *
   * @return
   */
  public URI getRemoteURI() {
    return remoteURI;
  }




  /**
   * Create a socket connected to the given URI.
   *
   * <p>This is the main way we create sockets.</p>
   *
   * @param uri
   *
   * @return
   *
   * @throws IOException
   */
  public static Socket createSocket( URI uri ) throws IOException {
    // First some sanity checks
    if ( uri == null ) {
      throw new IOException( "Can not create a socket from a null URI" );
    }

    if ( uri.getPort() < 1 ) {
      throw new IOException( "Can not create a remote socket without a port specified" );
    }

    // Get a factory to create our sockets
    ISocketFactory isocketfactory = getFactory( uri.getScheme() );

    if ( isocketfactory == null ) {
      throw new IOException( "No socket factory defined for '" + uri.getScheme() + "'" );
    }

    // sanity check on the address
    if ( UriUtil.getHostAddress( uri ) == null ) {
      throw new IOException( "Could not resolve host address '" + uri.getHost() + "'" );
    }

    // generate the socket and return it
    return isocketfactory.createSocket( UriUtil.getHostAddress( uri ), uri.getPort() );
  }




  /**
   * Create a SocketChannel connected to the given URI.
   *
   * @param uri
   *
   * @return
   *
   * @throws IOException
   */
  public static SocketChannel createSocketChannel( URI uri ) throws IOException {
    // Mark the time we started opening the socket
    long started = System.currentTimeMillis();

    // Open a socket
    Socket socket = createSocket( uri );

    // Mark the time we ended opening the socket;
    long ended = System.currentTimeMillis();

    // Create the SocketChannel with the given socket
    SocketChannel retval = new SocketChannel( socket, uri.getScheme() );

    // Set the time the connection was established in the return value
    retval.connectedTime = ended;

    // Set how long it took to make the connection
    retval.connectionTime = ended - started;

    // Return the new SocketChannel
    return retval;
  }




  /**
   * Method createSocket
   *
   * @param socket
   * @param uri
   *
   * @return
   *
   * @throws IOException
   */
  public static Socket createSocket( Socket socket, URI uri ) throws IOException {
    ISocketFactory isocketfactory = getFactory( uri.getScheme() );
    return isocketfactory.createSocket( socket, uri.getHost(), uri.getPort(), true );
  }




  /**
   * Epoch time the connection was established
   *
   * @return
   */
  public long getConnectedTime() {
    return connectedTime;
  }




  /**
   * Set the epoch time in milliseconds when the socket was connected.
   *
   * @param connectedTime
   */
  public void setConnectedTime( long connectedTime ) {
    this.connectedTime = connectedTime;
  }




  /**
   * Time it took to create the connection to the peer
   *
   * @return Total connection creation time in milliseconds
   */
  public long getConnectionTime() {
    return connectionTime;
  }




  /**
   * Return the total uptime of the connection.
   *
   * @return Uptime in milliseconds
   */
  public long getConnectionUpTime() {
    return ( System.currentTimeMillis() - connectedTime );
  }




  /**
   * Method setConnectionTime
   *
   * @param connectionTime
   */
  public void setConnectionTime( long connectionTime ) {
    this.connectionTime = connectionTime;
  }




  /**
   * Handle the socket by asking the server that created us to pass us to it's
   * IChannelSink.
   */
  public void run() {
    Log.debug( "SocketChannel is running" );

    // If we have a server reference, ask it to assign a handler to us
    if ( server != null ) {
      server.assignHandler( this );
    }

    Log.debug( "SocketChannel checking for handler" );

    // If we have a handler that will handle us
    if ( channelHandler != null ) {
      Log.debug( "SocketChannel found handler, setting channel reference" );
      channelHandler.setChannel( this );

      try {
        Log.debug( "SocketChannel running handler" );
        channelHandler.run();
        Log.debug( "SocketChannel handler run complete" );
      } catch ( Throwable t ) {
        Log.error( "SocketChannel threw: " + t.getClass().getName() + " while running handler message: " + t.getMessage() );
        Log.debug( ExceptionUtil.stackTrace( t ) );
      }
    }

    try {
      close();
    } catch ( Exception ex ) {}
  }




  /**
   * Assign an object to the channel that will handle the communications over
   * the channel
   *
   * @param handler
   */
  public void setHandler( IChannelHandler handler ) {
    channelHandler = handler;
  }




  /**
   * Return the reference to the socket this object represents.
   *
   * @return
   */
  public Socket getSocket() {
    return socket;
  }
}