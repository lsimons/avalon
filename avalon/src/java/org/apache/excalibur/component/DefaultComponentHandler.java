/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.component;

import org.apache.avalon.activity.Startable;
import org.apache.avalon.activity.Disposable;
import org.apache.avalon.activity.Initializable;
import org.apache.avalon.component.Component;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.context.Context;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.log.Logger;

/**
 * The DefaultComponentHandler to make sure components are initialized
 * and destroyed correctly.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.4 $ $Date: 2001/04/25 17:34:28 $
 */
class DefaultComponentHandler
    extends ComponentHandler
{
    /** The instance of the ComponentFactory that creates and disposes of the Component */
    private final DefaultComponentFactory    m_factory;

    /** State management boolean stating whether the Handler is initialized or not */
    private boolean                    m_initialized   = false;

    /** State management boolean stating whether the Handler is disposed or not */
    private boolean                    m_disposed      = false;

    /**
     * Create a ComponentHandler that takes care of hiding the details of
     * whether a Component is ThreadSafe, Poolable, or SingleThreaded.
     * It falls back to SingleThreaded if not specified.
     */
    protected DefaultComponentHandler( final Class componentClass,
                                       final Configuration config,
                                       final ComponentManager manager,
                                       final Context context,
                                       final RoleManager roles )
        throws Exception
    {
        m_factory = new DefaultComponentFactory( componentClass, config, manager, context, roles );
    }

    /**
     * Sets the logger that the ComponentHandler will use.
     */
    public void setLogger( final Logger logger )
    {
        m_factory.setLogger( logger );

        super.setLogger( logger );
    }

    /**
     * Initialize the ComponentHandler.
     */
    public void initialize()
    {
        if( m_initialized ) return;

        getLogger().debug("ComponentHandler initialized for: " + this.m_factory.getCreatedClass().getName());
        m_initialized = true;
    }

    /**
     * Get a reference of the desired Component
     */
    public Component get()
        throws Exception
    {
        if( ! m_initialized )
        {
            throw new IllegalStateException( "You cannot get a component from an uninitialized holder." );
        }

        if( m_disposed )
        {
            throw new IllegalStateException( "You cannot get a component from a disposed holder" );
        }

        return (Component)m_factory.newInstance();
    }

    /**
     * Return a reference of the desired Component
     */
    public void put( final Component component )
    {
        if( !m_initialized )
        {
            throw new IllegalStateException( "You cannot put a component in an uninitialized holder." );
        }

        if( m_disposed )
        {
            throw new IllegalStateException( "You cannot put a component in a disposed holder" );
        }

        try
        {
            m_factory.decommission( component );
        }
        catch( final Exception e )
        {
            getLogger().warn( "Error decommissioning component: " +
                              m_factory.getCreatedClass().getName(), e);
        }
    }

    /**
     * Dispose of the ComponentHandler and any associated Pools and Factories.
     */
    public void dispose()
    {
        m_disposed = true;

        try
        {
            // do nothing here

            if( m_factory instanceof Disposable )
            {
                ((Disposable)m_factory).dispose();
            }
        }
        catch( final Exception e )
        {
            getLogger().warn( "Error decommissioning component: " +
                              m_factory.getCreatedClass().getName(), e );
        }
    }
}
