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

package org.apache.avalon.excalibur.component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationTargetException;
import org.apache.avalon.framework.component.Component;

/**
 * Create a Component proxy.  Requires JDK 1.3+
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class ComponentProxyGenerator
{
    private final ClassLoader m_classLoader;

    /**
     * Initialize the ComponentProxyGenerator with the default classloader.
     * The default classloader is the Thread context classloader.
     */
    public ComponentProxyGenerator()
    {
        this( Thread.currentThread().getContextClassLoader() );
    }

    /**
     * Initialize the ComponentProxyGenerator with the supplied classloader.
     * If the supplied class loader is null, we use the Thread context class
     * loader.  If that is null, we use this class's classloader.
     */
    public ComponentProxyGenerator( final ClassLoader parentClassLoader )
    {
        m_classLoader = ( null == parentClassLoader ) ?
            ( ( null == Thread.currentThread().getContextClassLoader() ) ?
            getClass().getClassLoader()
            : Thread.currentThread().getContextClassLoader() )
            : parentClassLoader;
    }

    /**
     * Get the Component wrapped in the proxy.  The role must be the service
     * interface's fully qualified classname to work.
     */
    public Component getProxy( String role, Object service ) throws Exception
    {
        Class serviceInterface = m_classLoader.loadClass( role );

        return (Component)Proxy.newProxyInstance( m_classLoader,
                                                  new Class[]{Component.class, serviceInterface},
                                                  new ComponentInvocationHandler( service ) );
    }

    /**
     * Internal class to handle the wrapping with Component
     */
    private final static class ComponentInvocationHandler
        implements InvocationHandler
    {
        private final Object m_delagate;

        public ComponentInvocationHandler( final Object delegate )
        {
            if( null == delegate )
            {
                throw new NullPointerException( "delegate" );
            }

            m_delagate = delegate;
        }

        public Object invoke( final Object proxy,
                              final Method meth,
                              final Object[] args )
            throws Throwable
        {
            try
            {
                return meth.invoke( m_delagate, args );
            }
            catch( final InvocationTargetException ite )
            {
                throw ite.getTargetException();
            }
        }
    }
}
