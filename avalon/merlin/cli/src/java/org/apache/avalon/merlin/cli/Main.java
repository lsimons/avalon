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

package org.apache.avalon.merlin.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Locale;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import javax.naming.directory.Attributes;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.main.DefaultInitialContext;
import org.apache.avalon.repository.main.DefaultBuilder;
import org.apache.avalon.repository.meta.ArtifactDescriptor;
import org.apache.avalon.repository.util.RepositoryUtils;
import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.exception.ExceptionHelper;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;


/**
 * Merlin command line handler.
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.6 $
 */
public class Main 
{
    //----------------------------------------------------------
    // static
    //----------------------------------------------------------

    private static Resources REZ =
        ResourceManager.getPackageResources( Main.class );

    private static final File USER_HOME = 
      new File( System.getProperty( "user.home" ) );

    private static final String MERLIN = "merlin.properties";

    private static Options CL_OPTIONS = buildCommandLineOptions();

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

        Option locale = OptionBuilder
           .hasArg()
           .withArgName( "code" )
           .withDescription( REZ.getString( "cli-language-description" )  )
           .create( "lang" );

        Option implementation = OptionBuilder
           .hasArg()
           .withArgName( "artifact" )
           .withDescription( REZ.getString( "cli-implementation-description" )  )
           .create( "impl" );

        Option home = OptionBuilder
           .hasArg()
           .withArgName( REZ.getString( "directory" ) )
           .withDescription( REZ.getString( "cli-home-description" ) )
           .create( "home" );

        Option context = OptionBuilder
           .hasArg()
           .withArgName( REZ.getString( "directory" ) )
           .withDescription( REZ.getString( "cli-context-description" ) )
           .create( "context" );

        Option system = OptionBuilder
           .hasArg()
           .withArgName( REZ.getString( "directory" ) )
           .withDescription( REZ.getString( "cli-system-description" ) )
           .create( "system" );

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
        options.addOption( install );
        options.addOption( home );
        options.addOption( context );
        options.addOption( system );
        options.addOption( repository );
        options.addOption( library );
        options.addOption( config );
        options.addOption( kernel );
        options.addOption( implementation );
        return options;
    }

    private static Main MAIN = null;

   /**
    * Main command line enty point.
    * @param args the command line arguments
    */
    public static void main( String[] args )
    {
        try
        {
            //
            // parse the commandline
            //

            CommandLineParser parser = new BasicParser();
            CommandLine line = parser.parse( CL_OPTIONS, args );

            File dir = getWorkingDirectory( line );
            Artifact artifact = getDefaultImplementation( dir, line );
            File system = getMerlinSystemRepository( line );

            if( line.hasOption( "version" ) )
            {
                Main.printVersionInfo( system, artifact );
                return;     
            }
            else if( line.hasOption( "help" ) )
            {
                if( line.hasOption( "lang" ) )
                {
                    ResourceManager.clearResourceCache();
                    String language = line.getOptionValue( "lang" );
                    Locale locale = new Locale( language, "" );
                    Locale.setDefault( locale );
                    REZ = ResourceManager.getPackageResources( Main.class );
                }
                Main.printHelpInfo();
                return;
            }
            else
            {
                //
                // setup the initial context
                //

                ClassLoader parent = Main.class.getClassLoader();
                Artifact impl = null; // default
                String[] bootstrap = null; // default
                
System.out.println( "SETTING INITIAL WORKING DIR: " + dir );

                InitialContext context = 
                   new DefaultInitialContext( 
                     dir, parent, impl, system, bootstrap );

                //
                // process the commandline and do the real work
                //

                MAIN = new Main( context, artifact, line );

            }
        }
        catch( Throwable e )
        {
            String msg = 
              ExceptionHelper.packException( e, true );
            System.err.println( msg );
            System.exit( -1 );
        }
    }

    //----------------------------------------------------------
    // immutable state
    //----------------------------------------------------------

    private final Object m_kernel;

    //----------------------------------------------------------
    // constructor
    //----------------------------------------------------------

   /**
    * Creation of a new kernel cli handler.
    * @param context the repository inital context
    * @param artifact the merlin implementation artifact
    * @param line the command line construct
    * @param base the base working directory
    * @exception Exception if an error occurs
    */
    public Main( 
      InitialContext context, Artifact artifact, CommandLine line ) throws Exception
    {
        Builder builder = new DefaultBuilder( context, artifact );
        ClassLoader classloader = builder.getClassLoader();
        Factory factory = builder.getFactory();
        Map criteria = factory.createDefaultCriteria();

        //
        // update the criteria using the command line information
        //

        handleCommandLine( criteria, line );

        //
        // instantiate the kernel
        //

        m_kernel = factory.create( criteria );
    }

    private void handleCommandLine( Map criteria, CommandLine line )
    {
        setLanguage( criteria, line );
        setInfoPolicy( criteria, line );
        setDebugPolicy( criteria, line );
        setServerPolicy( criteria, line );
        setAnchorDirectory( criteria, line );
        setContextDirectory( criteria, line );
        setRepositoryDirectory( criteria, line );
        setKernelURL( criteria, line );
        setOverridePath( criteria, line );
        setDeploymentPath( criteria, line );
    }

    private void setLanguage( Map criteria, CommandLine line )
    {
        if( line.hasOption( "lang" ) )
        {
            String language = line.getOptionValue( "lang" );
            criteria.put( "merlin.lang", language );
        }
    }

    private void setKernelURL( Map criteria, CommandLine line )
    {
        if( line.hasOption( "kernel" ) )
        {
            String kernel = line.getOptionValue( "kernel" );
            criteria.put( "merlin.kernel", kernel );
        }
    }

    private void setOverridePath( Map criteria, CommandLine line )
    {
        if( line.hasOption( "config" ) )
        {
            String config = line.getOptionValue( "config" );
            criteria.put( "merlin.override", config );
        }
    }

    private void setWorkingDirectory( Map criteria, CommandLine line )
    {
        if( line.hasOption( "home" ) )
        {
            String home = line.getOptionValue( "home" );
            criteria.put( "merlin.dir", home );
        }
    }

    private void setAnchorDirectory( Map criteria, CommandLine line )
    {
        if( line.hasOption( "library" ) )
        {
            String library = line.getOptionValue( "library" );
            criteria.put( "merlin.anchor", library );
        }
    }

    private void setContextDirectory( Map criteria, CommandLine line )
    {
        if( line.hasOption( "context" ) )
        {
            String context = line.getOptionValue( "context" );
            criteria.put( "merlin.context", context );
        }
    }

    private void setRepositoryDirectory( Map criteria, CommandLine line )
    {
        if( line.hasOption( "repository" ) )
        {
            String repository = line.getOptionValue( "repository" );
            criteria.put( "merlin.repository", repository );
        }
    }

    private void setDebugPolicy( Map criteria, CommandLine line )
    {
        boolean debug = line.hasOption( "debug" );
        criteria.put( "merlin.debug", new Boolean( debug ) );
    }

    private void setInfoPolicy( Map criteria, CommandLine line )
    {
        boolean info = line.hasOption( "info" );
        criteria.put( "merlin.info", new Boolean( info ) );
    }

    private void setServerPolicy( Map criteria, CommandLine line )
    {
        boolean execute = line.hasOption( "execute" );
        criteria.put( "merlin.server", new Boolean( !execute ) );
    }

    private void setDeploymentPath( Map criteria, CommandLine line )
    {
        String[] arguments = line.getArgs();
        if( arguments.length > 0 )
        {
            criteria.put( "merlin.deployment", arguments );
        }
    }

   /**
    * Resolve the merlin.dir value.
    * @param line the command line construct
    * @return the working directory
    */
    private static File getWorkingDirectory( CommandLine line ) throws Exception
    {
        if( line.hasOption( "home" ) )
        {
            String dir = line.getOptionValue( "home" );
            return new File( dir ).getCanonicalFile();
        }
        else
        {
            return getBaseDirectory();
        }
    }

   /**
    * Resolve the default implementation taking into account 
    * command line arguments, local and hom properties, and 
    * application defaults.
    * @param line the command line construct
    * @return the artifact reference
    */
    private static Artifact getDefaultImplementation( 
      File base, CommandLine line ) throws Exception
    {
        if( line.hasOption( "impl" ) )
        {
            String spec = line.getOptionValue( "impl" );
            return Artifact.createArtifact( spec );
        }

        //
        // check in ${user.dir}/merlin.properties and ${user.home}/merlin.properties
        // for a "merlin.implementation" property and use it if decleared
        //

        final String key = "merlin.implementation";
        String home = getLocalProperties( USER_HOME, MERLIN ).getProperty( key );
        String work = getLocalProperties( base, MERLIN ).getProperty( key, home);
        if( null != work )
        {
            return Artifact.createArtifact( work );
        }

        //
        // otherwise go with the defaults packaged with the jar file
        //
 
        Properties properties = createDefaultProperties();

        final String group = 
          properties.getProperty( Artifact.GROUP_KEY );
        final String name = 
          properties.getProperty( Artifact.NAME_KEY  );
        final String version = 
          properties.getProperty( Artifact.VERSION_KEY );

        return Artifact.createArtifact( group, name, version );
    }

   /**
    * Load the default implementation properties.
    * @return the implementation properties
    */
    private static Properties createDefaultProperties()
    {
        final String path = "merlin.implementation";
        return loadProperties( path );
    }

   /**
    * Load a properties file from a supplied resource name.
    * @path the resource path
    * @return the properties instance
    */
    private static Properties loadProperties( String path )
    {
        try
        {
            Properties properties = new Properties();
            ClassLoader classloader = Main.class.getClassLoader();
            InputStream input = classloader.getResourceAsStream( path );
            if( input == null ) 
            {
                final String error = 
                  "Missing resource: [" + path + "]";
                throw new Error( error );
            }
            properties.load( input );
            return properties;
        }
        catch ( Throwable e )
        {
            final String error = 
              "Internal error. " 
              + "Unable to locate the resource: merlin.implementation.";
            throw new IllegalArgumentException( error );
        }
    }

   /**
    * Print out version information to System.out.  This function is 
    * invoked in response to the inclusion of the -version switch on
    * the command line.
    *
    * @param cache the local system cache
    * @param artifact the merlin implementation artifact descriptor
    */
    private static void printVersionInfo( File cache, Artifact artifact )
    {
        try
        {
            Attributes attr = RepositoryUtils.getAttributes( cache, artifact );
            ArtifactDescriptor desc = new ArtifactDescriptor( attr );
            System.out.println( "\n  Implementation: " 
              + artifact.getGroup() 
              + ":" + artifact.getName() 
              + ";" + artifact.getVersion() 
              + " (" + desc.getBuild() + ")"
            );
        }
        catch( Throwable e )
        {
            System.out.println( "\nImplementation: " 
              + artifact.getGroup() 
              + ":" + artifact.getName() 
              + ";" + artifact.getVersion() );
        }
    }

   /**
    * Print out information to System.out detailing theb help options.
    */
    private static void printHelpInfo()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "merlin [block]", " ", CL_OPTIONS, "", true );
    }

   /**
    * Return the merlin system repository root directory taking into 
    * account the supplied command-line, and merlin.properties files in 
    * the current and home directories.
    *
    * @param line the command line construct
    * @return the merlin system root repository directory
    */
    private static File getMerlinSystemRepository( CommandLine line )
    {
        if( line.hasOption( "system" ) )
        {
            String system = line.getOptionValue( "system" );
            return new File( system );
        }
        else
        {
            return new File( getMerlinHome( ), "system" );
        }
    }

   /**
    * Return the merlin home directory.
    * @return the merlin install directory
    */
    private static File getMerlinHome()
    {
        return new File( getMerlinHomePath() );
    }

   /**
    * Return the merlin home directory path.
    * @return the merlin install directory path
    */
    private static String getMerlinHomePath()
    {
        try
        {
            String merlin = 
              System.getProperty( 
                "merlin.home", 
                Env.getEnvVariable( "MERLIN_HOME" ) );
            if( null != merlin ) return merlin;
            return System.getProperty( "user.home" ) 
              + File.separator + ".merlin";
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to access MERLIN_HOME environment.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

   /**
    * Return the functional base directory.  The implementation looks
    * for the ${merlin.dir} system property and if not found, looks for 
    * the ${basedir} system property, and as a last resort, returns the 
    * JVM ${user.dir} value.
    *
    * @return the merlin install directory
    */
    private static File getBaseDirectory()
    {
        final String merlin = System.getProperty( "merlin.dir" );
        if( null != merlin )
        {
            return new File( merlin );
        }
        final String base = System.getProperty( "basedir" );
        if( null != base )
        {
            return new File( base );
        }
        return new File( System.getProperty( "user.dir" ) );
    }

   /**
    * Return a property file from a fir with a supplied filename.
    * @param dir the directory
    * @param filename the filename
    * @return a possibly empty properties instance
    */
    private static Properties getLocalProperties( 
      File dir, String filename ) 
    {
        Properties properties = new Properties();
        if( null == dir ) return properties;
        File file = new File( dir, filename );
        if( !file.exists() ) return properties;
        try
        {
            properties.load( new FileInputStream( file ) );
            return properties;
        }
        catch( Throwable e )
        {
            final String error = 
              "Unexpected exception while attempting to read properties from: " 
              + file + ". Cause: " + e.toString();
            throw new IllegalStateException( error );
        }
    }
}
