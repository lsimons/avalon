/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread.impl;

import org.apache.excalibur.threadcontext.ThreadContext;
import org.apache.avalon.excalibur.pool.ObjectFactory;
import org.apache.avalon.excalibur.pool.SoftResourceLimitingPool;
import org.apache.avalon.excalibur.thread.ThreadControl;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.framework.logger.Logger;

/**
 * This class is the public frontend for the thread pool code.
 *
 * TODO: Should this be configured with min threads, max threads and min spare threads ?
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DefaultThreadPool
    extends ThreadGroup
    implements ObjectFactory, Loggable, LogEnabled, Disposable, ThreadPool
{
    private SoftResourceLimitingPool m_pool;
    private int m_level;
    private Logger m_logger;
    private ThreadContext m_context;

    public DefaultThreadPool( final int capacity )
        throws Exception
    {
        this( "Worker Pool", capacity );
    }

    public DefaultThreadPool( final String name, final int capacity )
        throws Exception
    {
        this( name, capacity, null );
    }

    public DefaultThreadPool( final String name,
                              final int capacity,
                              final ThreadContext context )
        throws Exception
    {
        super( name );
        m_pool = new SoftResourceLimitingPool( this, capacity );
        m_context = context;
    }

    public void setLogger( final org.apache.log.Logger logger )
    {
        enableLogging( new LogKitLogger( logger ) );
    }

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
        m_pool.enableLogging( m_logger );
    }

    public void dispose()
    {
        m_pool.dispose();
        m_pool = null;
    }

    public Object newInstance()
    {
        final String name = new StringBuffer( getName() ).append( " Worker #" ).append( m_level++ ).toString();
        final WorkerThread worker = new WorkerThread( this, name, m_pool, m_context );
        worker.setDaemon( true );
        worker.enableLogging( m_logger );
        worker.start();
        return worker;
    }

    public void decommission( final Object object )
    {
        if( object instanceof WorkerThread )
        {
            ( (WorkerThread)object ).dispose();
        }
    }

    public Class getCreatedClass()
    {
        return WorkerThread.class;
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

    /**
     * Retrieve a worker thread from pool.
     *
     * @return the worker thread retrieved from pool
     * @exception Exception if an error occurs
     */
    protected WorkerThread getWorker()
    {
        try
        {
            final WorkerThread worker = (WorkerThread)m_pool.get();
            //TODO: Remove next line
            worker.setContextClassLoader( Thread.currentThread().getContextClassLoader() );
            return worker;
        }
        catch( final Exception e )
        {
            throw new IllegalStateException( "Unable to access thread pool due to " + e );
        }
    }
}
