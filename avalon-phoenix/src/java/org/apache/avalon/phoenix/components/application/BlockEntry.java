/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * This is the structure describing each block before it is loaded.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class BlockEntry
{
    private BlockMetaData m_blockMetaData;
    private BlockInvocationHandler m_invocationHandler;
    private Block m_block;
    private State m_state;

    public BlockEntry( final BlockMetaData blockMetaData )
    {
        m_blockMetaData = blockMetaData;
        setState( State.VOID );
    }

    public BlockMetaData getMetaData()
    {
        return m_blockMetaData;
    }

    public synchronized State getState()
    {
        return m_state;
    }

    public synchronized void setState( final State state )
    {
        m_state = state;
    }

    public synchronized Block getBlock()
    {
        return m_block;
    }

    public synchronized void setBlock( final Block block )
    {
        if( null != m_block )
        {
            invalidate();
        }

        if( null != block )
        {
            final BlockInfo blockInfo = getMetaData().getBlockInfo();
            final Class[] interfaces = getServiceClasses( block, blockInfo.getServices() );

            m_invocationHandler = new BlockInvocationHandler( block, interfaces );
            m_block = block;
        }
    }

    public synchronized Block getProxy()
    {
        if( null != m_invocationHandler )
        {
            return (Block)m_invocationHandler.getProxy();
        }
        else
        {
            return null;
        }
    }

    public synchronized void invalidate()
    {
        if( null != m_invocationHandler )
        {
            m_invocationHandler.invalidate();
        }

        m_invocationHandler = null;
        m_block = null;
    }

    private Class[] getServiceClasses( final Block block, final ServiceDescriptor[] services )
    {
        final Class[] classes = new Class[ services.length + 1 ];
        final ClassLoader classLoader = block.getClass().getClassLoader();

        for( int i = 0; i < services.length; i++ )
        {
            try
            {
                classes[ i ] = classLoader.loadClass( services[ i ].getName() );
            }
            catch( final Throwable throwable )
            {
            }
        }

        classes[ services.length ] = Block.class;
        return classes;
    }
}
