/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kernel;

import java.net.URL;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.avalon.phoenix.components.application.Application;
import org.apache.log.Hierarchy;

/**
 * This is the structure describing each server application before it is loaded.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
final class SarEntry
{
    private SarMetaData     m_metaData;
    private Configuration   m_configuration;
    private ClassLoader     m_classLoader;
    private Application     m_application;
    private Hierarchy       m_hierarchy;

    protected SarEntry( final SarMetaData metaData, 
                        final ClassLoader classLoader,
                        final Hierarchy hierarchy,
                        final Configuration configuration )
    {
        m_metaData = metaData;
        m_classLoader = classLoader;
        m_hierarchy = hierarchy;
        m_configuration = configuration;
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

    public Hierarchy getHierarchy()
    {
        return m_hierarchy;
    }

    public ClassLoader getClassLoader()
    {
        return m_classLoader;
    }

    public Configuration getConfiguration()
    {
        return m_configuration;
    }
}
