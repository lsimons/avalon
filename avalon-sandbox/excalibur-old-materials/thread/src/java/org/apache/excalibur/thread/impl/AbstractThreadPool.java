/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.thread.impl;

import org.apache.excalibur.thread.Executable;
import org.apache.excalibur.thread.ThreadControl;
import org.apache.excalibur.thread.ThreadPool;

/**
 * This is the base class of all ThreadPools.
 * Sub-classes should implement the abstract methods to
 * retrieve and return Threads to the pool.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public abstract class AbstractThreadPool
    implements ThreadPool
{
    /**
     * The thread group associated with pool.
     */
    private final ThreadGroup m_threadGroup;

    /**
     * The name of the thread pool.
     * Used in naming threads.
     */
    private final String m_name;

    /**
     * A Running number that indicates the number
     * of threads created by pool. Starts at 0 and
     * increases.
     */
    private int m_level;

    /**
     * Create a ThreadPool with the specified name.
     *
     * @param name the name of thread pool (appears in thread group
     *             and thread names)
     * @throws Exception if unable to create pool
     */
    public AbstractThreadPool( final String name,
                               final ThreadGroup threadGroup )
        throws Exception
    {
        if( null == name )
        {
            throw new NullPointerException( "name" );
        }
        if( null == threadGroup )
        {
            throw new NullPointerException( "threadGroup" );
        }

        m_name = name;
        m_threadGroup = threadGroup;
    }

    /**
     * Destroy a worker thread by scheduling it for shutdown.
     *
     * @param thread the worker thread
     */
    protected void destroyWorker( final WorkerThread thread )
    {
        thread.dispose();
    }

    /**
     * Create a WorkerThread and start it up.
     *
     * @return the worker thread.
     */
    protected WorkerThread createWorker()
    {
        final String name = m_name + " Worker #" + m_level++;

        final WorkerThread worker = newWorkerThread( name );
        worker.setDaemon( true );
        worker.start();
        return worker;
    }

    /**
     * Create a new worker for pool.
     *
     * @param name the name of worker
     * @return the new WorkerThread
     */
    protected WorkerThread newWorkerThread( final String name )
    {
        return new WorkerThread( this, m_threadGroup, name );
    }

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
     * Execute some executable work in a thread.
     *
     * @param work the work
     * @return the ThreadControl
     */
    public ThreadControl execute( final Executable work )
    {
        final WorkerThread worker = getWorker();
        return worker.execute( work );
    }

    /**
     * Get the name used for thread pool.
     * (Used in naming threads).
     *
     * @return the thread pool name
     */
    protected String getName()
    {
        return m_name;
    }

    /**
     * Return the thread group that thread pool is associated with.
     *
     * @return the thread group that thread pool is associated with.
     */
    protected ThreadGroup getThreadGroup()
    {
        return m_threadGroup;
    }

    /**
     * Retrieve a worker thread from pool.
     *
     * @return the worker thread retrieved from pool
     */
    protected abstract WorkerThread getWorker();

    /**
     * Return the WorkerThread to the pool.
     *
     * @param worker the worker thread to put back in pool
     */
    protected abstract void releaseWorker( final WorkerThread worker );
}
