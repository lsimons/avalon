/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.component;

import org.apache.avalon.Disposable;
import org.apache.avalon.Initializable;
import org.apache.avalon.Stoppable;
import org.apache.avalon.component.Component;
import org.apache.avalon.component.ComponentManager;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.context.Context;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.avalon.thread.ThreadSafe;
import org.apache.excalibur.pool.Poolable;
import org.apache.log.Logger;

/**
 * The DefaultComponentHandler to make sure components are initialized
 * and destroyed correctly.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/04/18 13:16:36 $
 */
class DefaultComponentHandler
    extends AbstractLoggable
    implements Initializable, Disposable
{
    /** Indicates that the Handler is holding a <code>ThreadSafe</code> Component */
    private final static int           THREADSAFE      = 0;

    /** Indicates that the Handler is holding a <code>Poolable</code> Component */
    private final static int           POOLABLE        = 1;

    /** Indicates that the Handler is holding a <code>SingleThreaded</code> Component */
    private final static int           SINGLETHREADED  = 2;

    /** The instance of the ComponentFactory that creates and disposes of the Component */
    private DefaultComponentFactory    m_factory;

    /** The pool of components for <code>Poolable</code> Components */
    private DefaultComponentPool       m_pool;

    /** The instance of the Component for <code>ThreadSafe</code> Components */
    private Component                  m_instance;

    /** The type of the Component: THREADSAFE, POOLABLE, or SINGLETHREADED */
    private final int                  m_type;

    /** State management boolean stating whether the Handler is initialized or not */
    private boolean                    m_initialized   = false;

    /** State management boolean stating whether the Handler is disposed or not */
    private boolean                    m_disposed      = false;

    /**
     * Create a ComponentHandler that takes care of hiding the details of
     * whether a Component is ThreadSafe, Poolable, or SingleThreaded.
     * It falls back to SingleThreaded if not specified.
     */
    DefaultComponentHandler( final Class componentClass,
                             final Configuration config,
                             final ComponentManager manager,
                             final Context context,
                             final RoleManager roles )
        throws Exception
    {
        m_factory = new DefaultComponentFactory( componentClass, config, manager, context, roles );

        if( Poolable.class.isAssignableFrom( componentClass ) )
        {
            m_pool = new DefaultComponentPool( m_factory );
            m_type = POOLABLE;
        }
        else if( ThreadSafe.class.isAssignableFrom( componentClass ) )
        {
            m_type = THREADSAFE;
        }
        else
        {
            m_type = SINGLETHREADED;
        }
    }

    /**
     * Create a ComponentHandler that takes care of hiding the details of
     * whether a Component is ThreadSafe, Poolable, or SingleThreaded.
     * It falls back to SingleThreaded if not specified.
     */
    DefaultComponentHandler( final Component component )
        throws Exception
    {
        m_type = THREADSAFE;
        m_instance = component;
    }

    /**
     * Sets the logger that the ComponentHandler will use.
     */
    public void setLogger( final Logger logger )
    {
        if( null != m_factory )
        {
            m_factory.setLogger( logger );
        }

        if( null != m_pool )
        {
            m_pool.setLogger( logger );
        }

        super.setLogger( logger );
    }

    /**
     * Initialize the ComponentHandler.
     */
    public void init()
    {
        if( m_initialized ) return;

        switch( m_type )
        {
        case THREADSAFE:
            try
            {
                if( null == m_instance )
                {
                    m_instance = (Component)m_factory.newInstance();
                }
            }
            catch( final Exception e )
            {
                getLogger().error( "Cannot use component: " +
                                   m_factory.getCreatedClass().getName(), e );
            }
            break;

        case POOLABLE:
            try
            {
                m_pool.init();
            }
            catch( Exception e )
            {
                getLogger().error( "Cannot use component: " + m_factory.getCreatedClass().getName(), e );
            }
            break;

        default:
            // Nothing to do for SingleThreaded Components
            break;
        }

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

        Component component = null;

        switch( m_type )
        {
        case THREADSAFE:
            component = m_instance;
            break;

        case POOLABLE:
            component = (Component)m_pool.get();
            break;

        default:
            component = (Component)m_factory.newInstance();
            break;
        }

        return component;
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

        switch( m_type )
        {
        case THREADSAFE:
            // Nothing to do for ThreadSafe Components
            break;

        case POOLABLE:
            m_pool.put( (Poolable)component );
            break;

        default:
            try
            {
                m_factory.decommission( component );
            }
            catch( final Exception e )
            {
                getLogger().warn( "Error decommissioning component: " +
                                  m_factory.getCreatedClass().getName(), e);
            }
            break;
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
            switch( m_type )
            {
            case THREADSAFE:
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
                break;

            case POOLABLE:
                if( m_pool instanceof Disposable )
                {
                    ((Disposable)m_pool).dispose();
                }

                m_pool = null;
                break;

            default:
                // do nothing here
                break;
            }

            if( m_factory instanceof Disposable )
            {
                ((Disposable)m_factory).dispose();
            }
            m_factory = null;
        }
        catch( final Exception e )
        {
            getLogger().warn( "Error decommissioning component: " +
                              m_factory.getCreatedClass().getName(), e );
        }
    }
}
