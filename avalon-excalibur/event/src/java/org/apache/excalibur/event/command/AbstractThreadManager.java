/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.excalibur.event.command;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.excalibur.thread.ThreadControl;
import org.apache.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.event.EventHandler;
import org.apache.excalibur.event.Source;

/**
 * Abstract base class for a ThreadManager that has a single ThreadPool for
 * all pipelines
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public abstract class AbstractThreadManager extends AbstractLogEnabled
    implements Runnable, ThreadManager, Initializable, Disposable
{
    private final Mutex m_mutex = new Mutex();
    private final HashMap m_pipelines = new HashMap();
    private final LinkedList m_controls = new LinkedList();
    private ThreadPool m_threadPool;
    private ThreadControl m_threadControl;
    private volatile boolean m_done = false;
    private long m_sleepTime = 1000L;
    private volatile boolean m_initialized = false;

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
                m_threadControl.join( 1000 );

                Iterator it = m_controls.iterator();

                while( it.hasNext() )
                {
                    ( (ThreadControl) it.next() ).join( 1000 );
                }

                m_pipelines.clear();

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
                        ThreadControl control = null;

                        while (control == null )
                        {
                            try
                            {
                                control = m_threadPool.execute( ( PipelineRunner ) i.next() );
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

                        m_controls.add(control);
                    }
                }
                finally
                {
                    m_mutex.release();
                }

                Thread.sleep( m_sleepTime );

                m_mutex.acquire();

                Iterator it = m_controls.iterator();

                while ( it.hasNext() )
                {
                    ThreadControl control = (ThreadControl) it.next();
                    if (control.isFinished()) it.remove();
                }

                m_mutex.release();
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
