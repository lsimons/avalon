/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.excalibur.util.SystemUtil;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

/**
 * This is a ThreadManager that uses a certain number of threads per
 * processor.  The number of threads in the pool is a direct proportion to
 * the number of processors. The size of the thread pool is (processors
 * threads-per-processor) + 1
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public final class TPCThreadManager extends AbstractThreadManager implements Parameterizable
{
    private PooledExecutor m_threadPool;
    private int m_processors = -1;
    private int m_threadsPerProcessor = 1;
    private boolean m_hardShutdown = false;

    /**
     * The following parameters can be set for this class:
     *
     * <table>
     *   <tr>
     *     <th>Name</th> <th>Description</th> <th>Default Value</th>
     *   </tr>
     *   <tr>
     *     <td>processors</td>
     *     <td>Number of processors (autodetected if less than one)</td>
     *     <td>Results from SystemUtil.numProcessors()</td>
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
     *     <td>force-shutdown</td>
     *     <td>At shutdown time, allow currently queued tasks to finish, or immediately quit</td>
     *     <td>false</td>
     *   </tr>
     * </table>
     *
     * @param parameters  The Parameters object
     *
     * @throws ParameterException if there is a problem with the parameters.
     */
    public void parameterize( Parameters parameters ) throws ParameterException
    {
        m_processors = Math.max(1, parameters.getParameterAsInteger( "processors", 1 ) );

        m_threadsPerProcessor =
            Math.max( parameters.getParameterAsInteger( "threads-per-processor", 1 ), 1 );

        setSleepTime( parameters.getParameterAsLong( "sleep-time", 1000L ) );

        m_hardShutdown = ( parameters.getParameterAsBoolean( "force-shutdown", false ) );
    }

    public void initialize() throws Exception
    {
        if( m_processors < 1 )
        {
            m_processors = Math.max( 1, SystemUtil.numProcessors() );
        }

        if( isInitialized() )
        {
            throw new IllegalStateException( "ThreadManager is already initailized" );
        }

        m_threadPool = new PooledExecutor( m_processors + 1 );
        m_threadPool.setMinimumPoolSize( 2 ); // at least two threads
        m_threadPool.setMaximumPoolSize( ( m_processors * m_threadsPerProcessor ) + 1 );
        m_threadPool.setKeepAliveTime( getSleepTime() );
        m_threadPool.waitWhenBlocked();

        if( null == getLogger() )
        {
            this.enableLogging( new NullLogger() );
        }

        setExecutor( m_threadPool );

        super.initialize();
    }

    protected final void doDispose()
    {
        if ( m_hardShutdown )
        {
            m_threadPool.shutdownNow();
        }
        else
        {
            m_threadPool.shutdownAfterProcessingCurrentlyQueuedTasks();
        }

        try
        {
            m_threadPool.awaitTerminationAfterShutdown( getSleepTime() );
        }
        catch (InterruptedException ie)
        {
            getLogger().warn("Thread pool took longer than " + getSleepTime() +
                 " ms to shut down", ie);
        }
    }
}
