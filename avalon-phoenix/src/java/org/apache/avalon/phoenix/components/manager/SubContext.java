/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.components.manager;

import java.util.HashMap;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.phoenix.interfaces.ManagerException;
import org.apache.avalon.phoenix.interfaces.SystemManager;

/**
 * Implements a management context local to a particular process with which
 * it can register its managed object.  The naming scheme that results is
 * meant to be compatible with jmx.
 *
 * @author <a href="mailto:huw@apache.org">Huw Roberts</a>
 */
class SubContext
    implements SystemManager
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( SubContext.class );

    private static final String EMPTY_STRING = "";

    private final HashMap m_subcontexts = new HashMap();
    private final SystemManager m_parent;
    private final String m_name;
    private final String m_type;

    /**
     * Creates new SubContext to the specified context.  Objects registered
     * under the subcontext will be typed with the name of the context so the
     * jmx name becomes 'contextName' + 'objectNameSoFar'
     *
     * @param parent the parent context
     * @param name the subcontext name
     */
    public SubContext( final SystemManager parent,
                       final String name,
                       final String type )
    {
        if( null == parent )
        {
            throw new NullPointerException( "parent" );
        }
        m_parent = parent;
        m_name = name;
        m_type = type;
    }

    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX) and the management is restricted
     * to the interfaces passed in as a parameter to method.
     *
     * @param name the name to register object under
     * @param object the object
     * @param interfaces the interfaces to register the component under
     * @throws ManagerException if an error occurs. An error could occur if the object doesn't
     *            implement the interfaces, the interfaces parameter contain non-instance
     *            classes, the name is already registered etc.
     * @throws IllegalArgumentException if object or interfaces is null
     */
    public void register( final String name, final Object object, final Class[] interfaces )
        throws ManagerException, IllegalArgumentException
    {
        m_parent.register( jmxName( name ), object, interfaces );
    }

    /**
     * Register an object for management.
     * The object is exported through some management scheme
     * (typically JMX). Note that the particular management scheme
     * will most likely use reflection to extract manageable information.
     *
     * @param name the name to register object under
     * @param object the object
     * @throws ManagerException if an error occurs such as name already registered.
     * @throws IllegalArgumentException if object is null
     */
    public void register( final String name, final Object object )
        throws ManagerException, IllegalArgumentException
    {
        m_parent.register( jmxName( name ), object );
    }

    /**
     * Unregister named object.
     *
     * @param name the name of object to unregister
     * @throws ManagerException if an error occurs such as when no such object registered.
     */
    public void unregister( final String name )
        throws ManagerException
    {
        m_parent.unregister( jmxName( name ) );
    }

    /**
     * Returns the subcontext of the specified name.  If it does not exist it
     * is created.
     *
     * @return the subcontext with the specified name
     * @throws ManagerException if context cannot be created or retrieved
     */
    public SystemManager getSubContext( final String name,
                                        final String type )
        throws ManagerException
    {
        if( null == type || EMPTY_STRING.equals( type ) )
        {
            final String message =
                REZ.getString( "subcontext.error.no.subcontext" );
            throw new ManagerException( message );
        }
        else if( null != name && this.m_type == null )
        {
            final String message =
                REZ.getString( "subcontext.error.no.subcontext" );
            throw new ManagerException( message );
        }

        // get from list if possible
        final String key = contextKey( name, type );
        SystemManager subcontext =
            (SystemManager)m_subcontexts.get( key );

        // otherwise create and add to list
        if( subcontext == null )
        {
            subcontext = new SubContext( this, name, type );
            m_subcontexts.put( key, subcontext );
        }

        return subcontext;
    }

    /**
     *  Helper method used to generate the jmx name by appending the current name
     *  and passing up the chain to the root context
     */
    private String jmxName( final String name )
    {
        final StringBuffer sb = new StringBuffer();
        if( null != m_name )
        {
            sb.append( m_name );
            sb.append( ',' );
        }
        if( null != m_type )
        {
            sb.append( m_type );
            sb.append( '=' );
        }
        sb.append( name );

        return sb.toString();
    }

    /**
     *  Helper method to get key used to store subcontexts
     * in m_subcontexts
     */
    private String contextKey( final String parent,
                               final String type )
    {
        return parent + "|" + type;
    }
}
