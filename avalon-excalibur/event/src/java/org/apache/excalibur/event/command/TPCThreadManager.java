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
import org.apache.excalibur.thread.ThreadControl;
import org.apache.excalibur.thread.ThreadPool;
import org.apache.excalibur.thread.impl.DefaultThreadPool;
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
    private DefaultThreadPool m_tpool;
    private long m_blockTimeout = 1000L;
    private int m_processors = SystemUtil.numProcessors();
    private int m_threadsPerProcessor = 1;

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
          Math.max( parameters.getParameterAsInteger( "processors", SystemUtil.numProcessors() ),
                    1 );

        this.m_threadsPerProcessor =
            Math.max( parameters.getParameterAsInteger( "threads-per-processor", 1 ), 1 );

        setSleepTime( parameters.getParameterAsLong( "sleep-time", 1000L ) );
        this.m_blockTimeout = parameters.getParameterAsLong( "block-timeout", 1000L );
    }

    public void initialize() throws Exception
    {
        if( isInitialized() )
        {
            throw new IllegalStateException( "ThreadManager is already initailized" );
        }

        m_tpool = new DefaultThreadPool( "TPCThreadManager",
                                                  ( m_processors * m_threadsPerProcessor ) + 1 );

        if( null == getLogger() )
        {
            this.enableLogging( new NullLogger() );
        }

        m_tpool.enableLogging( getLogger() );

        setThreadPool( m_tpool );

        super.initialize();
    }

    public final void dispose()
    {
        super.dispose();

        m_tpool.dispose();
    }
}
