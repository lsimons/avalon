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
package org.apache.avalon.fortress.impl.lookup;

import org.apache.avalon.fortress.Container;
import org.apache.avalon.fortress.impl.handler.ComponentHandler;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.commons.collections.StaticBucketMap;

import java.util.Map;

/**
 * This is the Default ServiceSelector for the Container.  It provides
 * a very simple abstraction, and makes it easy for the Container to manage
 * the references.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.12 $ $Date: 2003/04/21 15:15:06 $
 */
public class FortressServiceSelector
    implements ServiceSelector
{
    private final String m_key;
    private final Container m_container;
    private final Map m_used;

    /**
     * Creation of  new service selector.
     * @param container the impl
     * @param key a key
     */
    public FortressServiceSelector( final Container container,
                                    final String key )
    {
        if ( null == container )
        {
            throw new NullPointerException( "impl" );
        }
        if ( null == key )
        {
            throw new NullPointerException( "key" );
        }

        m_key = key;
        m_container = container;
        m_used = new StaticBucketMap();
    }

    public Object select( final Object hint )
        throws ServiceException
    {
        try
        {
            final ComponentHandler handler = getHandler( hint );
            final Object component = handler.get();
            m_used.put( new ComponentKey( component ), handler );
            return component;
        }
        catch ( final ServiceException ce )
        {
            throw ce; // rethrow
        }
        catch ( final Exception e )
        {
            final String name = m_key + "/" + hint.toString();
            final String message = "Could not return a reference to the Component";
            throw new ServiceException( name, message, e );
        }
    }

    public boolean isSelectable( final Object hint )
    {
        return m_container.has( m_key, hint );
    }

    public void release( final Object component )
    {
        final ComponentHandler handler =
            (ComponentHandler) m_used.remove( new ComponentKey( component ) );
        if ( null != handler )
        {
            handler.put( component );
        }
    }

    private ComponentHandler getHandler( final Object hint )
        throws ServiceException
    {
        if ( null == hint )
        {
            final String message = "hint cannot be null";
            throw new IllegalArgumentException( message );
        }

        final ComponentHandler handler =
            (ComponentHandler) m_container.get( m_key, hint );
        if ( null == handler )
        {
            final String message =
                "The hint does not exist in the ComponentSelector";
            throw new ServiceException( m_key + "/" + hint.toString(),
                message );
        }
        return handler;
    }
}
