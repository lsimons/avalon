/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.kernel;

import java.io.File;
import java.util.Map;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * This is the structure describing each server application before it is loaded.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
final class SarEntry
{
    private final SarMetaData m_metaData;
    private final ClassLoader m_classLoader;
    private final Logger m_logger;
    private final File m_workDirectory;
    private final Map m_classLoaders;
    private Application m_application;

    protected SarEntry( final SarMetaData metaData,
                        final File workDirectory,
                        final ClassLoader classLoader,
                        final Logger logger,
                        final Map classLoaders )
    {
        if( null == metaData )
        {
            throw new NullPointerException( "metaData" );
        }
        if( null == classLoader )
        {
            throw new NullPointerException( "classLoader" );
        }
        if( null == logger )
        {
            throw new NullPointerException( "logger" );
        }
        if( null == workDirectory )
        {
            throw new NullPointerException( "workDirectory" );
        }
        if( null == classLoaders )
        {
            throw new NullPointerException( "classLoaders" );
        }

        m_metaData = metaData;
        m_classLoader = classLoader;
        m_logger = logger;
        m_workDirectory = workDirectory;
        m_classLoaders = classLoaders;
    }

    public File getWorkDirectory()
    {
        return m_workDirectory;
    }

    public Application getApplication()
    {
        return m_application;
    }

    public void setApplication( final Application application )
    {
        m_application = application;
    }

    public SarMetaData getMetaData()
    {
        return m_metaData;
    }

    public Logger getLogger()
    {
        return m_logger;
    }

    public ClassLoader getClassLoader()
    {
        return m_classLoader;
    }

    public Map getClassLoaders()
    {
        return m_classLoaders;
    }
}
