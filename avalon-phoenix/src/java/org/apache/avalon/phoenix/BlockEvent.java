/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix;

import java.util.EventObject;
import org.apache.avalon.phoenix.metainfo.BlockInfo;

/**
 * This is the class that is used to deliver notifications
 * about <code>Block</code>s state changes to the 
 * <code>BlockListener</code>s of a Server Application.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class BlockEvent
    extends EventObject
{
    private final String     m_name;
    private final Block      m_block;
    private final BlockInfo  m_blockInfo;

    /**
     * Construct the <code>BlockEvent</code>.
     *
     * @param name the name of block
     * @param block the block object
     * @param blockInfo the BlockInfo object for block
     */
    public BlockEvent( final String name,
                       final Block block,
                       final BlockInfo blockInfo )
    {
        super( name );

        if( null == name )
        {
            throw new NullPointerException( "name proeprty is null" );
        }
        if( null == block )
        {
            throw new NullPointerException( "block proeprty is null" );
        }
        if( null == blockInfo )
        {
            throw new NullPointerException( "blockInfo proeprty is null" );
        }

        m_name = name;
        m_block = block;
        m_blockInfo = blockInfo;
    }

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Retrieve the block instance.
     *
     * @return the block instance
     */
    public Block getBlock()
    {
        return m_block;
    }

    /**
     * Retrieve the BlockInfo for block.
     *
     * @return the BlockInfo for block
     */
    public BlockInfo getBlockInfo()
    {
        return m_blockInfo;
    }
}
