/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.component;

import org.apache.avalon.component.Component;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.context.Context;
import org.apache.avalon.Stoppable;
import org.apache.avalon.Disposable;
import org.apache.log.Logger;

/**
 * The ThreadSafeComponentHandler to make sure components are initialized
 * and destroyed correctly.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/04/20 20:48:34 $
 */
public class ThreadSafeComponentHandler extends ComponentHandler {
    private Component m_instance;
    private final DefaultComponentFactory m_factory;
    private boolean m_initialized = false;
    private boolean m_disposed = false;

    /**
     * Create a ComponentHandler that takes care of hiding the details of
     * whether a Component is ThreadSafe, Poolable, or SingleThreaded.
     * It falls back to SingleThreaded if not specified.
     */
    protected ThreadSafeComponentHandler( final Class componentClass,
                             final Configuration config,
                             final ComponentManager manager,
                             final Context context,
                             final RoleManager roles )
        throws Exception
    {
        m_factory = new DefaultComponentFactory( componentClass, config, manager, context, roles );
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

    public void setLogger(Logger log)
    {
        if (m_factory != null)
        {
            m_factory.setLogger(log);
        }

        super.setLogger(log);
    }

    /**
     * Initialize the ComponentHandler.
     */
    public void init()
    throws Exception
    {
        if( m_initialized ) return;

        if (m_instance == null)
        {
            m_instance = (Component) this.m_factory.newInstance();
        }

        if (this.m_factory != null)
        {
            getLogger().debug("ComponentHandler initialized for: " + this.m_factory.getCreatedClass().getName());
        }
        else
        {
            getLogger().debug("ComponentHandler initialized for: " + this.m_instance.getClass().getName());
        }

        m_initialized = true;
    }

    /**
     * Get a reference of the desired Component
     */
    public final Component get()
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

        return m_instance;
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
    }

    /**
     * Dispose of the ComponentHandler and any associated Pools and Factories.
     */
    public void dispose()
    {
        m_disposed = true;

        try {
            if( null != m_factory )
            {
                m_factory.decommission( m_instance );
            }
            else
            {
                if( m_instance instanceof Stoppable )
                {
                    ((Stoppable)m_instance).stop();
                }

                if( m_instance instanceof Disposable )
                {
                    ((Disposable)m_instance).dispose();
                }
            }

            m_instance = null;
        }
        catch( final Exception e )
        {
            getLogger().warn( "Error decommissioning component: " +
                              m_factory.getCreatedClass().getName(), e );
        }
    }
}