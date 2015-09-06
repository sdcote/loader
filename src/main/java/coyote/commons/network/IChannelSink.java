/*
 * $Id: IChannelSink.java,v 1.2 2004/01/02 15:10:22 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.network;

import java.io.IOException;


/**
 * IChannelSink represents the consumer of an IChannel
 */
public interface IChannelSink {

  /**
   * Assign a handler that will service the given channel.
   *
   * <p>The normal mode of operation is for the channel (which is normally
   * running in its own thread of execution) to use the assigned handler as the
   * logic to handle the connection, running it in its thread of execution. If
   * no handler is assigned to the channel, the thread will simply close the
   * channel and terminate.</p>
   *
   * <p>The return value indicates that a handler was assigned and assists the
   * channel sink in performing an early-exit if the sink is passing the
   * channel to multiple sub-classes in the determination of which handler to
   * assign or in how to process the channel. It is not logically required by
   * the SocketServer or SocketChannel. It is a convenience value for the sake
   * of the IChannelSink.</p>
   *
   * <p>This method may also completely service the given channel the given
   * channel without assigning a handler if so desired. If this method of
   * operation is chosen, then the channel will simple close itself when this
   * method return a true value.</p>
   *
   * @param channel - the communications channel to service
   *
   * @return true if the channel was logically assigned a handler object or
   *         otherwise processed, false otherwise
   *
   * @throws IOException
   */
  public abstract boolean assignHandler( IChannel channel ) throws IOException;
}