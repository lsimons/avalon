/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
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
package org.apache.avalon.framework.component;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;

/**
 * This is a {@link ComponentManager} implementation that can wrap around a
 * {@link ServiceManager} object effectively adapting a {@link ServiceManager}
 * interface to a {@link ComponentManager} interface.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.14 $ $Date: 2003/02/14 15:13:12 $
 */
public class WrapperComponentManager
    implements ComponentManager
{
    /**
     * The service manager we are adapting.
     */
    private final ServiceManager m_manager;

   /**
    * Creation of a new wrapper component manger using a supplied
    * service manager as a source backing the wrapped.  This implementation
    * redirects lookup requests to the supplied service manager provided under
    * this constructor. No attempt is made to proxy object supplied by the 
    * primary manager as Component instances - as such, it is the responsibility  
    * of the application establishing the wrapper to ensure that objects 
    * accessed via the primary manager implement the Component interface.
    *
    * @param manager the service manager backing the wrapper.
    */
    public WrapperComponentManager( final ServiceManager manager )
    {
        if( null == manager )
        {
            throw new NullPointerException( "manager" );
        }

        m_manager = manager;
    }

    /**
     * Retrieve a component via a key.
     *
     * @param key the key
     * @return the component
     * @throws ComponentException if unable to aquire component
     */
    public Component lookup( final String key )
        throws ComponentException
    {
        try
        {
            final Object object = m_manager.lookup( key );
            if( object instanceof ServiceSelector )
            {
                return new WrapperComponentSelector( key, (ServiceSelector)object );
            }
            else if( object instanceof Component )
            {
                return (Component)object;
            }
        }
        catch( final ServiceException se )
        {
            throw new ComponentException( se.getKey(), se.getMessage(), se.getCause() );
        }

        final String message = "Role does not implement the Component "
            + "interface and thus can not be accessed via ComponentManager";
        throw new ComponentException( key, message );
    }

    /**
     * Check to see if a <code>Component</code> exists for a key.
     *
     * @param key  a string identifying the key to check.
     * @return True if the component exists, False if it does not.
     */
    public boolean hasComponent( final String key )
    {
        return m_manager.hasService( key );
    }

    /**
     * Return the <code>Component</code> when you are finished with it.  This
     * allows the <code>ComponentManager</code> to handle the End-Of-Life Lifecycle
     * events associated with the Component.  Please note, that no Exceptions
     * should be thrown at this point.  This is to allow easy use of the
     * ComponentManager system without having to trap Exceptions on a release.
     *
     * @param component The Component we are releasing.
     */
    public void release( final Component component )
    {
        if( component instanceof WrapperComponentSelector )
        {
            m_manager.
                release( ( (WrapperComponentSelector)component ).getWrappedSelector() );
        }
        else
        {
            m_manager.release( component );
        }
    }
}
