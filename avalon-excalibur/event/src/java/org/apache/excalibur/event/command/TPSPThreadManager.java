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

import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.avalon.excalibur.thread.ThreadControl;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.excalibur.thread.impl.ResourceLimitingThreadPool;
import org.apache.excalibur.event.EventHandler;
import org.apache.excalibur.event.Source;

/**
 * This is a <code>ThreadManager</code> which provides a threadpool per
 * <code>Sink</code> per <code>EventPipeline</code>.
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
                handler.handleEvents( sources[ i ].dequeueAll() );
            }
        }
    }
}
