

package org.apache.avalon.composition.model.test;

import java.io.File;

import org.apache.avalon.repository.Artifact;

import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.provider.SystemContext;
import org.apache.avalon.composition.provider.ModelFactory;

import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.main.DefaultInitialContext;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;

import org.apache.avalon.util.exception.ExceptionHelper;
import org.apache.avalon.util.env.Env;

import junit.framework.TestCase;

public abstract class AbstractTestCase extends TestCase
{
    public int PRIORITY = ConsoleLogger.LEVEL_INFO;

   //-------------------------------------------------------
   // state
   //-------------------------------------------------------

    public ContainmentModel m_model;

    private Logger m_logger = new ConsoleLogger( PRIORITY );

    private String m_path;

   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

   public AbstractTestCase( String path )
   {
       super( path );
       m_path = path;
   }

   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    protected Logger getLogger()
    {
        return m_logger;
    }

    protected File getTestDir()
    {
        return new File( System.getProperty( "basedir" ), "target" );
    }

    public void setUp() throws Exception
    {
        if( m_model == null )
        {
            File base = new File( getTestDir(), "test" );
            File root = new File( base, "repository" );

            File confDir = new File( base, "conf" );
            File source = new File( confDir, m_path );

            //
            // FIXME - need to read the current version for a 
            // properties file or something
            //

            InitialContext context = 
              new DefaultInitialContext( 
                getMavenRepositoryDirectory() );

            try
            {
                SystemContext system = 
                  SystemContextBuilder.createSystemContext( 
                    context, base, root, PRIORITY, true, 1000 );
                ModelFactory factory = system.getModelFactory();
                m_model = factory.createRootContainmentModel( source.toURL() );
            }
            catch( Throwable e )
            {
                final String error = ExceptionHelper.packException( e, true );
                System.err.println( error );
                fail( error );
            }
        }
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

}
