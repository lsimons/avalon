/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import org.apache.excalibur.event.socket.AsyncSocketHandlerBase;

/**
 * This interface describes all the handler methods for 
 * an asynchronous socket.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface AsyncSocketHandler extends AsyncSocketHandlerBase
{
    String ROLE = AsyncSocketHandler.class.getName();

    /**
     * Processes the passed in connect request by creating
     * a new connect socket state.
     * (Writing Stage)
     * @since Aug 27, 2002
     * 
     * @param connect
     *  The connect request to process.
     */
    void connect(ConnectRequest connect);
}
