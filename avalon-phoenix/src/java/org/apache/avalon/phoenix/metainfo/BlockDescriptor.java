/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metainfo;

import javax.swing.Icon;
import java.net.URL;
import java.util.Locale;
import org.apache.avalon.framework.Version;

/**
 * This descrbes information about the block that is used by administration 
 * tools during configuration and upgrade but is not neccesary for running.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockDescriptor
{
    private final Version         m_version;
    private final Contributor[]   m_contributors;
    private final Icon            m_icon;
    private final Icon            m_largeIcon;

    public BlockDescriptor( final Version version, 
                            final Contributor[] contributors,
                            final Icon icon,
                            final Icon largeIcon )
    {
        m_version = version;
        m_contributors = contributors;
        m_icon = icon;
        m_largeIcon = largeIcon;
    }

    /**
     * Get a list of contributors who helped create block.
     *
     * @return an array of Contributors
     */
    public Contributor[] getContributors()
    {
        return m_contributors;
    }

    /**
     * Get a 16x16 Color Icon for block.
     *
     * @return a 16x16 Color Icon for block
     */
    public Icon getIcon()
    {
        return m_icon;
    }

    /**
     * Get a 32x32 Color Icon for block.
     *
     * @return a 32x32 Color Icon for block
     */
    public Icon getLargeIcon()
    {
        return m_largeIcon;
    }

    /**
     * Retrieve Version of current Block.
     *
     * @return the version of block
     */
    public Version getVersion()
    {
        return m_version;
    }
}

