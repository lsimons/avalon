/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.phoenix.InterceptorContext;
import org.apache.avalon.phoenix.metadata.BlockMetaData;

/**
 * Context via which Interceptor communicates with a container.
 *
 * @todo This is quick/temporary solution for a bigger problem.
 * I believe that a block and its interceptors should share a context and
 * there should be an extendable way to publish arbitrary objects on the
 * context during application assembly. Different interceptors need different
 * information and obviously we cannot have all keys hardcoded here. Possible
 * solution would be to define BlockContextContributors in assemble.xml
 * which will populate an instance of DefaultContext with context data. This
 * will include current block meta data and management info as well as
 * context necessary for interceptors (security, transaction and so on).
 *
 * @author <a href="mailto:igorfie at yahoo.com">Igor Fedorenko</a>
 * @version $Revision: 1.1.2.1 $ $Date: 2002/10/20 01:16:39 $
 */
public class DefaultInterceptorContext
    implements InterceptorContext
{
    private BlockMetaData m_blockMetaData;

    public DefaultInterceptorContext( final BlockMetaData blockMetaData )
    {
        m_blockMetaData = blockMetaData;
    }

    /**
     * @see org.apache.avalon.framework.context.Context#get(Object)
     */
    public Object get( final Object key ) throws ContextException
    {
        if( InterceptorContext.BLOCK_INFO.equals( key ) )
        {
            return m_blockMetaData.getBlockInfo();
        }
        else if( InterceptorContext.SECURITY_INFO.equals( key ) )
        {
            return null;
//            return m_blockMetaData.getSecurityInfo();
        }
        throw new ContextException( "Unknown key: " + key );
    }
}
