/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.fortress.impl.factory;

import org.apache.excalibur.mpool.ObjectFactory;
import org.apache.avalon.framework.component.Component;

import java.lang.reflect.Proxy;

/**
 * An ObjectFactory that delegates to another ObjectFactory
 * and proxies results of that factory.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/04/22 16:11:23 $
 */
public final class ProxyObjectFactory extends AbstractObjectFactory
{
    /**
     * Create factory that delegates to specified factory.
     *
     * @param objectFactory the factory to delegate to
     * @exception NullPointerException if the supplied object factory is null
     */
    public ProxyObjectFactory( final ObjectFactory objectFactory ) throws NullPointerException
    {
        super(objectFactory);
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
        final Object object = m_delegateFactory.newInstance();
        return createProxy( object );
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
        final Object target = getObject( object );
        m_delegateFactory.dispose( target );
    }

    /**
     * Get the Component wrapped in the proxy.
     *
     * @param service the service object to proxy
     */
    public static Component createProxy( final Object service )
    {
        final Class clazz = service.getClass();
        final Class[] workInterfaces = guessWorkInterfaces( clazz );

        return (Component) Proxy.newProxyInstance( clazz.getClassLoader(),
            workInterfaces,
            new PassThroughInvocationHandler( service ) );
    }

    /**
     * Get the target object from specified proxy.
     *
     * @param proxy the proxy object
     * @return the target object
     * @throws NullPointerException if unable to aquire target object,
     *                   or specified object is not a proxy
     */
    public static Object getObject( final Object proxy )

    {
        if ( null == proxy )
        {
            throw new NullPointerException( "proxy" );
        }

        if ( !Proxy.isProxyClass( proxy.getClass() ) )
        {
            final String message = "object is not a proxy";
            throw new IllegalArgumentException( message );
        }

        final PassThroughInvocationHandler handler =
            (PassThroughInvocationHandler) Proxy.getInvocationHandler( proxy );
        return handler.getObject();
    }
}
