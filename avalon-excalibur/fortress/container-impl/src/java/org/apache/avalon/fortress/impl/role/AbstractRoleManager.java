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
package org.apache.avalon.fortress.impl.role;

import org.apache.avalon.fortress.RoleEntry;
import org.apache.avalon.fortress.RoleManager;
import org.apache.avalon.fortress.impl.handler.PerThreadComponentHandler;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import java.util.HashMap;
import java.util.Map;

/**
 * The Excalibur Role Manager is used for Excalibur Role Mappings.  All of
 * the information is hard-coded.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.11 $ $Date: 2003/05/22 14:36:20 $
 * @since 4.1
 */
public abstract class AbstractRoleManager
    extends AbstractLogEnabled
    implements RoleManager
{
    /**
     * The classloader used to load and check roles and components.
     */
    private final ClassLoader m_loader;

    /**
     * Map for shorthand to RoleEntry.
     */
    private final Map m_shorthands = new HashMap();

    /**
     * Map for classname to RoleEntry.
     */
    private final Map m_classnames = new HashMap();

    /**
     * Parent <code>RoleManager</code> for nested resolution.
     */
    private final RoleManager m_parent;

    /**
     * Default constructor--this RoleManager has no parent.
     */
    public AbstractRoleManager()
    {
        this( null );
    }

    /**
     * Alternate constructor--this RoleManager has the specified
     * parent.
     *
     * @param parent  The parent <code>RoleManager</code>.
     */
    public AbstractRoleManager( final RoleManager parent )
    {
        this( parent, Thread.currentThread().getContextClassLoader() );
    }

    /**
     * Create an AbstractRoleManager with the specified parent manager and the
     * supplied classloader.
     *
     * @param parent  The parent <code>RoleManager</code>
     * @param loader  The class loader
     */
    public AbstractRoleManager( final RoleManager parent,
                                final ClassLoader loader )
    {
        ClassLoader thisLoader = loader;
        if ( null == thisLoader )
        {
            thisLoader = Thread.currentThread().getContextClassLoader();
        }

        m_loader = thisLoader;
        m_parent = parent;
    }

    /**
     * Addition of a role to the role manager.
     * @param shortName the shor name for the role
     * @param role the role
     * @param className the class name
     * @param handlerClassName the handler classname
     */
    protected final boolean addRole( final String shortName,
                                  final String role,
                                  final String className,
                                  final String handlerClassName )
    {
        final Class clazz;
        final Class handlerKlass;

        try
        {
            clazz = m_loader.loadClass( className );
        }
        catch ( final Exception e )
        {
            final String message =
                "Unable to load class " + className + ". Skipping.";
            getLogger().warn( message );
            return false;
        }

        if ( null != handlerClassName )
        {
            try
            {
                handlerKlass = m_loader.loadClass( handlerClassName );
            }
            catch ( final Exception e )
            {
                final String message = "Unable to load handler " +
                    handlerClassName + " for class " + className + ". Skipping.";
                getLogger().warn( message );
                return false;
            }
        }
        else
        {
            handlerKlass = getDefaultHandler();
        }

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "addRole role: name='" + shortName + "', role='" + role + "', "
                + "class='" + className + "', handler='" + handlerClassName + "'" );
        }

        final RoleEntry entry = new RoleEntry( role, shortName, clazz, handlerKlass );
        m_shorthands.put( shortName, entry );
        m_classnames.put( className, entry );

        return true;
    }

    /**
     * Get the default component handler.
     *
     * @return the class for {@link PerThreadComponentHandler}
     */
    protected final Class getDefaultHandler()
    {
        return PerThreadComponentHandler.class;
    }

    public final RoleEntry getRoleForClassname( final String classname )
    {
        final RoleEntry roleEntry = (RoleEntry) m_classnames.get( classname );
        if ( null != roleEntry )
        {
            return roleEntry;
        }
        else if ( null != m_parent )
        {
            return m_parent.getRoleForClassname( classname );
        }
        else
        {
            return null;
        }
    }

    /**
     * Return a role name relative to a supplied short name.
     *
     * @param shortname the short name
     * @return the role entry
     */
    public final RoleEntry getRoleForShortName( final String shortname )
    {
        final RoleEntry roleEntry = (RoleEntry) m_shorthands.get( shortname );
        if ( null != roleEntry )
        {
            return roleEntry;
        }
        else if ( null != m_parent )
        {
            return m_parent.getRoleForShortName( shortname );
        }
        else
        {
            return null;
        }
    }

    /**
     * Get the classloader used for the RoleManager for any class that
     * extends this one.
     *
     * @return ClassLoader
     */
    protected final ClassLoader getLoader()
    {
        return m_loader;
    }
}
