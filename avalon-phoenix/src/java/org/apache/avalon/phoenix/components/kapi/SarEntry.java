/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kapi;

import java.net.URL;
import org.apache.avalon.excalibur.container.Entry;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * This is the structure describing each server application before it is loaded.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class SarEntry
    extends Entry
{
    private SarMetaData     m_metaData;
    private Configuration   m_configuration;
    private ClassLoader     m_classLoader;

    public SarEntry( final SarMetaData metaData, 
                     final ClassLoader classLoader,
                     final Configuration configuration )
    {
        m_metaData = metaData;
        m_classLoader = classLoader;
        m_configuration = configuration;
    }

    public ClassLoader getClassLoader()
    {
        return m_classLoader;
    }

    public SarMetaData getMetaData()
    {
        return m_metaData;
    }

    public Configuration getConfiguration()
    {
        return m_configuration;
    }
}
