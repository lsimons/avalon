

package org.apache.avalon.activation.appliance;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.avalon.activation.appliance.ServiceContext;
import org.apache.avalon.activation.appliance.impl.DefaultServiceContext;
import org.apache.avalon.composition.data.CategoryDirective;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.ClassLoaderDirective;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.logging.LoggerException;
import org.apache.avalon.composition.logging.LoggingDescriptor;
import org.apache.avalon.composition.logging.TargetDescriptor;
import org.apache.avalon.composition.logging.TargetProvider;
import org.apache.avalon.composition.logging.impl.DefaultLoggingManager;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.impl.DefaultSystemContext;
import org.apache.avalon.composition.model.impl.DefaultModelFactory;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.excalibur.event.command.CommandManager;
import org.apache.excalibur.event.command.TPCThreadManager;
import org.apache.excalibur.mpool.DefaultPoolManager;
import org.apache.excalibur.mpool.PoolManager;

import junit.framework.TestCase;

public abstract class AbstractTestCase extends TestCase
{
   //-------------------------------------------------------
   // state
   //-------------------------------------------------------

    protected Logger m_logger;

    protected ServiceContext m_context;

    protected ContainmentModel m_model;

    protected SystemContext m_system;

   //-------------------------------------------------------
   // constructor
   //-------------------------------------------------------

    public AbstractTestCase( )
    {
        this( "data" );
    }

    public AbstractTestCase( String name )
    {
        super( name );
    }

   //-------------------------------------------------------
   // setup
   //-------------------------------------------------------

    public abstract String getPath();

   /**
    * The setup process covers the establishment of the base
    * directory (from which relative references for extension directories
    * and fileset base directories are resolved), a file repository (not
    * used in this test case at this time), and a class loader model from 
    * which a classpath will be established.
    *
    * @exception Exception if things don't work out
    */
    public void setUp() throws Exception
    {
        File base = new File( getTestDir(), "test-classes" );

        //
        // WARNING: ALMOST EVIL
        // Next couple of lines are using a convinience operation
        // on DefaultSystemContext to create the system context.  This 
        // is temporary and will be replaced when a clean configurable 
        // system context factory is in place
        //

        File local = new File( base, "repository" );
        SystemContext system = 
              DefaultSystemContext.createSystemContext( 
                base, local, ConsoleLogger.LEVEL_ERROR );
        m_logger = system.getLogger();

        //
        // load the meta data using the profile returned from getPath()
        // and establish a containment model for the unit test
        //

        ContainmentProfile profile = setUpProfile( new File( base, getPath() ) );
        m_model = system.getFactory().createContainmentModel( profile );

        // 
        // create the service context now even thought
        // its not needed until we start playing with appliances
        // and blocks
        //

        PoolManager pool = createPoolManager();
        DefaultServiceContext context = new DefaultServiceContext();
        context.put( PoolManager.ROLE, pool );
        context.put( LoggingManager.KEY, system.getLoggingManager() );
        m_context = context;

    }

    protected ContainmentProfile setUpProfile( File file )
      throws Exception
    {
        XMLContainmentProfileCreator creator = new XMLContainmentProfileCreator();
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config = builder.buildFromFile( file );
        return creator.createContainmentProfile( config );
    }

    protected static File getTestDir()
    {
        return new File( System.getProperty( "basedir" ), "target" );
    }

    private PoolManager createPoolManager() throws Exception
    {
        try
        {
            //
            // Set up the ThreadManager that the CommandManager uses
            //

            TPCThreadManager threadManager = new TPCThreadManager();
            threadManager.enableLogging( getLogger().getChildLogger( "threads" ) );
            Parameters params = new Parameters();
            params.setParameter( "threads-per-processor", "2" );
            params.setParameter( "sleep-time", "1000" );
            params.setParameter( "block-timeout", "250" );
            threadManager.parameterize( params );
            threadManager.initialize();

            //
            // Set up the CommandManager that the PoolManager uses.
            //

            CommandManager commandManager = new CommandManager();
            threadManager.register( commandManager );

            //
            // Set up the PoolManager that the pooled lifecycle helper needs
            //

            DefaultPoolManager poolManager =
                    new DefaultPoolManager( commandManager.getCommandSink() );
            return poolManager;
        } 
        catch( Throwable e )
        {
            final String error =
                    "Internal error during establishment of the default pool manager. Cause: ";
            throw new Exception( error + e.toString() );
        }
    }

    protected Logger getLogger()
    {
        return m_logger;
    }

}
