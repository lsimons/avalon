/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.phoenix.framework.info.Attribute;
import org.apache.avalon.phoenix.framework.info.ComponentInfo;
import org.apache.avalon.phoenix.framework.info.ServiceDescriptor;
import org.apache.avalon.phoenix.interfaces.ContainerConstants;
import org.apache.avalon.phoenix.containerkit.profile.ComponentProfile;

/**
 * This is the structure describing each block before it is loaded.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
class BlockEntry
{
    private static final Class BLOCK_CLASS = getBlockClass();

    private Object m_object;
    private final ComponentProfile m_componentProfile;
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

        if( null != object && !isDisableProxy() )
        {
            final ComponentInfo blockInfo = m_componentProfile.getInfo();
            final Class[] interfaces = getServiceClasses( object, blockInfo.getServices() );
            m_invocationHandler = new BlockInvocationHandler( object, interfaces );
        }
        m_object = object;
    }

    public synchronized Object getProxy()
    {
        if( isDisableProxy() )
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
            if( attribute.getName().equals( ContainerConstants.DISABLE_PROXY_ATTR ) )
            {
                return true;
            }
        }
        return false;
    }

    synchronized void invalidate()
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
        classes[ services.length ] = BLOCK_CLASS;
        return classes;
    }


    private static Class getBlockClass()
    {
        try
        {
            return Class.forName( "org.apache.avalon.phoenix.Block" );
        }
        catch( ClassNotFoundException e )
        {
            throw new IllegalStateException( "Can not find block class" );
        }
    }
 }
