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
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.xfc.model.Definition;
import org.apache.excalibur.xfc.model.Model;
import org.apache.excalibur.xfc.model.instance.Instance;
import org.apache.excalibur.xfc.model.instance.InstanceVisitor;
import org.apache.excalibur.xfc.model.instance.MultiNonRoleInstance;
import org.apache.excalibur.xfc.model.instance.MultiRoleInstance;
import org.apache.excalibur.xfc.model.instance.SelectorHintInstance;
import org.apache.excalibur.xfc.model.instance.SingleNonRoleInstance;
import org.apache.excalibur.xfc.model.instance.SingleRoleInstance;
import org.apache.excalibur.xfc.model.role.MultiRoleRef;
import org.apache.excalibur.xfc.model.role.RoleRef;
import org.apache.excalibur.xfc.model.role.RoleRefVisitor;
import org.apache.excalibur.xfc.model.role.SingleRoleRef;
import org.apache.excalibur.xfc.modules.Constants;

/**
 * ECM module serialization class. This class contains the implementation
 * of the <code>serialize</code> method defined in {@link ECM}.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: ECMSerializer.java,v 1.4 2002/11/12 19:55:28 donaldp Exp $
 */
public class ECMSerializer extends AbstractLogEnabled
    implements RoleRefVisitor, InstanceVisitor, Constants
{
    // internals
    protected DefaultConfiguration m_roles = new DefaultConfiguration( ROLELIST, "" );
    protected DefaultConfiguration m_xconf = new DefaultConfiguration( "xconf", "" );

    protected final DefaultConfigurationSerializer m_serializer;

    /**
     * Constructor, initializes serializer.
     */
    public ECMSerializer()
    {
        // create our serializer and enable indentation
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
        buildRoles( model, context );
        buildXConf( model, context );
    }

    /**
     * Helper method to build the output roles file from the given
     * {@link Model} object.
     *
     * @param model a {@link Model} instance
     * @param context output context
     * @exception Exception if an error occurs
     */
    protected void buildRoles( final Model model, final String context )
        throws Exception
    {
        // create the role file
        RoleRef[] rolerefs = model.getDefinitions();

        // for each type object generate a roles file entry
        for( int i = 0; i < rolerefs.length; ++i )
        {
            rolerefs[ i ].accept( this );
        }

        m_serializer.serializeToFile( getRoleFile( context ), m_roles );
    }

    /**
     * Helper method to build the output xconf file from the given
     * {@link Model} object.
     *
     * @param model a {@link Model} instance
     * @param context output context
     * @exception Exception if an error occurs
     */
    protected void buildXConf( final Model model, final String context )
        throws Exception
    {
        // create the xconf file
        Instance[] instances = model.getInstances();

        // for each instance object generate an xconf file entry
        for( int i = 0; i < instances.length; ++i )
        {
            instances[ i ].accept( this );
        }

        m_serializer.serializeToFile( getConfigurationFile( context ), m_xconf );
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
    protected File getConfigurationFile( final String context )
    {
        int i = context.indexOf( CONTEXT_SEPARATOR );
        return new File( context.substring( i + 1 ) );
    }

    // ROLE GENERATION METHODS

    /**
     * Builds a single component Role definition from a {@link RoleRef}
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

        role.setAttribute( NAME, ref.getRole() );
        role.setAttribute( SHORTHAND, ref.getShorthand() );
        role.setAttribute( DEFAULT, def.getDefaultClass() );

        m_roles.addChild( role );
    }

    /**
     * Builds a multiple component Role definition (ie ComponentSelector based)
     * from a {@link RoleRef} definition.
     *
     * @param ref a {@link MultiRoleRef} instance
     * @exception Exception if an error occurs
     */
    public void visit( final MultiRoleRef ref )
        throws Exception
    {
        DefaultConfiguration role = new DefaultConfiguration( ROLE, "" );
        Definition[] defs = ref.getProviders();

        for( int i = 0; i < defs.length; ++i )
        {
            DefaultConfiguration hint = new DefaultConfiguration( HINT, "" );
            hint.setAttribute( SHORTHAND, defs[ i ].getShorthand() );
            hint.setAttribute( CLASS, defs[ i ].getDefaultClass() );
            role.addChild( hint );
        }

        role.setAttribute( NAME, ref.getRole() );
        role.setAttribute( SHORTHAND, ref.getShorthand() );
        role.setAttribute( DEFAULT, ECS );

        m_roles.addChild( role );
    }

    /**
     * Builds a multiple component role definition from a
     * {@link RoleRef} definition. (Note, this method is unused).
     *
     * @param ref a {@link RoleRef} instance
     * @exception Exception if an error occurs
     */
    public void visit( final RoleRef ref )
        throws Exception
    {
        throw new UnsupportedOperationException( "This method shouldn't be invoked" );
    }

    // XCONF GENERATION METHODS

    /**
     * Builds an xconf entry based on a {@link SingleRoleInstance} declaration,
     *
     * @param i a {@link SingleRoleInstance} instance
     * @exception Exception if an error occurs
     */
    public void visit( final SingleRoleInstance i )
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration( i.getShorthand(), "" );

        if( i.getConfiguration() != null )
        {
            Configuration[] kids = i.getConfiguration();

            for( int j = 0; j < kids.length; ++j )
            {
                conf.addChild( kids[ j ] );
            }
        }

        if( i.getClassImpl() != null )
        {
            conf.setAttribute( CLASS, i.getClassImpl() );
        }

        m_xconf.addChild( conf );
    }

    /**
     * Builds an xconf entry based on a {@link SingleNonRoleInstance}
     * declaration
     *
     * @param i an {@link SingleNonRoleInstance} instance
     * @exception Exception if an error occurs
     */
    public void visit( final SingleNonRoleInstance i )
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration( COMPONENT, "" );

        conf.setAttribute( ROLE, i.getRole() );
        conf.setAttribute( CLASS, i.getClassImpl() );

        if( i.getConfiguration() != null )
        {
            Configuration[] kids = i.getConfiguration();

            for( int j = 0; j < kids.length; ++j )
            {
                conf.addChild( kids[ j ] );
            }
        }

        m_xconf.addChild( conf );
    }

    /**
     * Builds an xconf entry based on a {@link MultiNonRoleInstance}
     * declaration
     *
     * @param i a {@link MultiNonRoleInstance} instance
     * @exception Exception if an error occurs
     */
    public void visit( final MultiNonRoleInstance i )
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration( COMPONENT, "" );

        conf.setAttribute( ROLE, i.getRole() );
        conf.setAttribute( CLASS, ECS );

        SingleRoleInstance[] subs = i.getSubInstances();

        for( int j = 0; j < subs.length; ++j )
        {
            DefaultConfiguration child =
                new DefaultConfiguration( COMPONENT_INSTANCE, "" );
            child.setAttribute( CLASS, subs[ j ].getClassImpl() );
            child.setAttribute( NAME, subs[ j ].getShorthand() );

            if( subs[ j ].getConfiguration() != null )
            {
                Configuration[] kids = subs[ j ].getConfiguration();

                for( int k = 0; k < kids.length; ++k )
                {
                    child.addChild( kids[ k ] );
                }
            }

            conf.addChild( child );
        }

        m_xconf.addChild( conf );
    }

    /**
     * Builds an xconf entry based on a {@link MultiRoleInstance} declaration.
     *
     * @param i a {@link MultiRoleInstance} instancex
     * @exception Exception if an error occurs
     */
    public void visit( final MultiRoleInstance i )
        throws Exception
    {
        DefaultConfiguration conf = new DefaultConfiguration( i.getShorthand(), "" );

        SelectorHintInstance[] subs = i.getSubInstances();

        for( int j = 0; j < subs.length; ++j )
        {
            DefaultConfiguration child =
                new DefaultConfiguration( subs[ j ].getShorthand(), "" );
            child.setAttribute( NAME, subs[ j ].getHint() );

            if( subs[ j ].getConfiguration() != null )
            {
                Configuration[] kids = subs[ j ].getConfiguration();

                for( int k = 0; k < kids.length; ++k )
                {
                    child.addChild( kids[ k ] );
                }
            }

            conf.addChild( child );
        }

        m_xconf.addChild( conf );
    }

    /**
     * Builds an xconf entry baesd on an {@link Instance} declaration.
     * (method not actually used).
     *
     * @param i an {@link Instance} instance
     * @exception Exception if an error occurs
     */
    public void visit( final Instance i )
        throws Exception
    {
        throw new UnsupportedOperationException( "This method shouldn't be invoked" );
    }
}
