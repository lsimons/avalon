/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.apache.log.Formatter;
import org.apache.log.LogEvent;
import org.apache.log.LogKit;
import org.apache.log.LogTarget;

/**
 * A datagram output target.
 * Useful for writing using custom protocols or 
 * else to standards such as syslog.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DatagramOutputTarget
    extends AbstractOutputTarget 
{
    protected DatagramSocket m_socket;

    /**
     * Create a output target with end point specified by address and port.
     *
     * @param address the address endpoint
     * @param port the address port
     * @exception IOException if an error occurs
     */
    public DatagramOutputTarget( final InetAddress address, final int port )
        throws IOException
    {
        m_socket = new DatagramSocket();
        m_socket.connect( address, port );
    }

    /**
     * Method to write output to datagram.
     *
     * @param stringData the data to be output
     */
    protected void output( final String stringData )
    {
        final byte[] data = stringData.getBytes();

        try
        {
            final DatagramPacket packet = new DatagramPacket( data, data.length );
            m_socket.send( packet );
        }
        catch( final IOException ioe )
        {
            LogKit.log( "Error sending datagram.", ioe );
            //TODO:
            //Can no longer route to global error handler - somehow need to pass down error 
            //handler from engine...
        }
    }
}
