/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.publishing;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.altrmi.server.impl.socket.AbstractPartialSocketStreamServer;

/**
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @author Peter Royal.
 * @version $Revision: 1.3 $
 */
public class PartialSocketStreamConnectionHandler
    extends AbstractLogEnabled
    implements ConnectionHandler
{
    private AbstractPartialSocketStreamServer m_PartialSockeStreamServer;

    public PartialSocketStreamConnectionHandler(
        AbstractPartialSocketStreamServer partialSocketStreamServer )
    {
        m_PartialSockeStreamServer = partialSocketStreamServer;
    }

    /**
     * Handle a connection.
     * This handler is responsible for processing connections as they occur.
     *
     * @param connection the connection
     * @exception IOException if an error reading from socket occurs
     * @exception ProtocolException if an error handling connection occurs
     */
    public void handleConnection( Socket connection )
        throws IOException, ProtocolException
    {
        m_PartialSockeStreamServer.handleConnection( connection );
    }
}
