/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.application;

import java.io.File;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.log.Logger;

/**
 * Context via which Blocks communicate with container.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
final class DefaultBlockContext
    extends DefaultContext
    implements BlockContext, Loggable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultBlockContext.class );

    private ApplicationContext  m_frame;
    private Logger              m_logger;
    private boolean             m_warningEmitted;

    protected DefaultBlockContext( final String name, final ApplicationContext frame )
    {
        super( (Context)null );
        m_frame = frame;

        put( BlockContext.APP_NAME, frame.getMetaData().getName() );
        put( BlockContext.APP_HOME_DIR, frame.getMetaData().getHomeDirectory() );
        put( BlockContext.NAME, name );
    }

    public void setLogger( final Logger logger )
    {
        m_logger = logger;
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
            throw new IllegalStateException();
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
            throw new IllegalStateException();
        }
    }

    /**
     * Retrieve thread pool by category.
     * ThreadPools are given names so that you can manage different thread
     * count to different components.
     *
     * @param category the category
     * @return the ThreadPool
     */
    public ThreadPool getThreadPool( final String category )
    {
        if( !m_warningEmitted )
        {
            final String message = REZ.getString( "context.warn.threadpool", getName() );
            m_logger.warn( message );
            System.err.println( message );
            m_warningEmitted = true;
        }

        return m_frame.getThreadPool( category );
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
        return m_frame.getLogger( getName() ).getChildLogger( name );
    }

    /**
     * Retrieve logger coresponding to root category of application.
     *
     * @return the base logger
     * @deprecated Use the getLogger(String) version
     */
    public Logger getBaseLogger()
    {
        return m_frame.getLogger( getName() );
    }
}
