/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.command;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.avalon.excalibur.thread.*;
import org.apache.avalon.excalibur.thread.impl.ResourceLimitingThreadPool;
import org.apache.avalon.excalibur.util.SystemUtil;

import org.apache.avalon.excalibur.event.Source;
import org.apache.avalon.excalibur.event.EventHandler;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This is a ThreadManager that uses a certain number of threads per processor.
 * The number of threads in the pool is a direct proportion to the number of
 * processors.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class TPCThreadManager implements Runnable, ThreadManager, Disposable
{
    private final ThreadPool    m_threadPool;
    private final Mutex         m_mutex = new Mutex();
    private final HashMap       m_pipelines = new HashMap();
    private       ThreadControl m_threadControl;
    private       boolean       m_done = false;
    private final long          m_sleepTime;

    /**
     * The default constructor assumes there is a system property named "os.arch.cpus"
     * that has a default for the number of CPUs on a system.  Otherwise, the value
     * is 1.
     */
    public TPCThreadManager()
    {
        this( SystemUtil.numProcessors() );
    }

    /**
     * Constructor provides one thread per number of processors.
     */
    public TPCThreadManager( int numProcessors )
    {
        this( numProcessors, 1 );
    }

    /**
     * Constructor provides a specified number of threads per processor.  If
     * either value is less then one, then the value is rewritten as one.
     */
    public TPCThreadManager( int numProcessors, int threadsPerProcessor )
    {
        this( numProcessors, threadsPerProcessor, 1000 );
    }

    /**
     * Constructor provides a specified number of threads per processor.  If
     * either value is less then one, then the value is rewritten as one.
     */
    public TPCThreadManager( int numProcessors, int threadsPerProcessor, long sleepTime )
    {
        int processors = Math.max( numProcessors, 1 );
        int threads = Math.max( threadsPerProcessor, 1 );

        ResourceLimitingThreadPool tpool = new ResourceLimitingThreadPool( "TPCThreadManager",
                ( processors * threads ) + 1, true, true, 1000L, 10L * 1000L );
        tpool.enableLogging( new NullLogger() );
        m_threadPool = tpool;

        m_sleepTime = sleepTime;
        m_threadControl = m_threadPool.execute( this );
    }

    /**
     * Register an EventPipeline with the ThreadManager.
     */
    public void register( EventPipeline pipeline )
    {
        try
        {
            m_mutex.acquire();

            m_pipelines.put( pipeline, new PipelineRunner( pipeline ) );

            if ( m_done )
            {
                m_threadControl = m_threadPool.execute( this );
            }
        }
        catch ( InterruptedException ie )
        {
            // ignore for now
        }
        finally
        {
            m_mutex.release();
        }
    }

    /**
     * Deregister an EventPipeline with the ThreadManager
     */
    public void deregister( EventPipeline pipeline )
    {
        try
        {
            m_mutex.acquire();

            m_pipelines.remove( pipeline );

            if ( m_pipelines.isEmpty() )
            {
                m_done = true;
                m_threadControl.join( 1000 );
            }
        }
        catch ( InterruptedException ie )
        {
            // ignore for now
        }
        finally
        {
            m_mutex.release();
        }
    }

    /**
     * Deregisters all EventPipelines from this ThreadManager
     */
    public void deregisterAll()
    {
        try
        {
            m_mutex.acquire();

            m_done = true;
            m_pipelines.clear();

            m_threadControl.join( 1000 );
        }
        catch ( InterruptedException ie )
        {
            // ignore for now
        }
        finally
        {
            m_mutex.release();
        }
    }

    public final void dispose()
    {
        deregisterAll();
        if ( m_threadPool instanceof Disposable )
        {
            ( (Disposable) m_threadPool ).dispose();
        }

        m_threadControl = null;
    }

    public void run()
    {
        while ( ! m_done )
        {
            try
            {
                m_mutex.acquire();

                Iterator i = m_pipelines.values().iterator();

                while ( i.hasNext() )
                {
                    m_threadPool.execute( (PipelineRunner) i.next() );
                }
            }
            catch ( InterruptedException ie )
            {
                // ignore for now
            }
            finally
            {
                m_mutex.release();
            }

            try
            {
                Thread.sleep( m_sleepTime );
            }
            catch ( InterruptedException ie )
            {
               // ignore and continue processing
            }
        }
    }

    public final static class PipelineRunner implements Runnable
    {
        private final EventPipeline m_pipeline;

        protected PipelineRunner( EventPipeline pipeline )
        {
            m_pipeline = pipeline;
        }

        public void run()
        {
            Source[] sources = m_pipeline.getSources();
            EventHandler handler = m_pipeline.getEventHandler();

            for (int i = 0; i < sources.length; i++)
            {
                handler.handleEvents( sources[i].dequeueAll() );
            }
        }
    }
}