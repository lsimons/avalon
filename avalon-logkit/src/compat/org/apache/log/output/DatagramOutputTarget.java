/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output;

import java.io.IOException;
import java.net.InetAddress;
import org.apache.log.format.Formatter;

/**
 * A datagram output target.
 * Useful for writing using custom protocols or writing to syslog daemons.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @deprecated Use org.apache.log.output.net.DatagramOutputTarget instead
 */
public class DatagramOutputTarget
    extends org.apache.log.output.net.DatagramOutputTarget
{
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
        super( address, port, formatter );
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
        super( address, port );
    }
}
