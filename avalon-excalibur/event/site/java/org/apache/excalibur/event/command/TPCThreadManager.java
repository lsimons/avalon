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
public final class TPCThreadManager extends AbstractThreadManager implements Parameterizable
{
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

        setThreadPool( tpool );

        super.initialize();
    }
}
