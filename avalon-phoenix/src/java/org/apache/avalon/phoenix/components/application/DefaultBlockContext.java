/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import java.io.File;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * Context via which Blocks communicate with container.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
final class DefaultBlockContext
    implements BlockContext, LogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultBlockContext.class );

    private String m_name;
    private ApplicationContext m_frame;
    private Logger m_logger;
    private boolean m_warningEmitted;

    protected DefaultBlockContext( final String name, final ApplicationContext frame )
    {
        m_name = name;
        m_frame = frame;
    }

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    public Object get( Object key )
        throws ContextException
    {
        final SarMetaData metaData = m_frame.getMetaData();
        if( BlockContext.APP_NAME.equals( key ) ) return metaData.getName();
        else if( BlockContext.APP_HOME_DIR.equals( key ) ) return metaData.getHomeDirectory();
        else if( BlockContext.NAME.equals( key ) ) return m_name;
        else
        {
            throw new ContextException( "Unknown key: " + key );
        }
    }

    /**
     * Base directory of .sar application.
     *
     * @return the base directory
     */
    public File getBaseDirectory()
    {
        return m_frame.getMetaData().getHomeDirectory();
    }

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    public String getName()
    {
        return m_name;
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
        return new LogKitLogger( m_frame.getLogger( getName() ).getChildLogger( name ) );
    }

    /**
     * Retrieve logger coresponding to root category of application.
     *
     * @return the base logger
     * @deprecated Use the getLogger(String) version
     */
    public Logger getBaseLogger()
    {
        return new LogKitLogger( m_frame.getLogger( getName() ) );
    }
}
