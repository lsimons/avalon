/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.thread;

import org.apache.avalon.logger.Loggable;
import org.apache.excalibur.pool.ObjectFactory;
import org.apache.excalibur.pool.Poolable;
import org.apache.excalibur.pool.SoftResourceLimitingPool;
import org.apache.log.Logger;

/**
 * This class is the public frontend for the thread pool code.
 *
 * TODO: Should this be configured with min threads, max threads and min spare threads ?
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultThreadPool
    extends ThreadGroup
    implements ObjectFactory, Loggable, ThreadPool
{
    protected final SoftResourceLimitingPool  m_pool;
    protected int                             m_level;
    protected Logger                          m_logger;

    public DefaultThreadPool( final int capacity )
        throws Exception
    {
        this( "Worker Pool", capacity );
    }

    public DefaultThreadPool( final String name, final int capacity )
        throws Exception
    {
        super( name );
        m_pool = new SoftResourceLimitingPool( this, 0 );
        m_pool.init();
    }

    public void setLogger( final Logger logger )
    {
        m_logger = logger;
    }

    public Object newInstance()
    {
        final WorkerThread worker =
            new WorkerThread( this, this, m_pool, getName() + " Worker #" + m_level++ );
        worker.setLogger( m_logger );
        worker.start();
        return worker;
    }

    public void decommission( final Object object )
    {
        if( object instanceof WorkerThread )
        {
            ((WorkerThread)object).dispose();
        }
    }

    public Class getCreatedClass()
    {
        return WorkerThread.class;
    }

    /**
     * Run work in separate thread.
     *
     * @param work the work to be executed.
     * @exception Exception if an error occurs
     */
    public void execute( final Runnable work )
        throws Exception
    {
        execute( work, Thread.NORM_PRIORITY );
    }

    /**
     * Run work in separate thread at a particular priority.
     *
     * @param work the work to be executed.
     * @param priority the priority
     * @exception Exception if an error occurs
     */
    public void execute( final Runnable work, final int priority )
        throws Exception
    {
        final WorkerThread worker = getWorker( priority );
        worker.execute( work );
    }

    /**
     * Run work in separate thread.
     * Wait till work is complete before returning.
     *
     * @param work the work to be executed.
     * @exception Exception if an error occurs
     */
    public void executeAndWait( final Runnable work )
        throws Exception
    {
        executeAndWait( work, Thread.NORM_PRIORITY );
    }

    /**
     * Run work in separate thread at a particular priority.
     * Wait till work is complete before returning.
     *
     * @param work the work to be executed.
     * @param priority the priority
     * @exception Exception if an error occurs
     */
    public void executeAndWait( final Runnable work, final int priority )
        throws Exception
    {
        final WorkerThread worker = getWorker( priority );
        worker.executeAndWait( work );
    }

    protected WorkerThread getWorker( final int priority )
        throws Exception
    {
        final WorkerThread worker = (WorkerThread)m_pool.get();
        worker.setContextClassLoader( Thread.currentThread().getContextClassLoader() );
        worker.setPriority( priority );
        return worker;
    }
}
