/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool;

import org.apache.avalon.activity.Initializable;
import org.apache.avalon.thread.SingleThreaded;

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class SingleThreadedPool
    implements Pool, SingleThreaded, Resizable
{
    protected int                     m_count;
    protected Poolable[]              m_pool;
    protected ObjectFactory           m_factory;
    protected PoolController          m_controller;
    protected int                     m_maximum;
    protected int                     m_initial;

    public SingleThreadedPool( final ObjectFactory factory,
                               final PoolController controller,
                               final int initial,
                               final int maximum ) throws Exception
    {
        m_count = 0;
        m_factory = factory;
        m_controller = controller;
        m_maximum = maximum;
        m_initial = initial;

        if( !(this instanceof Initializable) )
        {
            initialize();
        }
    }

    public void initialize()
        throws Exception
    {
        grow( m_maximum );
        fill( m_initial );
    }

    /**
     * Retrieve an object from pool.
     *
     * @return an object from Pool
     */
    public Poolable get() throws Exception
    {
        if( null == m_pool && null != m_controller )
        {
            final int increase = m_controller.grow();
            if( increase > 0 ) grow( increase );
        }

        if( 0 == m_count )
        {
            return (Poolable)m_factory.newInstance();
        }

        m_count--;

        final Poolable poolable = m_pool[ m_count ];
        m_pool[ m_count ] = null;
        return poolable;
    }

    /**
     * Place an object in pool.
     *
     * @param poolable the object to be placed in pool
     */
    public void put( final Poolable poolable )
    {
        if( poolable instanceof Recyclable )
        {
            ((Recyclable)poolable).recycle();
        }

        if(  m_pool.length == (m_count + 1) && null != m_controller )
        {
            final int decrease = m_controller.shrink();
            if( decrease > 0 ) shrink( decrease );
        }

        if ( m_pool.length > m_count + 1 )
        {
            m_pool[ m_count++ ] = poolable;
        }
    }

    /**
     * Return the total number of slots in Pool
     *
     * @return the total number of slots
     */
    public final int getCapacity()
    {
        return m_pool.length;
    }

    /**
     * Get the number of used slots in Pool
     *
     * @return the number of used slots
     */
    public final int getSize()
    {
        return m_count;
    }

    /**
     * This fills the pool to the size specified in parameter.
     */
    public final void fill( final int fillSize ) throws Exception
    {
        final int size = Math.min( m_pool.length, fillSize );

        for( int i = m_count; i < size; i++ )
        {
            m_pool[i] = (Poolable)m_factory.newInstance();
        }

        m_count = size;
    }

    /**
     * This fills the pool by the size specified in parameter.
     */
    public final void grow( final int increase )
    {
        if( null == m_pool )
        {
            m_pool = new Poolable[ increase ];
            return;
        }

        final Poolable[] poolables = new Poolable[ increase + m_pool.length ];
        System.arraycopy( m_pool, 0, poolables, 0, m_pool.length );
        m_pool = poolables;
    }

    /**
     * This shrinks the pool by parameter size.
     */
    public final void shrink( final int decrease )
    {
        final Poolable[] poolables = new Poolable[ m_pool.length - decrease ];
        System.arraycopy( m_pool, 0, poolables, 0, poolables.length );
        m_pool = poolables;
    }
}
