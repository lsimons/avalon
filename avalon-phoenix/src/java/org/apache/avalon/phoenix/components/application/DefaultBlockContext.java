/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import java.io.File;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * Context via which Blocks communicate with container.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
final class DefaultBlockContext
    implements BlockContext
{
    private String m_name;
    private ApplicationContext m_applicationContext;

    protected DefaultBlockContext( final String name,
                                   final ApplicationContext frame )
    {
        m_name = name;
        m_applicationContext = frame;
    }

    public Object get( Object key )
        throws ContextException
    {
        final SarMetaData metaData = m_applicationContext.getMetaData();
        if( BlockContext.APP_NAME.equals( key ) )
        {
            return metaData.getName();
        }
        else if( BlockContext.APP_HOME_DIR.equals( key ) )
        {
            return metaData.getHomeDirectory();
        }
        else if( BlockContext.NAME.equals( key ) )
        {
            return m_name;
        }
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
        return m_applicationContext.getMetaData().getHomeDirectory();
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

    public void requestShutdown()
    {
        m_applicationContext.requestShutdown();
    }

    /**
     * Retrieve logger coresponding to named category.
     *
     * @return the logger
     */
    public Logger getLogger( final String name )
    {
        return m_applicationContext.getLogger( getName() ).getChildLogger( name );
    }
}
