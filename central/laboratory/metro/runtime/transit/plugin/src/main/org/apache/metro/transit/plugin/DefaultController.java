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

package org.apache.metro.transit.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Properties;

import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.exception.ExceptionHelper;
import org.apache.avalon.util.defaults.Defaults;
import org.apache.avalon.util.defaults.DefaultsBuilder;
import org.apache.avalon.util.i18n.ResourceManager;
import org.apache.avalon.util.i18n.Resources;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionGroup;

import org.apache.metro.logging.Logger;
import org.apache.metro.logging.provider.LoggingFactory;
import org.apache.metro.logging.provider.LoggingManager;
import org.apache.metro.logging.provider.LoggingCriteria;
import org.apache.metro.logging.provider.LoggingException;
import org.apache.metro.transit.Repository;
import org.apache.metro.transit.Artifact;
import org.apache.metro.transit.RepositoryException;
import org.apache.metro.transit.InitialContext;
import org.apache.metro.transit.StandardLoader;
import org.apache.metro.transit.Monitor;
import org.apache.metro.transit.Policy;
import org.apache.metro.transit.PolicyException;
import org.apache.metro.transit.PluginDescriptor;
import org.apache.metro.transit.provider.SystemContext;
import org.apache.metro.transit.provider.ControllerException;


/**
 * The controller class provides services supporting the establishment of 
 * a system context. The system context provides access to a logging manager, 
 * repository, working and temporary directories, and system level fags.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class DefaultController implements SystemContext
{
    // ------------------------------------------------------------------------
    // static
    // ------------------------------------------------------------------------

    private static Resources REZ =
        ResourceManager.getPackageResources( DefaultController.class );

    public static Options OPTIONS = buildCommandLineOptions();

    public static String DEFAULT_LOGGING_IMPLEMENTATION = 
      "@DEFAULT_LOGGING_IMPLEMENTATION@";

    private static Options buildCommandLineOptions()
    {
        Options options = new Options();

        //
        // action group
        //

        OptionGroup group = new OptionGroup();
        group.addOption( 
          new Option( "help", REZ.getString( "cli-help-description" ) ) );
        group.addOption( 
          new Option( "version", REZ.getString( "cli-version-description" ) ) );
        group.addOption(
          OptionBuilder.hasArg().withArgName( REZ.getString( "artifact" ) )
           .withDescription( REZ.getString( "cli-load-description" ) )
           .create( "load" ) );
        options.addOptionGroup( group );

        //
        // on-line/offline switches
        //

        OptionGroup onlineMode = new OptionGroup();
        onlineMode.addOption( 
          new Option( "offline", REZ.getString( "cli-offline-description" ) ) );
        onlineMode.addOption( 
          new Option( "online", REZ.getString( "cli-online-description" ) ) );
        options.addOptionGroup( onlineMode );

        //
        // timestamp switches
        //

        OptionGroup timestamp = new OptionGroup();
        timestamp.addOption( 
          new Option( "fast", REZ.getString( "cli-fast-description" ) ) );
        timestamp.addOption( 
          new Option( "snapshot", REZ.getString( "cli-snapshot-description" ) ) );
        timestamp.addOption( 
          new Option( "timestamp", REZ.getString( "cli-timestamp-description" ) ) );
        timestamp.addOption( 
          new Option( "overwrite", REZ.getString( "cli-overwrite-description" ) ) );
        options.addOptionGroup( timestamp );

        //
        // parameters
        //

        options.addOption( 
          new Option( "debug", REZ.getString( "cli-debug-description" ) ) );
        options.addOption( 
          new Option( "info", REZ.getString( "cli-info-description" ) ) );
        options.addOption( 
          OptionBuilder.hasArg().withArgName( "code" )
           .withDescription( REZ.getString( "cli-language-description" ) )
           .create( "lang" ) );
        options.addOption(
          OptionBuilder.hasArg().withArgName( REZ.getString( "directory" ) )
           .withDescription( REZ.getString( "cli-dir-description" ) )
           .create( "dir" ) );
        options.addOption(
          OptionBuilder.hasArg().withArgName( REZ.getString( "artifact" ) )
           .withDescription( REZ.getString( "cli-logging-description" ) )
           .create( "logging" ) );
        options.addOption(
          OptionBuilder.hasArg().withArgName( REZ.getString( "directory" ) )
           .withDescription( REZ.getString( "cli-cache-description" ) )
           .create( "cache" ) );
        options.addOption(
          OptionBuilder.hasArgs().withArgName( REZ.getString( "host" ) )
           .withDescription( REZ.getString( "cli-hosts-description" ) )
           .withValueSeparator( ',' )
           .create( "hosts" ) );

        return options;
    }


    // ------------------------------------------------------------------------
    // immutable state
    // ------------------------------------------------------------------------

    private final InitialContext m_context;
    private final boolean m_debug;
    private final boolean m_info;
    private final File m_dir;
    private final ArrayList m_plugins = new ArrayList();
    private final Properties m_properties;
    private final Repository m_repository;
    private final LoggingManager m_logging;
    private final Logger m_logger;

    // ------------------------------------------------------------------------
    // mutable state
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------
    
    public DefaultController( 
      Repository repository, InitialContext context, PluginDescriptor descriptor, String[] args ) 
      throws IOException
    {
        if( null == context )
          throw new NullPointerException( "context" );
        if( null == args )
          throw new NullPointerException( "args" );
        if( null == descriptor )
          throw new NullPointerException( "descriptor" );

        //
        // get the application properties
        //

        CommandLine line = getCommandLine( args );
        m_dir = getWorkingDirectory( line );
        m_properties = getControllerProperties( m_dir );

        //
        // make sure the lange is switched to the users preference
        //

        if( line.hasOption( "lang" ) )
        {
            String language = line.getOptionValue( "lang" );
            ResourceManager.clearResourceCache();
            Locale locale = new Locale( language, "" );
            Locale.setDefault( locale );
            REZ = ResourceManager.getPackageResources( DefaultController.class );
        }

        //
        // create the application repository
        //

        m_debug = getInitialDebugPolicy( line, false );
        m_info = getInitialInfoPolicy( line, false );

        //
        // use the bootstrap repository to load the subsystemes needed for the 
        // controller
        //

        try
        {
            Artifact artifact = getLoggingSystemArtifact( line );
            URL url = getLoggingSystemConfigurationURL();
            m_logging = createLoggingManager( repository, artifact, m_dir, url, m_debug );
            m_logger = m_logging.getLoggerForCategory( "metro" );
        }
        catch( Exception e )
        {
            System.err.println( e.getMessage() );
            e.printStackTrace();
            throw new ControllerException( e.getMessage() );
        }

        //
        // create a managed repository
        //


        Policy policy = getTimesampPolicy( line, m_properties );

        try
        {
            boolean online = getInitialOnlinePolicy( line, true );
            File cache = getInitialCache( line, context.getCacheDirectory() );
            String[] hosts = getInitialHosts( line, m_properties, context.getHosts() );
            Logger child = getLogger().getChildLogger( "cache" );
            Monitor monitor = new LoggingMonitor( child );

            m_context = 
              new DefaultInitialContext( 
                monitor, cache, hosts, online, policy, m_info, m_debug );
            m_repository = new StandardLoader( m_context );
            getLogger().debug( "repository system established" );
        }
        catch( RepositoryException re )
        {
            System.err.println( re.getMessage() );
            throw new ControllerException( re.getMessage() );
        }

        //
        // do the initial actions
        //

        if( line.hasOption( "help" ) )
        {
            System.out.println("");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "metro ", " ", 
              buildCommandLineOptions(), "", true );
        }
        else if( line.hasOption( "version" ) )
        {
            System.out.println( 
              "\nPlugin controller: " + descriptor.getArtifact().getGroup() 
              + "/" + descriptor.getArtifact().getName() 
              + "\nVersion: " + descriptor.getArtifact().getVersion() 
              + "\nBuild: " + descriptor.getBuild() );
        }
        else
        {

            //
            // load the target plugins
            // 

            String[] applicationArgs = line.getArgs();
            for( int i=0; i<applicationArgs.length; i++ )
            {
                getLogger().debug( "application option (" + (i+1) + ") " + applicationArgs[i] );
            }

            String[] payload = getLoadArguments( line.getOptionValue( "load" ) );
            for( int i=0; i<payload.length; i++ )
            {
                String spec = payload[i];
                loadPlugin( spec, applicationArgs );
            }
        }
    }

    // ------------------------------------------------------------------------
    // SystemContext
    // ------------------------------------------------------------------------
    
   /**
    * Return the working directory from which containers may establish
    * persistent content between sessions.
    *
    * @return the working directory
    */
    public File getWorkingDirectory()
    {
        return m_dir;
    }

   /**
    * Return the temp directory from which containers may establish
    * transient non-persistent content.
    *
    * @return the temp directory
    */
    public File getTempDirectory()
    {
        return m_dir;
    }

   /**
    * Return the info status flag.  If TRUE plugins should list information
    * concerning initialization parameters during establishment.
    * 
    * @return the info policy flag
    */
    public boolean getInfoPolicy()
    {
        return m_info;
    }

   /**
    * Return the system wide logging manager.
    *
    * @return the logging manager.
    */
    public LoggingManager getLoggingManager()
    {
        return m_logging;
    }

   /**
    * Return the initial context.
    *
    * @return the repository inital context.
    */
    public InitialContext getInitalContext()
    {
        return m_context;
    }

   /**
    * Return the application repository cache controller.
    *
    * @return the repository
    */
    public Repository getRepository()
    {
        return m_repository;
    }

    // ------------------------------------------------------------------------
    // internals
    // ------------------------------------------------------------------------

    private URL loadResource( String spec ) throws ControllerException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "loading artifact: " + spec );
        }

        try
        {
            Artifact artifact = Artifact.createArtifact( spec );
            return getRepository().getResource( artifact );
        }
        catch( Throwable e )
        {
            final String error = 
              ExceptionHelper.packException( e, m_debug );
            if( getLogger().isErrorEnabled() )
            {
                getLogger().error( error );
            }
            throw new ControllerException( error );
        }
    }

    private Object loadPlugin( String spec, String[] args ) throws ControllerException
    {
        if( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "loading plugin: " + spec );
        }

        try
        {
            Artifact artifact = Artifact.createArtifact( spec );
            Logger logger = getLogger();
            Object[] params = 
              new Object[]{ m_context, m_repository, this, logger, args };
            ClassLoader classloader = DefaultController.class.getClassLoader();
            return getRepository().getPlugin( classloader, artifact, params );
        }
        catch( Throwable e )
        {
            final String error = 
              ExceptionHelper.packException( e, m_debug );
            if( getLogger().isErrorEnabled() )
            {
                getLogger().error( error );
            }
            throw new ControllerException( error );
        }
    }

    private URL getLoggingSystemConfigurationURL() throws IOException
    {
        String spec = getProperty( LOGGING_CONFIGURATION_KEY, null );
        if( spec != null )
        {
            if( spec.indexOf( ":" ) > -1 )
            {
                 return new URL( spec );
            }
            else
            {
                 return resolveFile( spec ).toURL();
            }
        }
        else
        {
            return null;
        }
    }

    private Artifact getLoggingSystemArtifact( CommandLine line ) 
    {
        if( line.hasOption( "logging" ) )
        {
            String spec = line.getOptionValue( "logging" );
            return Artifact.createArtifact( spec );
        }
        else
        {
            String spec = getProperty( LOGGING_IMPLEMENTATION_KEY, DEFAULT_LOGGING_IMPLEMENTATION );
            return Artifact.createArtifact( spec );
        }
    }

    private boolean getBooleanProperty( String key, boolean fallback )
    {
        String value = m_properties.getProperty( key );
        if( null != value )
        {
            return value.equalsIgnoreCase( "true" );
        }
        else
        {
            return fallback;
        }
    }

    private File getFileProperty( String key, File fallback ) throws IOException
    {
        String value = m_properties.getProperty( key );
        if( null != value )
        {
            return resolveFile( value );
        }
        else
        {
            return fallback;
        }
    }

    private String getProperty( String key, String fallback )
    {
        String value = m_properties.getProperty( key );
        if( null != value )
        {
            return value;
        }
        else
        {
            return fallback;
        }
    }

    private String[] getLoadArguments( String arg )
    {
        if( null == arg )
          return new String[0];
        if( arg.indexOf( "," ) == -1 )
          return new String[]{ arg };
        ArrayList list = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer( arg, "," );
        while( tokenizer.hasMoreTokens() )
        {
            list.add( tokenizer.nextToken() );
        }
        return (String[]) list.toArray( new String[0] );
    }


   /**
    * Utility method to create the LoggingManager.
    * @param context the initial context reference
    * @param artifact the logging implementation factory artifact 
    * @param dir the logging system base directory 
    * @param path the logging system configuration path
    * @param debug the debug flag
    * @return the logging manager
    * @exception NullPointerException if the supplied context is null
    */
    private LoggingManager createLoggingManager( 
      final Repository repository, final Artifact artifact, final File dir, 
      final URL path, boolean debug ) throws IOException, LoggingException
    {
        assertNotNull( artifact, "artifact" );
        assertNotNull( dir, "dir" );

        ClassLoader classloader = SystemContext.class.getClassLoader();
        Object[] args = new Object[0];
        Object object = repository.getPlugin( classloader, artifact, args );
        if( object instanceof LoggingFactory )
        {
            LoggingFactory factory = (LoggingFactory) object;
            LoggingCriteria params = factory.createDefaultLoggingCriteria();
            params.setBaseDirectory( dir );
            params.setLoggingConfiguration( path );
            params.setDebugEnabled( debug );
            return factory.createLoggingManager( params );
        }
        else
        {
            final String error =
              "Logging manager provider referenced by ["
              + artifact 
              + "] does not implement the service interface ["
              + LoggingFactory.class.getName()
              + "].";
            System.err.println( error );
            throw new ControllerException( error );
        }
    }

    private Properties getControllerProperties( File dir ) throws ControllerException
    {
        try
        {
            DefaultsBuilder builder = new DefaultsBuilder( GROUP, dir );
            Properties defaults = 
              Defaults.getStaticProperties( 
                DefaultController.class, "/" 
                + InitialContext.PROPERTY_FILENAME );

            //
            // set the ${metro.dir} value 
            //

            defaults.setProperty( SystemContext.DIR_KEY, dir.toString() );

            //
            // get the consolidated expanded properties
            //

            Properties properties = 
              builder.getConsolidatedProperties( defaults, SystemContext.KEYS );
            Defaults.macroExpand( properties, new Properties[0] );
            Defaults.macroExpand( properties, new Properties[0] );
        
            return properties;
        }
        catch( Throwable e )
        {
            System.err.println( e.getMessage() );
            throw new ControllerException( e.getMessage() );
        }
    }

    private CommandLine getCommandLine( String[] args ) throws ControllerException
    {
        try
        {
            CommandLineParser parser = new GnuParser();
            return parser.parse( OPTIONS, args, true );
        }
        catch( Throwable e )
        {
            System.err.println( e.getMessage() );
            throw new ControllerException( e );
        }
    }

    private File getWorkingDirectory( CommandLine line ) throws IOException
    {
        File dir = new File( System.getProperty( "user.dir" ) );
        if( line.hasOption( "dir" ) )
        {
            String path = line.getOptionValue( "dir" );
            return resolveFile( dir, path );
        }
        else
        {
            String path = System.getProperty( SystemContext.DIR_KEY );
            if( null != path )
            {
                return resolveFile( dir, path );
            }
            else
            {
                return dir;
            }
        }
    }

    private File resolveFile( String path ) throws IOException
    {
        return resolveFile( m_dir, path );
    }

    private File resolveFile( File anchor, String path ) throws IOException
    {
        File file = new File( path );
        if( file.isAbsolute() )
        {
            return file.getCanonicalFile();
        }
        else
        {
            File anchored = new File( anchor, path );
            return file.getCanonicalFile();
        }
    }

    private String[] expandSequence( String sequence, String token, String[] fallback )
    {
        if( null == sequence)
          return fallback;
        ArrayList list = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer( sequence, token );
        while( tokenizer.hasMoreTokens() )
        {
            list.add( tokenizer.nextToken() );
        }
        return (String[]) list.toArray( new String[0] );
    }

    private String[] getInitialHosts( CommandLine line, Properties properties, String[] fallback )
    {
        if( line.hasOption( "hosts" ) )
        {
            return line.getOptionValues( "hosts" );
        }
        else
        {
            return getInitialHosts( properties, fallback );
        }
    }

    private Policy getTimesampPolicy( CommandLine line, Properties properties )
      throws PolicyException
    {
        if( line.hasOption( "overwrite" ) )
        {
            return Policy.OVERWRITE;
        }
        if( line.hasOption( "timestamp" ) )
        {
            return Policy.TIMESTAMP;
        }
        else if( line.hasOption( "snapshot" ) )
        {
            return Policy.SNAPSHOT;
        }
        else if( line.hasOption( "fast" ) )
        {
            return Policy.FAST;
        }
        else
        {
            String value = properties.getProperty( TIMESTAMP_KEY );
            return Policy.createPolicy( value );
        }
    }

    private String[] getInitialHosts( Properties properties, String[] fallback )
    {
        String hosts = System.getProperty( HOSTS_KEY );
        if( null != hosts )
           return expandSequence( hosts, ",", fallback );
        hosts = properties.getProperty( HOSTS_KEY );
        if( null != hosts )
           return expandSequence( hosts, ",", fallback );
        return fallback;
    }

    private File getInitialCache( CommandLine line, File fallback ) throws IOException
    {
        if( line.hasOption( "cache" ) )
        {
            String cache = line.getOptionValue( "cache" );
            return resolveFile( cache );
        }
        else
        {
            return getFileProperty( CACHE_KEY, fallback );
        }
    }

    private boolean getInitialOnlinePolicy( CommandLine line, boolean flag )
    {
        if( line.hasOption( "offline" ) )
        {
            return false;
        }
        else
        {
            return getBooleanProperty( ONLINE_KEY, flag );
        }
    }

    private boolean getInitialDebugPolicy( CommandLine line, boolean flag ) 
    {
        if( line.hasOption( "debug" ) )
        {
            return true;
        }
        else
        {
            return getBooleanProperty( DEBUG_KEY, flag );
        }
    }

    private boolean getInitialInfoPolicy( CommandLine line, boolean flag )
    {
        if( line.hasOption( "info" ) )
        {
            return true;
        }
        else
        {
            return getBooleanProperty( INFO_KEY, flag );
        }
    }

    private void assertNotNull( Object object, String key ) 
      throws NullPointerException
    {
        if( null == object ) 
          throw new NullPointerException( key );
    }

    private Logger getLogger() 
    {
        return m_logger;
    }
}

