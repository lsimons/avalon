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
import org.apache.cornerstone.services.connection.ConnectionHandler;
import org.apache.cornerstone.services.connection.ConnectionHandlerFactory;

public abstract class AbstractHandlerFactory
    extends AbstractLoggable
    implements Component, Contextualizable, ConnectionHandlerFactory 
{
    protected Context             m_context;
    protected ComponentManager    m_componentManager;
    
    public void contextualize( final Context context )
    {
        m_context = context;
    }
    
    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_componentManager = componentManager;
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

        if( handler instanceof Initializable )
        {
            ((Initializable)handler).init();
        }

        return handler;
    }

    protected abstract ConnectionHandler newHandler() 
        throws Exception;
}
