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
package org.apache.excalibur.xfc.modules.ecm;

import java.io.File;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.xfc.model.Definition;
import org.apache.excalibur.xfc.model.Model;
import org.apache.excalibur.xfc.model.instance.Instance;
import org.apache.excalibur.xfc.model.instance.MultiNonRoleInstance;
import org.apache.excalibur.xfc.model.instance.MultiRoleInstance;
import org.apache.excalibur.xfc.model.instance.SelectorHintInstance;
import org.apache.excalibur.xfc.model.instance.SingleNonRoleInstance;
import org.apache.excalibur.xfc.model.instance.SingleRoleInstance;
import org.apache.excalibur.xfc.model.role.MultiRoleRef;
import org.apache.excalibur.xfc.model.role.RoleRef;
import org.apache.excalibur.xfc.model.role.SingleRoleRef;
import org.apache.excalibur.xfc.modules.Constants;

/**
 * ECM module generation class. This class contains the implementation
 * of the <code>generate</code> method defined in {@link ECM}.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: ECMGenerator.java,v 1.6 2002/11/12 20:07:50 donaldp Exp $
 */
public class ECMGenerator
    extends AbstractLogEnabled
    implements Constants
{
    protected final DefaultConfigurationBuilder m_builder =
        new DefaultConfigurationBuilder();

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
        Model model = new Model();

        addRoles( model, context );
        addInstances( model, context );

        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "Model built" );
        }

        return model;
    }

    /**
     * Helper method for adding all role definitions to the
     * given model.
     *
     * @param model a {@link Model} instance
     * @param context input context
     * @exception Exception if an error occurs
     */
    private void addRoles( final Model model, final String context )
        throws Exception
    {
        // locate all roles
        Configuration[] roles = getRoles( getRoleFile( context ) );

        if( getLogger().isDebugEnabled() )
        {
            final String message = 
                "Identified total of " + roles.length + " roles";
            getLogger().debug( message );
        }

        // for each role create a type object
        for( int i = 0; i < roles.length; ++i )
        {
            model.addRoleRef( buildRoleRef( roles[ i ] ) );
        }
    }

    /**
     * Helper method for adding all component instance definitions
     * to the given model.
     *
     * @param model a {@link Model} instance
     * @param context input context
     * @exception Exception if an error occurs
     */
    private void addInstances( final Model model, final String context )
        throws Exception
    {
        // locate all component instances
        Configuration[] instances = getInstanceList( getConfigurationFile( context ) );

        if( getLogger().isDebugEnabled() )
        {
            final String message = 
                "Identified total of " + instances.length + " component instances";
            getLogger().debug( message );
        }

        for( int i = 0; i < instances.length; ++i )
        {
            model.addInstance( buildInstance( instances[ i ], model ) );
        }
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
        return config.getChildren( ROLE );
    }

    /**
     * Helper method for obtaining the instances defined in
     * a particular input file
     *
     * @param input a <code>File</code> value
     * @return a <code>Configuration[]</code> value
     * @exception Exception if an error occurs
     */
    private Configuration[] getInstanceList( final File input )
        throws Exception
    {
        Configuration config = m_builder.buildFromFile( input );
        return config.getChildren();
    }

    // ROLE GENERATION METHODS

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
        if( isComponentSelectorRole( role ) )
        {
            return buildMultipleComponentRoleRef( role );
        }

        // single component definition
        return buildSingleComponentRoleRef( role );
    }

    /**
     * Method to determine whether the given role definition entails
     * a ComponentSelector style definition or not.
     *
     * @param role a <code>Configuration</code> value
     * @return true if the given role refers to a ECS definition, false otherwise
     */
    protected boolean isComponentSelectorRole( final Configuration role )
    {
        // if there are any 'hint's then we have a component selector
        return role.getChildren( HINT ).length > 0;
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
                role.getAttribute( DEFAULT ),
                role.getAttribute( SHORTHAND ),
                HandlerAnalyzer.getHandler( role.getAttribute( DEFAULT ) )
            );

        return new SingleRoleRef(
            role.getAttribute( NAME ), role.getAttribute( SHORTHAND ), def
        );
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
        Configuration[] hints = role.getChildren( HINT );
        Definition[] definitions = new Definition[ hints.length ];

        for( int i = 0; i < hints.length; ++i )
        {
            definitions[ i ] =
                new Definition(
                    hints[ i ].getAttribute( CLASS ),
                    hints[ i ].getAttribute( SHORTHAND ),
                    HandlerAnalyzer.getHandler( hints[ i ].getAttribute( CLASS ) )
                );
        }

        return new MultiRoleRef(
            role.getAttribute( NAME ), role.getAttribute( SHORTHAND ), definitions
        );
    }

    // INSTANCE GENERATION METHODS

    /**
     * Builds an {@link Instance} object from a given component configuration
     * definition.
     *
     * @param i a <code>Configuration</code> definition
     * @param model a {@link Model} instance
     * @return an {@link Instance} instance
     * @exception Exception if an error occurs
     */
    protected Instance buildInstance( final Configuration i, final Model model )
        throws Exception
    {
        if( i.getName().equals( COMPONENT ) )
        {
            Configuration[] kids = i.getChildren( COMPONENT_INSTANCE );

            if( kids.length > 0 )
            {
                // build non-role component selector
                return buildNonRoleComponentSelectorInstance( i );
            }

            // build non-role component
            return buildNonRoleComponentInstance( i );
        }

        if( isComponentSelectorXConf( i.getName(), model ) )
        {
            // build multi role based component
            return buildRoleComponentSelectorInstance( i );
        }

        // build single role based component
        return buildRoleComponentInstance( i );
    }

    /**
     * Helper method to determine whether the given shorthand corresponds
     * to a component selector based role reference object.
     *
     * @param shorthand shorthand name
     * @param model a {@link Model} instance
     * @return true if this shorthand refers to a component selector role,
     *         false otherwise.
     * @exception Exception if an error occurs
     */
    private boolean isComponentSelectorXConf(
        final String shorthand, final Model model
        )
        throws Exception
    {
        // check if shorthand corresponds to ECM
        RoleRef ref = model.findByShorthand( shorthand );
        return ( ref != null && ref instanceof MultiRoleRef );
    }

    /**
     * Builds an {@link Instance} object from a given xconf
     * configuration framgment, for component selector xconf definitions
     * that do not use RoleManager.
     *
     * @param i a <code>Configuration</code> instance
     * @return an {@link Instance} instance
     * @exception Exception if an error occurs
     */
    private Instance buildNonRoleComponentSelectorInstance(
        final Configuration i
        )
        throws Exception
    {
        final Configuration[] kids = i.getChildren( COMPONENT_INSTANCE );
        final SingleRoleInstance[] subs = new SingleRoleInstance[ kids.length ];

        for( int j = 0; j < kids.length; ++j )
        {
            String clazz = kids[ j ].getAttribute( CLASS );

            subs[ j ] =
                new SingleRoleInstance(
                    kids[ j ].getAttribute( NAME ),
                    clazz,
                    kids[ j ].getChildren(),
                    clazz == null ? null : HandlerAnalyzer.getHandler( clazz )
                );
        }

        return new MultiNonRoleInstance( i.getAttribute( ROLE ), subs );
    }

    /**
     * Builds an {@link Instance} object from a given xconf
     * Configuration fragment, for single component definitions that
     * do not use the role manager.
     *
     * @param i a <code>Configuration</code> instance
     * @return an {@link Instance} instance
     * @exception Exception if an error occurs
     */
    private Instance buildNonRoleComponentInstance( final Configuration i )
        throws Exception
    {
        return new SingleNonRoleInstance(
            i.getAttribute( ROLE ),
            i.getAttribute( CLASS, null ),
            i.getChildren(),
            HandlerAnalyzer.getHandler( i.getAttribute( CLASS, null ) )
        );
    }

    /**
     * Builds an {@link Instance} object from a given xconf
     * Configuration fragment, for single components using rolemanager.
     *
     * @param i a <code>Configuration</code> instance
     * @return an {@link Instance} instance
     * @exception Exception if an error occurs
     */
    private Instance buildRoleComponentInstance( final Configuration i )
        throws Exception
    {
        String clazz = i.getAttribute( CLASS, null );

        return new SingleRoleInstance(
            i.getName(),
            clazz,
            i.getChildren(),
            clazz == null ? null : HandlerAnalyzer.getHandler( clazz )
        );
    }

    /**
     * Builds an {@link Instance} object from a given xconf Configuration
     * fragment, for component selector definitions that use rolemanager.
     *
     * @param i a <code>Configuration</code> instance
     * @return an {@link Instance} instance
     */
    private Instance buildRoleComponentSelectorInstance( final Configuration i )
        throws Exception
    {
        // get the subinstances
        Configuration[] kids = i.getChildren();
        SelectorHintInstance[] subinstances = new SelectorHintInstance[ kids.length ];

        for( int j = 0; j < kids.length; ++j )
        {
            subinstances[ j ] =
                new SelectorHintInstance(
                    kids[ j ].getName(),
                    kids[ j ].getAttribute( NAME ),
                    kids[ j ].getAttribute( CLASS, null ),
                    kids[ j ].getChildren()
                );
        }

        // create the root instance
        return new MultiRoleInstance( i.getName(), subinstances );
    }
}
