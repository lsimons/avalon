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
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;

import org.apache.excalibur.configuration.ConfigurationUtil;

import org.apache.excalibur.xfc.modules.ECM;
import org.apache.excalibur.xfc.model.Definition;
import org.apache.excalibur.xfc.model.Model;
import org.apache.excalibur.xfc.model.RoleRef;
import org.apache.excalibur.xfc.Main;
import org.apache.excalibur.xfc.Module;

import org.apache.excalibur.xfc.test.util.ECMTestRig;
import org.apache.excalibur.xfc.test.util.FortressTestRig;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * XFC TestCase.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Id: xfcTestCase.java,v 1.5 2002/10/08 15:02:02 crafterm Exp $
 */
public final class xfcTestCase extends TestCase
{
    // location of ECM roles/xconf configuration data
    private static final String ECM_ROLES =
        "../testclasses/org/apache/excalibur/xfc/test/ecm.roles";
    private static final String ECM_XCONF = "ecm.xconf";

    // location of Fortress roles/xconf configuration data
    private static final String FORTRESS_ROLES =
        "../testclasses/org/apache/excalibur/xfc/test/fortress.roles";
    private static final String FORTRESS_XCONF = "fortress.xconf";

    // misc internals
    private DefaultConfigurationBuilder m_builder = new DefaultConfigurationBuilder();
    private Logger m_logger;

    public xfcTestCase()
    {
        super( "xfcTestCase" );
    }

    public xfcTestCase( String name )
    {
        super( name );

        m_logger = new ConsoleLogger( ConsoleLogger.LEVEL_WARN );
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
            Module ecm = new ECM();
            ecm.enableLogging( m_logger );
            Model model = ecm.generate( "just-roles-no-xconf" );

            fail( "Context validation failed" );
        }
        catch ( IllegalArgumentException e )
        {
            // should end up in here if all is ok.
        }
    }

    /**
     * Method to test the XFC ECM module, generation stage.
     *
     * <p>
     *  This test case compares a generated {@link Model} instance with
     *  the configuration file that was used to create it. If any differences
     *  are found, assertions should occur.
     * </p>
     *
     * @exception Exception if an error occurs
     */
    public void testXFC_ECM_generate()
        throws Exception
    {
        // create an ECM module test rig instance
        ECMTestRig ecm = new ECMTestRig();
        ecm.enableLogging( m_logger );

        // generate model from predefined configuration
        Model model = ecm.generate( ECM_ROLES + ":" + ECM_XCONF );

        // load the same config and manually verify that model is correct
        Configuration[] rolesREAL =
            m_builder.buildFromFile( ECM_ROLES ).getChildren( "role" );
        RoleRef[] rolesMODEL = model.getDefinitions();

        // check that the generated model has the right number of roles
        assertEquals(
            "Model contains incorrect number of roles",
            rolesREAL.length, rolesMODEL.length
        );

        // check each role has the right values, compared against the master copy
        for ( int i = 0; i < rolesMODEL.length; ++i )
        {
            String modelRoleName = rolesMODEL[i].getRole();
            Configuration masterRoleConfig = null;

            // get the real role configuration object
            for ( int j = 0; j < rolesREAL.length; ++j )
            {
                if ( modelRoleName.equals( rolesREAL[j].getAttribute( "name" ) ) )
                {
                    masterRoleConfig = rolesREAL[j];
                    break;
                }
            }

            // check that we found a Configuration fragment for the role in the model
            assertNotNull(
                "Master Configuration for role '" + modelRoleName + "' not found",
                masterRoleConfig
            );

            // convert our RoleRef object into a Configuration and compare with the master
            Configuration modelRoleConfig = ecm.buildRole( rolesMODEL[i] );

            assertTrue(
                "Role configuration trees differ\n" +
                "(master)" + ConfigurationUtil.list( masterRoleConfig ) +
                "(model)" + ConfigurationUtil.list( modelRoleConfig ),
                ConfigurationUtil.equals( masterRoleConfig, modelRoleConfig )
            );
        }

        // all done, good show
    }

    /**
     * Method to test the XFC ECM module, serialization stage.
     *
     * <p>
     *  This test case serializes a given {@link Model} instance to a
     *  temporary file, and the resultant file is compared against the
     *  original. If any differences are found, assertions should occur.
     * </p>
     *
     * @exception Exception if an error occurs
     */
    public void testXFC_ECM_serialize()
        throws Exception
    {
        String ECM_ROLES_GENERATED = "ecm-generated.roles";

        // create an ECM module test rig instance
        ECMTestRig ecm = new ECMTestRig();
        ecm.enableLogging( m_logger );

        // generate model from predefined configuration
        Model model = ecm.generate( ECM_ROLES + ":" + ECM_XCONF );

        // serialize the model out to a temporary file
        ecm.serialize( model, ECM_ROLES_GENERATED + ":" + ECM_XCONF );

        // compare original with generated copy, they should be equal
        Configuration master = m_builder.buildFromFile( ECM_ROLES );
        Configuration generated = m_builder.buildFromFile( ECM_ROLES_GENERATED );

        assertTrue(
            "Generated roles file differs from master " +
            "master: " + ECM_ROLES + ", generated: " + ECM_ROLES_GENERATED,
            ConfigurationUtil.equals( master, generated )
        );

        // all done, good show
    }

    /**
     * Method to test the XFC Fortress module, generation stage.
     *
     * <p>
     *  This test case compares a generated {@link Model} instance with
     *  the configuration file that was used to create it. If any differences
     *  are found, assertions should occur.
     * </p>
     *
     * @exception Exception if an error occurs
     */
    public void testXFC_Fortress_generate()
        throws Exception
    {
        // create a Fortress module test rig instance
        FortressTestRig ecm = new FortressTestRig();
        ecm.enableLogging( m_logger );

        // generate model from predefined configuration
        Model model = ecm.generate( FORTRESS_ROLES + ":" + FORTRESS_XCONF );

        // load the same config and manually verify that model is correct
        Configuration[] rolesREAL =
            m_builder.buildFromFile( FORTRESS_ROLES ).getChildren( "role" );
        RoleRef[] rolesMODEL = model.getDefinitions();

        // check that the generated model has the right number of roles
        assertEquals(
            "Model contains incorrect number of roles",
            rolesREAL.length, rolesMODEL.length
        );

        // check each role has the right values, compared against the master copy
        for ( int i = 0; i < rolesMODEL.length; ++i )
        {
            String modelRoleName = rolesMODEL[i].getRole();
            Configuration masterRoleConfig = null;

            // get the real role configuration object
            for ( int j = 0; j < rolesREAL.length; ++j )
            {
                if ( modelRoleName.equals( rolesREAL[j].getAttribute( "name" ) ) )
                {
                    masterRoleConfig = rolesREAL[j];
                    break;
                }
            }

            // check that we found a Configuration fragment for the role in the model
            assertNotNull(
                "Master Configuration for role '" + modelRoleName + "' not found",
                masterRoleConfig
            );

            // convert our RoleRef object into a Configuration and compare with the master
            Configuration modelRoleConfig = ecm.buildRole( rolesMODEL[i] );

            assertTrue(
                "Role configuration trees differ\n" +
                "(master)" + ConfigurationUtil.list( masterRoleConfig ) +
                "(model)" + ConfigurationUtil.list( modelRoleConfig ),
                ConfigurationUtil.equals( masterRoleConfig, modelRoleConfig )
            );
        }

        // all done, good show
    }

    /**
     * Method to test the XFC Fortress module, serialization stage.
     *
     * <p>
     *  This test case serializes a given {@link Model} instance to a
     *  temporary file, and the resultant file is compared against the
     *  original. If any differences are found, assertions should occur.
     * </p>
     *
     * @exception Exception if an error occurs
     */
    public void testXFC_Fortress_serialize()
        throws Exception
    {
        String FORTRESS_ROLES_GENERATED = "fortress-generated.roles";

        // create a Fortress module test rig instance
        FortressTestRig ecm = new FortressTestRig();
        ecm.enableLogging( m_logger );

        // generate model from predefined configuration
        Model model = ecm.generate( FORTRESS_ROLES + ":" + FORTRESS_XCONF );

        // serialize the model out to a temporary file
        ecm.serialize( model, FORTRESS_ROLES_GENERATED + ":" + FORTRESS_XCONF );

        // compare original with generated copy, they should be equal
        Configuration master = m_builder.buildFromFile( FORTRESS_ROLES );
        Configuration generated = m_builder.buildFromFile( FORTRESS_ROLES_GENERATED );

        assertTrue(
            "Generated roles file differs from master " +
            "master: " + FORTRESS_ROLES + ", generated: " + FORTRESS_ROLES_GENERATED,
            ConfigurationUtil.equals( master, generated )
        );

        // all done, good show
    }

    /**
     * Method to test class handler analysis on the ECM module. ECM roles
     * do not specify component handlers like Fortress roles do, so the ECM
     * module examines the given class to find out what handler it should use.
     *
     * @exception Exception if an error occurs
     */
    public void testXFC_ECM_ClassHandlerAnalysis()
        throws Exception
    {
        // create an ECM module instance
        ECMTestRig ecm = new ECMTestRig();
        ecm.enableLogging( m_logger );

        // input classes to analyse
        final String[] classes =
            {
                "org.apache.avalon.excalibur.xml.JaxpParser",
                "org.apache.avalon.excalibur.xml.xslt.XSLTProcessorImpl",
                "org.apache.avalon.excalibur.xml.xpath.XPathProcessorImpl",
            };

        // actual handlers these classes should use
        final String[] handlers =
            {
                "pooled", // org.apache.excalibur.fortress.handler.PoolableComponentHandler
                "transient", // org.apache.excalibur.fortress.handler.FactoryComponentHandler
                "singleton", // org.apache.excalibur.fortress.handler.ThreadSafeComponentHandler
            };

        for ( int i = 0; i < classes.length; ++i )
        {
            String result = ecm.getHandler( classes[i] );
            assertTrue(
                "Class handler analysis failed for :" + classes[i] +
                ", expected was '" + handlers[i] + "', received was '" + result + "'",
                handlers[i].equals( result )
            );
        }
    }

    /**
     * Method to test the conversion of an ECM style configuration to a
     * equivalent Fortress style one.
     *
     * @exception Exception if an error occurs
     */
    public void testXFC_ECM2Fortress()
        throws Exception
    {
        // create an ECM module instance
        ECMTestRig ecm = new ECMTestRig();
        ecm.enableLogging( m_logger );

        // create a Fortress module instance
        FortressTestRig fortress = new FortressTestRig();
        fortress.enableLogging( m_logger );

        // generate model from predefined ECM configuration
        Model model = ecm.generate( ECM_ROLES + ":" + ECM_XCONF );

        // serialize the model out to a Fortress temporary file
        //fortress.serialize( model, FORTRESS_ROLES_GENERATED + ":" + FORTRESS_XCONF );

        // load the same config and manually verify that model is correct
        Configuration[] rolesREAL =
            m_builder.buildFromFile( FORTRESS_ROLES ).getChildren( "role" );
        RoleRef[] rolesMODEL = model.getDefinitions();

        // check that the generated model has the right number of roles
        assertEquals(
            "Model contains incorrect number of roles",
            rolesREAL.length, rolesMODEL.length
        );

        // check each role has the right values, compared against the master copy
        for ( int i = 0; i < rolesMODEL.length; ++i )
        {
            String modelRoleName = rolesMODEL[i].getRole();
            Configuration masterRoleConfig = null;

            // get the real role configuration object
            for ( int j = 0; j < rolesREAL.length; ++j )
            {
                if ( modelRoleName.equals( rolesREAL[j].getAttribute( "name" ) ) )
                {
                    masterRoleConfig = rolesREAL[j];
                    break;
                }
            }

            // check that we found a Configuration fragment for the role in the model
            assertNotNull(
                "Master Configuration for role '" + modelRoleName + "' not found",
                masterRoleConfig
            );

            // convert our RoleRef object into a Configuration and compare with the master
            Configuration modelRoleConfig = fortress.buildRole( rolesMODEL[i] );

            assertTrue(
                "Role configuration trees differ\n" +
                "(master)" + ConfigurationUtil.list( masterRoleConfig ) +
                "(model)" + ConfigurationUtil.list( modelRoleConfig ),
                ConfigurationUtil.equals( masterRoleConfig, modelRoleConfig )
            );
        }

        // all done, good show
    }

    public static final void main( String[] args )
    {
        TestRunner.run( xfcTestCase.class );
    }
}
