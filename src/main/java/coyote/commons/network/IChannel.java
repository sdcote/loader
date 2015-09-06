/*
 * $Id: IChannel.java,v 1.2 2004/01/02 15:10:22 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;


/**
 * IChannel is an abstraction of a bidirectional communications channel.
 */
public interface IChannel
{

  /**
   * Return the input stream.
   *
   * @return
   *
   * @throws IOException
   */
  public abstract InputStream getInputStream() throws IOException;




  /**
   * Return the output stream.
   *
   * @return
   *
   * @throws IOException
   */
  public abstract OutputStream getOutputStream() throws IOException;




  /**
   * Close the channel.
   *
   * @throws IOException
   */
  public abstract void close() throws IOException;




  /**
   * Return a byte array of the specified size that contains a look-ahead at
   * that many bytes of input.
   *
   * <p>If that number of bytes is not available, pad the remainder with
   * zeroes.</p>
   *
   * @param i
   *
   * @return
   *
   * @throws IOException
   */
  public abstract byte[] peek( int i ) throws IOException;




  /**
   * Return the URI of the local endpoint.
   *
   * <p>If that number of bytes is not available, pad the remainder with
   * zeroes.</p>
   *
   * @return
   */
  public abstract URI getLocalURI();




  /**
   * Return the URI of the remote endpoint.
   *
   * @return
   */
  public abstract URI getRemoteURI();




  /**
   * Assign an object to the channel that will handle the communications over
   * the channel
   *
   * @param handler
   */
  public abstract void setHandler( IChannelHandler handler );
}