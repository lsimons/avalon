/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.connection;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Helper class to extend to create handler factorys.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractHandlerFactory
    extends AbstractLogEnabled
    implements Component, Contextualizable, Composable, Configurable, ConnectionHandlerFactory
{
    protected Context             m_context;
    protected ComponentManager    m_componentManager;
    protected Configuration       m_configuration;

    public void contextualize( final Context context )
    {
        m_context = context;
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_componentManager = componentManager;
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

        setupLogger( handler );

        if( handler instanceof Contextualizable )
        {
            ((Contextualizable)handler).contextualize( m_context );
        }

        if( handler instanceof Composable )
        {
            ((Composable)handler).compose( m_componentManager );
        }

        if( handler instanceof Configurable )
        {
            ((Configurable)handler).configure( m_configuration );
        }

        if( handler instanceof Initializable )
        {
            ((Initializable)handler).initialize();
        }

        return handler;
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
