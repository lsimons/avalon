/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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

import java.util.Map;
import org.apache.avalon.fortress.Container;
import org.apache.avalon.fortress.impl.AbstractContainer;
import org.apache.avalon.fortress.impl.handler.ComponentHandler;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.WrapperServiceSelector;
import org.apache.commons.collections.StaticBucketMap;

/**
 * This is the Default ServiceManager for the Container.  It provides
 * a very simple abstraction, and makes it easy for the Container to manage
 * the references.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.11 $ $Date: 2003/03/22 12:31:52 $
 */
public class FortressServiceManager
    implements ServiceManager
{
    private final Container m_container;
    private final Map m_used;
    private final ServiceManager m_parent;

    /**
     * This constructor is for a ContainerComponentManager with a parent
     * ComponentLocator
     * @param container the impl
     * @param parent the parent service manager
     * @exception NullPointerException if the supplied impl is null
     * @exception NullPointerException if the supplied parent is null
     */
    public FortressServiceManager( final Container container,
                                   final ServiceManager parent ) throws NullPointerException
    {
        if( null == container )
        {
            throw new NullPointerException( "impl" );
        }
        if( null == parent )
        {
            throw new NullPointerException( "parent" );
        }

        m_parent = parent;
        m_container = container;
        m_used = new StaticBucketMap();
    }

    public Object lookup( final String role )
        throws ServiceException
    {
        Lookup lookup = parseRole( role );

        if( !m_container.has( lookup.m_role, lookup.m_hint ) )
        {
            return m_parent.lookup( role );
        }

        final Object result = m_container.get( lookup.m_role, lookup.m_hint );
        if( result instanceof ServiceSelector )
        {
            return result;
        }

        if( result instanceof ComponentSelector )
        {
            return new WrapperServiceSelector( lookup.m_role, (ComponentSelector)result );
        }

        if( !( result instanceof ComponentHandler ) )
        {
            final String message = "Invalid entry in component manager";
            throw new ServiceException( role, message );
        }

        try
        {
            final ComponentHandler handler = (ComponentHandler)result;
            final Object component = handler.get();

            m_used.put( new ComponentKey( component ), handler );
            return component;
        }
        catch( final ServiceException ce )
        {
            throw ce; // rethrow
        }
        catch( final Exception e )
        {
            final String message =
                "Could not return a reference to the Component";
            throw new ServiceException( role, message, e );
        }
    }

    public boolean hasService( final String role )
    {
        Lookup lookup = parseRole( role );

        if( m_container.has( lookup.m_role, lookup.m_hint ) )
        {
            return true;
        }
        else
        {
            return null != m_parent ? m_parent.hasService( role ) : false;
        }
    }

    public void release( final Object component )
    {
        final ComponentHandler handler = (ComponentHandler)m_used.remove( new ComponentKey( component ) );
        if( null == handler )
        {
            if( null == m_parent )
            {
                /* This is a purplexing problem.  SOmetimes the m_used hash
                 * returns null for the component--usually a ThreadSafe
                 * component.  When there is no handler and no parent, that
                 * is an error condition--but if the component is usually
                 * ThreadSafe, the impact is essentially nill.
                 */
                //Pete: This occurs when objects are released more often than
                //when they are aquired
                //Pete: It also happens when a release of a ComponentSelector occurs
            }
            else
            {
                m_parent.release( component );
            }
        }
        else
        {
            handler.put( component );
        }
    }

    private Lookup parseRole( String role )
    {
        Lookup lookup = new Lookup();
        lookup.m_role = role;
        lookup.m_hint = AbstractContainer.DEFAULT_ENTRY;

        if( role.endsWith( "Selector" ) )
        {
            lookup.m_role = role.substring( 0, role.length() - "Selector".length() );
            lookup.m_hint = AbstractContainer.SELECTOR_ENTRY;
        }

        int index = role.lastIndexOf( "/" );

        // needs to be further than the first character
        if( index > 0 )
        {
            lookup.m_role = role.substring( 0, index );
            lookup.m_hint = role.substring( index + 1 );
        }

        return lookup;
    }

    private final static class Lookup
    {
        String m_role;
        String m_hint;
    }
}
