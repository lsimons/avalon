/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;

/**
 * The ThreadSafeComponentHandler to make sure components are initialized
 * and destroyed correctly.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:ryan@silveregg.co.jp">Ryan Shaw</a>
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.5 $ $Date: 2002/09/24 20:39:53 $
 * @since 4.0
 */
public class ThreadSafeComponentHandler
    extends ComponentHandler
{
    private Component m_instance;
    private final DefaultComponentFactory m_factory;
    private boolean m_initialized = false;
    private boolean m_disposed = false;

    /**
     * Create a ComponentHandler that takes care of hiding the details of
     * whether a Component is ThreadSafe, Poolable, or SingleThreaded.
     * It falls back to SingleThreaded if not specified.
     */
    protected ThreadSafeComponentHandler( final String role,
                                          final Class componentClass,
                                          final Configuration config,
                                          final ComponentManager manager,
                                          final Context context,
                                          final RoleManager roles,
                                          final LogkitLoggerManager logkit )
        throws Exception
    {
        this(
            new DefaultComponentFactory( role, componentClass, config, manager, context, roles, logkit ),
            config );
    }

    /**
     * Create a ThreadSafeComponentHandler which manages a pool of Components
     *  created by the specified factory object.
     *
     * @param factory The factory object which is responsible for creating the components
     *                managed by the ComponentHandler.
     * @param config The configuration to use to configure the pool.
     */
    public ThreadSafeComponentHandler( final DefaultComponentFactory factory,
                                       final Configuration config )
        throws Exception
    {
        m_factory = factory;
    }

    /**
     * Create a ComponentHandler that takes care of hiding the details of
     * whether a Component is ThreadSafe, Poolable, or SingleThreaded.
     * It falls back to SingleThreaded if not specified.
     */
    protected ThreadSafeComponentHandler( final Component component )
        throws Exception
    {
        m_instance = component;
        m_factory = null;
    }

    /**
     * Initialize the ComponentHandler.
     */
    public void initialize()
        throws Exception
    {
        if( m_initialized )
        {
            return;
        }
        if( null != m_factory )
        {
            m_factory.setLogger( getLogkitLogger() );
            m_factory.enableLogging( getLogger() );
        }

        if( m_instance == null )
        {
            m_instance = (Component)m_factory.newInstance();
        }

        if( getLogger().isDebugEnabled() )
        {
            if( m_factory != null )
            {
                getLogger().debug( "ComponentHandler initialized for: " + m_factory.getCreatedClass().getName() );
            }
            else
            {
                getLogger().debug( "ComponentHandler initialized for: " + m_instance.getClass().getName() );
            }
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

        return m_instance;
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
    }

    /**
     * Dispose of the ComponentHandler and any associated Pools and Factories.
     */
    public void dispose()
    {
        try
        {
            if( null != m_factory )
            {
                m_factory.decommission( m_instance );
            }
            else
            {
                if( m_instance instanceof Startable )
                {
                    ( (Startable)m_instance ).stop();
                }

                if( m_instance instanceof Disposable )
                {
                    ( (Disposable)m_instance ).dispose();
                }
            }

            m_instance = null;
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
