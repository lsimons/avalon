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

import java.util.Iterator;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;
import org.apache.commons.collections.StaticBucketMap;
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
    private final StaticBucketMap m_pipelines = new StaticBucketMap();
    private volatile boolean m_done = false;
    private final long m_sleepTime;
    private int m_threadsPerPool = 2;

    /**
     * The default constructor assumes there is a system property named "os.arch.cpus"
     * that has a default for the number of CPUs on a system.  Otherwise, the value
     * is 1.
     *
     * @throws Exception if there is any problems creating the ThreadManager
     */
    public TPSPThreadManager()
        throws Exception
    {
        this( 2, 1000 );
    }

    /**
     * Constructor provides a specified number of threads per processor.  If
     * either value is less then one, then the value is rewritten as one.
     *
     * @param maxThreadPerPool     The number of processors in the machine
     * @param sleepTime            The number of milliseconds to wait between cycles
     *
     * @throws Exception when there is a problem creating the ThreadManager
     */
    public TPSPThreadManager( int maxThreadPerPool, long sleepTime )
        throws Exception
    {
        m_threadsPerPool = maxThreadPerPool;

        m_sleepTime = sleepTime;

        Thread runner = new Thread(this);
        runner.setDaemon(true);
        runner.start();
    }

    /**
     * Register an EventPipeline with the ThreadManager.
     *
     * @param pipeline  The pipeline we are registering
     */
    public void register( EventPipeline pipeline )
    {
        m_pipelines.put( pipeline, new PipelineRunner( pipeline ) );

        if( m_done )
        {
            Thread runner = new Thread(this);
            runner.setDaemon(true);
            runner.start();
        }
    }

    /**
     * Deregister an EventPipeline with the ThreadManager
     *
     * @param pipeline  The pipeline to unregister
     */
    public void deregister( EventPipeline pipeline )
    {
        m_pipelines.remove( pipeline );

        if( m_pipelines.isEmpty() )
        {
            m_done = true;
        }
    }

    /**
     * Deregisters all EventPipelines from this ThreadManager
     */
    public void deregisterAll()
    {
        m_done = true;
        m_pipelines.clear();
    }

    public void run()
    {
        while( !m_done )
        {
            Iterator i = m_pipelines.values().iterator();

            while( i.hasNext() )
            {
                Thread runner = new Thread( (PipelineRunner)i.next() );
                runner.setDaemon(true);
                runner.start();
            }

            if (! m_done)
            {
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
    }

    /**
     * The PipelineRunner will run the pipelines
     */
    public static final class PipelineRunner implements Runnable
    {
        private final EventPipeline m_pipeline;

        /**
         * Create a PipelineRunner
         *
         * @param pipeline  The pipeline we are wrapping
         */
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
