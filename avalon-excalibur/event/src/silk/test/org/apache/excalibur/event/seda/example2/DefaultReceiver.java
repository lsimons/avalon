/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example2;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.socket.AsyncConnection;
import org.apache.excalibur.event.socket.Buffer;
import org.apache.excalibur.event.socket.tcp.IncomingPacket;

/**
 * Default implementation of the HttpRequestHandler interface. Receives 
 * and handles socket connection events.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultReceiver extends AbstractLogEnabled
    implements Receiver
{
    //------------------------- HttpRequestHandler implementation
    /**
     * @see HttpRequestHandler#echo(IncomingPacket)
     */
    public void echo(IncomingPacket packet) throws SinkException
    {
        final String text = new String(packet.getBytes());
        if(getLogger().isInfoEnabled())
        {
            getLogger().info("<<" + text);
        }

        final Buffer element = new Buffer(packet.getBytes());
        final AsyncConnection connection = packet.getConnection();
        connection.write(element);
        
        if(getLogger().isDebugEnabled())
        {
            getLogger().debug("Packet returned!");
        }
        
        connection.flush();
        connection.close();
    }

    /**
     * @see HttpRequestHandler#read(AsyncConnection)
     */
    public void read(AsyncConnection connection) throws SinkException
    {
        if(getLogger().isInfoEnabled())
        {
            getLogger().info("==============================");
            getLogger().info("Received Connection. Now read.");
            getLogger().info("==============================");
        }
        
        connection.read();
    }
}