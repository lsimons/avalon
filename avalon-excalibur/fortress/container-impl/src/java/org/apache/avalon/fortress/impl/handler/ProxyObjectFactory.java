/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.fortress.impl.handler;

import org.apache.excalibur.mpool.ObjectFactory;

/**
 * An ObjectFactory that delegates to another ObjectFactory
 * and proxies results of that factory.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/02/07 16:08:12 $
 */
public class ProxyObjectFactory
    implements ObjectFactory
{
    /**
     * The underlying object factory that this factory proxies.
     */
    private final ObjectFactory m_objectFactory;

    /**
     * Create factory that delegates to specified factory.
     *
     * @param objectFactory the factory to delegate to
     * @exception NullPointerException if the supplied object factory is null
     */
    public ProxyObjectFactory( final ObjectFactory objectFactory ) throws NullPointerException
    {
        if( null == objectFactory )
        {
            throw new NullPointerException( "objectFactory" );
        }

        m_objectFactory = objectFactory;
    }

    /**
     * Create a new instance from delegated factory and proxy it.
     *
     * @return the proxied object
     * @throws Exception if unable to create new instance
     */
    public Object newInstance()
        throws Exception
    {
        final Object object = m_objectFactory.newInstance();
        return ProxyHelper.createProxy( object );
    }

    /**
     * Return the class created by factory.
     *
     * @return the class created by factory.
     */
    public Class getCreatedClass()
    {
        return m_objectFactory.getCreatedClass();
    }

    /**
     * Dispose of objects created by this factory.
     * Involves deproxying object and delegating to real ObjectFactory.
     *
     * @param object the proxied object
     * @throws Exception if unable to dispose of object
     */
    public void dispose( final Object object )
        throws Exception
    {
        final Object target = ProxyHelper.getObject( object );
        m_objectFactory.dispose( target );
    }
}
