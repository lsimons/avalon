/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.connection;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Helper class to extend to create handler factorys.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public abstract class AbstractHandlerFactory
    extends AbstractLogEnabled
    implements Contextualizable, Serviceable, Configurable, ConnectionHandlerFactory
{
    private Context m_context;
    private ServiceManager m_serviceManager;
    private Configuration m_configuration;

    public void contextualize( final Context context )
    {
        m_context = context;
    }

    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_serviceManager = serviceManager;
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    /**
     * Construct an appropriate ConnectionHandler.
     *
     * @return the new ConnectionHandler
     * @exception Exception if an error occurs
     */
    public ConnectionHandler createConnectionHandler()
        throws Exception
    {
        final ConnectionHandler handler = newHandler();
        ContainerUtil.enableLogging( handler, getLogger() );
        ContainerUtil.contextualize( handler, m_context );
        ContainerUtil.service( handler, m_serviceManager );
        ContainerUtil.compose( handler, new AdaptingComponentManager( m_serviceManager ) );
        ContainerUtil.configure( handler, m_configuration );
        ContainerUtil.initialize( handler );

        return handler;
    }

    public void releaseConnectionHandler( ConnectionHandler connectionHandler )
    {
        ContainerUtil.dispose( connectionHandler );
    }

    /**
     * Overide this method to create actual instance of connection handler.
     *
     * @return the new ConnectionHandler
     * @exception Exception if an error occurs
     */
    protected abstract ConnectionHandler newHandler()
        throws Exception;
}
