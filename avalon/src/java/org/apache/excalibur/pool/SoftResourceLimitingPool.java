/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool;

import org.apache.avalon.activity.Initializable;

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class SoftResourceLimitingPool 
    extends AbstractPool 
    implements Resizable
{
    protected final PoolController m_controller;

    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory,
     * and can optionally have a controller.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory,
                                     final int min ) throws Exception
    {
        super(factory, min, min * 2);
        this.m_controller = null;
    }

    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory,
     * and can optionally have a controller.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory,
                                     final int min,
                                     final int max ) throws Exception
    {
        super(factory, min, max);
        this.m_controller = null;
    }

    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory,
     * and can optionally have a controller.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory,
                                     final PoolController controller,
                                     final int min,
                                     final int max ) 
        throws Exception
    {
        super( factory, min, max );
        m_controller = controller;
    }

    public void initialize() 
    {
        grow( m_min );
    }

    public synchronized void grow( final int amount )
    {
        for( int i = 0; i < amount; i++ ) 
        {
            try
            {
                m_ready.push( m_factory.newInstance() );
                m_currentCount++;
            } 
            catch( final Exception e )
            {
                if( null != getLogger() )
                {
                    getLogger().debug( m_factory.getCreatedClass().getName() +
                                       ": could not be instantiated.", e );
                }
            }
            
            notify();
        }
    }

    public synchronized void shrink( final int amount )
    {
        for( int i = 0; i < amount; i++ )
        {
            if( m_ready.size() > m_min )
            {
                try
                {
                    m_factory.decommission( m_ready.pop() );
                    m_currentCount--;
                } 
                catch( final Exception e )
                {
                    if( null != getLogger() )
                    {
                        getLogger().debug( m_factory.getCreatedClass().getName() +
                                           ": improperly decommissioned.", e );
                    }
                }
            }
        }
    }

    public Poolable get() 
        throws Exception
    {
        if( m_ready.size() == 0 )
        {
            grow( (null == m_controller) ? m_max - m_min : m_controller.grow() );
        }

        return super.get();
    }

    public void put( final Poolable poolable )
    {
        if( m_ready.size() > m_max )
        {
            shrink( (null == m_controller) ? m_ready.size() - (m_max + 1) : m_controller.shrink() );
        }

        super.put( poolable );
    }
}
