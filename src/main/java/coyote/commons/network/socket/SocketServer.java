/*
 * $Id: SocketServer.java,v 1.3 2005/03/01 20:44:04 cotes Exp $
 */
package coyote.commons.network.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

import coyote.commons.UriUtil;
import coyote.commons.network.IChannelSink;
import coyote.commons.network.IpAcl;
import coyote.loader.log.Log;
import coyote.loader.thread.ThreadPool;


/**
 * SocketServer accepts socket connections and processes incoming socket messages.
 *
 * <p>When a socket connection is accepted, it is wrapped in a SocketChannel and
 * allocated a thread from the thread pool. When the thread is serviced, it
 * passes the SocketChannel to the IChannelSink for processing.</p>
 *
 * <p>An IChannelSink typically reads data from the channel, writes back a
 * response, and then returns.</p>
 *
 * <p>If the SocketChannel is flagged as &quot;keep-alive&quot; and the maximum
 * number of open connections has not been reached, a new thread is allocated to
 * the SocketChannel and the process is repeated, otherwise the SocketChannel is
 * closed.</p>
 */
public final class SocketServer implements Runnable {

  /** Field DEFAULT_MAX_INBOUND_KEEP_ALIVE */
  public static final int DEFAULT_MAX_INBOUND_KEEP_ALIVE = 5;

  /** Field DEFAULT_BACKLOG */
  public static final int DEFAULT_BACKLOG = 50;

  /** Field defaultBacklog */
  static int defaultBacklog = 50;

  /** Field backlog */
  int backlog;

  /** Field sink */
  IChannelSink sink;

  /** Field stop */
  volatile boolean stop;

  /** Field offline */
  volatile boolean offline;

  /** Field uri */
  URI uri;

  /** Field serverSocket */
  ServerSocket serverSocket;

  /** Field threadPool */
  ThreadPool threadPool;

  /** Field padlock */
  Object padlock = new Object();

  /** Our Access Control List of IpAddresses */
  public final IpAcl ACL = new IpAcl();




  /**
   * Construct a Socket server that listens on the given URI and passes the
   * connections to the given ChannelSink for processing.
   *
   * <p>The URI is used to setup a ServerSocket of the appropriate type on the
   * proper address and port.</p>
   *
   * <p>The given channel sink better be thread-safe as all the threads in the
   * threadpool will be accessing that object to service requests.</p>
   *
   * @param uri the URI describing the server socket to build
   * @param backlog the number of connections to queue when we are busy
   * @param threadpool the ThreadPool that is used to queue the SocketRequests
   * @param channelsink
   */
  public SocketServer( URI uri, int backlog, ThreadPool threadpool, IChannelSink channelsink ) {
    this.stop = true;
    this.offline = false;
    this.uri = uri;
    this.backlog = backlog;
    this.threadPool = threadpool;
    this.sink = channelsink;
  }




  /**
   * Create a new thread and start this server running within it.
   *
   * @throws IOException
   */
  public synchronized void startup() throws IOException {
    if ( threadPool == null ) {
      threadPool = new ThreadPool( uri.toString() );

      threadPool.setMinThreadCount( 2 );
      threadPool.setMaxThreadCount( 128 );
      threadPool.start();
    }

    if ( stop ) {
      stop = false;
      offline = false;
      serverSocket = createServerSocket( uri );

      Thread thred = new Thread( this );
      thred.setName( "Srvr:" + uri.getHost() + ":" + uri.getPort() );
      thred.start();
    } else {
      Log.debug( "Server '" + uri.toString() + "' is running and offline=" + offline );
    }
  }




  /**
   * Shut down the server.
   */
  public void shutdown() {
    stop = true;

    try {
      serverSocket.close();
    } catch ( Exception exception ) {}
    finally {
      // padlock.notifyAll();
    }
  }




  /**
   * Go offline for a while
   */
  public void offline() {
    if ( !offline ) {
      stop = false;
      offline = true;

      try {
        serverSocket.close();
        Log.info( "Server '" + uri.toString() + "' is offline" );
      } catch ( Exception exception ) {}
      finally {}
    } else {
      Log.info( "Server '" + uri.toString() + "' is already offline" );
    }
  }




  /**
   * Go online
   */
  public void online() {
    if ( offline ) {
      stop = false;
      offline = false;

      try {
        serverSocket = createServerSocket( uri );
      } catch ( IOException ioe ) {
        Log.error( "Server '" + uri.toString() + "' could not go online: " + ioe.getMessage() );
      }
      finally {
        shutdown();
      }

      padlock.notifyAll();
    } else {
      Log.info( "Server '" + uri.toString() + "' is already online" );
    }
  }




  /**
   * The main run loop.
   *
   * <p>While the server is running, the server socket blocks indefinitely until
   * a connection is received. It is then wrapped in a SocketChannel object and
   * placed inside a SocketRequest which is given to the ThreadPool to be
   * serviced.</p>
   *
   * <p>Since the SocketRequest is given this object as it's server, it "returns"
   * later via the run(SocketRequest) method in this object. Now the
   * SocketRequest is running in its own thread from the ThreadPool leaving this
   * server free to work on accepting new socket connections.</p>
   */
  public void run() {
    Log.debug( "listening on '" + serverSocket.getInetAddress().getHostName() + ":" + serverSocket.getLocalPort() + "'" );

    while ( !stop ) {
      try {
        Socket socket = serverSocket.accept();
        Log.debug( "accepted connection from '" + socket.getInetAddress().getHostName() + ":" + socket.getPort() + "'" );

        if ( ACL.allows( socket.getInetAddress() ) ) {
          // Create the socket channel and run it
          try {
            SocketChannel socketchannel = new SocketChannel( socket, uri.getScheme(), this );
            socketchannel.setConnectedTime( System.currentTimeMillis() );
            threadPool.run( socketchannel );
          } catch ( IOException ioexception1 ) {
            shutdown();
          }
        } else {
          Log.info( "ACL rejected connection from '" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "'" );

          // The peer is not allowed to connect to this server
          try {
            socket.close();
          } catch ( Exception e ) {
            // who cares?
          }
        }

      } catch ( IOException ioexception ) {
        if ( stop ) {
          shutdown();
        } else {
          if ( offline ) {
            try {
              padlock.wait();
            } catch ( Exception e ) {
              Log.debug( "Server '" + uri.toString() + "' could not go offline: " + e.getClass().getName() + " - " + e.getMessage() );
            }
          } else {
            ioexception.printStackTrace();
          }
        }
      }
    }
  }




  /**
   * Assign a handler to, or otherwise service the SocketChannel with the
   * ChannelSink we were given in our constructor.
   *
   * <p>We are handling the SocketChannel we origionally created in our main
   * run() method, but now the channel is under the control of one of the
   * threads from the ThreadPool we were given in our constructor. This means
   * the SocketServer is free to accept connections, while the ThreadPool
   * threads handle the processing of the SocketChannel.</p>
   *
   * @param socketchannel is the SocketChannel we placed in the threadpool for servicing.
   */
  public void assignHandler( SocketChannel socketchannel ) {
    try {
      Log.debug( "Assigning handler" );

      // Service the socket channel and if all goes well...
      if ( sink.assignHandler( socketchannel ) ) {
        // Log something?
        Log.debug( "Handler assigned" );
      }
    } catch ( Exception ex ) {
      Log.warn( sink.getClass().getName() + " could not assign a handler or otherwise process the channel: " + ex.getMessage() );
    }

  }




  /**
   * Method shutdown
   *
   * @param socketchannel
   */
  void shutdown( SocketChannel socketchannel ) {
    try {
      socketchannel.close();
    } catch ( Exception exception ) {}
    finally {}

  }




  /**
   * Method getURI
   *
   * @return
   */
  public URI getServiceUri() {
    return uri;
  }




  /**
   * Return the InetAddres to which the server is bound
   *
   * @return
   */
  public InetAddress getBindAddress() {
    return UriUtil.getHostAddress( uri );
  }




  /**
   * Return the port on which the server is bound
   *
   * @return
   */
  public int getBindPort() {
    if ( serverSocket != null ) {
      return serverSocket.getLocalPort();
    } else {
      return uri.getPort();
    }
  }




  /**
   * Create a SocketFactory based upon the scheme of the given URI.
   *
   * @param uri
   *
   * @return
   *
   * @throws IOException
   */
  ServerSocket createServerSocket( URI uri ) throws IOException {
    ISocketFactory isocketfactory = SocketChannel.getFactory( uri.getScheme() );
    return isocketfactory.createServerSocket( uri.getPort(), backlog, UriUtil.getHostAddress( uri ) );
  }




  /**
   * @return
   */
  public ServerSocket getServerSocket() {
    return serverSocket;
  }

}