/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.blocks;

import java.io.File;
import org.apache.framework.context.Context;
import org.apache.avalon.context.DefaultContext;
import org.apache.framework.thread.ThreadManager;
import org.apache.framework.thread.ThreadPool;
import org.apache.log.Logger;
import org.apache.phoenix.BlockContext;

/**
 * Context via which Blocks communicate with container.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultBlockContext
    extends DefaultContext
    implements BlockContext
{
    protected ThreadManager  m_threadManager;
    protected Logger         m_baseLogger;

    public DefaultBlockContext( final Logger logger, final ThreadManager threadManager )
    {
        this( logger, threadManager, null );
    }

    public DefaultBlockContext( final Logger logger,
                                final ThreadManager threadManager,
                                final Context context )
    {
        super( context );
        m_baseLogger = logger;
        m_threadManager = threadManager;
    }

    /**
     * Base directory of .sar application.
     *
     * @return the base directory
     */
    public File getBaseDirectory()
    {
        return (File)get( APP_HOME_DIR );
    }

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    public String getName()
    {
        return (String)get( NAME );
    }

    /**
     * Retrieve thread manager by category.
     * ThreadManagers are given names so that you can manage different thread
     * count to different components.
     *
     * @param category the category
     * @return the ThreadManager
     */
    public ThreadPool getThreadPool( final String category )
    {
        return m_threadManager.getThreadPool( category );
    }

    /**
     * Retrieve the default thread pool.
     * Equivelent to getThreadPool( "default" );
     * @return the ThreadPool
     */
    public ThreadPool getDefaultThreadPool()
    {
        return getThreadPool( "default" );
    }

    /**
     * Retrieve logger coresponding to root category of application.
     *
     * @return the base logger
     */
    public Logger getBaseLogger()
    {
        return m_baseLogger;
    }
}
