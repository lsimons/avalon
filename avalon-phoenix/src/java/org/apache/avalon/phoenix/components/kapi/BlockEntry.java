/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kapi;

import org.apache.avalon.excalibur.container.Entry;
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
    private BlockMetaData   m_blockMetaData;
    private BlockProxy      m_proxy;

    public BlockEntry( final BlockMetaData blockMetaData )
    {
        m_blockMetaData = blockMetaData;
        setState( State.VOID );
    }

    public BlockMetaData getBlockMetaData()
    {
        return m_blockMetaData;
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
}
