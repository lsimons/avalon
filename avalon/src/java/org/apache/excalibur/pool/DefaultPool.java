/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool;

import org.apache.avalon.activity.Disposable;

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 * Please note that this pool offers no resource limiting whatsoever.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultPool
    extends AbstractPool
    implements Disposable
{
    protected int                  m_min;
    protected int                  m_max;
    protected PoolController       m_controller;
    protected boolean              m_disposed = false;

    public DefaultPool( final ObjectFactory factory,
                        final PoolController controller )
        throws Exception
    {
        this( factory, controller, AbstractPool.DEFAULT_POOL_SIZE, AbstractPool.DEFAULT_POOL_SIZE );
    }

    public DefaultPool( final ObjectFactory factory,
                        final PoolController controller,
                        final int initial,
                        final int maximum )
        throws Exception
    {
        super( factory );

        int t_max = maximum;
        int t_min = initial;

        if( t_min < 0 )
        {
            if( null != getLogger() )
            {
                getLogger().warn( "Minumum number of poolables specified is " +
                                  "less than 0, using 0" );
            }

            t_min = 0;
        }

        if( ( t_max < t_min ) || ( t_max < 1 ) )
        {
            if( null != getLogger() )
            {
                getLogger().warn( "Maximum number of poolables specified must be at " +
                                  "least 1 and must be greater than the minumum number " +
                                  "of connections" );
            }
            t_max = ( t_min > 1 ) ? t_min : 1;
        }

        m_max = t_max;
        m_min = t_min;

        if (controller != null)
        {
            this.m_controller = controller;
        }
        else
        {
            this.m_controller = new DefaultPoolController(t_min / 2);
        }
    }

    public DefaultPool( final ObjectFactory factory )
        throws Exception
    {
        this( factory, null, AbstractPool.DEFAULT_POOL_SIZE, AbstractPool.DEFAULT_POOL_SIZE );
    }

    public DefaultPool( final Class clazz, final int initial, final int maximum )
        throws NoSuchMethodException, Exception
    {
        this( new DefaultObjectFactory( clazz ), null, initial, maximum );
    }

    public DefaultPool( final Class clazz, final int initial )
        throws NoSuchMethodException, Exception
    {
        this( clazz, initial, initial );
    }

    /**
     * This is the method to override when you need to enforce creational
     * policies.
     */
    protected Poolable newPoolable() throws Exception
    {
        m_count++;
        return (Poolable) this.m_factory.newInstance();
    }

    public final Poolable get() throws Exception
    {
        Poolable obj = null;

        if (this.m_initialized == false)
        {
            throw new IllegalStateException("You cannot get a Poolable before the pool is initialized");
        }

        if (this.m_disposed)
        {
            throw new IllegalStateException("You cannot get a Poolable after the pool is disposed");
        }

        try
        {
            m_mutex.lock();

            if (this.m_ready.size() == 0)
            {
                if (this instanceof Resizable) {
                    m_mutex.unlock();

                    ((Resizable)this).grow(this.m_controller.grow());

                    m_mutex.lock();
                }
                else
                {
                    obj = this.newPoolable();
                }
            }
            else
            {
                obj = (Poolable) this.m_ready.remove(0);
            }

            this.m_active.add(obj);

            return obj;
        }
        finally
        {
            m_mutex.unlock();
        }
    }

    public final void put( final Poolable obj )
    {
        if (this.m_initialized == false)
        {
            throw new IllegalStateException("You cannot get a Poolable before the pool is initialized");
        }

        try
        {
            m_mutex.lock();

            this.m_active.remove(this.m_active.indexOf( obj ));

            if (this.m_disposed == false)
            {
                this.m_ready.add(obj);

                if ((m_count > m_max) && (this instanceof Resizable))
                {
                    m_mutex.unlock();
                    ((Resizable)this).shrink(this.m_controller.shrink());
                    m_mutex.lock();
                }
            }
            else
            {
                this.m_factory.decommission(obj);
                m_count--;
            }
        }
        catch (Exception e)
        {
            getLogger().warn("Pool interrupted while waiting for lock.", e);
        }
        finally
        {
            m_mutex.unlock();
        }
    }

    public final void dispose()
    throws Exception
    {
        try
        {
            m_mutex.lock();

            while (m_ready.size() > 0) {
                m_factory.decommission(m_ready.remove(0));
                m_count--;
            }
        }
        finally
        {
            m_mutex.unlock();
        }
    }
}
