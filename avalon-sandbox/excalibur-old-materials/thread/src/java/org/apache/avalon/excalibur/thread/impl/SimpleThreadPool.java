/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread.impl;

import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.excalibur.pool.SoftResourceLimitingPool;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.excalibur.thread.ThreadControl;
import org.apache.excalibur.thread.impl.AbstractThreadPool;
import org.apache.excalibur.thread.impl.WorkerThread;
import org.apache.excalibur.threadcontext.ThreadContext;

/**
 * The ThreadPool that binds to Legacy Pooling implementation.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @deprecated Only used by deprecated components. Will remove in the future
 */
class SimpleThreadPool
    extends AbstractThreadPool
    implements ObjectFactory, LogEnabled, Disposable, ThreadPool
{
    /**
     * The underlying pool.
     */
    private SoftResourceLimitingPool m_pool;

    /**
     * The logger to use for debugging purposes.
     */
    private Logger m_logger;

    /**
     * The base ThreadContext that can be duplicated for
     * each thread.
     */
    private ThreadContext m_context;

    /**
     * Create a new ThreadPool with specified capacity.
     *
     * @param threadGroup the thread group used in pool
     * @param capacity the capacity of pool
     * @throws Exception if unable to create pool
     */
    public SimpleThreadPool( final ThreadGroup threadGroup,
                             final int capacity )
        throws Exception
    {
        this( threadGroup, "Worker Pool", capacity );
    }

    /**
     * Create a new ThreadPool with specified capacity.
     *
     * @param threadGroup the thread group used in pool
     * @param name the name of pool (used in naming threads)
     * @param capacity the capacity of pool
     * @throws Exception if unable to create pool
     */
    public SimpleThreadPool( final ThreadGroup threadGroup,
                             final String name,
                             final int capacity )
        throws Exception
    {
        this( threadGroup, name, capacity );
    }


    /**
     * Create a new ThreadPool with specified capacity.
     *
     * @param threadGroup the thread group used in pool
     * @param name the name of pool (used in naming threads)
     * @param capacity the capacity of pool
     * @param context the thread context associated with pool (May be null).
     * @throws Exception if unable to create pool
     */
    public SimpleThreadPool( final ThreadGroup threadGroup,
                             final String name,
                             final int capacity,
                             final ThreadContext context )
        throws Exception
    {
        super( name, threadGroup );
        m_pool = new SoftResourceLimitingPool( this, capacity );
        m_context = context;
    }

    /**
     * Setup Logging.
     *
     * @param logger the logger
     */
    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
        m_pool.enableLogging( m_logger );
    }

    /**
     * Dispose of underlying pool and cleanup resources.
     */
    public void dispose()
    {
        m_pool.dispose();
        m_pool = null;
    }

    /**
     * Create new Poolable instance.
     *
     * @return the new Poolable instance
     */
    public Object newInstance()
    {
        return createWorker();
    }

    /**
     * Overide newWorkerThread to provide a WorkerThread
     * that is Poolable and LogEnabled.
     *
     * @param name the name of WorkerThread
     * @return the created WorkerThread
     */
    protected WorkerThread newWorkerThread( final String name )
    {
        ThreadContext context = null;
        if( null != m_context )
        {
            context = m_context.duplicate();
        }
        final SimpleWorkerThread thread =
            new SimpleWorkerThread( this, getThreadGroup(), name, context );
        ContainerUtil.enableLogging( thread, m_logger );
        return thread;
    }

    public void decommission( final Object object )
    {
        if( object instanceof WorkerThread )
        {
            destroyWorker( (WorkerThread) object );
        }
    }

    /**
     * Return the class of poolable instance.
     *
     * @return the class of poolable instance.
     */
    public Class getCreatedClass()
    {
        return SimpleWorkerThread.class;
    }

    /**
     * Run work in separate thread.
     * Return a valid ThreadControl to control work thread.
     *
     * @param work the work to be executed.
     * @return the ThreadControl
     */
    public ThreadControl execute( final Executable work )
    {
        return execute( new ExecutableExecuteable( work ) );
    }

    /**
     * Retrieve a worker thread from pool.
     *
     * @return the worker thread retrieved from pool
     */
    protected WorkerThread getWorker()
    {
        try
        {
            return (WorkerThread) m_pool.get();
        }
        catch( final Exception e )
        {
            throw new IllegalStateException( "Unable to access thread pool due to " + e );
        }
    }

    /**
     * Release worker back into pool.
     *
     * @param worker the worker (Should be a {@link SimpleWorkerThread}).
     */
    protected void releaseWorker( final WorkerThread worker )
    {
        m_pool.put( (SimpleWorkerThread)worker );
    }
}
