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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

import org.apache.excalibur.xfc.model.Model;
import org.apache.excalibur.xfc.model.Definition;
import org.apache.excalibur.xfc.model.RoleRef;

/**
 * <code>ECM</code> module implementation.
 *
 * <p>
 *  This implementation supports ECM style role files. ie:
 *
 *  <pre>
 *   &lt;role-list&gt;
 *     &lt;role name="..." shorthand="..." default-class="..."/&gt;
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
 * @version CVS $Id: ECM.java,v 1.4 2002/10/07 17:13:17 crafterm Exp $
 */
public class ECM extends AbstractModule
{
    // Avalon Framework and Excalibur Pool markers
    private static final String SINGLETHREADED =
        "org.apache.avalon.framework.thread.SingleThreaded";
    private static final String THREADSAFE =
        "org.apache.avalon.framework.thread.ThreadSafe";
    private static final String POOLABLE =
        "org.apache.avalon.excalibur.mpool.Poolable";
    private static final String RECYCLABLE =
        "org.apache.avalon.excalibur.mpool.Recyclable";

    // ExcaliburComponentSelector name
    private static final String ECS =
        "org.apache.avalon.excalibur.component.ExcaliburComponentSelector";

    private static Map m_handlers = new HashMap();

    // Normalized mappings for ECM lifestyles
    static
    {
        // ECM -> Normalized
        m_handlers.put( SINGLETHREADED, TRANSIENT );
        m_handlers.put( POOLABLE, POOLED );
        m_handlers.put( RECYCLABLE, POOLED );
        m_handlers.put( THREADSAFE, SINGLETON );
    }

    /**
     * Generates a {@link Model} based on an a given ECM style
     * Context.
     *
     * <p>
     *  The specified Context string names the ECM role and
     *  xconf files, separated by a ':' character. ie:
     *  <code>ecm.roles:ecm.xconf</code>
     * </p>
     *
     * @param context a <code>String</code> context value
     * @return a {@link Model} instance
     * @exception Exception if an error occurs
     */
    public Model generate( final String context )
        throws Exception
    {
        validateContext( context );

        Configuration[] roles = getRoles( getRoleFile( context ) );
        Model model = new Model();

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Identified total of " + roles.length + " roles" );
        }

        // for each role create a type object
        for ( int i = 0; i < roles.length; ++i )
        {
            model.addRoleRef( buildRoleRef( roles[i] ) );
        }

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Model built" );
        }

        return model;
    }

    /**
     * Helper method for obtaining the Role file.
     *
     * @param context a <code>String</code> value
     * @return a <code>File</code> value
     */
    private File getRoleFile( final String context )
    {
        int i = context.indexOf( CONTEXT_SEPARATOR );
        return new File( context.substring( 0, i ) );
    }

    /**
     * Helper method for obtaining the Configuration file.
     *
     * @param context a <code>String</code> value
     * @return a <code>File</code> value
     */
    private File getConfigurationFile( final String context )
    {
        int i = context.indexOf( CONTEXT_SEPARATOR );
        return new File( context.substring( i + 1 ) );
    }

    /**
     * Helper method for obtaining the roles defined in
     * a particular input file.
     *
     * @param input a <code>File</code> value
     * @return a <code>Configuration[]</code> value
     * @exception Exception if an error occurs
     */
    private Configuration[] getRoles( final File input )
        throws Exception
    {
        Configuration config = m_builder.buildFromFile( input );
        return config.getChildren( "role" );
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
        if ( role.getChildren( "hint" ).length > 0 )   // component selector definition
        {
            return buildMultipleComponentRoleRef( role );
        }

        // single component definition
        return buildSingleComponentRoleRef( role );
    }

    /**
     * Method for constructing a {@link RoleRef} object from a single
     * component role definition.
     *
     * @param role a <code>Configuration</code> value
     * @return a {@link RoleRef} value
     * @exception Exception if an error occurs
     */
    protected RoleRef buildSingleComponentRoleRef( final Configuration role )
        throws Exception
    {
        Definition def =
            new Definition(
                getDefaultClass( role ),
                getShorthand( role ),
                getHandler( getDefaultClass( role ) )
            );

        return new RoleRef( getRole( role ), getShorthand( role ), def );
    }

    /**
     * Method for constructing a {@link RoleRef} object from a multiple
     * component role definition (ie. component selector).
     *
     * @param role a <code>Configuration</code> value
     * @return a {@link RoleRef} value
     * @exception Exception if an error occurs
     */
    protected RoleRef buildMultipleComponentRoleRef( final Configuration role )
        throws Exception
    {
        Configuration[] hints = role.getChildren( "hint" );
        Definition[] definitions = new Definition[ hints.length ];

        for ( int i = 0; i < hints.length; ++i )
        {
            definitions[i] =
                new Definition(
                    getHintClass( hints[i] ),
                    getShorthand( hints[i] ),
                    getHandler( getHintClass( hints[i] ) )
                );
        }

        return new RoleRef( getRole( role ), getShorthand( role ), definitions );
    }

    /**
     * Method to extract the role name ECM style.
     *
     * @param role role <code>Configuration</code> information
     * @return the role name
     * @exception Exception if an error occurs
     */
    protected String getRole( final Configuration role )
        throws Exception
    {
        return role.getAttribute( "name" );
    }

    /**
     * Method to extract a role's implementing class, ECM
     * style.
     *
     * @param role role <code>Configuration</code> information
     * @return the implementing class name
     * @exception Exception if an error occurs
     */
    protected String getDefaultClass( final Configuration role )
        throws Exception
    {
        return role.getAttribute( "default-class" );
    }

    /**
     * Method to extract a hint's implementing class, ECM
     * style.
     *
     * @param role role <code>Configuration</code> information
     * @return the implementing class name
     * @exception Exception if an error occurs
     */
    protected String getHintClass( final Configuration role )
        throws Exception
    {
        return role.getAttribute( "class" );
    }

    /**
     * Method for extracting a role's shorthand name, ECM
     * style.
     *
     * @param role role <code>Configuration</code> information
     * @return the shorthand name
     * @exception Exception if an error occurs
     */
    protected String getShorthand( final Configuration role )
        throws Exception
    {
        return role.getAttribute( "shorthand" );
    }

    /**
     * Method for extracting a role's ComponentHandler name,
     * ECM style. ECM roles don't define ComponentHandlers explicitly,
     * so some simple class analysis is made in this method to
     * try to ascertain which handler has been chosed by the 
     * implementor.
     *
     * @param classname class name as a <code>String</code> value
     * @return normalized handler name
     * @exception Exception if an error occurs
     */
    protected String getHandler( final String classname )
        throws Exception
    {
        try
        {
            Class clazz = Class.forName( classname );
            Class[] interfaces = clazz.getInterfaces();

            for ( int i = 0; i < interfaces.length; ++i )
            {
                if ( m_handlers.containsKey( interfaces[ i ] ) )
                {
                    return (String) m_handlers.get( interfaces[ i ] );
                }
            }

            if ( getLogger().isWarnEnabled() )
            {
                getLogger().warn(
                    "Defaulting to 'transient' lifestyle for component " +
                    clazz.getName()
                );
            }

            return TRANSIENT;
        }
        catch ( ClassNotFoundException e )
        {
            if ( getLogger().isWarnEnabled() )
            {
                getLogger().warn(
                    "Could not load Class " + classname +
                    " for Component Handler analysis, defaulting to 'transient'"
                );
            }

            /* leave out for the moment
            if ( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Exception: ", e );
            }
            */

            return TRANSIENT;
        }
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

        RoleRef[] rolerefs = model.getDefinitions();
        DefaultConfiguration roles = new DefaultConfiguration( "role-list", "" );

        // for each type object generate a roles file entry
        for ( int i = 0; i < rolerefs.length; ++i )
        {
            roles.addChild( buildRole( rolerefs[i] ) );
        }

        m_serializer.serializeToFile( getRoleFile( context ), roles );
    }

    /**
     * Method to build a Role definition from a {@link RoleRef}
     * object.
     *
     * @param roleref a {@link RoleRef} instance
     * @return role definition as a <code>Configuration</code> instance
     * @exception Exception if an error occurs
     */
    protected Configuration buildRole( final RoleRef roleref )
        throws Exception
    {
        Definition[] defs = roleref.getProviders();

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Building role for model: " + roleref.getRole() );
        }

        if ( roleref.getProviders().length > 1 )
        {
            return buildMultipleComponentRole( roleref );
        }

        return buildSingleComponentRole( roleref );
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
            DefaultConfiguration hint = new DefaultConfiguration( "hint", "" );
            hint.setAttribute( "shorthand", defs[i].getShorthand() );
            hint.setAttribute( "class", defs[i].getDefaultClass() );
            role.addChild( hint );
        }

        role.setAttribute( "name", ref.getRole() );
        role.setAttribute( "shorthand", ref.getShorthand() );
        role.setAttribute( "default-class", ECS );

        return role;
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
        DefaultConfiguration role = new DefaultConfiguration( "role", "" );
        Definition[] defs = ref.getProviders();

        // there is only 1 provider, use index 0 directly
        role.setAttribute( "name", ref.getRole() );
        role.setAttribute( "shorthand", ref.getShorthand() );
        role.setAttribute( "default-class", defs[0].getDefaultClass() );

        return role;
    }

    /**
     * Helper method to validate the input & output context's
     * given to this module.
     *
     * @param context a <code>String</code> context value
     * @exception Exception if an error occurs
     */
    private void validateContext( final String context )
        throws Exception
    {
        if ( context.indexOf( CONTEXT_SEPARATOR ) == -1 )
            throw new IllegalArgumentException(
                "Module requires the role and xconf filename " +
                "separated by a '" + CONTEXT_SEPARATOR + "' character"
            );
    }
}
