/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.blocks;

import org.apache.avalon.camelot.Entry;
import org.apache.avalon.camelot.Locator;
import org.apache.avalon.configuration.Configuration;
import org.apache.phoenix.Block;
import org.apache.phoenix.metainfo.BlockInfo;

/**
 * This is the structure describing each block before it is loaded.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockEntry
    extends Entry
{
    protected final RoleEntry[]           m_roleEntrys;

    //UGLY HACK should be stored in another server Facility (ie ConfigurationRepository)
    protected Configuration               m_configuration;

    public BlockEntry( final RoleEntry[] roleEntrys ) 
    {
        m_roleEntrys = roleEntrys;
    }

    public BlockInfo getBlockInfo()
    {
        return (BlockInfo)getInfo();
    }

    public void setBlockInfo( final BlockInfo blockInfo )
    {
        setInfo(  blockInfo );
    }

    /**
     * Get a RoleEntry from entry with a particular role.
     *
     * @param role the role of RoleEntry to look for
     * @return the matching deendency else null
     */
    public RoleEntry getRoleEntry( final String role )
    {
        for( int i = 0; i < m_roleEntrys.length; i++ )
        {
            if( m_roleEntrys[ i ].getRole().equals( role ) )
            {
                return m_roleEntrys[ i ];
            }
        }
        
        return null;
    }

    public RoleEntry[] getRoleEntrys()
    {
        return m_roleEntrys;
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
