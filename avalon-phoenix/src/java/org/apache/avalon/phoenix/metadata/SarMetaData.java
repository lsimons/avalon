/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metadata;

import java.io.File;
import java.net.URL;

/**
 * This describes each server application.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class SarMetaData
{
    private File                    m_homeDirectory;
    private URL[]                   m_classPath;
    private BlockMetaData[]         m_blocks;
    private BlockListenerMetaData[] m_listeners;

    public SarMetaData( final File homeDirectory,
                        final URL[] classPath,
                        final BlockMetaData[] blocks,
                        final BlockListenerMetaData[] listeners )
    {
        m_homeDirectory = homeDirectory;
        m_classPath = classPath;
        m_blocks = blocks;
        m_listeners = listeners;
    }

    public File getHomeDirectory()
    {
        return m_homeDirectory;
    }

    public URL[] getClassPath()
    {
        return m_classPath;
    }

    public BlockMetaData[] getBlocks()
    {
        return m_blocks;
    }

    public BlockListenerMetaData[] getListeners()
    {
        return m_listeners;
    }
}
