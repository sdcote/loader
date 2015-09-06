/*
 * $Id: IChannelHandler.java,v 1.2 2004/01/02 15:10:21 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.network;

/**
 * IChannelHandler defines an interface for a class that handles IChannels.
 *
 * <p>Handlers normally embed the protocols or business logic of communications
 * over a channel.</p>
 */
public interface IChannelHandler extends Runnable
{

  /**
   * Assign an IChannel object to the handler so that when it is run it has a
   * reference to the channel.
   *
   * @param channel
   */
  public abstract void setChannel( IChannel channel );
}