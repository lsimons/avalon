/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.command;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.avalon.excalibur.event.EventHandler;
import org.apache.avalon.excalibur.event.Source;
import org.apache.avalon.excalibur.thread.ThreadControl;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.excalibur.thread.impl.ResourceLimitingThreadPool;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * This is a ThreadManager which provides a threadpool per Sink per EventPipeline.
 *
 * ::NOTE:: This is not implemented yet!
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class TPSPThreadManager implements Runnable, ThreadManager
{
    private final ThreadPool m_threadPool;
    private final Mutex m_mutex = new Mutex();
    private final HashMap m_pipelines = new HashMap();
    private ThreadControl m_threadControl;
    private boolean m_done = false;
    private final long m_sleepTime;

    /**
     * The default constructor assumes there is a system property named "os.arch.cpus"
     * that has a default for the number of CPUs on a system.  Otherwise, the value
     * is 1.
     */
    public TPSPThreadManager()
    {
        this( 1, 1, 1000 );
    }

    /**
     * Constructor provides a specified number of threads per processor.  If
     * either value is less then one, then the value is rewritten as one.
     */
    public TPSPThreadManager( int numProcessors, int threadsPerProcessor, long sleepTime )
    {
        int processors = Math.max( numProcessors, 1 );
        int threads = Math.max( threadsPerProcessor, 1 );

        m_threadPool = new ResourceLimitingThreadPool( "TPCThreadManager",
                                                       ( processors * threads ) + 1, true, true, 1000L, 10L * 1000L );

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

            if( m_done )
            {
                m_threadControl = m_threadPool.execute( this );
            }
        }
        catch( InterruptedException ie )
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

            if( m_pipelines.isEmpty() )
            {
                m_done = true;
                m_threadControl.join( 1000 );
            }
        }
        catch( InterruptedException ie )
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
        catch( InterruptedException ie )
        {
            // ignore for now
        }
        finally
        {
            m_mutex.release();
        }
    }

    public void run()
    {
        while( !m_done )
        {
            try
            {
                m_mutex.acquire();

                Iterator i = m_pipelines.values().iterator();

                while( i.hasNext() )
                {
                    m_threadPool.execute( (PipelineRunner)i.next() );
                }
            }
            catch( InterruptedException ie )
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
            catch( InterruptedException ie )
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

            for( int i = 0; i < sources.length; i++ )
            {
                handler.handleEvents( sources[ i ].dequeueAll() );
            }
        }
    }
}