/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.component;

import java.util.ArrayList;
import java.util.List;
import org.apache.avalon.Disposable;
import org.apache.avalon.Initializable;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.avalon.thread.ThreadSafe;
import org.apache.excalibur.concurrent.Lock;
import org.apache.excalibur.pool.ObjectFactory;
import org.apache.excalibur.pool.Pool;
import org.apache.excalibur.pool.Poolable;
import org.apache.excalibur.pool.Recyclable;

/**
 * This is a implementation of <code>Pool</code> for SitemapComponents
 * that is thread safe.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:Giacomo.Pati@pwr.ch">Giacomo Pati</a>
 */
public class DefaultComponentPool
    extends AbstractLoggable
    implements Pool, Initializable, Disposable, Runnable, ThreadSafe
{
    public final static int  DEFAULT_POOL_SIZE     = 8;

    /** The resources that are currently free */
    protected List           m_availableResources  = new ArrayList();

    /** Resources that have been allocated out of the pool */
    protected List           m_usedResources       = new ArrayList();

    private Lock             m_mutex               = new Lock();

    private boolean          m_initialized;
    private boolean          m_disposed;
    private Thread           m_initializationThread;
    protected ObjectFactory  m_factory;
    protected int            m_initial             = DEFAULT_POOL_SIZE/2;
    protected int            m_maximum             = DEFAULT_POOL_SIZE;

    public DefaultComponentPool( final ObjectFactory factory )
        throws Exception
    {
        init( factory, DEFAULT_POOL_SIZE/2, DEFAULT_POOL_SIZE );
    }

    public DefaultComponentPool( final ObjectFactory factory,
                                 final int initial )
        throws Exception
    {
        init( factory, initial, initial );
    }

    public DefaultComponentPool( final ObjectFactory factory,
                                 final int initial,
                                 final int maximum )
        throws Exception
    {
        init( factory, initial, maximum );
    }

    private void init( final ObjectFactory factory,
                       final int initial,
                       final int maximum )
        throws Exception
    {
        m_factory = factory;
        m_initial = initial;
        m_maximum = maximum;
    }

    public void init()
        throws Exception
    {
        m_initializationThread = new Thread( this );
        m_initializationThread.start();
    }

    public void run()
    {
        try
        {
            m_mutex.lock();

            for( int i = 0; i < m_initial; i++ )
            {
                try
                {
                    m_availableResources.add( m_factory.newInstance() );
                }
                catch( final Exception e )
                {
                    getLogger().warn( "Could not create poolable resource", e );
                }
            }

            if( m_availableResources.size() > 0 )
            {
                m_initialized = true;
            }
        }
        catch( final Exception e )
        {
            getLogger().debug( "ComponentPool.run()", e );
        }
        finally
        {
            m_mutex.unlock();
        }
    }

    public void dispose()
    {
        try
        {
            m_mutex.lock();
            m_disposed = true;

            while( !m_availableResources.isEmpty() )
            {
                m_availableResources.remove( 0 );
            }
        }
        catch( final Exception e )
        {
            getLogger().debug( "ComponentPool.dispose()", e );
        }
        finally
        {
            m_mutex.unlock();
        }
    }

    /**
     * Allocates a resource when the pool is empty. By default, this method
     * returns null, indicating that the requesting. This
     * allows a thread pool to expand when necessary, allowing for spikes in
     * activity.
     *
     * @return A new resource
     */
    protected Poolable getOverflowResource()
        throws Exception
    {
        final Poolable poolable = (Poolable)m_factory.newInstance();
        getLogger().debug( "Component Pool - creating Overflow Resource:" +
                           " Resource=" + poolable +
                           " Available=" + m_availableResources.size() +
                           " Used=" + m_usedResources.size() );
        return poolable;
    }

    /** Requests a resource from the pool.
     * No extra information is associated with the allocated resource.
     * @return The allocated resource
     */
    public Poolable get()
        throws Exception
    {
        if( !m_initialized )
        {
            if( null == m_initializationThread )
            {
                throw new IllegalStateException( "You cannot get a resource before " +
                                                 "the pool is initialized" );
            }
            else
            {
                m_initializationThread.join();
                m_initializationThread = null;
            }
        }

        if( m_disposed )
        {
            throw new IllegalStateException("You cannot get a resource after the pool is disposed");
        }

        Poolable resource = null;

        try
        {
            m_mutex.lock();
            // See if there is a resource in the pool already

            if( m_availableResources.size() > 0 )
            {
                resource = (Poolable)m_availableResources.remove( 0 );

                m_usedResources.add( resource );
            }
            else
            {
                resource = getOverflowResource();

                if( null != resource )
                {
                    m_usedResources.add( resource );
                }
            }
        }
        catch( final Exception e )
        {
            getLogger().debug( "ComponentPool.get()", e );
        }
        finally
        {
            m_mutex.unlock();
        }

        if( null == resource )
        {
            throw new RuntimeException( "Could not get the component from the pool" );
        }

        return resource;
    }

    /** Releases a resource back to the pool of available resources
     * @param resource The resource to be returned to the pool
     */
    public void put( Poolable resource )
    {
        int pos = -1;

        try
        {
            m_mutex.lock();

            // Make sure the resource is in the used list
            pos = m_usedResources.indexOf( resource );

            if( resource instanceof Recyclable )
            {
                ((Recyclable)resource).recycle();
            }

            // If the resource was in the used list, remove it from the used list and
            // add it back to the free list
            if( pos >= 0 )
            {
                m_usedResources.remove( pos );

                if( m_availableResources.size() < m_maximum )
                {
                    // If the available resources are below the maximum add this back.
                    m_availableResources.add( resource );
                }
                else
                {
                    // If the available are above the maximum destroy this resource.
                    try
                    {
                        m_factory.decommission( resource );

                        getLogger().debug( "Component Pool - decommissioning Overflow Resource:" +
                                           " Resource=" + resource +
                                           " Available=" + m_availableResources.size() +
                                           " Used=" + m_usedResources.size() );
                        resource = null;
                    }
                    catch( final Exception e )
                    {
                        throw new RuntimeException( "caught exception decommissioning " +
                                                    "resource: " + resource);
                    }
                }
            }
        }
        catch( final Exception e )
        {
            getLogger().debug( "ComponentPool.put()", e );
        }
        finally
        {
            m_mutex.unlock();
        }
    }
}
