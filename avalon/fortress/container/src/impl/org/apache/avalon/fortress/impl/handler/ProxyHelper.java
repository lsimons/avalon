/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.avalon.fortress.impl.handler;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.Recomposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Recontextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Reparameterizable;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Create a Component proxy.  Requires JDK 1.3+
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public final class ProxyHelper
{
    /**
     * The list interfaces that will not be proxied.
     */
    private static final Class[] INVALID_INTERFACES = new Class[]
    {
        Loggable.class,
        LogEnabled.class,
        Contextualizable.class,
        Recontextualizable.class,
        Composable.class,
        Recomposable.class,
        Serviceable.class,
        Configurable.class,
        Reconfigurable.class,
        Parameterizable.class,
        Reparameterizable.class,
        Initializable.class,
        Startable.class,
        Suspendable.class,
        Disposable.class,
        Serializable.class
    };

    /**
     * Get the Component wrapped in the proxy.
     * @param service the service object to proxy
     * @exception Exception if a proxy establishment error occurs
     */
    public static Component createProxy( Object service ) throws Exception
    {
        final Class clazz = service.getClass();
        final Class[] workInterfaces = guessWorkInterfaces( clazz );

        return (Component)Proxy.newProxyInstance( clazz.getClassLoader(),
                                                  workInterfaces,
                                                  new PassThroughInvocationHandler( service ) );
    }

    /**
     * Get the target object from specified proxy.
     *
     * @param proxy the proxy object
     * @return the target object
     * @throws Exception if unable to aquire target object,
     *                   or specified object is not a proxy
     */
    public static Object getObject( final Object proxy )
        throws Exception
    {
        if( null == proxy )
        {
            throw new NullPointerException( "proxy" );
        }

        if( !Proxy.isProxyClass( proxy.getClass() ) )
        {
            final String message = "object is not a proxy";
            throw new IllegalArgumentException( message );
        }

        final PassThroughInvocationHandler handler =
            (PassThroughInvocationHandler)Proxy.getInvocationHandler( proxy );
        return handler.getObject();
    }

    /**
     * Get a list of interfaces to proxy by scanning through
     * all interfaces a class implements and skipping invalid interfaces
     * (as defined in {@link #INVALID_INTERFACES}).
     *
     * @param clazz the class
     * @return the list of interfaces to proxy
     */
    private static Class[] guessWorkInterfaces( final Class clazz )
    {
        final ArrayList list = new ArrayList();
        guessWorkInterfaces( clazz, list );

        list.add( Component.class );
        return (Class[])list.toArray( new Class[ list.size() ] );
    }

    /**
     * Get a list of interfaces to proxy by scanning through
     * all interfaces a class implements and skipping invalid interfaces
     * (as defined in {@link #INVALID_INTERFACES}).
     *
     * @param clazz the class
     * @param list the list of current work interfaces
     */
    private static void guessWorkInterfaces( final Class clazz,
                                             final ArrayList list )
    {
        if( null != clazz )
        {
            final Class[] interfaces = clazz.getInterfaces();

            boolean skip = false;
            for( int i = 0; i < interfaces.length; i++ )
            {
                skip = false;
                for( int j = 0; j < INVALID_INTERFACES.length; j++ )
                {
                    if( interfaces[ i ] == INVALID_INTERFACES[ j ] )
                    {
                        skip = true;
                        continue;
                    }
                }

                if( !skip )
                {
                    list.add( interfaces[ i ] );
                }
            }

            guessWorkInterfaces( clazz.getSuperclass(), list );
        }
    }
}
