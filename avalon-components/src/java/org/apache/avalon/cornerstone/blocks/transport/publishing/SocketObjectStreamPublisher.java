/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.transport.publishing;

import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.commons.altrmi.server.impl.socket.PartialSocketObjectStreamServer;

/**
 * Class SocketObjectStreamPublisher
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.6 $
 */
public class SocketObjectStreamPublisher extends AbstractStreamPublisher
{

    /**
     * Construct an appropriate ConnectionHandler.
     *
     * @return the new ConnectionHandler
     * @exception Exception if an error occurs
     */
    public ConnectionHandler createConnectionHandler() throws Exception
    {

        final SocketObjectStreamConnectionHandler handler =
            new SocketObjectStreamConnectionHandler(
                (PartialSocketObjectStreamServer)m_AbstractServer );

        setupLogger( handler );

        return handler;
    }

    /**
     * Release a previously created ConnectionHandler.
     * e.g. for spooling.
     */
    public void releaseConnectionHandler( ConnectionHandler connectionHandler )
    {
    }

    /**
     * Initialialize the component. Initialization includes
     * allocating any resources required throughout the
     * components lifecycle.
     *
     * @exception Exception if an error occurs
     */
    public void initialize() throws Exception
    {

        m_AbstractServer = new PartialSocketObjectStreamServer();

        setupLogger( m_AbstractServer );
        super.initialize();
        m_connectionManager.connect( "SocketObjectStreamListener", makeServerSocket(), this );
    }
}
