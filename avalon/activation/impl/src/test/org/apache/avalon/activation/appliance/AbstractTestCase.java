/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.activation.appliance;

import java.io.File;

import junit.framework.TestCase;

import org.apache.avalon.activation.appliance.impl.AbstractBlock;

import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.impl.DefaultSystemContext;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.activity.Disposable;

import org.apache.avalon.util.exception.ExceptionHelper;




public abstract class AbstractTestCase extends TestCase
{
   //-------------------------------------------------------
   // state
   //-------------------------------------------------------

    protected Logger m_logger;

    protected ContainmentModel m_model;

    protected SystemContext m_system;
    
    protected boolean m_secured;

   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public AbstractTestCase( )
    {
        this( "data", false );
    }

    public AbstractTestCase( String name )
    {
        this( name, false );
    }
    
    public AbstractTestCase( String name, boolean secured )
    {
        super( name );
        m_secured = secured;
    }

   //-------------------------------------------------------
   // setup
   //-------------------------------------------------------

    //public abstract String getPath();

   /**
    * The setup process covers the establishment of the base
    * directory (from which relative references for extension directories
    * and fileset base directories are resolved), a file repository (not
    * used in this test case at this time), and a class loader model from 
    * which a classpath will be established.
    *
    * @exception Exception if things don't work out
    */
    public void setUp( String filename ) throws Exception
    {
        File base = new File( getTargetDirectory(), "test-classes" );
        File conf = new File( getBaseDirectory(), "src/test/conf" );
        File block = new File( conf, filename );
        setUp( base, block );
    }

   /**
    * The setup process covers the establishment of the base
    * directory (from which relative references for extension directories
    * and fileset base directories are resolved), a file repository (not
    * used in this test case at this time), and a class loader model from 
    * which a classpath will be established.
    *
    * @exception Exception if things don't work out
    */
    public void setUp( File base, File block ) throws Exception
    {
        //
        // Next couple of lines are using a convinience operation
        // on DefaultSystemContext to create the system context.  This 
        // is temporary and will be replaced when a clean configurable 
        // system context factory is in place
        //

        File local = new File( base, "repository" );
        m_system = 
          DefaultSystemContext.createSystemContext( 
            base, local, ConsoleLogger.LEVEL_INFO, m_secured );
        m_logger = m_system.getLogger();

        //
        // load the meta data using the profile returned from getPath()
        // and establish a containment model for the unit test
        //

        ContainmentProfile profile = setUpProfile( block );
        m_model = m_system.getFactory().createContainmentModel( profile );
    }

    protected ContainmentProfile setUpProfile( File file )
      throws Exception
    {
        XMLContainmentProfileCreator creator = new XMLContainmentProfileCreator();
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config = builder.buildFromFile( file );
        return creator.createContainmentProfile( config );
    }

    protected static File getBaseDirectory()
    {
        return new File( System.getProperty( "basedir" ) );
    }

    protected static File getTargetDirectory()
    {
        return new File( getBaseDirectory(), "target" );
    }

    protected Logger getLogger()
    {
        return m_logger;
    }

   //-------------------------------------------------------
   // tests
   //-------------------------------------------------------

   /**
    * Validate the composition model.
    */
    public void executeDeploymentCycle() throws Exception
    {

        //
        // 1. assemble the model during which all dependencies
        //    are resolved (deployment and runtime)
        //

        getLogger().debug( "model assembly" );
        m_model.assemble();

        //
        // 2. create the root block using the service context
        //    and the root containment model
        //

        getLogger().debug( "creating root block" );
        Block block = AbstractBlock.createRootBlock( m_model );
        getLogger().debug( "block: " + block );

        //
        // 3. deploy the block during which any 'activate on startup'
        //    components are created which in turn my cause activation
        //    of lazy components
        //

        block.deploy();

        //
        // 4-5. suspend and resume the root block (not implemented yet)
        //
        // 6. decommission the block during which all managed appliances
        //    are decommissioned resulting in the decommissioning of all
        //    appliance instances
        //

        block.decommission();

        //
        // 7. disassemble the block during which reference between 
        //    appliances established at assembly time are discarded
        //

        block.getContainmentModel().disassemble();

        //
        // 8. dispose of the appliance during which all subsidiary 
        //    appliances are disposed of in an orderly fashion
        //

        if( block instanceof Disposable )
        {
            ((Disposable)block).dispose();
        }

        assertTrue( true );
    }

}
