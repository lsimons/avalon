/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example2;

import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.socket.AsyncConnection;
import org.apache.excalibur.event.socket.tcp.IncomingPacket;

/**
 * A sender handles all events on the client side. 
 * Each incoming packet is displayed.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface Sender
{
    /**
     * Notifies that a connection is there to be written on.
     * @since Sep 23, 2002
     * 
     * @param connection
     *  The connection to be written on
     * @throws SinkException
     *  If an event cannot be enqueued
     */   
    void write(AsyncConnection connection) throws SinkException;
    
    /**
     * Displays the passed in packet to the user
     * @since Sep 23, 2002
     * 
     * @param packet
     *  The packet to be displayed
     * @throws SinkException
     *  If an event cannot be enqueued
     */   
    void display(IncomingPacket packet) throws SinkException;

}
