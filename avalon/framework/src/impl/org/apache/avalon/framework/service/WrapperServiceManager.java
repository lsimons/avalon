/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation. All rights
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
package org.apache.avalon.framework.service;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;

/**
 * This is a {@link ServiceManager} implementation that can wrap around a legacy
 * {@link ComponentManager} object effectively adapting a {@link ComponentManager}
 * interface to a {@link ServiceManager} interface.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.7 $ $Date: 2003/02/10 07:19:18 $
 */
public class WrapperServiceManager
    implements ServiceManager
{
    /**
     * The component manager thaty this class wraps.
     */
    private final ComponentManager m_componentManager;

    /**
     * This constructor is a constructor for a WrapperServiceManager.
     *
     * @param componentManager the ComponentManager instance that is being wrapped
     */
    public WrapperServiceManager( final ComponentManager componentManager )
    {
        if( null == componentManager )
        {
            throw new NullPointerException( "componentManager" );
        }

        m_componentManager = componentManager;
    }

    /**
     * Retrieve a service using specified key.
     *
     * @param key the key to use to lookup component
     * @return the matching service
     * @throws ServiceException if unable to provide the service
     * @see ServiceManager#lookup
     */
    public Object lookup( final String key )
        throws ServiceException
    {
        try
        {
            final Object service = m_componentManager.lookup( key );
            if( service instanceof ComponentSelector )
            {
                return new WrapperServiceSelector( key, (ComponentSelector)service );
            }
            else
            {
                return service;
            }
        }
        catch( final ComponentException ce )
        {
            throw new ServiceException( key, ce.getMessage(), ce );
        }
    }

    /**
     * Return true if the component is available in ServiceManager.
     *
     * @param key the lookup
     * @return true if the component is available in ServiceManager
     */
    public boolean hasService( final String key )
    {
        return m_componentManager.hasComponent( key );
    }

    /**
     * Release the service back to the ServiceManager.
     *
     * @param service the service
     */
    public void release( final Object service )
    {
        if ( service instanceof WrapperServiceSelector )
        {
            m_componentManager.
                release( ((WrapperServiceSelector)service).getWrappedSelector() );
        }
        else
        {
            m_componentManager.release( (Component)service );
        }
    }
}
