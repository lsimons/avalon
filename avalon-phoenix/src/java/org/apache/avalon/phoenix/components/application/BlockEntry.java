/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.phoenix.components.ContainerConstants;
import org.apache.avalon.framework.info.Attribute;
import org.apache.avalon.framework.info.ComponentInfo;
import org.apache.avalon.framework.info.ServiceDescriptor;
import org.apache.avalon.phoenix.containerkit.registry.ComponentProfile;

/**
 * This is the structure describing each block before it is loaded.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
class BlockEntry
{
    private Object m_object;
    private ComponentProfile m_componentProfile;
    private BlockInvocationHandler m_invocationHandler;

    public BlockEntry( final ComponentProfile componentProfile )
    {
        invalidate();
        m_componentProfile = componentProfile;
    }

    public String getName()
    {
        return getProfile().getMetaData().getName();
    }

    public ComponentProfile getProfile()
    {
        return m_componentProfile;
    }

    public synchronized Object getObject()
    {
        return m_object;
    }

    public synchronized void setObject( final Object object )
    {
        invalidate();

        if( null != object && ! isDisableProxy() )
        {
            final ComponentInfo blockInfo = m_componentProfile.getInfo();
            final Class[] interfaces = getServiceClasses( object, blockInfo.getServices() );
            m_invocationHandler = new BlockInvocationHandler( object, interfaces );
        }
        m_object = object;
    }

    public synchronized Object getProxy()
    {
        if ( isDisableProxy() )
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

    private boolean isDisableProxy()
    {
        final Attribute[] attributes = getProfile().getMetaData().getAttributes();
        for( int i = 0; i < attributes.length; i++ )
        {
            final Attribute attribute = attributes[ i ];
            if( attribute.getName().equals( ContainerConstants.DISABLE_PROXY_ATTR  ) )
            {
                return true;
            }
        }
        return false;
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

    private Class[] getServiceClasses( final Object block,
                                       final ServiceDescriptor[] services )
    {
        final Class[] classes = new Class[ services.length + 1 ];
        final ClassLoader classLoader = block.getClass().getClassLoader();

        for( int i = 0; i < services.length; i++ )
        {
            try
            {
                classes[ i ] = classLoader.loadClass( services[ i ].getType() );
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
