/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.blocks;

import java.io.File;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.log.Logger;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.engine.facilities.ThreadManager;

/**
 * Context via which Blocks communicate with container.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultBlockContext
    extends DefaultContext
    implements BlockContext
{
    private ThreadManager  m_threadManager;
    private Logger         m_baseLogger;

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
        try
        {
            return (File)get( APP_HOME_DIR );
        }
        catch( final ContextException ce )
        {
            //Should never happen
            throw new RuntimeException( "Invalid block context" );
        }
    }

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    public String getName()
    {
        try
        {
            return (String)get( NAME );
        }
        catch( final ContextException ce )
        {
            //Should never happen
            throw new RuntimeException( "Invalid block context" );
        }
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
     * Retrieve logger coresponding to named category.
     *
     * @return the logger
     */
    public Logger getLogger( final String name )
    {
        return m_baseLogger.getChildLogger( name );
    }

    /**
     * Retrieve logger coresponding to root category of application.
     *
     * @return the base logger
     * @deprecated Use the getLogger(String) version
     */
    public Logger getBaseLogger()
    {
        return m_baseLogger;
    }
}
