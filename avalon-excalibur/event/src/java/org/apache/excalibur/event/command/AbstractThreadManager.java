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
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.event.EventHandler;
import org.apache.excalibur.event.Source;

/**
 * Abstract base class for a ThreadManager that has a single ThreadPool for all pipelines
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public abstract class AbstractThreadManager extends AbstractLogEnabled
    implements Runnable, ThreadManager, Initializable, Disposable
{
    private final Mutex m_mutex = new Mutex();
    private final HashMap m_pipelines = new HashMap();
    private ThreadPool m_threadPool;
    private ThreadControl m_threadControl;
    private boolean m_done = false;
    private long m_sleepTime = 1000L;
    private boolean m_initialized = false;

    protected boolean isInitialized()
    {
        return m_initialized;
    }

    protected void setSleepTime( long sleepTime )
    {
        m_sleepTime = sleepTime;
    }

    protected void setThreadPool( ThreadPool threadPool )
    {
        if( null == m_threadPool )
        {
            m_threadPool = threadPool;
        }
        else
        {
            throw new IllegalStateException( "Can only set thread pool once" );
        }
    }

    public void initialize() throws Exception
    {
        if( null == m_threadPool )
        {
            throw new IllegalStateException( "No thread pool set" );
        }

        this.m_threadControl = this.m_threadPool.execute( this );
        this.m_initialized = true;
    }

    /**
     * Register an EventPipeline with the ThreadManager.
     */
    public void register( EventPipeline pipeline )
    {
        if( !isInitialized() )
        {
            throw new IllegalStateException( "ThreadManager must be initialized before "
                                             + "registering a pipeline" );
        }

        try
        {
            m_mutex.acquire();

            try
            {
                PipelineRunner runner = new PipelineRunner( pipeline );
                runner.enableLogging( getLogger() );
                m_pipelines.put( pipeline, runner );

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
        if( !isInitialized() )
        {
            throw new IllegalStateException( "ThreadManager must be initialized before "
                                             + "deregistering a pipeline" );
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
        if( !isInitialized() )
        {
            throw new IllegalStateException( "ThreadManager must be initialized "
                                             + "before deregistering pipelines" );
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

    public void dispose()
    {
        deregisterAll();

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
                                                  + "increase block-timeout or number of threads "
                                                  + "per processor", e );
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
        catch( RuntimeException e )
        {
            if( getLogger().isFatalErrorEnabled() )
            {
                getLogger().fatalError( "ThreadManager management thread aborting "
                                        + " due to exception", e );
            }

            throw e;
        }
    }

    public static final class PipelineRunner
        extends AbstractLogEnabled
        implements Runnable
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
                try
                {
                    handler.handleEvents( sources[i].dequeueAll() );
                }
                catch( RuntimeException e )
                {
                    // We want to catch this, because this is the only
                    // place where exceptions happening in this thread
                    // can be logged

                    if( getLogger().isErrorEnabled() )
                    {
                        getLogger().error( "Exception processing EventPipeline [msg: "
                                           + e.getMessage() + "]", e );
                    }
                }
            }
        }
    }
}
