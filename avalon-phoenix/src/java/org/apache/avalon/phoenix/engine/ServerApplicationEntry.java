/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine;

import java.io.File;
import org.apache.avalon.framework.camelot.Entry;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.log.Logger;

/**
 * This is the structure describing each server application before it is loaded.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ServerApplicationEntry
    extends Entry
{
    private Logger            m_logger;
    private File              m_homeDirectory;
    private Configuration     m_configuration;

    public File getHomeDirectory()
    {
        return m_homeDirectory;
    }

    public void setHomeDirectory( final File homeDirectory )
    {
        m_homeDirectory = homeDirectory;
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

    public void setConfiguration( final Configuration configuration )
    {
        m_configuration = configuration;
    }
}
