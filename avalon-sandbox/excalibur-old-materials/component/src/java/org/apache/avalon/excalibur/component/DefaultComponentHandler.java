/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.log.Logger;

/**
 * The DefaultComponentHandler to make sure components are initialized
 * and destroyed correctly.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:ryan@silveregg.co.jp">Ryan Shaw</a>
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:02 $
 * @since 4.0
 */
public class DefaultComponentHandler
    extends ComponentHandler
{
    /** The instance of the ComponentFactory that creates and disposes of the Component */
    private final DefaultComponentFactory m_factory;

    /** State management boolean stating whether the Handler is initialized or not */
    private boolean m_initialized = false;

    /** State management boolean stating whether the Handler is disposed or not */
    private boolean m_disposed = false;

    /**
     * Create a ComponentHandler that takes care of hiding the details of
     * whether a Component is ThreadSafe, Poolable, or SingleThreaded.
     * It falls back to SingleThreaded if not specified.
     */
    protected DefaultComponentHandler( final Class componentClass,
                                       final Configuration config,
                                       final ComponentManager manager,
                                       final Context context,
                                       final RoleManager roles,
                                       final LogKitManager logkit )
        throws Exception
    {
        this(
            new DefaultComponentFactory( componentClass, config, manager, context, roles, logkit ),
            config );
    }

    /**
     * Create a DefaultComponentHandler which manages a pool of Components
     *  created by the specified factory object.
     *
     * @param factory The factory object which is responsible for creating the components
     *                managed by the ComponentHandler.
     * @param config The configuration to use to configure the pool.
     */
    public DefaultComponentHandler( final DefaultComponentFactory factory,
                                    final Configuration config )
        throws Exception
    {
        m_factory = factory;
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
        if( m_initialized )
        {
            return;
        }

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "ComponentHandler initialized for: " + this.m_factory.getCreatedClass().getName() );
        }
        m_initialized = true;
    }

    /**
     * Get a reference of the desired Component
     */
    protected Component doGet()
        throws Exception
    {
        if( !m_initialized )
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
    protected void doPut( final Component component )
    {
        if( !m_initialized )
        {
            throw new IllegalStateException( "You cannot put a component in an uninitialized holder." );
        }

        try
        {
            m_factory.decommission( component );
        }
        catch( final Exception e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Error decommissioning component: " +
                                  m_factory.getCreatedClass().getName(), e );
            }
        }
    }

    /**
     * Dispose of the ComponentHandler and any associated Pools and Factories.
     */
    public void dispose()
    {
        try
        {
            // do nothing here

            if( m_factory instanceof Disposable )
            {
                ( (Disposable)m_factory ).dispose();
            }
        }
        catch( final Exception e )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Error decommissioning component: " +
                                  m_factory.getCreatedClass().getName(), e );
            }
        }

        m_disposed = true;
    }
}
