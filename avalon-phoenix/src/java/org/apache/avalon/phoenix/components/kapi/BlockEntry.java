/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kapi;

import org.apache.avalon.excalibur.container.Entry;
import org.apache.avalon.excalibur.container.Locator;
import org.apache.avalon.excalibur.container.State;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metadata.RoleMetaData;
import org.apache.avalon.phoenix.metadata.BlockMetaData;

/**
 * This is the structure describing each block before it is loaded.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockEntry
    extends Entry
{
    private final RoleMetaData[]   m_roles;
    //private BlockMetaData    m_blockMetaData;

    private final String        m_name;

    private BlockProxy          m_proxy;

    public BlockEntry( final String name,
                       final RoleMetaData[] roles,
                       final Locator locator )
    {
        //m_blockMetaData = blockMetaData;
        m_name = name;
        m_roles = roles;
        setLocator( locator );
        setState( State.VOID );
    }

    public String getName()
    {
        return m_name;
    }

    public BlockProxy getBlockProxy()
    {
        return m_proxy;
    }

    public void setBlockProxy( final BlockProxy proxy )
    {
        m_proxy = proxy;
    }

    public BlockInfo getBlockInfo()
    {
        return (BlockInfo)getInfo();
    }

    public void setBlockInfo( final BlockInfo blockInfo )
    {
        setInfo(  blockInfo );
    }

    public RoleMetaData getRole( final String role )
    {
        for( int i = 0; i < m_roles.length; i++ )
        {
            if( m_roles[ i ].getRole().equals( role ) )
            {
                return m_roles[ i ];
            }
        }

        return null;
    }

    public RoleMetaData[] getRoles()
    {
        return m_roles;
    }
}
