/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool;

import java.util.ArrayList;
import java.util.List;
import org.apache.avalon.excalibur.collections.Buffer;
import org.apache.avalon.excalibur.collections.VariableSizeBuffer;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This is an <code>Pool</code> that caches Poolable objects for reuse.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.5 $ $Date: 2002/08/07 16:31:38 $
 * @since 4.0
 */
public abstract class AbstractPool
    extends AbstractLogEnabled
    implements Pool, ThreadSafe
{
    public static final int DEFAULT_POOL_SIZE = 8;
    protected final ObjectFactory m_factory;
    protected List m_active = new ArrayList();
    protected Buffer m_ready = new VariableSizeBuffer();
    protected Mutex m_mutex = new Mutex();
    protected boolean m_initialized = false;
    protected int m_min;

    /**
     * Create an AbstractPool.  The pool requires a factory, and can
     * optionally have a controller.
     */
    public AbstractPool( final ObjectFactory factory ) throws Exception
    {
        m_factory = factory;

        if( !( this instanceof Initializable ) )
        {
            initialize();
        }
    }

    protected void initialize()
        throws Exception
    {
        lock();

        for( int i = 0; i < AbstractPool.DEFAULT_POOL_SIZE; i++ )
        {
            this.m_ready.add( this.newPoolable() );
        }

        m_initialized = true;

        unlock();
    }

    protected final void lock()
        throws InterruptedException
    {
        m_mutex.acquire();
    }

    protected final void unlock()
        throws InterruptedException
    {
        m_mutex.release();
    }

    /**
     * This is the method to override when you need to enforce creational
     * policies.
     */
    protected Poolable newPoolable() throws Exception
    {
        Object obj = m_factory.newInstance();
        return (Poolable)obj;
    }

    /**
     * This is the method to override when you need to enforce destructional
     * policies.
     */
    protected void removePoolable( Poolable poolable )
    {
        try
        {
            m_factory.decommission( poolable );
        }
        catch( Exception e )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Error decommissioning object", e );
            }
        }
    }

    public final int size()
    {
        synchronized( this )
        {
            // this is actually not 100% correct as the pool should always
            // reflect the current size (i.e. m_ready.size()) and not the
            // total size.
            return this.m_active.size() + this.m_ready.size();
        }
    }

    public abstract Poolable get() throws Exception;

    public abstract void put( Poolable object );

    protected void internalGrow( final int amount )
        throws Exception
    {
        for( int i = 0; i < amount; i++ )
        {
            try
            {
                m_ready.add( newPoolable() );
            }
            catch( final Exception e )
            {
                if( null != getLogger() && getLogger().isDebugEnabled() )
                {
                    Class createdClass = m_factory.getCreatedClass();
                    if( createdClass == null )
                    {
                        getLogger().debug( "factory created class was null so a new "
                                           + "instance could not be created.", e );
                    }
                    else
                    {
                        getLogger().debug( createdClass.getName() +
                                           ": could not be instantiated.", e );
                    }
                }
            }
        }
    }

    protected void internalShrink( final int amount )
        throws Exception
    {
        for( int i = 0; i < amount; i++ )
        {
            if( m_ready.size() > m_min )
            {
                try
                {
                    this.removePoolable( (Poolable)m_ready.remove() );
                }
                catch( final Exception e )
                {
                    if( null != getLogger() && getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( m_factory.getCreatedClass().getName() +
                                           ": improperly decommissioned.", e );
                    }
                }
            }
        }
    }
}
