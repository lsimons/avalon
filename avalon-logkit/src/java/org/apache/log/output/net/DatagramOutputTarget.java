/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.apache.log.Formatter;
import org.apache.log.output.AbstractOutputTarget;

/**
 * A datagram output target.
 * Useful for writing using custom protocols or writing to syslog daemons.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DatagramOutputTarget
    extends AbstractOutputTarget
{
    ///Socket on which to send datagrams
    private DatagramSocket m_socket;

    /**
     * Create a output target with end point specified by address and port.
     *
     * @param address the address endpoint
     * @param port the address port
     * @exception IOException if an error occurs
     */
    public DatagramOutputTarget( final InetAddress address,
                                 final int port,
                                 final Formatter formatter )
        throws IOException
    {
        super( formatter );
        m_socket = new DatagramSocket();
        m_socket.connect( address, port );
        open();
    }

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
        this( address, port, null );
    }

    /**
     * Method to write output to datagram.
     *
     * @param stringData the data to be output
     */
    protected void write( final String stringData )
    {
        final byte[] data = stringData.getBytes();

        try
        {
            final DatagramPacket packet = new DatagramPacket( data, data.length );
            m_socket.send( packet );
        }
        catch( final IOException ioe )
        {
            error( "Error sending datagram.", ioe );
        }
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     */
    public synchronized void close()
    {
        super.close();
        m_socket = null;
    }
}
