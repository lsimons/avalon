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
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.excalibur.xfc.model.Model;
import org.apache.excalibur.xfc.model.Definition;
import org.apache.excalibur.xfc.model.Instance;
import org.apache.excalibur.xfc.model.RoleRef;

import org.apache.excalibur.xfc.modules.Constants;

/**
 * ECM module serialization class. This class contains the implementation
 * of the <code>serialize</code> method defined in {@link ECM}.

 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: ECMSerializer.java,v 1.1 2002/10/16 16:20:38 crafterm Exp $
 */
public class ECMSerializer extends AbstractLogEnabled
    implements Constants
{
    protected final DefaultConfigurationSerializer m_serializer;

    /**
     * Constructor, initializes serializer.
     */
    public ECMSerializer()
    {
        m_serializer = new DefaultConfigurationSerializer();
        m_serializer.setIndent( true );
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
        // create the role file
        RoleRef[] rolerefs = model.getDefinitions();
        DefaultConfiguration roles = new DefaultConfiguration( ROLELIST, "" );

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
            xconf.addChild( buildXConf( instances[j] ) );
        }

        m_serializer.serializeToFile( getConfigurationFile( context ), xconf );
    }

    /**
     * Helper method for obtaining the Role file.
     *
     * @param context a <code>String</code> value
     * @return a <code>File</code> value
     */
    protected File getRoleFile( final String context )
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
    protected File getConfigurationFile( final String context )
    {
        int i = context.indexOf( CONTEXT_SEPARATOR );
        return new File( context.substring( i + 1 ) );
    }

    // ROLE GENERATION METHODS

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
        DefaultConfiguration role = new DefaultConfiguration( ROLE, "" );
        Definition[] defs = ref.getProviders();

        for ( int i = 0; i < defs.length; ++i )
        {
            DefaultConfiguration hint = new DefaultConfiguration( HINT, "" );
            hint.setAttribute( SHORTHAND, defs[i].getShorthand() );
            hint.setAttribute( CLASS, defs[i].getDefaultClass() );
            role.addChild( hint );
        }

        role.setAttribute( NAME, ref.getRole() );
        role.setAttribute( SHORTHAND, ref.getShorthand() );
        role.setAttribute( DEFAULT, ECS );

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
        DefaultConfiguration role = new DefaultConfiguration( ROLE, "" );
        Definition[] defs = ref.getProviders();

        // there is only 1 provider, use index 0 directly
        role.setAttribute( NAME, ref.getRole() );
        role.setAttribute( SHORTHAND, ref.getShorthand() );
        role.setAttribute( DEFAULT, defs[0].getDefaultClass() );

        return role;
    }

    // XCONF GENERATION METHODS

    /**
     * Builds a Configuration object from an instance declaration
     *
     * @param i an {@link Instance} instance
     * @return a <code>Configuration</code> instance
     * @exception Exception if an error occurs
     */
    private Configuration buildXConf( final Instance i )
        throws Exception
    {
        // has shorthand
        if ( i.getShorthand() != null )
        {
            return buildSingleRoleXConf( i );
        }

        if ( i.getSubInstances() == null )
        {
            // has no shorthand, no subinstances
            return buildNonRoleSingleXConf( i );
        }

        // has no shorthand, has subinstances
        return buildNonRoleMultiXConf( i );

        // return buildMultiRoleXConf();
    }

    /**
     * Builds a Configuration object from an Instance declaration,
     * referring to a single role based component.
     *
     * @param i an <code>Instance</code> value
     * @return a <code>Configuration</code> value
     * @exception Exception if an error occurs
     */
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

        if ( i.getClassImpl() != null )
        {
            conf.setAttribute( CLASS, i.getClassImpl() );
        }

        return conf;
    }

    /**
     * Builds a Configuration object from an Instance declaration,
     * referring to a single non role based component. 
     *
     * @param i an <code>Instance</code> value
     * @return a <code>Configuration</code> value
     * @exception Exception if an error occurs
     */
    private Configuration buildNonRoleSingleXConf( final Instance i )
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration( COMPONENT, "" );

        conf.setAttribute( ROLE, i.getRole() );
        conf.setAttribute( CLASS, i.getClassImpl() );

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

    /**
     * Builds a Configuration object from an Instance declaration,
     * referring to a non role based component selector component.
     *
     * @param i an <code>Instance</code> value
     * @return a <code>Configuration</code> value
     * @exception Exception if an error occurs
     */
    private Configuration buildNonRoleMultiXConf( final Instance i )
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration( COMPONENT, "" );

        conf.setAttribute( ROLE, i.getRole() );
        conf.setAttribute( CLASS, ECS );

        Instance[] subs = i.getSubInstances();

        for ( int j = 0; j < subs.length; ++j )
        {
            DefaultConfiguration child =
                new DefaultConfiguration( COMPONENT_INSTANCE, "" );
            child.setAttribute( CLASS, subs[j].getClassImpl() );
            child.setAttribute( NAME, subs[j].getShorthand() );

            if ( subs[j].getConfiguration() != null )
            {
                Configuration[] kids = subs[j].getConfiguration();

                for ( int k = 0; k < kids.length; ++k )
                {
                    child.addChild( kids[k] );
                }
            }

            conf.addChild( child );
        }

        return conf;
    }

    /**
     * Describe <code>buildRoleMultiXConf</code> method here.
     *
     * @param i an <code>Instance</code> value
     * @return a <code>Configuration</code> value
     * @exception Exception if an error occurs
     */
    private Configuration buildRoleMultiXConf( final Instance i )
        throws Exception
    {
        // REVISIT
        throw new UnsupportedOperationException( "Not yet implemented" );
    }
}
