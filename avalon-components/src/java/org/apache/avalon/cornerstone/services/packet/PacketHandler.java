/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.ProtocolException;

/**
 * This interface is the way in which handlers are created.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface PacketHandler
{
    /**
     * Handle a datgram packet.
     * This handler is responsible for processing packets as they occur.
     *
     * @param packet the packet
     * @exception IOException if an error reading from socket occurs
     * @exception ProtocolException if an error handling connection occurs
     */
    void handlePacket( DatagramPacket packet )
        throws IOException, ProtocolException;
}

