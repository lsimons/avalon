/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.components.lifecycle.ComponentEntry;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * This is the structure describing each block before it is loaded.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class BlockEntry
    extends ComponentEntry
{
    private BlockMetaData m_blockMetaData;
    private BlockInvocationHandler m_invocationHandler;

    public BlockEntry( final BlockMetaData blockMetaData )
    {
        super( blockMetaData.getName() );
        m_blockMetaData = blockMetaData;
    }

    public BlockMetaData getMetaData()
    {
        return m_blockMetaData;
    }

    public synchronized Block getBlock()
    {
        return (Block)getObject();
    }

    public synchronized void setBlock( final Block block )
    {
        setObject( block );
    }

    public synchronized void setObject( final Object object )
    {
        invalidate();

        if( null != object )
        {
            final BlockInfo blockInfo = getMetaData().getBlockInfo();
            final Class[] interfaces = getServiceClasses( object, blockInfo.getServices() );

            m_invocationHandler = new BlockInvocationHandler( object, interfaces );
            super.setObject( object );
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
            m_invocationHandler = null;
        }
        super.invalidate();
    }

    private Class[] getServiceClasses( final Object block, final ServiceDescriptor[] services )
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
