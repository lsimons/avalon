/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * This is the structure describing each block before it is loaded.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
class BlockEntry
{
    private Object m_object;

    private BlockMetaData m_blockMetaData;

    private BlockInvocationHandler m_invocationHandler;

    public BlockEntry( final BlockMetaData blockMetaData )
    {
        invalidate();
        m_blockMetaData = blockMetaData;
    }

    public String getName()
    {
        return getMetaData().getName();
    }

    public BlockMetaData getMetaData()
    {
        return m_blockMetaData;
    }

    public synchronized Object getObject()
    {
        return m_object;
    }

    public synchronized void setObject( final Object object )
    {
        invalidate();

        if( null != object && ! getMetaData().isDisableProxy() )
        {
            final BlockInfo blockInfo = getMetaData().getBlockInfo();
            final Class[] interfaces = getServiceClasses( object, blockInfo.getServices() );
            m_invocationHandler = new BlockInvocationHandler( object, interfaces );
        }
        m_object = object;
    }

    public synchronized Object getProxy()
    {
        if ( getMetaData().isDisableProxy() )
        {
            return m_object;
        }
        else
        {
            if( null != m_invocationHandler )
            {
                return m_invocationHandler.getProxy();
            }
            else
            {
                return null;
            }
        }
    }

    protected synchronized void invalidate()
    {
        if( null != m_invocationHandler )
        {
            m_invocationHandler.invalidate();
            m_invocationHandler = null;
        }
        m_object = null;
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
                //Ignore
            }
        }

        //Note that the proxy is still built using the
        //Block interface so that ComponentManaers can
        //still be used to provide blocks with services.
        //Block extends Component and thus the proxy
        //extends Component. The magic is that the Block
        //interface has no methods and thus will never cause
        //any issues for Proxy class.
        classes[ services.length ] =
            org.apache.avalon.phoenix.Block.class;
        return classes;
    }
}
