/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.udp;

import java.io.IOException;

import org.apache.excalibur.event.socket.AsyncSocketHandlerBase;

/**
 * This interface describes all the handler methods for 
 * an asynchronous socket.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncDatagramSocketHandler extends AsyncSocketHandlerBase
{
    String ROLE = AsyncDatagramSocketHandler.class.getName();
    
    /**
     * Processes the passed in create request by creating
     * a new unconnected socket.
     * (Writing Stage)
     * @since Aug 27, 2002
     * 
     * @param connect
     *  The connect request to process.
     * @throws IOException
     *  If the socket could not be created.
     */
    void open(OpenRequest create) throws IOException;

    /**
     * Processes the passed in connect request by connecting
     * the socket with a specified address.
     * (Writing Stage)
     * @since Aug 27, 2002
     * 
     * @param connect
     *  The connect request to process.
     */
    void connect(ConnectRequest connect);
    
    /**
     * Processes the passed in disconnect request by 
     * disconnecting the socket.
     * (Writing Stage)
     * @since Aug 27, 2002
     * 
     * @param connect
     *  The connect request to process.
     */
    void disconnect(DisconnectRequest disconnect);
}
