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

package org.apache.avalon.merlin.kernel.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.avalon.composition.data.CategoryDirective;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.Targets;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.data.builder.XMLTargetsCreator;
import org.apache.avalon.composition.data.builder.XMLDeploymentProfileCreator;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.logging.LoggingDescriptor;
import org.apache.avalon.composition.logging.TargetDescriptor;
import org.apache.avalon.composition.logging.TargetProvider;
import org.apache.avalon.composition.logging.impl.DefaultLoggingManager;
import org.apache.avalon.composition.logging.impl.FileTargetProvider;
import org.apache.avalon.composition.model.ModelFactory;
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.avalon.composition.model.impl.DefaultContainmentContext;
import org.apache.avalon.composition.model.impl.DefaultSystemContext;
import org.apache.avalon.composition.model.impl.DefaultModelFactory;
import org.apache.avalon.composition.util.StringHelper;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.merlin.kernel.KernelContext;
import org.apache.avalon.merlin.kernel.KernelException;
import org.apache.avalon.merlin.kernel.KernelRuntimeException;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.ProxyContext;
import org.apache.avalon.repository.impl.DefaultAuthenticator;
import org.apache.avalon.repository.impl.DefaultFileRepository;
import org.apache.excalibur.configuration.ConfigurationUtil;
import org.apache.excalibur.mpool.PoolManager;

import org.xml.sax.InputSource;

/**
 * Default implementation of a kernel context.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2003/10/12 17:12:45 $
 */
public class DefaultKernelContext extends AbstractLogEnabled 
  implements KernelContext
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    public static final String PRODUCT = "Merlin SMP";

    public static final String VERSION = "3.0";

    private static final XMLContainmentProfileCreator CONTAINER_CREATOR = 
      new XMLContainmentProfileCreator();

    private static final XMLDeploymentProfileCreator CREATOR = 
      new XMLDeploymentProfileCreator();

    private static final XMLTargetsCreator TARGETS = 
      new XMLTargetsCreator();

    private static final String CATEGORY_NAME = "context";

    private static final String USER_DIR = 
      System.getProperty( "user.dir" ).replace( '\\', '/' );

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    /**
     * The system repository.
     */
    private final Repository m_repository;

    /**
     * The runtime user repository path
     */
    private final File m_user;

    /**
     * The library path
     */
    private final File m_library;

    /**
     * The home path
     */
    private final File m_home;

    /**
     * The temp path
     */
    private final File m_temp;

    /**
     * The kernel configuration url.
     */
    private String m_kernelURL;

    /**
     * The block url.
     */
    private final URL[] m_blocks;

    /**
     * The block configuration override.
     */
    private final URL m_config;

    /**
     * The debug flag.
     */
    private final boolean m_debug;

    /**
     * The server flag.
     */
    private final boolean m_server;

   /**
    * The logging manager.
    */
    private final LoggingManager m_logging;

   /**
    * The pool manager.
    */
    private final PoolManager m_pool;

   /**
    * The model factory.
    */
    private ModelFactory m_factory;

    /**
     * The custom targets.
     */
    private TargetDirective[] m_targets;

    private final Logger m_kernelLogger;

    private final ContainmentContext m_root;

    private final String m_bootstrap;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new default kernel context.
    * @param bootstrap the system repository
    * @param user the user repository directory
    * @param library directory against which extension jar 
    *   directives shall be anchored
    * @param home local working directory
    * @param kernel the kernel configuration URL - if null, the 
    *   default kernel profile will be loaded from the bootstrap repository
    * @param blocks a sequence of block urls
    * @param config a url to a configuration override descriptor
    * @param server server mode flag
    * @param debug debug flag
    * @exception NullPointerException if the supplied bootstrap repository is null
    * @exception KernelException if an error occurs during context creation
    */
    public DefaultKernelContext( 
      Repository bootstrap, File user, File library, File home, 
      URL kernel, URL[] blocks, URL config, boolean server, boolean info, boolean debug )
      throws NullPointerException, KernelException
    {
        if( bootstrap == null ) throw new NullPointerException( "bootstrap" );

        m_bootstrap = bootstrap.getLocation();
        m_temp = new File( System.getProperty( "java.io.tmpdir" ) );
        final File base = new File( System.getProperty( "user.dir" ) );

        if( home != null )
        {
            m_home = home;
        }
        else
        {
            m_home = new File( base, "home" );
        }

        if( library != null )
        {
            m_library = library;
        }
        else
        {
            m_library = base;
        }

        if( user != null )
        {
            m_user = user;
        }
        else
        {
            m_user = getMerlinLocalRepositoryDirectory();
        }

        Configuration kernelConfig = null;
        if( kernel != null )
        {

            //
            // a kernel defintion has been supplied
            //

            m_kernelURL = kernel.toString();
            kernelConfig = getKernelConfiguration( kernel );
        }
        else
        {
            //
            // use the kernel.xml file in ${merlin.home}/config/kernel.xml
            //

            String kernelURL = null;
            File standard = new File( getMerlinHomeDirectory(), "config/kernel.xml" );
            if( standard.exists() )
            {
                try
                {
                    m_kernelURL = standard.toURL().toString();
                    kernelConfig = getKernelConfiguration( standard.toURL() );
                }
                catch( Throwable ee )
                {
                    final String error = 
                      "Unable to resolve kernel profile: " + standard;
                    throw new KernelException( error, ee );
                }
            }
            else
            {
                //
                // umm, making things hard for us - try a locate a kernel from 
                // system repository
                //

                try
                {
                    URL url = loadKernelDirective( bootstrap );
                    kernelURL = url.toString();
                    kernelConfig = getKernelConfiguration( url );
                }
                catch( Throwable e )
                {
                    //
                    // last resort - get the static default from the 
                    // jar file
                    //

                    kernelURL = "resource:/kernel.xml";
                    try
                    {
                        final InputStream stream =
                          DefaultKernelContext.class.getClassLoader().
                            getResourceAsStream( "kernel.xml" );
                        final InputSource source = 
                          new InputSource( stream );
                        DefaultConfigurationBuilder builder = 
                          new DefaultConfigurationBuilder();
                        kernelConfig = builder.build( source );
                    }
                    catch( Throwable ee )
                    {
                       final String error = 
                          "Unable to resolve kernel profile.";
                        throw new KernelException( error, ee );
                    }
                }
            }
            m_kernelURL = kernelURL;
        }

        m_debug = debug;
        m_server = server;
        m_blocks = blocks;
        m_config = config;

        if( m_config == null )
        {
            m_targets = new TargetDirective[0];
        }
        else
        {
            m_targets = getTargets( m_config );
        }

        //
        // prepare the logging subsystem using on the kernel configuration
        // as the directive source
        //

        Configuration conf = kernelConfig.getChild( "logging" );
        LoggingDescriptor logging = createLoggingDescriptor( conf );
        m_logging = bootstrapLoggingManager( m_home, logging, debug );
        m_kernelLogger = m_logging.getLoggerForCategory( logging.getName() );
        enableLogging( getKernelLogger().getChildLogger( CATEGORY_NAME ) );
        getLogger().debug( "logging system established" );

        //
        // if the kernel configuration declares a repository then we build
        // a repository based on that defintion otherwise we default to the 
        // standard user repository
        //

        Configuration repositoryConfig = kernelConfig.getChild( "repository" );
        m_repository = createRepository( m_user, repositoryConfig );
        getLogger().debug( "repository established: " + m_repository.getLocation() );

        //
        // if the debug flag is enabled then print the context object
        //

        if( info )
        {
            System.out.println( "\n" + this.toString() + "\n" );
        }

        //
        // setup the pool manager
        // TODO: an implementation following more thinking (pools should
        // probably be declared at the component directive level)
        //

        m_pool = null;

        //
        // setup the model factory
        //

        try
        {
            m_factory = new DefaultModelFactory( 
              new DefaultSystemContext( 
                getLoggingManager(),
                getLibraryPath(),
                getHomePath(),
                getTempPath(),
                getRepository(),
                logging.getName(),
                debug ) );
            getLogger().debug( "model factory established" );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unexpected exception while creating internal model factory.";
            throw new KernelException( error, e );
        }

        //
        // create the root containment context
        //

        try
        {
            final ContainmentProfile profile = 
              CONTAINER_CREATOR.createContainmentProfile( 
                kernelConfig.getChild( "container" ) );
            m_root = m_factory.createContainmentContext( profile );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while build default containment context.";
            throw new KernelException( error, e );
        }
    }

    private Repository createRepository( File root, Configuration config ) 
      throws KernelException
    {
        File base = null; 
        String cache = config.getChild( "cache" ).getValue( null ); 
        if( cache != null ) 
        { 
            base = new File( cache ); 
            getLogger().debug( "setting runtime repository cache: " + base ); 
        } 
        else 
        { 
            base = root;
            getLogger().debug( "setting runtime repository to: " + base ); 
        } 

        if( !base.exists() ) base.mkdirs();

        //
        // set the remote repository urls
        //

        final Configuration[] hosts = config.getChild( "hosts" ).getChildren( "host" );
        final URL[] list = new URL[ hosts.length ];
        for( int i=0; i<hosts.length; i++ )
        {
            Configuration host = hosts[i];
            try
            {
                String path = host.getAttribute( "path", null );
                if( path == null )
                {
                    if( host.getValue( null ) != null )
                    {
                        path = host.getValue( null );
                        final String warning = 
                          "\n#"
                          + "\n# WARNING:" + path
                          + "\n# The kernel file is using a depricated <host> format. "
                          + "\n# Please replace all <host>...</host> references "
                          + "\n# with <host path=\"...\"/>."
                          + "\n#"
                          + "\n# Source kernel defintion: " + m_kernelURL
                          + "\n#"
                          + ConfigurationUtil.list( host );
                        getLogger().warn( warning );
                    }
                    else
                    {
                        final String error = 
                          "Missing host path attribute."
                          + ConfigurationUtil.list( host );
                        throw new KernelException( error );
                    }
                }
                if( !path.endsWith( "/" ) )
                {
                    path = path + "/";
                }
                URL url = new URL( path );
                final String protocol = url.getProtocol();
                if( url.getProtocol().equals( "http" ) )
                { 
                    list[i] = url;
                }
                else
                {
                    final String error = 
                      "Unsupported protocol: " + protocol;
                    throw new KernelException( error );
                }
            }
            catch( Throwable e )
            {
                final String error =
                  "Invalid host declaration: " 
                  + ConfigurationUtil.list( host );
                throw new KernelException( error, e );
            }
        }

        //
        // handle the proxy declaration
        //

        ProxyContext proxy = null;
        Configuration proxyConfig = config.getChild( "proxy", false );
        if( proxyConfig != null )
        {
            DefaultAuthenticator authenticator = null;
            Configuration credentials = proxyConfig.getChild( "credentials", false );
            if( credentials != null )
            {
                final String username = credentials.getChild( "username" ).getValue( null );
                if( username == null )
                {
                    final String error =
                      "Credentials configuration does not contain the required 'username' element."
                      + ConfigurationUtil.list( credentials );
                    throw new KernelException( error );                
                }

                final String password = credentials.getChild( "password" ).getValue( null );
                if( password == null )
                {
                    final String error =
                      "Credentials configuration does not contain the required 'password' element."
                      + ConfigurationUtil.list( credentials );
                    throw new KernelException( error );                
                }
                authenticator = new DefaultAuthenticator( username, password );
            }

            final String host = proxyConfig.getChild( "host" ).getValue( null );
            final int port = proxyConfig.getChild( "port" ).getValueAsInteger( 0 );
            if( host == null )
            {
                final String error =
                  "Proxy configuration does not contain the required 'host' element."
                  + ConfigurationUtil.list( proxyConfig );
                throw new KernelException( error );                
            }
            getLogger().debug( "repository proxy: " + host + ":" + port );
            proxy = new ProxyContext( host, port, authenticator );
        }

        return new DefaultFileRepository( base, proxy, list );
    }

    //--------------------------------------------------------------
    // KernelContext
    //--------------------------------------------------------------

   /**
    * Return the Logger for the specified category.
    * @param category the category path
    * @return the logging channel
    */
    public Logger getLoggerForCategory( final String category )
    {
        return m_logging.getLoggerForCategory( category );
    }

   /**
    * Return the kernel logging channel.
    * @return the kernel logging channel
    */
    public Logger getKernelLogger()
    {
        return m_kernelLogger;
    }

   /**
    * Return the model factory.
    * @return the factory
    */
    public ModelFactory getModelFactory()
    {
        return m_factory;
    }

   /**
    * Return the runtime repository.
    * @return the repository
    */
    public Repository getRepository()
    {
        return m_repository;
    }

   /**
    * Return the library path
    * @return the path (possibly null)
    */
    public File getLibraryPath()
    {
        return m_library;
    }

   /**
    * Return the home path
    * @return the path
    */
    public File getHomePath()
    {
        return m_home;
    }

   /**
    * Return the temporary directory path
    * @return the path (possibly null)
    */
    public File getTempPath()
    {
        return m_temp;
    }

   /**
    * Return the root containment context.
    * @return the kernel directive url
    */
    public ContainmentContext getContainmentContext()
    {
        return m_root;
    }

   /**
    * Return the URLs to install into the kerenel on startup.
    * @return the block directive urls
    */
    public URL[] getInstallSequence()
    {
        return m_blocks;
    }

   /**
    * Return the URL for the block configuration override directive.
    * @return the configuration override directive url
    */
    private URL getOverrideDirective()
    {
        return m_config;
    }

   /**
    * Return the kernel debug flag.
    * @return the debug flag
    */
    public boolean getDebugFlag()
    {
        return m_debug;
    }

   /**
    * Return the kernel server flag.
    * @return the server flag
    */
    public boolean getServerFlag()
    {
        return m_server;
    }

   /**
    * Return the logging manager for the kernel.
    * @return the logging manager
    */
    public LoggingManager getLoggingManager()
    {
        return m_logging;
    }

   /**
    * Return the kernel pool manager.
    * @return the pool manager
    */
    public PoolManager getPoolManager()
    {
        return m_pool;
    }

   /**
    * Return the set of target overrides.
    * @return the target override directives
    */
    public TargetDirective[] getTargetDirectives()
    {
        return m_targets;
    }

   /**
    * Return the runtime repository directory.
    * @return the repository path
    */
    public String getBootstrapRepositoryPath()
    {
        return m_bootstrap;
    }

    //--------------------------------------------------------------
    // implementation
    //--------------------------------------------------------------

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "  ${user.dir}: " + System.getProperty( "user.dir" ) );
        buffer.append( "\n  ${merlin.home}: " + getMerlinHomeDirectory() );
        buffer.append( "\n  Version: " + getVersionString() );
        buffer.append( "\n  Build: " + getBuildSignature() );
        buffer.append( "\n  Environment: " 
              + System.getProperty( "os.name" ) + " "
              + System.getProperty( "os.version" ) + " Java " 
              + System.getProperty( "java.version" ) );
        buffer.append( "\n  Deployment Home: " + StringHelper.toString( getHomePath() ) );
        buffer.append( "\n  System Repository: " + getBootstrapRepositoryPath() );
        buffer.append( "\n  Runtime Repository: " + getRepository().getLocation() );
        buffer.append( "\n  Library Anchor: " + StringHelper.toString( getLibraryPath() ) );
        buffer.append( "\n  Kernel Path: " + StringHelper.toString( m_kernelURL ) );
        buffer.append( "\n  Deployment Blocks: " + StringHelper.toString( m_blocks ) );
        buffer.append( "\n  Override Path: " );
        if( getOverrideDirective() != null )
        {
            buffer.append( StringHelper.toString( getOverrideDirective() ) );
        }
        buffer.append( "\n  Server Flag: " + getServerFlag() );
        buffer.append( "\n  Debug Flag: " + getDebugFlag() );
        return buffer.toString();
    }

   /**
    * Return a string representation of the product name and version.
    * @return the product and version string
    */
    private static String getVersionString()
    {
         return PRODUCT + " " + VERSION;
    }

    private Configuration getKernelConfiguration( final URL url )
      throws KernelException
    {
        if( url == null )
        {
           return new DefaultConfiguration( 
             "kernel", DefaultKernelContext.class.getName() );
        }

        try
        {
            DefaultConfigurationBuilder builder = 
              new DefaultConfigurationBuilder();
            return builder.build( url.toString() );
        }
        catch( Throwable e )
        {
            final String error = 
              "Could not load the kernel directive: " + url;
            throw new KernelException( error, e );
        }
    }

    private TargetDirective[] getTargets( final URL url )
      throws KernelException
    {
        try
        {
            DefaultConfigurationBuilder builder = 
              new DefaultConfigurationBuilder();
            Configuration config = builder.build( url.toString() );
            return TARGETS.createTargets( config ).getTargets();
        }
        catch( Throwable e )
        {
            final String error = 
              "Could not load the targets directive: " + url;
            throw new KernelException( error, e );
        }
        
    }

   /**
    * Creation of the bootstrap logging manager.
    *
    * @param base the base directory for file logging targets
    * @param descriptor  the logging descriptor
    * @return the logging manager
    * @exception Exception if a logging manager establishment error occurs
    */
    private LoggingManager bootstrapLoggingManager(
       File base, LoggingDescriptor descriptor, boolean debug )
       throws KernelException
    {
        try
        {
            return new DefaultLoggingManager( base, descriptor, debug );
        }
        catch( Throwable e )
        {
            final String error =
              "Internal error while bootstrapping the logging subsystem.";
            throw new KernelException( error, e );
        }
    }

    /**
     * Utility method to create a new logging descriptor from a
     * configuration instance.
     * @param config a configuration defining the logging descriptor
     * @return the logging descriptor
     * @exception ConfigurationException if the configuration is
     *   incomplete
     */
    private LoggingDescriptor createLoggingDescriptor(
        Configuration config )
        throws KernelException
    {
        final String name = config.getAttribute( "name", "kernel" );
        CategoriesDirective categories = null;
        try
        {
            categories = CREATOR.getCategoriesDirective( config, name );
        }
        catch( Throwable e )
        {
            final String error =
              "Invalid logging directive.";
            throw new KernelException( error, e );
        }

        //
        // create any custom targets declared in the kernel directive
        //

        ArrayList list = new ArrayList();
        Configuration[] configs = config.getChildren( "target" );
        for( int i = 0; i < configs.length; i++ )
        {
            Configuration c = configs[ i ];
            try
            {
                list.add( createTargetDescriptor( c ) );
            }
            catch( Throwable e )
            {
                final String error = 
                  "Invalid target descriptor.";
                throw new KernelException( error, e );
            }
        }

        TargetDescriptor[] targets =
            (TargetDescriptor[])list.toArray(
                new TargetDescriptor[ 0 ] );

        //
        // return the logging descriptor
        //

        return new LoggingDescriptor(
          categories.getName(), 
          categories.getPriority(), 
          categories.getTarget(), 
          categories.getCategories(), 
          targets );
    }

    /**
     * Utility method to create a new target descriptor from a
     * configuration instance.
     * @param config a configuration defining the target descriptor
     * @return the logging target descriptor
     * @exception ConfigurationException if the configuration is
     *   incomplete
     */
    private TargetDescriptor createTargetDescriptor( Configuration config )
        throws ConfigurationException
    {
        final String name = config.getAttribute( "name" );
        if( config.getChildren().length == 0 )
        {
            throw new ConfigurationException(
                "missing target provider element in '"
                + config.getName() + "'." );
        }

        final Configuration c = config.getChildren()[ 0 ];
        TargetProvider provider = null;
        if( c.getName().equals( "file" ) )
        {
            provider = createFileTargetProvider( c );
        }
        else
        {
            throw new ConfigurationException(
                "Unrecognized provider: " + c.getName() + " in " + config.getName() );
        }
        return new TargetDescriptor( name, provider );
    }

    /**
     * Utility method to create a new file target descriptor from a
     * configuration instance.
     * @param config a configuration defining the file target descriptor
     * @return the file target descriptor
     * @exception ConfigurationException if the configuration is
     *   incomplete
     */
    private FileTargetProvider createFileTargetProvider( Configuration config )
        throws ConfigurationException
    {
        String file = config.getAttribute( "location" );
        return new FileTargetProvider( file );
    }

    private URL loadKernelDirective( Repository repository ) throws Exception
    {
        return repository.getArtifact( "merlin", "kernel", "", "xml" );
    }

   /**
    * If the user has declare a local repository then return that (based on the
    * merlin.local.repository sytem property, otherwise return the merlin.home
    * repository directory.
    *
    * @return the local repository directory
    */
    public static File getMerlinLocalRepositoryDirectory()
    {
        final String local = System.getProperty( "merlin.local.repository" );
        if( local != null )
        {
            return new File( local );
        }
        else
        {
            return new File( getMerlinHomeDirectory(), "repository" );
        }
    }

   /**
    * Return the merlin.home directory.  If undefined, return the 
    * ${user.home}/.merlin directory.
    *
    * @return the merlin home directory
    */
    public static File getMerlinHomeDirectory()
    {
        final String home = System.getProperty( "merlin.home" );
        if( home != null )
        {
            return new File( home );
        }
        else
        {
            File root = new File( System.getProperty( "user.home" ) );
            return new File( root, ".merlin" );
        }
    }

    private static String getBuildSignature()
    {
        try
        {
            final ClassLoader classloader = DefaultKernelContext.class.getClassLoader();
            final InputStream input = 
              classloader.getResourceAsStream( "snapshot.properties"  );
            if( input == null ) return "ERROR-MISSING-PROPERTIES: snapshot.properties";
            final Properties properties = new Properties();
            properties.load( input );
            final String snapshot = properties.getProperty( "build.signature" );
            if( snapshot == null ) return "ERROR-MISSING-PROPERTY: build.signature";
            final int start = snapshot.lastIndexOf("-");
            final int end = snapshot.lastIndexOf(".");
            return snapshot.substring( start + 1, end );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to build the build signature.";
            throw new KernelRuntimeException( error, e );
        }
    }
}
