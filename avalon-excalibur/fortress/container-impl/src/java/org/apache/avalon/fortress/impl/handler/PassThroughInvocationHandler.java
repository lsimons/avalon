/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.fortress.impl.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * InvocationHandler that just passes on all methods to target object.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
final class PassThroughInvocationHandler
    implements InvocationHandler
{
    /**
     * The target object delegated to.
     */
    private final Object m_object;

    /**
     * Create an Invocation handler for specified object.
     *
     * @param object the object to delegate to
     */
    public PassThroughInvocationHandler( final Object object )
    {
        if( null == object )
        {
            throw new NullPointerException( "object" );
        }

        m_object = object;
    }

    /**
     * Invoke the appropriate method on underlying object.
     *
     * @param proxy the proxy object
     * @param meth the method
     * @param args the arguments
     * @return the return value of object
     * @exception Throwable method throws an exception
     */
    public Object invoke( final Object proxy,
                          final Method meth,
                          final Object[] args )
        throws Throwable
    {
        try
        {
            return meth.invoke( m_object, args );
        }
        catch( final InvocationTargetException ite )
        {
            throw ite.getTargetException();
        }
    }

    /**
     * Retrieve the underlying object delegated to.
     *
     * @return the object delegated to
     */
    Object getObject()
    {
        return m_object;
    }
}
