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
package org.apache.excalibur.xfc.test;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.NullLogger;

//import org.apache.excalibur.configuration.ConfigurationUtil;

import org.apache.excalibur.xfc.modules.ECM;
import org.apache.excalibur.xfc.model.Definition;
import org.apache.excalibur.xfc.model.Model;
import org.apache.excalibur.xfc.model.RoleRef;
import org.apache.excalibur.xfc.Main;
import org.apache.excalibur.xfc.Module;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * XFC TestCase
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: xfcTestCase.java,v 1.1 2002/10/07 14:31:20 crafterm Exp $
 */
public final class xfcTestCase extends TestCase
{
    private static final String ECM_ROLES =
        "../testclasses/org/apache/excalibur/xfc/test/ecm.roles";
    private static final String ECM_XCONF = "ecm.xconf";

    private DefaultConfigurationBuilder m_builder = new DefaultConfigurationBuilder();

    public xfcTestCase()
    {
        super( "xfcTestCase" );
    }

    public xfcTestCase( String name )
    {
        super( name );
    }

    /**
     * Method to test Context validation on {@link Module} objects.
     *
     * @exception Exception if an error occurs
     */
    public void testXFC_Module_ContextValidation()
        throws Exception
    {
        try
        {
            Module ecm = getModule( ECM.class );
            Model model = ecm.generate( "just-roles-no-xconf" );

            fail( "Context validation failed" );
        }
        catch ( IllegalArgumentException e )
        {
            // should end up in here if all is ok.
        }
    }

    /**
     * Method to test the ECM XFC module.
     *
     * @exception Exception if an error occurs
     */
    public void testXFC_ECM()
        throws Exception
    {
        Module ecm = getModule( ECM.class );
        Model model = ecm.generate( ECM_ROLES + ":" + ECM_XCONF );
        Configuration[] real_roles =
            m_builder.buildFromFile( ECM_ROLES ).getChildren( "role" );
        RoleRef[] model_roles = model.getDefinitions();

        // check that the generated model has the right number of roles
        assertEquals(
            "Model contains incorrect number of roles",
            real_roles.length,
            model_roles.length
        );

        // check each role has the right values
        for ( int i = 0; i < model_roles.length; ++i )
        {
            RoleRef r = model_roles[i];
            String roleName = r.getRole();
            Configuration realRole = null;

            // get the real role configuration object
            for ( int j = 0; j < real_roles.length; ++j )
            {
                if ( roleName.equals( real_roles[j].getAttribute( "name" ) ) )
                {
                    realRole = real_roles[j];
                    break;
                }
            }

            assertNotNull( "Configuration for role " + roleName + " not found", realRole );

            Definition[] providers = model_roles[i].getProviders();

            if ( providers.length > 1 )
            {
                // component selector
                // REVISIT.
            }
            else
            {
                // single component

                // check that the role name from the model is correct
                assertEquals(
                    "Role names not equal",
                    model_roles[i].getRole(),
                    realRole.getAttribute( "name" )
                );

                // check that the shorthand name from the model is correct
                assertEquals(
                    "Shorthand names not equal",
                    providers[0].getShorthand(),
                    realRole.getAttribute( "shorthand" )
                );

                // check that the default-class name from the model is correct
                assertEquals(
                    "Default-class names not equal",
                    providers[0].getDefaultClass(),
                    realRole.getAttribute( "default-class" )
                );
            }
        }
    }

    public void testXFC_Fortress()
    {
        // REVISIT
    }

    private Module getModule( final Class clazz )
        throws Exception
    {
        Module module = (Module) clazz.newInstance();
        module.enableLogging( new NullLogger() /*ConsoleLogger()*/ );
        return module;
    }

    public static final void main( String[] args )
    {
        TestRunner.run( xfcTestCase.class );
    }
}


