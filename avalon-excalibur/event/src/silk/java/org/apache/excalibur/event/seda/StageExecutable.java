/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda;

import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.event.command.ThreadManager;

/**
 * A stage executable wraps the stage event pipeline and
 * an associated thread manager reference. The stage executable
 * is used by the stage manager to register and deregister the 
 * execution for the stages it set up.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
final class StageExecutable extends AbstractLogEnabled
    implements Initializable, Startable, Disposable
{
    /** The thread manager for the pipeline */
    private final ThreadManager m_threadManager;

    /** The pipelines to execute */
    private final StagePipeline m_eventPipeline;

    private boolean m_disposed = false;
    private boolean m_stopped = true;

    //--------------------- StageExecutable constructor
    /**
     * Constructor that creates a pipeline wrapper 
     * with the defined stage, thread manager, main sink
     * and sink map.
     * @since May 14, 2002
     * 
     * @param eventPipeline
     *  The pipelined stage
     * @param threadManager
     *  The thread manager to run the pipelines
     */
    StageExecutable(StagePipeline eventPipeline, ThreadManager threadManager)
    {
        m_threadManager = threadManager;
        m_eventPipeline = eventPipeline;
    }

    //----------------------- Startable implementation
    /**
     * @see Startable#start()
     */
    public void start() throws Exception
    {
        if (m_disposed)
        {
            throw new CascadingException("Pipeline already disposed");
        }

        m_stopped = false;
        m_threadManager.register(m_eventPipeline);
    }

    /**
     * @see Startable#stop()
     */
    public void stop() throws Exception
    {
        if (m_disposed)
        {
            throw new CascadingException("Pipeline already disposed");
        }

        m_threadManager.deregister(m_eventPipeline);
        m_stopped = true;
    }

    //----------------------- Initializable implementation
    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        ContainerUtil.enableLogging(m_eventPipeline, getLogger());
        ContainerUtil.initialize(m_eventPipeline);
        ContainerUtil.start(m_eventPipeline);
    }
    
    //----------------------- Disposable implementation
    /**
     * @see Disposable#dispose()
     */
    public void dispose()
    {
        if (m_disposed)
        {
            throw new RuntimeException("Pipeline already disposed");
        }

        if (!m_stopped)
        {
            try
            {
                stop();
            }
            catch (Exception e)
            {
                // ignore
            }
        }

        // perform a shutdown of the stage pipeline
        try
        {
            ContainerUtil.shutdown(m_eventPipeline);
        }
        catch (Exception e)
        {
            // ignore
        }
        m_disposed = true;
    }

    //------------------------ StageExecutable specific implementation
    /**
     * Returns the queues associated with this stage by 
     * extracting the queues from the stage pipelines.
     * Used to construct the sink map.
     * @since May 14, 2002
     * 
     * @return {@link Queue}[]
     *  the queues associated with the stage.
     */
    Queue getEventQueue()
    {
        return m_eventPipeline.getQueue();
    }

    /**
     * Returns the threadManager associated with the stage
     * and event pipeline.
     * @since Sep 17, 2002
     * 
     * @return ThreadManager
     *  The threadManager associated with the stage and event 
     *  pipeline.
     */
    ThreadManager getThreadManager()
    {
        return m_threadManager;
    }

}
