/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metadata;

import org.apache.avalon.phoenix.metainfo.BlockInfo;

/**
 * This is the structure describing each block.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class BlockMetaData
{
    private final String m_name;
    private final String m_classname;
    private final DependencyMetaData[] m_dependencies;

    private BlockInfo m_blockInfo;

    public BlockMetaData( final String name,
                          final String classname,
                          final DependencyMetaData[] dependencies,
                          final BlockInfo blockInfo )
    {
        m_name = name;
        m_classname = classname;
        m_dependencies = dependencies;
        m_blockInfo = blockInfo;
    }

    public String getName()
    {
        return m_name;
    }

    public String getClassname()
    {
        return m_classname;
    }

    public BlockInfo getBlockInfo()
    {
        return m_blockInfo;
    }

    public DependencyMetaData getDependency( final String name )
    {
        for( int i = 0; i < m_dependencies.length; i++ )
        {
            if( m_dependencies[ i ].getRole().equals( name ) )
            {
                return m_dependencies[ i ];
            }
        }

        return null;
    }

    public DependencyMetaData[] getDependencies()
    {
        return m_dependencies;
    }
}
