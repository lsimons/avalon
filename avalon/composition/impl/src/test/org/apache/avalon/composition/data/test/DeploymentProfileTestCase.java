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
package org.apache.avalon.composition.data.test;

import junit.framework.TestCase;
import org.apache.avalon.meta.info.*;
import org.apache.avalon.composition.data.*;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.Version;

import java.util.Properties;

/**
 * ProfileTestCase does XYZ
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $ Revision: 1.1 $
 */
public class DeploymentProfileTestCase extends TestCase
{
    private Type m_type;
    private CategoriesDirective m_categories;
    private Mode m_mode;
    private boolean m_activation;
    private Parameters m_parameters;
    private Configuration m_configuration;
    private ContextDirective m_context;
    private DependencyDirective[] m_dependencies;
    private StageDirective[] m_stages;
    private String m_name;
    private String m_classname;
    private int m_collection;

    public DeploymentProfileTestCase( String name )
    {
        super( name );
    }

    public void setUp()
    {
        m_name = "Test";
        m_classname = DeploymentProfileTestCase.class.getName();
        m_context = 
          new ContextDirective( getClass().getName(), new ImportDirective[0] );
        m_configuration = new DefaultConfiguration("test");
        m_dependencies = new DependencyDirective[0];
        m_stages = new StageDirective[0];
        m_parameters = Parameters.fromProperties(System.getProperties());
        m_activation = true;
        m_mode = Mode.IMPLICIT;
        m_categories = new CategoriesDirective( new CategoryDirective[0] );
        m_collection = InfoDescriptor.DEMOCRAT;
    }

    public void testProfile() throws ContextException
    {
        DeploymentProfile profile = new DeploymentProfile(
          m_name, m_activation, m_collection, m_classname, m_categories, 
          m_context, m_dependencies, m_stages, m_parameters, 
          m_configuration, m_mode );

        assertEquals( "name", m_name, profile.getName() );
        assertEquals( "collection", m_collection, profile.getCollectionPolicy() );
        assertEquals( "classname", m_classname, profile.getClassname() );
        assertEquals( "categories", m_categories, profile.getCategories() );
        assertEquals( "mode", m_mode, profile.getMode() );
        assertEquals( "activation", m_activation, profile.getActivationPolicy() );
        assertEquals( "parameters", m_parameters, profile.getParameters() );
        assertEquals( "configuration", m_configuration, profile.getConfiguration() );
        assertEquals( "context", m_context, profile.getContext() );
    }
}