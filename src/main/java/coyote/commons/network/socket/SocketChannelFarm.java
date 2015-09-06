/*
 * $Id: SocketChannelFarm.java,v 1.2 2004/01/02 15:10:22 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.network.socket;

import java.net.URI;

import coyote.loader.thread.ThreadPool;


/**
 * Represents an entity that can create SocketChannels in a way that allows the
 * caller to time-out if a connection can not be made.
 *
 * <p>Creating a java.net.Socket can take quite a bit of time and if a
 * connection fails, the thread that is creating the Socket will be forced to
 * block the entire time it take the socket connection to time out. In multi-
 * threaded applications that need to respond quickly, this is a major pain.
 * Even if a sub thread is created, the parent thread will end-up waiting for
 * the sub thread to finish blocking on the Socket connection until it exists
 * and that in itself is anoying.<p>
 *
 * <p>That is why this class was created. It creates a threadpool of socket
 * &quot;farmers&quot; that grow sockets. If the caller decides that the
 * connection process is taking too long, it can bail-out, and let the farmer
 * complete it's connection cycle and exit without causing the caller to wait.
 * This allows for really snappy response times when all a caller needs to do
 * is check to see if a connection is available within a short time out. If
 * not, then the caller can try another address:port or URI combination.<p>
 *
 * <p>Why SocketChannelFarm and not SocketChannelFactory? Well, I got tired of
 * all the factories being built and thought the world would be better for
 * having a few more farms and a few less factories.</p>
 */
public class SocketChannelFarm {

  /** The field where we grow our SocketChannels */
  private ThreadPool field = null;

  /** The default maximum number (64) of farm workers to have in our field */
  private static final int DEFAULT_MAX_WORKERS = 64;

  /** The default minimum number (2) of farm workers to have in our field */
  private static final int DEFAULT_MIN_WORKERS = 2;

  /** The maximum number of workers to have running at one time */
  private int max = DEFAULT_MAX_WORKERS;

  /** The minimum number of workers to have running at one time */
  private int min = DEFAULT_MIN_WORKERS;

  /**
   * The number of milliseconds the threadworkers will block waiting for a job
   * before exiting
   */
  private int DEFAULT_SHRINK_TIME = 60000;




  /**
   * Constructor SocketChannelFarm
   */
  public SocketChannelFarm() {
    this( DEFAULT_MAX_WORKERS, DEFAULT_MIN_WORKERS );
  }




  /**
   * Constructor SocketChannelFarm
   *
   * @param max
   * @param min
   */
  public SocketChannelFarm( int max, int min ) {
    this.max = max;
    this.min = min;

    initThreadPool();
  }




  /**
   * Setup the field, or threadpool, in which the farmers will work.
   */
  private void initThreadPool() {
    // Setup our thread pool
    field = new ThreadPool( "SocketChannelFarm" );

    field.setMaxThreadCount( max );
    field.setMinThreadCount( min );
    field.setJobWaitTime( DEFAULT_SHRINK_TIME );
  }




  /**
   * Create a new SocketChannelFarmer that will start "growing" a SocketChannel
   * to the given URI in the field.
   *
   * <p>The caller can then call getSocketChannel() on the farmer at the
   * caller's leisure to get the SocketChannel when the connection is
   * ready.</p>
   *
   * @param uri The URI representing the service to which the farmer is to
   *          connect.
   * @return A SocketChannelFarmer that is already hard at work growing a
   *         SocketChannel to the given URI.
   */
  public synchronized SocketChannelFarmer getFarmer( URI uri ) {
    SocketChannelFarmer farmer = new SocketChannelFarmer( uri );
    field.run( (Runnable)farmer );

    return farmer;
  }




  /**
   * Method shutdown
   */
  public void shutdown() {
    field.stop();
  }
}