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
import org.apache.avalon.phoenix.metadata.BlockMetaData;

/**
 * This is the structure describing each block before it is loaded.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class BlockEntry
    extends Entry
{
    private BlockMetaData           m_blockMetaData;
    private BlockInvocationHandler  m_invocationHandler;

    public BlockEntry( final BlockMetaData blockMetaData )
    {
        m_blockMetaData = blockMetaData;
        setState( State.VOID );
    }

    public BlockMetaData getMetaData()
    {
        return m_blockMetaData;
    }

    public BlockInvocationHandler getBlockInvocationHandler()
    {
        return m_invocationHandler;
    }

    public void setBlockInvocationHandler( final BlockInvocationHandler invocationHandler )
    {
        m_invocationHandler = invocationHandler;
    }

    public BlockInfo getBlockInfo()
    {
        return (BlockInfo)getInfo();
    }
}
