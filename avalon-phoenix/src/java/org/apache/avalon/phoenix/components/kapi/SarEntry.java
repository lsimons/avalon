/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kapi;

import java.io.File;
import java.net.URL;
import org.apache.avalon.excalibur.container.Entry;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.log.Logger;

/**
 * This is the structure describing each server application before it is loaded.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class SarEntry
    extends Entry
{
    private SarMetaData     m_metaData;
    private Logger          m_logger;
    private Configuration   m_configuration;
    private URL[]           m_classPath;

    public SarEntry( final SarMetaData metaData, 
                     final URL[] classPath,
                     final Configuration configuration )
    {
        m_metaData = metaData;
        m_classPath = classPath;
        m_configuration = configuration;
    }

    public URL[] getClassPath()
    {
        return m_classPath;
    }

    public SarMetaData getMetaData()
    {
        return m_metaData;
    }

    public Logger getLogger()
    {
        return m_logger;
    }

    public void setLogger( final Logger logger )
    {
        m_logger = logger;
    }

    public Configuration getConfiguration()
    {
        return m_configuration;
    }
}
