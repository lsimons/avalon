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
    extends DefaultPool
    implements Resizable
{
    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory,
     * and can optionally have a controller.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory,
                                     final int min ) throws Exception
    {
        super(factory, null, min, min * 2);
    }

    /**
     * Create an SoftResourceLimitingPool.  The pool requires a factory,
     * and can optionally have a controller.
     */
    public SoftResourceLimitingPool( final ObjectFactory factory,
                                     final int min,
                                     final int max ) throws Exception
    {
        super(factory, null, min, max);
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
        super( factory, controller, min, max );
    }

    public void initialize()
    {
        grow( m_min );
    }

    public void grow( final int amount )
    {
        try
        {
            m_mutex.lock();

            for( int i = 0; i < amount; i++ )
            {
                try
                {
                    m_ready.add( this.newPoolable() );
                }
                catch( final Exception e )
                {
                    if( null != getLogger() )
                    {
                        getLogger().debug( m_factory.getCreatedClass().getName() +
                                           ": could not be instantiated.", e );
                    }
                }
            }
        }
        catch (InterruptedException ie)
        {
            getLogger().warn("Interrupted while waiting on lock", ie);
        }
        finally
        {
            m_mutex.unlock();
        }
    }

    public void shrink( final int amount )
    {
        try
        {
            m_mutex.lock();

            for( int i = 0; i < amount; i++ )
            {
                if( m_ready.size() > m_min )
                {
                    try
                    {
                        m_factory.decommission( m_ready.remove(0) );
                        m_count--;
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
        catch (InterruptedException ie)
        {
        }
        finally
        {
            m_mutex.unlock();
        }
    }
}
