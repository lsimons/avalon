

package org.apache.avalon.merlin.kernel.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.net.MalformedURLException;
import java.net.URL;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.merlin.kernel.Kernel;
import org.apache.avalon.merlin.kernel.KernelContext;
import org.apache.avalon.merlin.kernel.KernelException;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.repository.BlockManifest;
import org.apache.avalon.repository.Repository;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.xml.sax.SAXException;

/**
 * The CLIKernelLoader loads a Merlin Kernel based on a set of 
 * command line arguments.
 */
public class CLIKernelLoader
{
    //--------------------------------------------------------------------------
    // static
    //--------------------------------------------------------------------------

    private static final String PRODUCT = "Merlin SMP";

    private static final String VERSION = "3.0";

    private static final File HOME = new File( System.getProperty( "user.dir" ) );

    private static Resources REZ =
        ResourceManager.getPackageResources( CLIKernelLoader.class );

    private static Options CL_OPTIONS = buildCommandLineOptions();

    //--------------------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------------------

   /**
    * Creation of a new kernel loader.  The supplied repository is 
    * used as the default deployment repository.  Depending on command
    * line arguments, the repository established for runtime deployment
    * may be changed.
    *
    * @param system the bootstrap repository from which merlin 
    *   system jar files have been resolved
    * @param args the set of command line arguments 
    */
    public CLIKernelLoader( final Repository system, String[] args ) 
      throws Exception
    {
        //
        // parse the commandline
        //

        CommandLineParser parser = new BasicParser();
        CommandLine line = parser.parse( CL_OPTIONS, args );

        //
        // check for a language override from the commandline
        // and reset the cache if needed
        //

        if( line.hasOption( "lang" ) )
        {
            ResourceManager.clearResourceCache();
            String language = line.getOptionValue( "lang" );
            Locale locale = new Locale( language, "" );

            Locale.setDefault( locale );
            REZ = ResourceManager.getPackageResources( CLIKernelLoader.class );
        }

        //
        // check for quick exit commands - help and version and if 
        // present - print content and return
        //

        if( line.hasOption( "help" ) )
        {
            doHelp();
            return;
        }
        else if( line.hasOption( "version" ) )
        {
            System.out.println( "Version: " + getVersionString() );
            return;
        }

        //
        // build the kernel context
        //

        KernelContext context = null;
        try
        {
            context = createContext( system, line );
        }
        catch( FileNotFoundException e )
        {
            System.out.println( "");
            System.out.println( "FILE-NOT-FOUND: " + e.getMessage() );
            System.out.println( "");
            return;
        }
        catch( KernelException e )
        {
            System.out.println( "KERNEL-ERROR: " + e.getMessage() );
            final String error = 
              ExceptionHelper.packException( e.getMessage(), e.getCause(), true );
            System.err.println( error );
            return;
        }
        catch( Throwable e )
        {
            final String error =
              "Internal error while attempting to create kernel context.";
            String msg = 
              ExceptionHelper.packException( error, e, true );
            System.err.println( msg );
            return;
        }

        //
        // With the kernel context established we can now handle the 
        // the runtime objective.  If the commandline contains the 
        // -install switch we use the repository established by the 
        // context to install a bar file, otherwise this is a normal
        // runtime deployment scenario.
        //

        if( line.hasOption( "install" ) )
        {
            String path = line.getOptionValue( "install" );
            Logger log = context.getKernelLogger().getChildLogger( "installer" );
            URL url = resolveURL( path );
            log.info( "installing: " + url );
            StringBuffer buffer = new StringBuffer();
            BlockManifest manifest = context.getRepository().install( url, buffer );
            log.info( buffer.toString() );
            return;
        }

        //
        // This is a classic deployment scenario under which we are launching a 
        // kernel and processing executable block descriptors.  Two approaches 
        // are provided, one is a direct instantiation of the kernel and the 
        // other is a jmx managed startup.
        //

        if( line.hasOption( "jmx" ) )
        {
            managedStartup( context );
        }
        else
        {
            standardStartup( context );
        }
    }

    private boolean managedStartup( KernelContext context )
    {
        MBeanServer server = MBeanServerFactory.createMBeanServer();
        try
        {
            //mx4j.log.Log.setDefaultPriority( mx4j.log.Logger.DEBUG );
            Logger logger = context.getKernelLogger().getChildLogger( "jmx" );
            JRMPKernelAdaptor adapter = new JRMPKernelAdaptor( logger, server );
            setShutdownHook( adapter );
            adapter.start();
        }
        catch( Throwable e )
        {
            final String error =
              "\nUnexpected error during jmx server establishment.";
            String msg = 
              ExceptionHelper.packException( error, e, true );
            System.err.println( msg );
            return false;
        }

        Kernel kernel = null;
        try
        {
            kernel = createKernel( server, context );
        }
        catch( Throwable e )
        {
            final String error = 
              "Could not establish the kernel.";
            String message = ExceptionHelper.packException( error, e );
            System.err.println( message );
            return false;
        }

        try
        {
            kernel.startup();
        }
        catch( Throwable e )
        {
            final String error = 
              "Kernel startup failure.";
            String message = ExceptionHelper.packException( error, e );
            System.err.println( message );
            kernel.shutdown();
            return false;
        }

        return true;
    }

    private boolean standardStartup( KernelContext context )
    {
        //
        // ready to roll -
        // create the kernel and set a shutdown hook
        //

        Kernel kernel = null;
        try
        {
            kernel = createKernel( null, context );
        }
        catch( KernelException e )
        {
            final String error =
              "\nInternal error during kernel instantiation.";
            String msg = 
              ExceptionHelper.packException( error, e, context.getDebugFlag() );
            System.err.println( msg );
            return false;
        }

        try
        {
            kernel.startup();
        }
        catch( KernelException e )
        {
            String msg = 
              ExceptionHelper.packException( 
                e.getMessage(), e.getCause(), context.getDebugFlag() );
            System.err.println( msg );
            kernel.shutdown();
            return false;
        }
        catch( Throwable e )
        {
            kernel.shutdown();
            final String error =
              "\nInternal error during kernel startup.";
            String msg = 
              ExceptionHelper.packException( error, e, context.getDebugFlag() );
            System.err.println( msg );
            return false;
        }

        return true;
    }

    private Kernel createKernel( MBeanServer server, KernelContext context ) 
      throws KernelException
    {
        Kernel kernel = new DefaultKernel( server, context );
        setShutdownHook( kernel );
        return kernel;
    }

    //--------------------------------------------------------------------------
    // implementation
    //--------------------------------------------------------------------------

   /**
    * Create the kernel context using the supplied command line arguments.
    * @param system the system repository
    * @param args the command line arguments
    * @return the kernel context
    */
    private KernelContext createContext( final Repository system, CommandLine line ) 
      throws Exception
    {
        File base = HOME;
        URL[] blocks = null;

        boolean server = !line.hasOption( "execute" );
        String[] arguments = line.getArgs();
        if( arguments.length == 0 )
        {
            if( !server )
            {
                File target = new File( HOME, "block.xml" );
                if( target.exists() ) 
                {
                    try
                    {
                        blocks = new URL[]{ target.toURL() };
                    }
                    catch( MalformedURLException mue )
                    {
                        final String error = 
                          "Unable to establish url to target: " + target;
                        throw new KernelException( error, mue );
                    }
                }
                else
                {
                    throw new FileNotFoundException( target.toString() );
                }
            }
            else
            {
                 blocks = new URL[0];
            }
        }
        else
        {
            blocks = new URL[ arguments.length ];
            for( int i=0; i<arguments.length; i++ )
            {
                String blockarg = arguments[i];
                blocks[i] = resolveURL( blockarg );
            }
        }

        //
        // get the debug flag
        //

        boolean debug = line.hasOption( "debug" );
 
        //
        // get the info flag
        //

        boolean info = line.hasOption( "info" );
 
        //
        // the kernel configuration (used by the kernel loader)
        //

        URL kernel = getKernelPath( line );

        //
        // get the system path 
        //

        File repository = getUserRepositoryPath( line );

        //
        // get the library path
        //

        File library = getLibraryPath( line, HOME );

        //
        // get the working home directory
        //

        File home = getHomePath( line );

        //
        // get the optional configuration override
        //

        URL config = getConfigPath( line );

        //
        // create the kernel
        //

        try
        {
            return new DefaultKernelContext( 
              system, repository, library, home, kernel, 
              blocks, config, server, info, debug );
        }
        catch( KernelException e )
        {
            final String error = 
              "Kernel context instantiation error.";
            throw new KernelException( error, e );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unexpected error during kernel context instantiation.";
            throw new KernelException( error, e );
        }
    }

    private URL resolveURL( String path ) throws IOException
    {
        try
        {
            return new URL( path );
        }
        catch( Throwable e )
        {
            return getFile( HOME, path ).toURL();
        }
    }

   /**
    * Create a shutdown hook that will trigger shutdown of the supplied kernel.
    * @param kernel the kernel to be shutdown
    */
    private void setShutdownHook( final Kernel kernel )
    {
        //
        // Create a shutdown hook to trigger clean disposal of the
        // Merlin kernel
        //

        Runtime.getRuntime().addShutdownHook(
          new Thread()
          {
              public void run()
              {
                  try
                  {
                      kernel.shutdown();
                  }
                  catch( Throwable e )
                  {
                      // ignore it
                  }
              }
          }
        );
    }

   /**
    * Create a shutdown hook that will trigger shutdown of the supplied adapter.
    * @param adapter the jmx jrmp adapter
    */
    private void setShutdownHook( final JRMPKernelAdaptor adapter )
    {
        //
        // Create a shutdown hook to trigger clean disposal of the
        // Merlin kernel
        //

        Runtime.getRuntime().addShutdownHook(
          new Thread()
          {
              public void run()
              {
                  try
                  {
                      adapter.stop();
                  }
                  catch( Throwable e )
                  {
                      // ignore it
                  }
              }
          }
        );
    }


    private Configuration getKernelConfiguration( final File file )
      throws ConfigurationException, IOException, SAXException
    {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        InputStream is = new FileInputStream( file );
        if( is == null )
        {
            throw new IOException(
              "Could not load kernel configuration resource \"" + file + "\"" );
        }
        return builder.build( is );
    }

   /**
    * Return a string representation of the product name and version.
    * @return the product and version string
    */
    private static String getVersionString()
    {
         return PRODUCT + " " + VERSION;
    }

    private static void doHelp()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "merlin [block]", " ", CL_OPTIONS, "", true );
    }

   /**
    * Return the configuration overrides url.
    * @param block the directory containing the block defintion
    * @param command the command line
    * @return the target override configuration (possibly null)
    */
    private URL getConfigPath( CommandLine command ) throws IOException
    {
        final String key = "config";
        if( !command.hasOption( key ) ) return null;
        String path = command.getOptionValue( key );
        return resolveURL( path );
    }

   /**
    * Return the kernel url.
    * @param block the directory containing the block defintion
    * @param command the command line
    * @return the target override configuration (possibly null)
    */
    private URL getKernelPath( CommandLine command ) throws Exception
    {
        final String key = "kernel";
        if( !command.hasOption( key ) ) return null;
        String path = command.getOptionValue( key );
        return resolveURL( path );
    }

   /**
    * Return the directory to be used as the working home directory.  If no command line
    * argument is supplied, the home directory defaults null.
    *
    * @param command the command line arguments
    * @return the home directory or null if undefined
    * @exception IOException if an error occurs in directory resolution
    */
    private File getHomePath( CommandLine command ) throws IOException
    {
        final String key = "home";
        if( !command.hasOption( key ) ) return null;
        final String path = command.getOptionValue( key );
        return getFile( HOME, path );
    }

    private File getLibraryPath( CommandLine command, File system ) throws IOException
    {
        final String key = "library";
        if( !command.hasOption( key ) ) return null;
        final String path = command.getOptionValue( key );
        return getFile( HOME, path );
    }

    private File getUserRepositoryPath( CommandLine command ) throws IOException
    {
        final String key = "repository";
        if( !command.hasOption( key ) ) return null;
        final String path = command.getOptionValue( key );
        return getFile( HOME, path );
    }

    private File getFile( File base, String path ) throws IOException
    {
        if( path == null )
        {
            throw new NullPointerException( "path" );
        }
        if( base == null )
        {
            throw new NullPointerException( "base" );
        }

        File file = new File( path );
        if( file.isAbsolute() )
        {
            return file.getCanonicalFile();
        }
        else
        {
            return new File( base, path ).getCanonicalFile();
        }
    }

    private static Options buildCommandLineOptions()
    {
        Options options = new Options();

        Option help = new Option(
           "help",
           REZ.getString( "cli-help-description" ) );

        Option version = new Option(
           "version",
           REZ.getString( "cli-version-description" ) );

        Option execute = new Option(
           "execute",
           REZ.getString( "cli-execute-description" ) );

        Option debug = new Option(
           "debug",
           REZ.getString( "cli-debug-description" ) );

        Option info = new Option(
           "info",
           REZ.getString( "cli-info-description" ) );

        Option jmx = new Option(
           "jmx",
           REZ.getString( "cli-jmx-description" ) );

        Option locale = OptionBuilder
           .hasArg()
           .withArgName( "code" )
           .withDescription( REZ.getString( "cli-language-description" )  )
           .create( "lang" );

        Option home = OptionBuilder
           .hasArg()
           .withArgName( REZ.getString( "directory" ) )
           .withDescription( REZ.getString( "cli-home-description" ) )
           .create( "home" );

        Option repository = OptionBuilder
           .hasArg()
           .withArgName( REZ.getString( "directory" ) )
           .withDescription( REZ.getString( "cli-repository-description" ) )
           .create( "repository" );

        Option library = OptionBuilder
           .hasArg()
           .withArgName( REZ.getString( "directory" ) )
           .withDescription( REZ.getString( "cli-library-description" ) )
           .create( "library" );

        Option config = OptionBuilder
           .hasArg()
           .withArgName( REZ.getString( "url" ) )
           .withDescription( REZ.getString( "cli-config-description" ) )
           .create( "config" );

        Option kernel = OptionBuilder
           .hasArg()
           .withArgName( REZ.getString( "url" ) )
           .withDescription( REZ.getString( "cli-kernel-description" ) )
           .create( "kernel" );

        Option install = OptionBuilder
           .hasArg()
           .withArgName( REZ.getString( "url" ) )
           .withDescription( REZ.getString( "cli-install-description" ) )
           .create( "install" );

        options.addOption( help );
        options.addOption( locale );
        options.addOption( execute );
        options.addOption( version );
        options.addOption( info );
        options.addOption( debug );
        options.addOption( jmx );
        options.addOption( install );
        options.addOption( home );
        options.addOption( repository );
        options.addOption( library );
        options.addOption( config );
        options.addOption( kernel );
        return options;
    }
}
