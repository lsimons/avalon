/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.activation.appliance;

import java.io.File;

import junit.framework.TestCase;

import org.apache.avalon.activation.appliance.impl.AbstractBlock;

import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;

import org.apache.avalon.logging.provider.LoggingManager;

import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.impl.DefaultSystemContext;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.activity.Disposable;

import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.main.DefaultInitialContext;

import org.apache.avalon.util.exception.ExceptionHelper;
import org.apache.avalon.util.env.Env;

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
        File local = new File( base, "repository" );
        InitialContext context = 
          new DefaultInitialContext( getMavenRepositoryDirectory() );
  
        m_system = 
          DefaultSystemContext.createSystemContext( 
            context, base, local, ConsoleLogger.LEVEL_INFO, m_secured, 3000 );
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


    private static File getMavenRepositoryDirectory()
    {
        return new File( getMavenHomeDirectory(), "repository" );
    }

    private static File getMavenHomeDirectory()
    {
        return new File( getMavenHome() );
    }

    private static String getMavenHome()
    {
        try
        {
            String local = 
              System.getProperty( 
                "maven.home.local", 
                Env.getEnvVariable( "MAVEN_HOME_LOCAL" ) );
            if( null != local ) return local;

            return System.getProperty( "user.home" ) + File.separator + ".maven";

        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to access environment.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

}
