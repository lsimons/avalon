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


package org.apache.avalon.composition.model.test;

import java.io.File;


import org.apache.avalon.composition.data.SecurityProfile;
import org.apache.avalon.composition.data.builder.XMLSecurityProfileBuilder;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.impl.DefaultSystemContextFactory;
import org.apache.avalon.composition.provider.SystemContext;
import org.apache.avalon.composition.provider.ModelFactory;
import org.apache.avalon.composition.provider.SystemContextFactory;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.InitialContextFactory;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.repository.provider.RepositoryCriteria;
import org.apache.avalon.repository.main.DefaultInitialContextFactory;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.avalon.util.exception.ExceptionHelper;
import org.apache.avalon.util.env.Env;

import org.apache.excalibur.configuration.ConfigurationUtil;

import junit.framework.TestCase;

public abstract class AbstractTestCase extends TestCase
{
    //-------------------------------------------------------
    // state
    //-------------------------------------------------------

    public static int PRIORITY = ConsoleLogger.LEVEL_INFO;

    private static Logger LOGGER = new ConsoleLogger( PRIORITY );

    public static final File BASEDIR = 
      new File( System.getProperty( "basedir" ) );

    public static final File SYS_CONF = 
      new File( BASEDIR, "src/test/conf/system/kernel.xml" );

    private static final XMLSecurityProfileBuilder SECURITY_BUILDER = 
      new XMLSecurityProfileBuilder();

    //-------------------------------------------------------
    // state
    //-------------------------------------------------------

    public ContainmentModel m_model;

   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

   /**
    * Setup the system context and create m_model using a path
    * relative to the ${basedir}/target/test/conf directory.
    */
    public ContainmentModel setUp( String path ) throws Exception
    {
        //
        // grab the platform configuration
        //

        Configuration config = getConfiguration( SYS_CONF );

        //
        // create the initial context using the maven repository as the 
        // system repository
        //

        File test = new File( getTargetDir(), "test" );
        Configuration sysConfig = config.getChild( "system" );
        InitialContext context = setUpInitialContext( test, sysConfig );

        //
        // create a system context and add a test repository to use during 
        // testcase execution
        //

        SystemContextFactory factory = 
          new DefaultSystemContextFactory( context );

        Configuration secConfig = config.getChild( "security" );
        SecurityProfile[] profiles = 
          SECURITY_BUILDER.createSecurityProfiles( secConfig );
        factory.setSecurityProfiles( profiles );
        factory.setSecurityEnabled( true );

        Repository repository = 
          createTestRepository( context, new File( test, "repository" ) );
        factory.setRepository( repository );

        SystemContext system = factory.createSystemContext();

        //
        // create a containment model using the supplied path
        //

        ModelFactory modelFactory = system.getModelFactory();
        File confDir = new File( test, "conf" );
        File source = new File( confDir, path );

        return modelFactory.createRootContainmentModel( source.toURL() );
    }

    private Repository createTestRepository( InitialContext context, File cache ) 
      throws Exception
    {
        Factory factory = context.getInitialFactory();
        RepositoryCriteria criteria = 
          (RepositoryCriteria) factory.createDefaultCriteria();
        criteria.setCacheDirectory( cache );
        criteria.setHosts( new String[0] );
        return (Repository) factory.create( criteria );
    }

    InitialContext setUpInitialContext( File base, Configuration config ) 
      throws Exception
    {
        InitialContextFactory initial = 
          new DefaultInitialContextFactory( "test", base );
        initial.setCacheDirectory( getMavenRepositoryDirectory() );
        registerSystemArtifacts( initial, config );
        return initial.createInitialContext();
    }

    private void registerSystemArtifacts( InitialContextFactory factory, Configuration config )
      throws Exception
    {
        Artifact[] artifacts = getArtifactsToRegister( config );
        for( int i=0; i<artifacts.length; i++ )
        {
            Artifact artifact = artifacts[i];
            factory.addFactoryArtifact( artifact );
        }
    }

    private static Artifact[] getArtifactsToRegister( Configuration config ) throws Exception
    {
        Configuration[] children = 
          config.getChildren( "artifact" );
        Artifact[] artifacts = new Artifact[ children.length ];
        for( int i=0; i<children.length; i++ )
        {
            Configuration child = children[i];
            String spec = child.getAttribute( "spec" );
            Artifact artifact = Artifact.createArtifact( "artifact:" + spec );
            artifacts[i] = artifact;
        }
        return artifacts;
    }

    public void printModel( String lead, DeploymentModel model )
    {
        if( model instanceof ContainmentModel )
        {
            printContainmentModel( lead, (ContainmentModel) model );
        }
        else if( model instanceof ComponentModel ) 
        {
            printComponentModel( lead, (ComponentModel) model );
        }
    }

    public void printContainmentModel( String lead, ContainmentModel model )
    {
        printDeploymentModel( lead, model );
        DeploymentModel[] models = model.getModels();
        if( models.length > 0 )
        {
            System.out.println( lead + "  children:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                printModel( "    " + lead, m );
            }
        }
        models = model.getStartupGraph();
        if( models.length > 0 )
        {
            System.out.println( lead + "  startup:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                System.out.println( "    " + lead + (i+1) + ": " + m );
            }
        }
        models = ((ContainmentModel)model).getShutdownGraph();
        if( models.length > 0 )
        {
            System.out.println( lead + "  shutdown:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                System.out.println( "    " + lead + (i+1) + ": " + m );
            }
        }
    }

    public void printComponentModel( String lead, ComponentModel model )
    {
        printDeploymentModel( lead, model );
    }

    public void printDeploymentModel( String lead, DeploymentModel model )
    {
        System.out.println( 
          lead 
          + "model:" 
          + model + "(" 
          + model.getDeploymentTimeout() 
          + ")" );

        DeploymentModel[] providers = model.getProviderGraph();
        DeploymentModel[] consumers = model.getConsumerGraph();

        if(( providers.length == 0 ) && ( consumers.length == 0 ))
        {
            return;
        }

        if( providers.length > 0 ) for( int i=0; i<providers.length; i++ )
        {
            DeploymentModel m = providers[i];
            System.out.println( lead + "  <-- " + m );
        }

        if( consumers.length > 0 ) for( int i=0; i<consumers.length; i++ )
        {
            DeploymentModel m = consumers[i];
            System.out.println( lead + "  --> " + m );
        }
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

    private static Configuration getConfiguration( File file ) throws Exception
    {
        DefaultConfigurationBuilder builder = 
          new DefaultConfigurationBuilder();
        return builder.buildFromFile( file );  
    }

    protected Logger getLogger()
    {
        return LOGGER;
    }

    protected File getBaseDir()
    {
        return BASEDIR;
    }

    protected File getTargetDir()
    {
        return new File( getBaseDir(), "target" );
    }
}
