/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.event.command;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.avalon.excalibur.thread.ThreadControl;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.excalibur.thread.impl.ResourceLimitingThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.excalibur.event.EventHandler;
import org.apache.excalibur.event.Source;
import org.apache.excalibur.util.SystemUtil;

/**
 * This is a ThreadManager that uses a certain number of threads per processor.
 * The number of threads in the pool is a direct proportion to the number of
 * processors. The size of the thread pool is (processors * threads-per-processor) + 1
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public final class TPCThreadManager extends AbstractLogEnabled
  implements Runnable, ThreadManager, Parameterizable, Initializable, Disposable
{
    private final Mutex m_mutex = new Mutex();
    private final HashMap m_pipelines = new HashMap();

    private ThreadPool m_threadPool;
    private ThreadControl m_threadControl;
    private boolean m_done = false;

    //Set reasonable defaults in case parameterize() is never called.
    private long m_sleepTime = 1000L;
    private long m_blockTimeout = 1000L;
    private int m_processors = SystemUtil.numProcessors();
    private int m_threadsPerProcessor = 1;

    private boolean m_initialized = false;

    /**
     * The following parameters can be set for this class:
     *
     * <table>
     *   <tr>
     *     <th>Name</th> <th>Description</td> <th>Default Value</th>
     *   </tr>
     *   <tr>
     *     <td>processors</td>
     *     <td>Number of processors (Rewritten to 1 if less than one)</td>
     *     <td>System property named "os.arch.cpus", otherwise 1</td>
     *   </tr>
     *   <tr>
     *     <td>threads-per-processor</td>
     *     <td>Threads per processor to use (Rewritten to 1 if less than one)</td>
     *     <td>1</td>
     *   </tr>
     *   <tr>
     *     <td>sleep-time</td>
     *     <td>Time (in milliseconds) to wait between queue pipeline processing runs</td>
     *     <td>1000</td>
     *   </tr>
     *   <tr>
     *     <td>block-timeout</td>
     *     <td>Time (in milliseconds) to wait for a thread to process a pipeline</td>
     *     <td>1000</td>
     *   </tr>
     * </table>
     */
    public void parameterize( Parameters parameters ) throws ParameterException
    {
        this.m_processors =
          Math.max( parameters.getParameterAsInteger( "processors", SystemUtil.numProcessors() ), 1 );

        this.m_threadsPerProcessor = Math.max( parameters.getParameterAsInteger( "threads-per-processor", 1 ), 1 );
        this.m_sleepTime = parameters.getParameterAsLong( "sleep-time", 1000L );
        this.m_blockTimeout = parameters.getParameterAsLong( "block-timeout", 1000L );
    }

    public void initialize() throws Exception
    {
        if( this.m_initialized )
        {
            throw new IllegalStateException( "ThreadManager is already initailized" );
        }

        final ResourceLimitingThreadPool tpool =
          new ResourceLimitingThreadPool( "TPCThreadManager",
                                          ( m_processors * m_threadsPerProcessor ) + 1,
                                          true,
                                          true,
                                          this.m_blockTimeout,
                                          10L * 1000L );

        if( null == getLogger() )
        {
            this.enableLogging( new NullLogger() );
        }

        tpool.enableLogging( getLogger() );

        this.m_threadPool = tpool;
        this.m_threadControl = this.m_threadPool.execute( this );
        this.m_initialized = true;
    }

    /**
     * Register an EventPipeline with the ThreadManager.
     */
    public void register( EventPipeline pipeline )
    {
        if( !this.m_initialized )
        {
            throw new IllegalStateException( "ThreadManager must be initialized before registering a pipeline" );
        }

        try
        {
            m_mutex.acquire();

            try
            {
                m_pipelines.put( pipeline, new PipelineRunner( pipeline ) );

                if( m_done )
                {
                    m_threadControl = m_threadPool.execute( this );
                }
            }
            finally
            {
                m_mutex.release();
            }
        }
        catch( InterruptedException ie )
        {
            // ignore for now
        }
    }

    /**
     * Deregister an EventPipeline with the ThreadManager
     */
    public void deregister( EventPipeline pipeline )
    {
        if( !this.m_initialized )
        {
            throw new IllegalStateException( "ThreadManager must be initialized before deregistering a pipeline" );
        }

        try
        {
            m_mutex.acquire();

            try
            {
                m_pipelines.remove( pipeline );

                if( m_pipelines.isEmpty() )
                {
                    m_done = true;
                    m_threadControl.join( 1000 );
                }
            }
            finally
            {
                m_mutex.release();
            }
        }
        catch( InterruptedException ie )
        {
            // ignore for now
        }
    }

    /**
     * Deregisters all EventPipelines from this ThreadManager
     */
    public void deregisterAll()
    {
        if( !this.m_initialized )
        {
            throw new IllegalStateException( "ThreadManager must be initialized before deregistering pipelines" );
        }

        try
        {
            m_mutex.acquire();
            try
            {
                m_done = true;
                m_pipelines.clear();

                m_threadControl.join( 1000 );
            }
            finally
            {
                m_mutex.release();
            }
        }
        catch( InterruptedException ie )
        {
            // ignore for now
        }
    }

    public final void dispose()
    {
        deregisterAll();

        if( m_threadPool instanceof Disposable )
        {
            ( ( Disposable ) m_threadPool ).dispose();
        }

        m_threadControl = null;
    }

    public void run()
    {
        try
        {
            while( !m_done )
            {
                m_mutex.acquire();

                try
                {
                    Iterator i = m_pipelines.values().iterator();

                    while( i.hasNext() )
                    {
                        try
                        {
                            m_threadPool.execute( ( PipelineRunner ) i.next() );
                        }
                        catch( IllegalStateException e )
                        {
                            // that's the way ResourceLimitingThreadPool reports
                            // that it has no threads available, will still try
                            // to go on, hopefully at one point there will be
                            // a thread to execute our runner

                            if( getLogger().isWarnEnabled() )
                            {
                                getLogger().warn( "Unable to execute pipeline (If out of threads, "
                                                  + "increase block-timeout or number of threads per processor", e );
                            }
                        }
                        catch( RuntimeException e )
                        {
                            //We want to catch this, because if an unexpected runtime exception comes through a single
                            //pipeline it can bring down the primary thread

                            if( getLogger().isErrorEnabled() )
                            {
                                getLogger().error( "Exception processing EventPipeline [msg: " + e.getMessage() + "]",
                                                   e );
                            }
                        }
                    }
                }
                finally
                {
                    m_mutex.release();
                }

                Thread.sleep( m_sleepTime );
            }
        }
        catch( InterruptedException e )
        {
            Thread.interrupted();
        }
    }

    public static final class PipelineRunner implements Runnable
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
                handler.handleEvents( sources[i].dequeueAll() );
            }
        }
    }
}
