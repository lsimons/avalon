/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import java.lang.reflect.Proxy;

import org.apache.avalon.phoenix.AbstractChainedInvocable;

/**
 * This makes a dynamic proxy for an object.  The object can be represented
 * by one, some or all of it's interfaces.
 *
 * <p>Amongst other things, it's an anti hackinge measure.  Suitable armed code
 * could have case an interface for a thing back to it's impl and used methods
 * and properties that were not it's authors intention.  Reflection too allows
 * some powerful introspection things and some traversal even more things
 * including private member vars by a serialisation trick... hence the transient.</p>
 *
 * <p>This proxy also allows itself to be invalidated thus making it
 * impossible to call methods on a Block after it has been shutdown.</p>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 * @version CVS $Revision: 1.10 $ $Date: 2002/10/04 00:37:41 $
 */
final class BlockInvocationHandler extends AbstractChainedInvocable
{
    private transient Object m_object;

    private transient Object m_proxy;

    /**
     * Create a proxy object that has specified interfaces implemented by proxy.
     *
     * @param object the underlying object
     * @param interfaces the interfaces to proxy
     */
    protected BlockInvocationHandler( final ClassLoader classLoader,
                                      final Class[] interfaces )
    {
        super();
        m_proxy = Proxy.newProxyInstance( classLoader, interfaces, this );
    }

    /**
     * Invalidate Proxy making it impossible to call methods
     * of real-object.
     */
    public void invalidate()
    {
        setObject( null );
        m_proxy = null;
    }

    /**
     * Return the proxy object.
     *
     * @return the proxy object
     */
    public Object getProxy()
    {
        return m_proxy;
    }
}
