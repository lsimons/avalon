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
import org.apache.excalibur.xfc.model.instance.Instance;
import org.apache.excalibur.xfc.model.instance.InstanceVisitor;
import org.apache.excalibur.xfc.model.instance.SingleRoleInstance;
import org.apache.excalibur.xfc.model.instance.SingleNonRoleInstance;
import org.apache.excalibur.xfc.model.instance.MultiRoleInstance;
import org.apache.excalibur.xfc.model.instance.MultiNonRoleInstance;
import org.apache.excalibur.xfc.model.Model;
import org.apache.excalibur.xfc.model.role.SingleRoleRef;
import org.apache.excalibur.xfc.model.role.MultiRoleRef;

import org.apache.excalibur.xfc.modules.ecm.ECMSerializer;

/**
 * Implementation of the <code>serialize</code> method on the {@link Fortress}
 * module.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: FortressSerializer.java,v 1.2 2002/10/17 14:38:18 crafterm Exp $
 */
public class FortressSerializer extends ECMSerializer
{
    // ROLE GENERATION METHODS

    /**
     * Builds a single component Role definition from a {@link SingleRoleRef}
     * definition.
     *
     * @param ref a {@link SingleRoleRef} instance
       * @exception Exception if an error occurs
     */
    public void visit( final SingleRoleRef ref )
        throws Exception
    {
        DefaultConfiguration role = new DefaultConfiguration( ROLE, "" );
        Definition def = ref.getProvider();

        DefaultConfiguration hint = new DefaultConfiguration( COMPONENT, "" );
        hint.setAttribute( SHORTHAND, def.getShorthand() );
        hint.setAttribute( CLASS, def.getDefaultClass() );
        hint.setAttribute( HANDLER, HandlerMapper.getHandler( def.getHandler() ) );

        role.addChild( hint );
        role.setAttribute( NAME, ref.getRole() );

        m_roles.addChild( role );
    }

    /**
     * Builds a multiple component Role definition (ie ComponentSelector based)
     * from a {@link MultiRoleRef} definition.
     *
     * @param ref a {@link MultiRoleRef} instance
     * @exception Exception if an error occurs
     */
    public void visit( final MultiRoleRef ref )
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

        m_roles.addChild( role );
    }

    // XCONF GENERATION METHODS

    /**
     * Builds an xconf entry based on a {@link SingleRoleInstance} object.
     *
     * @param i an {@link SingleRoleInstance} instance
     * @exception Exception if an error occurs
     */
    public void visit( final SingleRoleInstance i )
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

        m_xconf.addChild( conf );
    }

    /**
     * Builds an xconf entry based on a {@link MultiRoleInstance} object.
     *
     * @param i a {@link MultiRoleInstance} instance
     * @exception Exception if an error occurs
     */
    public void visit( final MultiRoleInstance i )
        throws Exception
    {
        Instance[] subinstances = i.getSubInstances();

        for ( int j = 0; j < subinstances.length; ++j )
        {
            subinstances[j].accept( this );
        }
    }

    /**
     * Builds an xconf entry based on a {@link SingleNonRoleInstance} object.
     *
     * @param i a {@link SingleNonRoleInstance} instance.
     * @exception Exception if an error occurs
     */
    public void visit( final SingleNonRoleInstance i )
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

        m_xconf.addChild( conf );
    }

    /**
     * Builds an xconf entry based on a {@link MultiNonRoleInstance} object.
     *
     * @param i a {@link MultiNonRoleInstance} instance
     * @return a <code>Configuration[]</code> value
     */
    public void visit( final MultiNonRoleInstance i )
        throws Exception
    {
        SingleRoleInstance[] subs = i.getSubInstances();
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

            m_xconf.addChild( conf );
        }
    }
}
