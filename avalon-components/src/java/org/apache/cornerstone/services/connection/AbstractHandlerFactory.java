/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.connection;

import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Component;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.Initializable;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;

/**
 * Helper class to extend to create handler factorys.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractHandlerFactory
    extends AbstractLoggable
    implements Component, Contextualizable, Composer, Configurable, ConnectionHandlerFactory
{
    protected Context             m_context;
    protected ComponentManager    m_componentManager;
    protected Configuration       m_configuration;

    public void contextualize( final Context context )
    {
        m_context = context;
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
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

        if( handler instanceof Composer )
        {
            ((Composer)handler).compose( m_componentManager );
        }

        if( handler instanceof Configurable )
        {
            ((Configurable)handler).configure( m_configuration );
        }

        if( handler instanceof Initializable )
        {
            ((Initializable)handler).init();
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
