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
 * Default implementation of the sender interface. Writes 
 * to a connection and displays the returned information.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultSender extends AbstractLogEnabled
    implements Sender
{
    //------------------------ Sender implementation
    /**
     * @see Sender#display(IncomingPacket)
     */
    public void display(IncomingPacket packet) throws SinkException
    {
        if(getLogger().isDebugEnabled())
        {
            getLogger().debug("+-------------------+");
            getLogger().debug("|  Packet received  |");
            getLogger().debug("+-------------------+");
        }
        
        final String text = new String(packet.getBytes());
        if(getLogger().isInfoEnabled())
        {
            getLogger().info(">>" + text);
        }
        
        final AsyncConnection connection = packet.getConnection();
        connection.close();
        
        synchronized(EchoClient.lock)
        {
            EchoClient.lock.notifyAll();
        }
    }

    /**
     * @see Sender#write(AsyncConnection)
     */
    public void write(AsyncConnection connection) throws SinkException
    {
        if(getLogger().isInfoEnabled())
        {
            getLogger().info("===============================");
            getLogger().info("Received Connection. Now write.");
            getLogger().info("         Message 1-4           ");
            getLogger().info("===============================");
        }
        
        connection.write(new Buffer("Message 1\r\n".getBytes()));
        connection.write(new Buffer("Message 2\r\n".getBytes()));
        connection.write(new Buffer("Message 3\r\n".getBytes()));
        connection.write(new Buffer("Message 4\r\n".getBytes()));
        
        connection.flush();

        if(getLogger().isInfoEnabled())
        {
            getLogger().info("... Messages written... Start reading...");
        }

        connection.read(3);
    }
}