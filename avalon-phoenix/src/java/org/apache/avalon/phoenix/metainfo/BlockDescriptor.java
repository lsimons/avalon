/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metainfo;

import org.apache.avalon.framework.Version;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockDescriptor
{
    private final String m_classname;
    private final Version m_version;
    //private final ConfigSchema    m_schema;

    public BlockDescriptor( final String classname, final Version version )
    {
        m_classname = classname;
        m_version = version;
    }

    /**
     * Retrieve the Class Name of Block.
     *
     * @return the Class Name of block
     */
    public String getClassName()
    {
        return m_classname;
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

