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
package org.apache.excalibur.xfc.modules;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

import org.apache.excalibur.xfc.model.Definition;
import org.apache.excalibur.xfc.model.RoleRef;

/**
 * <code>Fortress</code> module implementation.
 *
 * <p>
 *  Fortress style role files are an extension to the ECM model.
 *  This implementation supports Fortress style role files. ie:
 *
 *  <pre>
 *   &lt;role-list&gt;
 *     &lt;role name=""&gt;
 *       &lt;component shorthand="..." class="..." handler="..."/&gt;
 *     &lt;/role&gt;
 *   &lt;/role-list&gt;
 *  </pre>
 * </p>
 *
 * <p>
 *  The input context should be the name of the roles file, followed
 *  by the name of the configuration file, separated by a colon.
 *  eg: definitions.roles:config.xconf
 * </p>
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: Fortress.java,v 1.2 2002/10/04 14:46:35 crafterm Exp $
 */
public class Fortress extends ECM
{
    // ComponentHandler constants
    private static final String FACTORY =
        "org.apache.excalibur.fortress.handler.FactoryComponentHandler";
    private static final String PERTHREAD =
        "org.apache.excalibur.fortress.handler.PerThreadComponentHandler";
    private static final String POOLABLE =
        "org.apache.excalibur.fortress.handler.PoolableComponentHandler";
    private static final String THREADSAFE =
        "org.apache.excalibur.fortress.handler.ThreadSafeComponentHandler";

    // Map of fortress/type handlers
    private static final Map m_handlers = new HashMap();

    // <role-list>
    //  <role name="">
    //   <component shorthand="" class="" handler="">
    //  </role>
    // </role-list>

    /**
     * Method to construct a {@link RoleRef} object from
     * a Role definition.
     *
     * @param role role information
     * @return a {@link RoleRef} instance
     * @exception Exception if an error occurs
     */
    protected RoleRef buildRoleRef( final Configuration role )
        throws Exception
    {
        Configuration[] hints = role.getChildren( "component" );
        Definition[] definitions = new Definition[ hints.length ];

        for ( int i = 0; i < hints.length; ++i )
        {
            definitions[i] =
                new Definition(
                    getRole( role ),
                    getHintClass( hints[i] ),
                    getShorthand( hints[i] ),
                    getHandler( getHintClass( hints[i] ) )
                );
        }

        return new RoleRef( getRole( role ), definitions );
    }

    /**
     * Method for extracting a role's default implementing class, 
     * Fortress style.
     *
     * @param role role <code>Configuration</code> information
     * @return the role's default implementing class name
     * @exception Exception if an error occurs
     */
    protected String getDefaultClass( final Configuration role )
        throws Exception
    {
        return role.getAttribute( "class" );
    }

    /**
     * Method for extracting a role's ComponentHandler name.
     *
     * @param role role <code>Configuration</code> information
     * @return the role's normalized handler name
     * @exception Exception if an error occurs
     */
    protected String getHandler( final Configuration role )
        throws Exception
    {
        return getLifestyleType( role.getAttribute( "handler" ), TRANSIENT );
    }

    /**
     * Helper method to convert known Fortress ComponentHandler types to meta
     * REVISIT: meta should define transient/thread/pooled/etc as constants.
     *
     * @param handler a <code>String</code> value
     * @param defaultValue a <code>String</code> default value if handler
     *                     type cannot be found
     * @return a <code>String</code> value
     */
    private String getLifestyleType( String handler, String defaultValue )
    {
        if ( handler != null )
        {
            String type = (String) m_handlers.get( handler );

            if ( type != null )
                return type;
        }

        if ( getLogger().isWarnEnabled() )
        {
            getLogger().warn(
                "Custom or unknown handler " + handler +
                " defined, defaulting to " + defaultValue
            );
        }

        return defaultValue;
    }

    /**
     * Builds a single component Role definition from a {@link RoleRef}
     * definition.
     *
     * @param ref a {@link RoleRef} instance
     * @return a <code>Configuration</code> instance
     * @exception Exception if an error occurs
     */
    protected Configuration buildSingleComponentRole( final RoleRef ref )
        throws Exception
    {
        return buildMultipleComponentRole( ref );
    }

    /**
     * Builds a multiple component Role definition (ie ComponentSelector based)
     * from a {@link RoleRef} definition.
     *
     * @param ref a {@link RoleRef} instance
     * @return a <code>Configuration</code> instance
     * @exception Exception if an error occurs
     */
    protected Configuration buildMultipleComponentRole( final RoleRef ref )
        throws Exception
    {
        DefaultConfiguration role = new DefaultConfiguration( "role", "" );
        Definition[] defs = ref.getProviders();

        for ( int i = 0; i < defs.length; ++i )
        {
            DefaultConfiguration hint = new DefaultConfiguration( "component", "" );
            hint.setAttribute( "shorthand", defs[i].getShorthand() );
            hint.setAttribute( "class", defs[i].getDefaultClass() );
            hint.setAttribute(
                "handler", getLifestyleType( defs[i].getHandler(), TRANSIENT )
            );

            role.addChild( hint );
        }

        role.setAttribute( "name", ref.getRole() );

        return role;
    }

    // Default mappings for Fortress and Type component handlers
    static
    {
        // Fortress -> Type
        m_handlers.put( FACTORY, TRANSIENT );
        m_handlers.put( PERTHREAD, THREAD );
        m_handlers.put( POOLABLE, POOLED );
        m_handlers.put( THREADSAFE, SINGLETON );

        // Type -> Fortress
        m_handlers.put( TRANSIENT, FACTORY );
        m_handlers.put( THREAD, PERTHREAD );
        m_handlers.put( POOLED, POOLABLE );
        m_handlers.put( SINGLETON, THREADSAFE );
    }
}
