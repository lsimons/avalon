/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metadata;

import java.io.File;

/**
 * MetaData for the application. 
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class SarMetaData
{
    private String                  m_name;
    private File                    m_homeDirectory;
    private BlockMetaData[]         m_blocks;
    private BlockListenerMetaData[] m_listeners;

    public SarMetaData( final String name,
                        final File homeDirectory,
                        final BlockMetaData[] blocks,
                        final BlockListenerMetaData[] listeners )
    {
        m_name = name;
        m_homeDirectory = homeDirectory;
        m_blocks = blocks;
        m_listeners = listeners;
    }

    public String getName()
    {
        return m_name;
    }

    public File getHomeDirectory()
    {
        return m_homeDirectory;
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
