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
import org.apache.excalibur.xfc.model.Instance;
import org.apache.excalibur.xfc.model.Model;
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
 * @version CVS $Id: Fortress.java,v 1.5 2002/10/14 16:17:50 crafterm Exp $
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

    // Normalized mappings for Fortress component handlers
    static
    {
        // Fortress -> Normalized
        m_handlers.put( FACTORY, TRANSIENT );
        m_handlers.put( PERTHREAD, THREAD );
        m_handlers.put( POOLABLE, POOLED );
        m_handlers.put( THREADSAFE, SINGLETON );

        // Normalized -> Fortress
        m_handlers.put( TRANSIENT, FACTORY );
        m_handlers.put( THREAD, PERTHREAD );
        m_handlers.put( POOLED, POOLABLE );
        m_handlers.put( SINGLETON, THREADSAFE );
    }

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

        if ( hints.length > 1 )
        {
            return buildMultipleComponentRoleRef( role );
        }

        return buildSingleComponentRoleRef( role );
    }

    /**
     * Method to construct a {@link RoleRef} object from a Configuration
     * definition that defines a multiple component based role.
     *
     * @param role a <code>Configuration</code> definition of a role
     * @return a {@link RoleRef} instance
     * @exception Exception if an error occurs
     */
    private RoleRef buildMultipleComponentRoleRef( final Configuration role )
        throws Exception
    {
        Configuration[] hints = role.getChildren( "component" );
        Definition[] definitions = new Definition[ hints.length ];

        for ( int i = 0; i < hints.length; ++i )
        {
            definitions[i] =
                new Definition(
                    getHintClass( hints[i] ),
                    getShorthand( hints[i] ),
                    getHandler( hints[i] )
                );
        }

        return new RoleRef( getRole( role ), "UNKNOWN", definitions );
    }

    /**
     * Method to create a {@link RoleRef} object for a Configuration 
     * definition that defines a single component based role.
     *
     * @param role a <code>Configuration</code> definition of a role
     * @return a {@link RoleRef} instance
     * @exception Exception if an error occurs
     */
    private RoleRef buildSingleComponentRoleRef( final Configuration role )
        throws Exception
    {
        Configuration config = role.getChild( "component" );
        Definition definition =
            new Definition(
                getHintClass( config ),
                getShorthand( config ),
                getHandler( config )
            );

        return new RoleRef( getRole( role ), getShorthand( config ), definition );
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
        return getLifestyleType( role.getAttribute( "handler" ), FACTORY );
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

    protected Instance buildInstance( final Configuration i )
        throws Exception
    {
        if ( i.getName().equals( COMPONENT ) )
        {
            // build non-role component
            return buildNonRoleComponentInstance( i );
        }

        // build role based component
        return buildRoleComponentInstance( i );
    }

    protected Instance buildNonRoleComponentInstance( final Configuration i )
        throws Exception
    {
        return new Instance(
            i.getChildren(),
            i.getAttribute( "class" ),
            i.getAttribute( "role" ),
            null
        );
    }

    protected Instance buildRoleComponentInstance( final Configuration i )
        throws Exception
    {
        return new Instance(
            i.getName(),
            i.getChildren(),
            null,
            null
        );
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
        // single role definitions are the same as multiple definitions
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
                "handler", getLifestyleType( defs[i].getHandler(), FACTORY )
            );

            role.addChild( hint );
        }

        role.setAttribute( "name", ref.getRole() );

        return role;
    }

    /**
     * Serializes a {@link Model} definition, ECM style, to an
     * output context.
     *
     * @param model a {@link Model} instance
     * @param context ECM output Context
     * @exception Exception if an error occurs
     */
    public void serialize( final Model model, final String context )
        throws Exception
    {
        validateContext( context );

        // create the role file
        RoleRef[] rolerefs = model.getDefinitions();
        DefaultConfiguration roles = new DefaultConfiguration( "role-list", "" );

        // for each type object generate a roles file entry
        for ( int i = 0; i < rolerefs.length; ++i )
        {
            roles.addChild( buildRole( rolerefs[i] ) );
        }

        m_serializer.serializeToFile( getRoleFile( context ), roles );

        // create the xconf file
        Instance[] instances = model.getInstances();
        DefaultConfiguration xconf = new DefaultConfiguration( "xconf", "" );

        // for each instance object generate an xconf file entry
        for ( int j = 0; j < instances.length; ++j )
        {
            Configuration[] xconfs = buildXConf( instances[j] );

            for ( int k = 0; k < xconfs.length; ++k )
            {
                xconf.addChild( xconfs[k] );
            }
        }

        m_serializer.serializeToFile( getConfigurationFile( context ), xconf );
    }

    protected Configuration[] buildXConf( final Instance i )
        throws Exception
    {
        if ( i.getShorthand() != null )
        {

            if ( i.getSubInstances() == null )
            {
                // has shorthand, single component
                return new Configuration[] { buildSingleRoleXConf( i ) };
            }

            // has shorthand, multi component
            return buildMultiRoleXConf( i );
        }

        if ( i.getSubInstances() == null )
        {
            // has no shorthand, no subinstances
            return new Configuration[] { buildNonRoleSingleXConf( i ) };
        }

        // has no shorthand, has subinstances
        return buildNonRoleMultiXConf( i );
    }

    private Configuration buildSingleRoleXConf( final Instance i )
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration( i.getShorthand(), "" );

        if ( i.getConfiguration() != null )
        {
            Configuration[] kids = i.getConfiguration();

            for ( int j = 0; j < kids.length; ++j )
            {
                conf.addChild( kids[j] );
            }
        }

        conf.setAttribute( "id", i.getShorthand() );

        return conf;
    }

    private Configuration[] buildMultiRoleXConf( final Instance i )
        throws Exception
    {
        Instance[] subinstances = i.getSubInstances();
        Configuration[] xconf = new Configuration[ subinstances.length ];

        for ( int j = 0; j < subinstances.length; ++j )
        {
            xconf[j] = buildSingleRoleXConf( subinstances[j] );
        }

        return xconf;
    }

    private Configuration buildNonRoleSingleXConf( final Instance i )
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration( "component", "" );

        conf.setAttribute( "role", i.getRole() );
        conf.setAttribute( "class", i.getClassImpl() );
        conf.setAttribute( "handler", getLifestyleType( i.getHandler(), FACTORY ) );
        conf.setAttribute( "id", "UNKNOWN" );

        if ( i.getConfiguration() != null )
        {
            Configuration[] kids = i.getConfiguration();

            for ( int j = 0; j < kids.length; ++j )
            {
                conf.addChild( kids[j] );
            }
        }

        return conf;
    }

    private Configuration[] buildNonRoleMultiXConf( final Instance i )
    {
        Instance[] subs = i.getSubInstances();
        Configuration[] xconfs = new Configuration[ subs.length ];

        for ( int j = 0; j < subs.length; ++j )
        {
            DefaultConfiguration conf = new DefaultConfiguration( COMPONENT, "" );

            conf.setAttribute( "role", i.getRole() );
            conf.setAttribute( "class", subs[j].getClassImpl() );
            conf.setAttribute( "handler", getLifestyleType( subs[j].getHandler(), FACTORY ) );
            conf.setAttribute( "id", subs[j].getShorthand() );

            if ( subs[j].getConfiguration() != null )
            {
                Configuration[] kids = subs[j].getConfiguration();

                for ( int k = 0; k < kids.length; ++k )
                {
                    conf.addChild( kids[k] );
                }
            }

            xconfs[j] = conf;
        }

        return xconfs;
    }
}
