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


package org.apache.avalon.merlin.impl;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.activation.appliance.BlockContext;
import org.apache.avalon.activation.appliance.Composite;
import org.apache.avalon.activation.appliance.impl.AbstractBlock;
import org.apache.avalon.activation.appliance.impl.DefaultServiceContext;

import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.data.ContainmentProfile;
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
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ModelFactory;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.ClassLoaderContext;
import org.apache.avalon.composition.model.ClassLoaderModel;
import org.apache.avalon.composition.model.ModelRepository;
import org.apache.avalon.composition.model.impl.DefaultSystemContext;
import org.apache.avalon.composition.model.impl.DefaultModelFactory;
import org.apache.avalon.composition.model.impl.DefaultContainmentContext;
import org.apache.avalon.composition.model.impl.DefaultContainmentModel;
import org.apache.avalon.composition.model.impl.DefaultClassLoaderModel;
import org.apache.avalon.composition.model.impl.DefaultClassLoaderContext;
import org.apache.avalon.composition.model.impl.DefaultModelRepository;
import org.apache.avalon.composition.util.StringHelper;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.avalon.merlin.Kernel;
import org.apache.avalon.merlin.KernelException;
import org.apache.avalon.merlin.KernelRuntimeException;
import org.apache.avalon.merlin.KernelCriteria;

import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.provider.CacheManager;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Factory;

import org.apache.excalibur.configuration.ConfigurationUtil;

import org.xml.sax.InputSource;


/**
 * The DefaultFactory provides support for the establishment of a 
 * new merlin kernel.
 */
public class DefaultFactory implements Factory
{
    //--------------------------------------------------------------------------
    // static
    //--------------------------------------------------------------------------

    private static Resources REZ =
        ResourceManager.getPackageResources( DefaultFactory.class );

    private static final XMLDeploymentProfileCreator CREATOR = 
      new XMLDeploymentProfileCreator();

    private static final XMLContainmentProfileCreator CONTAINER_CREATOR = 
      new XMLContainmentProfileCreator();

    private static final XMLTargetsCreator TARGETS = 
      new XMLTargetsCreator();

    //--------------------------------------------------------------------------
    // state
    //--------------------------------------------------------------------------

    private Logger m_logger;

    private LoggingManager m_logging;

    private InitialContext m_context;

    private ClassLoader m_classloader;

    private Block m_application;

    private Block m_system;

    //--------------------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------------------

   /**
    * Creation of a new default factory.
    * @param context the repository inital context
    * @param classloader the factory classloader
    */
    public DefaultFactory( InitialContext context, ClassLoader classloader )
    {
        m_context = context;
        m_classloader = classloader;
    }

    //--------------------------------------------------------------------------
    // Factory
    //--------------------------------------------------------------------------

   /**
    * Return of map containing the default parameters.
    *
    * @return the default parameters 
    */
    public Map createDefaultCriteria()
    {
        return new DefaultCriteria( m_context );
    }

   /**
    * Creation of a new kernel using the default criteria.
    *
    * @return the kernel instance
    * @exception Exception if an error occurs during root block establishment
    */
    public Object create() throws Exception
    {
        return create( createDefaultCriteria() );
    }

   /**
    * Creation of a new kernel using the supplied criteria.
    *
    * @param map the parameters 
    * @return the kernel instance
    * @exception Exception if an error occurs during kernel creation
    */
    public Object create( Map map ) throws Exception
    {
        if( null == map ) 
          throw new NullPointerException( "map" );

        KernelCriteria criteria = null;
        if( map instanceof KernelCriteria) 
        {
            criteria = (KernelCriteria) map;
        }
        else
        {
            final String error = 
             "Suppied map was not created by this factory. ";
            throw new IllegalArgumentException( error );
        }

        //
        // set the language code
        //

        String language = criteria.getLanguageCode();
        if( null != language )
        { 
            ResourceManager.clearResourceCache();
            Locale locale = new Locale( language, "" );
            Locale.setDefault( locale );
            REZ = 
              ResourceManager.getPackageResources( 
                DefaultFactory.class );
        }

        //
        // create the kernel configuration
        //

        URL kernelURL = (URL) criteria.getKernelURL();
        Configuration kernelConfig = getKernelConfiguration( kernelURL );
        String listing = ConfigurationUtil.list( kernelConfig );

        //
        // create the logging subsystem
        //

        Configuration loggingConfig = 
          kernelConfig.getChild( "logging" );
        LoggingDescriptor loggingDescriptor = 
          createLoggingDescriptor( loggingConfig );

        m_logging = 
          new DefaultLoggingManager( 
            criteria.getWorkingDirectory(), 
            loggingDescriptor, 
            criteria.isDebugEnabled() );

        m_logger = 
          m_logging.getLoggerForCategory( 
            loggingDescriptor.getName() );
        getLogger().debug( "logging system established" );

        if( criteria.isInfoEnabled() )
        {
            StringBuffer buffer = 
              new StringBuffer( REZ.getString( "info.listing" ) );

            buffer.append( "\n" );
            buffer.append( 
              "\n  ${user.dir} == " 
              + System.getProperty( "user.dir" ) );
            buffer.append( 
              "\n  ${user.home} == " 
              + System.getProperty( "user.home" ) );

            buffer.append( "\n" );
            buffer.append( "\n  ${avalon.repository.cache} == " 
              + m_context.getInitialCacheDirectory() );
            buffer.append( "\n  ${avalon.repository.hosts} == " );
            String[] hosts = m_context.getInitialHosts();
            for( int i=0; i<hosts.length; i++ )
            {
                if( i>0 ) buffer.append( "," );
                buffer.append( hosts[i] );
            }

            buffer.append( "\n" );

            buffer.append( 
              "\n  ${merlin.repository} == " 
              + criteria.getRepositoryDirectory() );

            buffer.append( 
              "\n  ${merlin.lang} == " 
              + criteria.getLanguageCode() );

            buffer.append( 
              "\n  ${merlin.home} == " 
              + criteria.getHomeDirectory() );

            buffer.append( 
              "\n  ${merlin.system} == " 
              + criteria.getSystemDirectory() );

            buffer.append( 
              "\n  ${merlin.config} == " 
              + criteria.getConfigDirectory() );

            buffer.append( 
              "\n  ${merlin.kernel} == " 
              + criteria.getKernelURL() );

            buffer.append( 
              "\n  ${merlin.override} == " 
              + criteria.getOverridePath() );

            buffer.append( 
              "\n  ${merlin.dir} == " 
              + criteria.getWorkingDirectory() );

            buffer.append( 
              "\n  ${merlin.temp} == " 
              + criteria.getTempDirectory() );

            buffer.append( 
              "\n  ${merlin.context} == " 
              + criteria.getContextDirectory() );

            buffer.append( 
              "\n  ${merlin.anchor} == " 
              + criteria.getAnchorDirectory() );

            buffer.append( 
              "\n  ${merlin.info} == " 
              + criteria.isInfoEnabled() );

            buffer.append( 
              "\n  ${merlin.debug} == " 
              + criteria.isDebugEnabled() );

            buffer.append( 
              "\n  ${merlin.server} == " 
              + criteria.isServerEnabled() );

            buffer.append( 
              "\n  ${merlin.autostart} == " 
              + criteria.isAutostartEnabled() );

            buffer.append( "\n  ${merlin.deployment} == " );
            URL[] urls = criteria.getDeploymentURLs();
            for( int i=0; i<urls.length; i++ )
            {   
                if( i>0 ) buffer.append( "," );  
                buffer.append( StringHelper.toString( urls[i] ) );
            }
            buffer.append( "\n" );
            getLogger().info( buffer.toString() );
        }

        //
        // create the common repository
        //

        Configuration repositoryConfig = 
          kernelConfig.getChild( "repository" );
        File cache = criteria.getRepositoryDirectory();
        CacheManager manager = 
          createCacheManager( m_context, cache, repositoryConfig );
        Repository repository = manager.createRepository();
        getLogger().debug( 
          "repository established: " + repository );

        //
        // create the system context
        //

        File anchor = criteria.getAnchorDirectory();

        DefaultSystemContext applicationContext = 
          new DefaultSystemContext( 
            m_logging,
            anchor,
            criteria.getContextDirectory(),
            criteria.getTempDirectory(),
            repository,
            loggingDescriptor.getName(),
            criteria.isDebugEnabled() );

        //
        // create the application model
        //

        getLogger().info( "building application model" );
        final Logger applicationLogger = m_logging.getLoggerForCategory("");
        ClassLoader api = applicationContext.getCommonClassLoader();
        ContainmentModel application = 
          new DefaultContainmentModel(
            createContainmentContext( 
              applicationContext, applicationLogger, api,
              getContainmentProfile( 
                kernelConfig.getChild( "container" ) ) ) );

        //
        // create the system model and add the application model
        // as an available system context entry
        //

        getLogger().info( "facilities deployment" );

        Configuration facilities = 
          kernelConfig.getChild( "system" );

        DefaultSystemContext systemContext = 
          new DefaultSystemContext( 
            m_logging,
            anchor,
            criteria.getContextDirectory(),
            criteria.getTempDirectory(),
            repository,
            loggingDescriptor.getName(),
            criteria.isDebugEnabled() );

        systemContext.put( "urn:merlin:dir", criteria.getWorkingDirectory() );
        systemContext.put( "urn:merlin:anchor", criteria.getAnchorDirectory() );
        systemContext.put( "urn:merlin:model", application );

        systemContext.makeReadOnly();

        ClassLoader spi = BlockContext.class.getClassLoader();
        final Logger systemLogger = getLogger();
        ContainmentModel system = 
          new DefaultContainmentModel(
            createContainmentContext( 
              systemContext, systemLogger, spi,
              getContainmentProfile( facilities ) ) );

        //
        // TODO: now that the system context is established we 
        // need to create a block and supply the system block
        // (perhaps under a service manager) to the new model
        // model factory to ensure that new system services 
        // established under system scope override the bootstrap 
        // facilities
        //

        DefaultServiceContext services = new DefaultServiceContext();
        services.put( LoggingManager.KEY, m_logging );

        try
        {
            m_system = AbstractBlock.createRootBlock( services, system );
            if( m_system instanceof Composite )
            {
                try
                {
                    getLogger().debug( "system assembly" );
                    ((Composite)m_system).assemble();
                }
                catch( Throwable e )
                {
                    final String error = 
                      "Facilities assembly failure.";
                    throw new KernelException( error, e );
                }
            }
        }
        catch( Throwable e )
        {
            final String error = 
              "Facilities composition failure.";
            throw new KernelException( error, e );
        }

        try
        {
            getLogger().debug( "system deployment" );
            m_system.deploy();
            getLogger().debug( "system ready" );
        }
        catch( Throwable e )
        {
            final String error = 
              "System deployment failure.";
            throw new KernelException( error, e );
        }

        //
        // install any blocks declared within the kernel context
        //

        getLogger().info( "block installation" );
        getLogger().debug( "install phase" );
        URL[] urls = criteria.getDeploymentURLs();
        for( int i=0; i<urls.length; i++ )
        {
            URL url = urls[i];

            if( getLogger().isInfoEnabled() )
            {
                getLogger().info( 
                  "installing: " 
                  + StringHelper.toString( url ) );
            }

            try
            {
                application.addModel( url );
            }
            catch( Throwable e )
            {
                final String error = 
                  "Install failure: " + url;
                throw new KernelException( error, e );
            }
        }

        //
        // customize the meta model
        //

        getLogger().debug( "customize phase" );
        try
        {
            final String path = criteria.getOverridePath();
            if( null != path ) 
            {
                File base = criteria.getWorkingDirectory();
                URL url = resolveURL( base, path );
                application.applyTargets( url );
            }
        }
        catch( Throwable e )
        {
            final String error = 
              "Target override assignment failure.";
            throw new KernelException( error, e );
        }

        //
        // instantiate the runtime root application block
        //
        getLogger().info( "deployment" );

        getLogger().debug( "activation phase" );
        try
        {
            m_application = 
              AbstractBlock.createRootBlock( services, application );
        }
        catch( Throwable e )
        {
            final String error = 
              "Composition failure.";
            throw new KernelException( error, e );
        }

        //
        // instantiate the kernel
        //

        Kernel kernel = 
          createKernel( getLogger(), criteria, m_system, m_application );
        setShutdownHook( getLogger(), kernel );
        if( criteria.isAutostartEnabled() )
        {
            getLogger().debug( "startup phase" );
            try
            {
                kernel.startup();
            }
            catch( Throwable e )
            {
                final String error = 
                  "Kernel startup failure.";
                throw new KernelException( error, e );
            }

            if( !criteria.isServerEnabled() )
            {
                getLogger().debug( "shutdown phase" );
                try
                {
                    kernel.shutdown();
                }
                catch( Throwable e )
                {
                    final String error = 
                      "Kernel startup failure.";
                    throw new KernelException( error, e );
                }
            }
        }
        else
        {
            getLogger().debug( "autostart disabled" );
        }

        return kernel;
    }

    private Kernel createKernel(
      Logger logger, KernelCriteria criteria, Block system, Block application )
      throws KernelException
    {
        try
        {
            return new DefaultKernel( 
              logger, criteria, system, application );
        }
        catch( Throwable e )
        {
            final String error = 
              "Kernel establishment failure.";
            throw new KernelException( error, e );
        }
    }

   /**
    * Creation of a new root containment context.
    *
    * @param profile a containment profile 
    * @return the containment context
    */
    private ContainmentContext createContainmentContext( 
      SystemContext context, Logger logger, ClassLoader parent, 
      ContainmentProfile profile ) 
      throws Exception
    {
        LoggingManager logging = context.getLoggingManager();
        logging.addCategories( profile.getCategories() );

        ClassLoaderContext classLoaderContext =
          new DefaultClassLoaderContext( 
            logger, 
            context.getRepository(),
            context.getBaseDirectory(),
            parent,
            profile.getClassLoaderDirective() );

        ClassLoaderModel classLoaderModel =
          new DefaultClassLoaderModel( classLoaderContext );

        return new DefaultContainmentContext( 
          logger, 
          context, 
          classLoaderModel,
          null,
          null,
          profile );
    }

    private ContainmentProfile getContainmentProfile( Configuration config )
      throws KernelException
    {
        try
        {
            return CONTAINER_CREATOR.createContainmentProfile( config );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while build a containment profile."
              + ConfigurationUtil.list( config );
            throw new KernelException( error, e );
        }
    }

    private CacheManager createCacheManager( 
      InitialContext context, File root, Configuration config ) 
      throws KernelException
    {

        //
        // the supplied root argument is the root cache resolved relative
        // to system properties and environment variables.  This value is 
        // overriden if a cache is declared in the kernel repository 
        // configuration
        //

        File cache = getCacheDirectory( root, config.getChild( "cache" ) );
        String[] hosts = getHosts( config.getChild( "hosts" ) );

        try
        {
            Factory factory = context.getInitialFactory();
            Map criteria = factory.createDefaultCriteria();
            criteria.put( "avalon.repository.cache", cache );
            criteria.put( "avalon.repository.hosts", hosts );

            Configuration proxyConfig = config.getChild( "proxy", false );
            if( null != proxyConfig )
            {
                final String host = 
                  proxyConfig.getChild( "host" ).getValue( null );
                criteria.put( "avalon.repository.proxy.host", hosts );

                final int port = 
                  proxyConfig.getChild( "port" ).getValueAsInteger( 0 );
                criteria.put( "avalon.repository.proxy.port", new Integer( port ) );

                Configuration credentials = 
                  proxyConfig.getChild( "credentials", false );
                if( credentials != null )
                {
                    final String username = 
                      credentials.getChild( "username" ).getValue( null );
                    if( username == null )
                    {
                        final String error =
                          "Credentials configuration does not contain the required 'username' element."
                          + ConfigurationUtil.list( credentials );
                        throw new KernelException( error );                
                    }
                    else
                    {
                        criteria.put( "avalon.repository.proxy.username", username );
                    }

                    final String password = 
                        credentials.getChild( "password" ).getValue( null );
                    if( password == null )
                    {
                        final String error =
                          "Credentials configuration does not contain the required 'password' element."
                          + ConfigurationUtil.list( credentials );
                        throw new KernelException( error );                
                    }
                    else
                    {
                        criteria.put( "avalon.repository.proxy.password", password );
                    }
                }
            }

            return (CacheManager) factory.create( criteria );
        }
        catch ( Throwable e )
        {
            final String error = 
              "Internal error while attempting to create the common repository "
              + " using the supplied cache: [" + cache + "].";
            throw new KernelException( error, e );
        }
    }

    private File getCacheDirectory( File cache, Configuration config )
    {
        String directive = config.getValue( null );
        if( null == directive ) return cache;
        return new File( directive );  
    }

    private String[] getHosts( Configuration config ) throws KernelException
    {
        ArrayList list = new ArrayList();
        Configuration[] hosts = config.getChildren( "host" );
        for( int i=0; i<hosts.length; i++ )
        {
            Configuration host = hosts[i];
            String path = host.getAttribute( "path", null );
            if( path == null )
            {      
                final String error = 
                  "Missing host element value: " 
                + ConfigurationUtil.list( host );
                throw new KernelException( error );
            }
            else
            {
                list.add( path );
            }
        }
        return (String[]) list.toArray( new String[0] );
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
    * Create the kernel configuration using a supplied url.  If the supplied
    * url is null the implementation will attempt to resolve a "/kernel.xml" 
    * resource within the deployment unit.
    *
    * @param url the location of the kernel confiuration
    * @return the kernel configuration
    * @exception if the configuration could not be resolved
    */
    private Configuration getKernelConfiguration( URL url ) throws Exception
    {
        if( null != url )
        {
            
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
        else
        {
            try
            {
                final InputStream stream =
                DefaultFactory.class.getClassLoader().getResourceAsStream( 
                  "kernel.xml" );
                final InputSource source = new InputSource( stream );
                DefaultConfigurationBuilder builder = 
                  new DefaultConfigurationBuilder();
                return builder.build( source );
            }
            catch( Throwable ee )
            {
                final String error = 
                  "Internal error while attempting to build default kernel "
                  + "configuration from the kernel spec: " + url;
                throw new KernelException( error, ee );
            }
        }
    }

    private TargetDirective[] getTargetOverrides( KernelCriteria criteria )
      throws KernelException
    {
        final String path = criteria.getOverridePath();
        if( null == path ) return new TargetDirective[0];
        File base = criteria.getWorkingDirectory();
        URL url = resolveURL( base, path );

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

    private URL resolveURL( File base, String path )
    {
        try
        {
            if( path.indexOf( ":/" ) > 0 )
            {
                return new URL( path );
            }
            else
            {
                File absolute = new File( path );
                if( absolute.isAbsolute() ) return absolute.toURL();
                return 
                  new File( 
                    base, path ).getCanonicalFile().toURL();      
            }
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to convert the supplied path [" 
              + path + "] to a url.";
            throw new KernelRuntimeException( error, e );
        }
    }

    private Logger getLogger()
    {
        return m_logger;
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


   /**
    * Create a shutdown hook that will trigger shutdown of the supplied kernel.
    * @param kernel the kernel to be shutdown
    */
    private void setShutdownHook( final Logger logger, final Kernel kernel )
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
                      logger.debug("shutdown event");
                      kernel.shutdown();
                  }
                  catch( Throwable e )
                  {
                      // ignore it
                  }
                  finally
                  {
                      System.runFinalization();
                  }
              }
          }
        );
    }

}
