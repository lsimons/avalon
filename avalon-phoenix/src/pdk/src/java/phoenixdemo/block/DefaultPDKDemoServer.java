
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package phoenixdemo.block;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.Block;
import phoenixdemo.api.PDKDemoServer;
import phoenixdemo.server.PDKDemoServerImpl;

/**
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class DefaultPDKDemoServer 
    extends AbstractLogEnabled
    implements Block, PDKDemoServer, Configurable, Startable, Initializable
{
    private int m_port;
    private PDKDemoServerImpl m_pdkServer;
    private SocketThread m_socketThread;

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_port = configuration.getChild("port").getValueAsInteger(7777);
    }

    public void initialize() 
        throws Exception
    {
        m_pdkServer = new PDKDemoServerImpl();
    }

    public void start() 
        throws Exception
    {
        m_socketThread = new SocketThread( m_pdkServer, m_port );
        m_socketThread.start();

        System.out.println( "Server started on port " + m_port );
    }

    public void stop() 
        throws Exception
    {
        m_socketThread = new SocketThread( m_pdkServer, m_port );
        m_socketThread.interrupt();
        m_socketThread = null;

        System.out.println( "Server stopped on port " + m_port );
    }
}
