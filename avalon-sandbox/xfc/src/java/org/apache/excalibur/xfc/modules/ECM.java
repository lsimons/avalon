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
 * @version CVS $Id: ECM.java,v 1.1 2002/10/02 17:32:28 crafterm Exp $
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

    private static Map m_handlers = new HashMap();

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
     * @return a <code>Model</code> instance
     * @exception Exception if an error occurs
     */
    public Model generate( final String context )
        throws Exception
    {
        Configuration[] roles = getRoles( getRoleFile( context ) );
        Model model = new Model();

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Identified total of " + roles.length + " roles" );
        }

        // for each role create a type object
        for ( int i = 0; i < roles.length; ++i )
        {
            model.addDefinition( buildDefinition( roles[i] ) );
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
     * Method to construct a {@link Definition} object from
     * a Role definition.
     *
     * @param role role information
     * @return a <code>Definition</code> instance
     * @exception Exception if an error occurs
     */
    protected Definition buildDefinition( final Configuration role )
        throws Exception
    {
        return new Definition(
            getRole( role ),
            getDefaultClass( role ),
            getShorthand( role ),
            getHandler( role )
        );
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
     * style
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
     * Method for extracting a role's shorthand name, ECM
     * style
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
     * @param role role <code>Configuration</code> information
     * @return normalized handler name
     * @exception Exception if an error occurs
     */
    protected String getHandler( final Configuration role )
        throws Exception
    {
        try
        {
            Class clazz = Class.forName( getDefaultClass( role ) );
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
                    "Could not load Class " + role.getAttribute( "default-class" ) +
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
     * @param model a <code>Model</code> instance
     * @param context ECM output Context
     * @exception Exception if an error occurs
     */
    public void serialize( final Model model, final String context )
        throws Exception
    {
        RoleRef[] rolerefs = model.getDefinitions();
        DefaultConfiguration roles = new DefaultConfiguration("role-list", "");

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
     * @param roleref a <code>RoleRef</code> instance
     * @return role definition as a <code>Configuration</code> instance
     * @exception Exception if an error occurs
     */
    protected Configuration buildRole( final RoleRef roleref )
        throws Exception
    {
        DefaultConfiguration role = new DefaultConfiguration("role", "");

        Definition[] defs = roleref.getProviders();

        if ( defs.length > 1 )
        {
            // REVISIT: generate component selector
        }
        else
        {
            role.setAttribute( "name", roleref.getRole() );
            role.setAttribute( "shorthand", defs[0].getShorthand() );
            role.setAttribute( "default-class", defs[0].getDefaultClass() );
        }

        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Building role for model: " + roleref.getRole() );
        }

        return role;
    }

    // Default mappings for ECM and Type lifestyles
    static
    {
        // ECM -> Type
        m_handlers.put( SINGLETHREADED, TRANSIENT );
        m_handlers.put( POOLABLE, POOLED );
        m_handlers.put( RECYCLABLE, POOLED );
        m_handlers.put( THREADSAFE, SINGLETON );
    }
}
