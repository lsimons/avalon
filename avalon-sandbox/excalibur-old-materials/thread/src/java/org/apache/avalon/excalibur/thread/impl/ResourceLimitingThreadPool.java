/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread.impl;

import org.apache.avalon.excalibur.lang.ThreadContext;
import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.excalibur.pool.ResourceLimitingPool;
import org.apache.avalon.excalibur.thread.ThreadControl;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * A Thread Pool which can be configured to have a hard limit on the maximum number of threads
 *  which will be allocated.  This is very important for servers to avoid running out of system
 *  resources.  The pool can be configured to block for a new thread or throw an exception.
 *  The maximum block time can also be set.
 *
 * Based on org.apache.avalon.excalibur.thread.impl.DefaultThreadPool in the Excalibur
 *  sandbox.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:06 $
 * @since 4.1
 */
public class ResourceLimitingThreadPool
    extends ThreadGroup
    implements ObjectFactory, LogEnabled, Disposable, ThreadPool
{

    private ResourceLimitingPool m_pool;
    private int m_level;
    private Logger m_logger;
    private ThreadContext m_context;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/

    /**
     * Creates a new <code>ResourceLimitingThreadPool</code>.
     *
     * @param max Maximum number of Poolables which can be stored in the pool, 0 implies no limit.
     */
    public ResourceLimitingThreadPool( final int max )
    {
        this( "Worker Pool", max );
    }

    /**
     * Creates a new <code>ResourceLimitingThreadPool</code> with maxStrict enabled,
     *  blocking enabled, no block timeout and a trim interval of 10 seconds.
     *
     * @param name Name which will used as the thread group name as well as the prefix of the
     *  names of all threads created by the pool.
     * @param max Maximum number of WorkerThreads which can be stored in the pool,
     *  0 implies no limit.
     */
    public ResourceLimitingThreadPool( final String name, final int max )
    {
        this( name, max, true, true, 0, 10000 );
    }

    /**
     * Creates a new <code>ResourceLimitingThreadPool</code>.
     *
     * @param name Name which will used as the thread group name as well as the prefix of the
     *  names of all threads created by the pool.
     * @param max Maximum number of WorkerThreads which can be stored in the pool,
     *  0 implies no limit.
     * @param maxStrict true if the pool should never allow more than max WorkerThreads to
     *  be created.  Will cause an exception to be thrown if more than max WorkerThreads are
     *  requested and blocking is false.
     * @param blocking true if the pool should cause a thread calling get() to block when
     *  WorkerThreads are not currently available on the pool.
     * @param blockTimeout The maximum amount of time, in milliseconds, that a call to get() will
     *  block before an exception is thrown.  A value of 0 implies an indefinate wait.
     * @param trimInterval The minimum interval with which old unused WorkerThreads will be
     *  removed from the pool.  A value of 0 will cause the pool to never trim WorkerThreads.
     */
    public ResourceLimitingThreadPool( final String name,
                                       final int max,
                                       final boolean maxStrict,
                                       final boolean blocking,
                                       final long blockTimeout,
                                       final long trimInterval )
    {
        this( name, max, maxStrict, blocking, blockTimeout, trimInterval, null );
    }

    /**
     * Creates a new <code>ResourceLimitingThreadPool</code>.
     *
     * @param name Name which will used as the thread group name as well as the prefix of the
     *  names of all threads created by the pool.
     * @param max Maximum number of WorkerThreads which can be stored in the pool,
     *  0 implies no limit.
     * @param maxStrict true if the pool should never allow more than max WorkerThreads to
     *  be created.  Will cause an exception to be thrown if more than max WorkerThreads are
     *  requested and blocking is false.
     * @param blocking true if the pool should cause a thread calling get() to block when
     *  WorkerThreads are not currently available on the pool.
     * @param blockTimeout The maximum amount of time, in milliseconds, that a call to get() will
     *  block before an exception is thrown.  A value of 0 implies an indefinate wait.
     * @param trimInterval The minimum interval with which old unused WorkerThreads will be
     *  removed from the pool.  A value of 0 will cause the pool to never trim WorkerThreads.
     * @param context ThreadContext
     */
    public ResourceLimitingThreadPool( final String name,
                                       final int max,
                                       final boolean maxStrict,
                                       final boolean blocking,
                                       final long blockTimeout,
                                       final long trimInterval,
                                       final ThreadContext context )
    {
        super( name );

        m_pool = new ResourceLimitingPool
            ( this, max, maxStrict, blocking, blockTimeout, trimInterval );
        m_context = context;
    }

    /*---------------------------------------------------------------
     * ObjectFactory Methods
     *-------------------------------------------------------------*/

    /**
     * Creates and returns a new <code>WorkerThread</code>.
     *
     * @return new worker thread
     *
     */
    public Object newInstance()
    {
        final String name =
            new StringBuffer( getName() ).append( " Worker #" ).append( m_level++ ).toString();
        final WorkerThread worker = new WorkerThread( this, name, m_pool, m_context );
        worker.setDaemon( true );
        worker.enableLogging( m_logger );
        worker.start();

        return worker;
    }

    /**
     * Returns the class of which this <code>ObjectFactory</code> creates instances.
     *
     * @return WorkerThread.class
     *
     */
    public Class getCreatedClass()
    {
        return WorkerThread.class;
    }

    /**
     * Cleans up any resources associated with the specified object and takes it
     * out of commission.
     *
     * @param object the object to be decommissioned
     *
     */
    public void decommission( final Object object )
    {
        if( object instanceof WorkerThread )
        {
            ( (WorkerThread)object ).dispose();
        }
    }

    /*---------------------------------------------------------------
     * LogEnabled Methods
     *-------------------------------------------------------------*/
    /**
     * Set the logger.
     *
     * @param logger
     *
     */
    public void enableLogging( final Logger logger )
    {
        m_logger = logger;

        m_pool.enableLogging( m_logger );
    }

    /*---------------------------------------------------------------
     * Disposable Methods
     *-------------------------------------------------------------*/
    /**
     * Clean up resources and references.
     *
     */
    public void dispose()
    {
        m_pool.dispose();

        m_pool = null;
    }

    /*---------------------------------------------------------------
     * ThreadPool Methods
     *-------------------------------------------------------------*/
    /**
     * Run work in separate thread.
     * Return a valid ThreadControl to control work thread.
     *
     * @param work the work to be executed.
     * @return the ThreadControl
     */
    public ThreadControl execute( final Runnable work )
    {
        return execute( new ExecutableRunnable( work ) );
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
        final WorkerThread worker = getWorker();

        return worker.execute( work );
    }

    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Retrieve a worker thread from pool.
     *
     * @return the worker thread retrieved from pool
     *
     * @todo remove the line:
     * <code>worker.setContextClassLoader(Thread.currentThread().getContextClassLoader());</code>
     */
    protected WorkerThread getWorker()
    {
        try
        {
            final WorkerThread worker = (WorkerThread)m_pool.get();

            // TODO: Remove next line
            worker.setContextClassLoader( Thread.currentThread().getContextClassLoader() );

            return worker;
        }
        catch( final Exception e )
        {
            throw new IllegalStateException( "Unable to access thread pool due to " + e );
        }
    }
}
