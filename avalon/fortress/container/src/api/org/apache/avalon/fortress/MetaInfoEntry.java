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
package org.apache.avalon.fortress;

import org.apache.avalon.fortress.impl.handler.FactoryComponentHandler;
import org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler;
import org.apache.avalon.fortress.impl.handler.PoolableComponentHandler;
import org.apache.avalon.fortress.impl.handler.ThreadSafeComponentHandler;

import java.util.*;

/**
 * Keeps track of the relationship of all the associated meta data for a
 * component type.  It records all the roles, short name, component class, and
 * the handler class used to manage it.  The short name is included strictly
 * to enable "self-healing" configuration files.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2003/04/22 12:37:08 $
 */
public final class MetaInfoEntry
{
    private final Class m_klass;
    private final String m_configName;
    private final Class m_handler;
    private final Set m_roles;
    private volatile boolean m_readOnly = false;

    /** Translate from lifestyle to component handler. */
    private static final Map m_lifecycleMap;

    // Initialize the scope map
    static
    {
        Map lifecycleMap = new HashMap();
        lifecycleMap.put( "singleton", ThreadSafeComponentHandler.class.getName() );
        lifecycleMap.put( "thread", PerThreadComponentHandler.class.getName() );
        lifecycleMap.put( "pooled", PoolableComponentHandler.class.getName() );
        lifecycleMap.put( "transient", FactoryComponentHandler.class.getName() );

        m_lifecycleMap = Collections.unmodifiableMap( lifecycleMap );
    }

    /**
     * Create a MetaInfoEntry from the supplied component class, and the
     * supplied meta information.
     *
     * @param componentClass  The <code>Class</code> for the component type
     * @param properties      The <code>Properties</code> object for meta info
     *
     * @throws ClassNotFoundException if the component handler class could not be found
     */
    public MetaInfoEntry( final Class componentClass, final Properties properties ) throws ClassNotFoundException
    {
        if ( null == componentClass ) throw new NullPointerException( "\"componentClass\" cannot be null." );
        if ( null == properties ) throw new NullPointerException( "\"properties\" cannot be null." );

        m_klass = componentClass;
        m_configName = properties.getProperty( "x-avalon.name", createShortName( componentClass.getName() ) );
        m_handler = Thread.currentThread().getContextClassLoader().loadClass( getHandler( properties ) );
        m_roles = new HashSet();
    }

    /**
     * Create a MetaInfoEntry from the supplied <code>RoleEntry</code>.
     *
     * @param roleEntry  The <code>RoleEntry</code> to convert
     */
    public MetaInfoEntry( final RoleEntry roleEntry )
    {
        if ( null == roleEntry ) throw new NullPointerException( "\"roleEntry\" cannot be null." );

        m_klass = roleEntry.getComponentClass();
        m_configName = roleEntry.getShortname();
        m_handler = roleEntry.getHandlerClass();
        m_roles = new HashSet();
        m_roles.add( roleEntry.getRole() );
        makeReadOnly();
    }

    /**
     * Make the component entry read only, so no more services can be added.
     */
    public void makeReadOnly()
    {
        m_readOnly = true;
    }

    /**
     * Get the <code>Class</code> for the component type.
     *
     * @return the <code>Class</code>
     */
    public Class getComponentClass()
    {
        return m_klass;
    }

    /**
     * Get the <code>Class</code> for the component type's
     * {@link org.apache.avalon.fortress.impl.handler.ComponentHandler}.
     *
     * @return the <code>Class</code>
     */
    public Class getHandlerClass()
    {
        return m_handler;
    }

    /**
     * Get the configuration name for the component type.  This is used in
     * "self-healing" configuration files.
     *
     * @return the config name
     */
    public String getConfigurationName()
    {
        return m_configName;
    }

    /**
     * Add a service/role for the component entry.
     *
     * @param role  The new role
     *
     * @throws SecurityException if this MetaInfoEntry is read-only
     */
    public void addRole( final String role )
    {
        if ( null == role ) throw new NullPointerException( "\"role\" cannot be null" );
        if ( m_readOnly ) throw new SecurityException( "This MetaInfoEntry is read-only." );

        m_roles.add( role );
    }

    /**
     * Tests to see if a component exposes a role.
     *
     * @param role  The role to check
     * @return <code>true</code> if it does
     */
    public boolean containsRole( final String role )
    {
        if ( null == role ) throw new NullPointerException( "\"role\" cannot be null" );
        return m_roles.contains( role );
    }

    /**
     * Get an iterator for all the roles.
     *
     * @return the iterator
     */
    public Iterator getRoles()
    {
        return m_roles.iterator();
    }

    /**
     * Get the name of the requested component handler.
     *
     * @param meta  The properties object from the constructor
     * @return String name of the component handler
     */
    private String getHandler( final Properties meta )
    {
        final String lifecycle = meta.getProperty( "x-avalon.lifestyle", null );
        String handler;

        if ( null != lifecycle )
        {
            handler = (String) m_lifecycleMap.get( lifecycle );
        }
        else
        {
            handler = meta.getProperty( "fortress.handler" );
        }

        if ( null == handler )
        {
            handler = PerThreadComponentHandler.class.getName();
        }

        return handler;
    }

    /**
     * Convert a Component implmentation classname into a shorthand
     * name.  It assumes all classnames for a particular component is
     * unique.
     *
     * @param className  The classname of a component
     * @return String the short name
     */
    public static final String createShortName( final String className )
    {
        final StringBuffer shortName = new StringBuffer();

        final char[] name = className.substring(
            className.lastIndexOf( '.' ) + 1 ).toCharArray();
        char last = '\0';

        for (int i = 0; i < name.length; i++)
        {
            if (Character.isUpperCase(name[i]))
            {
                if ( Character.isLowerCase( last ) )
                {
                    shortName.append('-');
                }

                shortName.append(Character.toLowerCase(name[i]));
            }
            else
            {
                shortName.append(name[i]);
            }

            last = name[i];
        }

        return shortName.toString().toLowerCase();
    }
}
