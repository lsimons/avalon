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
package org.apache.excalibur.xfc.modules.fortress;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

import org.apache.excalibur.xfc.model.Definition;
import org.apache.excalibur.xfc.model.Instance;
import org.apache.excalibur.xfc.model.Model;
import org.apache.excalibur.xfc.model.RoleRef;

import org.apache.excalibur.xfc.modules.ecm.ECMSerializer;

/**
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: FortressSerializer.java,v 1.1 2002/10/16 16:20:38 crafterm Exp $
 */
public class FortressSerializer extends ECMSerializer
{
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

    // ROLE GENERATION METHODS

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
        DefaultConfiguration role = new DefaultConfiguration( ROLE, "" );
        Definition[] defs = ref.getProviders();

        for ( int i = 0; i < defs.length; ++i )
        {
            DefaultConfiguration hint = new DefaultConfiguration( COMPONENT, "" );
            hint.setAttribute( SHORTHAND, defs[i].getShorthand() );
            hint.setAttribute( CLASS, defs[i].getDefaultClass() );
            hint.setAttribute(
                HANDLER, HandlerMapper.getHandler( defs[i].getHandler() )
            );

            role.addChild( hint );
        }

        role.setAttribute( NAME, ref.getRole() );

        return role;
    }

    // XCONF GENERATION METHODS

    /**
     * Describe <code>buildXConf</code> method here.
     *
     * @param i an <code>Instance</code> value
     * @return a <code>Configuration[]</code> value
     * @exception Exception if an error occurs
     */
    private Configuration[] buildXConf( final Instance i )
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

    /**
     * Describe <code>buildSingleRoleXConf</code> method here.
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

        conf.setAttribute( ID, i.getShorthand() );

        return conf;
    }

    /**
     * Describe <code>buildMultiRoleXConf</code> method here.
     *
     * @param i an <code>Instance</code> value
     * @return a <code>Configuration[]</code> value
     * @exception Exception if an error occurs
     */
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

    /**
     * Describe <code>buildNonRoleSingleXConf</code> method here.
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
        conf.setAttribute( HANDLER, HandlerMapper.getHandler( i.getHandler() ) );
        conf.setAttribute( ID, "UNKNOWN" );

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
     * Describe <code>buildNonRoleMultiXConf</code> method here.
     *
     * @param i an <code>Instance</code> value
     * @return a <code>Configuration[]</code> value
     */
    private Configuration[] buildNonRoleMultiXConf( final Instance i )
    {
        Instance[] subs = i.getSubInstances();
        Configuration[] xconfs = new Configuration[ subs.length ];

        for ( int j = 0; j < subs.length; ++j )
        {
            DefaultConfiguration conf = new DefaultConfiguration( COMPONENT, "" );

            conf.setAttribute( ROLE, i.getRole() );
            conf.setAttribute( CLASS, subs[j].getClassImpl() );
            conf.setAttribute( HANDLER, HandlerMapper.getHandler( subs[j].getHandler() ) );
            conf.setAttribute( ID, subs[j].getShorthand() );

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
