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


package org.apache.avalon.merlin.impl;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.activation.appliance.impl.AbstractBlock;

import org.apache.avalon.logging.data.CategoriesDirective;
import org.apache.avalon.logging.provider.LoggingManager;
import org.apache.avalon.logging.provider.LoggingCriteria;

import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.data.builder.XMLTargetsCreator;
import org.apache.avalon.composition.data.builder.XMLComponentProfileCreator;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.ClassLoaderContext;
import org.apache.avalon.composition.model.ClassLoaderModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ModelRepository;
import org.apache.avalon.composition.model.impl.DefaultSystemContext;
import org.apache.avalon.composition.model.impl.DelegatingSystemContext;
import org.apache.avalon.composition.model.impl.DefaultContainmentContext;
import org.apache.avalon.composition.model.impl.DefaultContainmentModel;
import org.apache.avalon.composition.model.impl.DefaultClassLoaderModel;
import org.apache.avalon.composition.model.impl.DefaultClassLoaderContext;
import org.apache.avalon.composition.model.impl.DefaultModelRepository;
import org.apache.avalon.composition.util.StringHelper;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.activity.Disposable;

import org.apache.avalon.merlin.Kernel;
import org.apache.avalon.merlin.KernelException;
import org.apache.avalon.merlin.KernelRuntimeException;
import org.apache.avalon.merlin.KernelCriteria;
import org.apache.avalon.merlin.KernelContext;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.provider.Builder;
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

    private static final String LINE = InitialContext.LINE;

    private static final XMLComponentProfileCreator CREATOR = 
      new XMLComponentProfileCreator();

    private static final XMLContainmentProfileCreator CONTAINER_CREATOR = 
      new XMLContainmentProfileCreator();

    private static final XMLTargetsCreator TARGETS = 
      new XMLTargetsCreator();


    //--------------------------------------------------------------------------
    // state
    //--------------------------------------------------------------------------

    private Logger m_logger;

    private InitialContext m_context;

    private ClassLoader m_classloader;

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
        if( map instanceof KernelCriteria ) 
        {
            criteria = (KernelCriteria) map;
        }
        else
        {
            final String error = 
             "Suppied map was not created by this factory. ";
            throw new IllegalArgumentException( error );
        }

        setupLanguageCode( criteria );

        //
        // create the kernel configuration
        //

        URL kernelURL = (URL) criteria.getKernelURL();
        Configuration config = getKernelConfiguration( kernelURL );

        //
        // create the logging subsystem
        //

        LoggingManager logging = 
          createLoggingManager( criteria, config );

        m_logger = 
          logging.getLoggerForCategory( "kernel" );
        getLogger().debug( "logging system established" );

        //
        // Create the system context.
        //

        String[] hosts = 
          getHosts( 
            config.getChild( "repository" ).getChild( "hosts" ) );

        SystemContext systemContext = 
          createSystemContext( 
            m_context, criteria, hosts, logging, config, "kernel" );

        //
        // with the logging system established, check if the 
        // info listing mode is set and if so, generate a report
        // of the current parameters
        //

        if( criteria.isInfoEnabled() )
        {
            StringBuffer buffer = new StringBuffer( "info report" );
            buffer.append( LINE );
            buffer.append( "\n" );
            buffer.append( REZ.getString( "info.listing" ) );
            buffer.append( LINE );
            createInfoListing( buffer, hosts, m_context, criteria );
            buffer.append( "\n" );
            buffer.append( LINE );
            getLogger().info( buffer.toString() );
        }

        //
        // Create the application model.  Normally the application 
        // model is empty and we will not get any errors in this 
        // process.  Development errors will normally occur when 
        // adding block directives to the application model.
        //

        Configuration appConfig = 
          config.getChild( "container" );
        ContainmentModel application = 
          createApplicationModel( systemContext, appConfig );

        //
        // Create the containment model describing all of the 
        // system facilities. These facilities may include model 
        // listeners and dependent components that facilitate the
        // customization of the runtime merlin system.  The 
        // facilities model receives a privaliged system context
        // that contains a reference to the root application model
        // enabling listeners to register themselves for model 
        // changes.
        // 

        getLogger().info( "facilities deployment" );
        Configuration facilitiesConfig = 
          config.getChild( "facilities" );
        Logger facilitiesLogger = getLogger();

        DelegatingSystemContext system = 
          new DelegatingSystemContext( systemContext );
        system.put( "urn:composition:dir", criteria.getWorkingDirectory() );
        system.put( "urn:composition:anchor", criteria.getAnchorDirectory() );
        system.put( "urn:composition:application", application );
        system.makeReadOnly();

        ContainmentModel facilities = 
          createFacilitiesModel( 
            system, facilitiesLogger, facilitiesConfig );

        //
        // Assembly of the system containment model. Note .. its not sure
        // if this function should be a part of the kernel initialization
        // or if this belongs here in the factory.  The current view is
        // the factory does the work of constructing the artifacts for
        // the kernel and the kernel implements the kernel 
        // startup/shutdown behaviour and the embeddor handles any post
        // kernel management logic.
        //

        KernelContext kernelContext = 
          new DefaultKernelContext( getLogger(), facilities, application );
        Kernel kernel = new DefaultKernel( kernelContext );
        setShutdownHook( getLogger(), kernel );

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
        // instantiate the kernel
        //

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

            if( criteria.isAuditEnabled() )
            {
                printModel( application );
            }

            if( !criteria.isServerEnabled() )
            {
                getLogger().debug( "shutdown phase" );
                try
                {
                    kernel.shutdown();
                    if( kernel instanceof Disposable )
                    {
                        ((Disposable)kernel).dispose();
                    }
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

   /**
    * If the kernel criteria includes a language code
    * then set the current local to the declared value.
    *
    * @param criteria the kernel criteria
    */
    public void setupLanguageCode( KernelCriteria criteria )
    {
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
    }

   /**
    * Utility method to construct the system context.
    * @param criteria the kernel criteria
    * @param logging the logging manager
    * @param config the kernel configuration
    * @param name not sure - need to check
    */
    private SystemContext createSystemContext( 
      InitialContext context, KernelCriteria criteria, String[] hosts, 
      LoggingManager logging, Configuration config, String name ) throws Exception
    {

        //
        // create the application repository
        //

        Configuration repositoryConfig = 
          config.getChild( "repository" );
        CacheManager cache = 
          createCacheManager( context, criteria, hosts, config );
        Repository repository = cache.createRepository();
        getLogger().debug( 
          "repository established: " + repository );

        //
        // create the system context
        //

        File anchor = criteria.getAnchorDirectory();

        DefaultSystemContext system = new DefaultSystemContext( 
            logging,
            anchor,
            criteria.getContextDirectory(),
            criteria.getTempDirectory(),
            repository,
            name,
            criteria.isDebugEnabled(),
            criteria.getDeploymentTimeout(),
            criteria.isCodeSecurityEnabled() );

        system.put( "urn:composition:dir", criteria.getWorkingDirectory() );
        system.put( "urn:composition:anchor", criteria.getAnchorDirectory() );
        system.makeReadOnly();

        return system;
    }

    private ContainmentModel createApplicationModel( 
      SystemContext system, Configuration config ) throws Exception
    {
        getLogger().info( "building application model" );
        LoggingManager logging = system.getLoggingManager();
        final Logger logger = logging.getLoggerForCategory("");
        ClassLoader api = system.getCommonClassLoader();
        ContainmentProfile profile = getContainmentProfile( config );
        ContainmentContext context = 
          createContainmentContext( system, logger, api, profile );
        return new DefaultContainmentModel( context );
    }

    private ContainmentModel createFacilitiesModel(
      SystemContext system, Logger logger, Configuration config )
      throws Exception
    {   
        ClassLoader spi = Block.class.getClassLoader();
        ContainmentProfile profile = getContainmentProfile( config );
        return new DefaultContainmentModel(
            createContainmentContext( 
              system, logger, spi, profile ) );
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

   /**
    * Utility method to create the application repository.
    * @param context the initial context
    * @param criteria the supplied factory criteria
    * @param config the repositotry configuration element
    * @return the repository
    */
    private CacheManager createCacheManager( 
      InitialContext context, KernelCriteria criteria, 
      String[] hosts, Configuration config )
      throws KernelException
    {
        File root = criteria.getRepositoryDirectory();
        File cache = getCacheDirectory( root, config.getChild( "cache" ) );
        Configuration proxy = config.getChild( "proxy", false );
        CacheManager manager = 
          createCacheManager( context, cache, hosts, proxy );
        return manager;
    }

    private CacheManager createCacheManager( 
      InitialContext context, File cache, String[] hosts, 
      Configuration proxyConfig ) 
      throws KernelException
    {
        //
        // the supplied root argument is the root cache resolved relative
        // to system properties and environment variables.  This value is 
        // overriden if a cache is declared in the kernel repository 
        // configuration
        //

        try
        {
            Factory factory = context.getInitialFactory();
            Map criteria = factory.createDefaultCriteria();
            criteria.put( "avalon.repository.cache", cache );
            criteria.put( "avalon.repository.hosts", hosts );

            if( null != proxyConfig )
            {
                final String host = 
                  proxyConfig.getChild( "host" ).getValue( null );
                criteria.put( "avalon.repository.proxy.host", host );

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
    * Utility method to create the LoggingManager.
    * @param criteria the kernel creation criteria
    * @param config the kernel configuration 
    * @return the logging manager
    */
    private LoggingManager createLoggingManager( 
      KernelCriteria criteria, Configuration config )
      throws Exception
    {
        File dir = criteria.getWorkingDirectory();
        Configuration conf = getLoggingConfiguration( criteria, config );
        Artifact artifact = criteria.getLoggingImplementation();
        Builder builder = m_context.newBuilder( m_classloader, artifact );
        Factory factory = builder.getFactory();
        LoggingCriteria params = getLoggingCriteria( factory );
        if( conf.getAttribute( "debug", "false" ).equals( "true" ) )
        {
            params.setBootstrapLogger( 
              new ConsoleLogger( ConsoleLogger.LEVEL_DEBUG ) );
        }
        params.setBaseDirectory( dir );
        params.setConfiguration( conf );
        return (LoggingManager) factory.create( params );
    }

    private LoggingCriteria getLoggingCriteria( Factory factory )
    {
        Map map = factory.createDefaultCriteria();
        if( map instanceof LoggingCriteria )
        {
            return (LoggingCriteria) map;
        }
        else
        {
            final String error =
              "Logging factory criteria class not recognized: ["
              + map.getClass().getName() 
              + "].";
            throw new IllegalArgumentException( error );
        }
    }

    private Configuration getLoggingConfiguration( KernelCriteria criteria, Configuration config )
      throws Exception
    {
        if( null != config.getChild( "logging", false ) )
        {
            return config.getChild( "logging" );
        }
        else
        {
            File file = criteria.getLoggingConfiguration();
            if( null != file )
            {
                try
                {
                    DefaultConfigurationBuilder builder = 
                      new DefaultConfigurationBuilder();
                    return builder.buildFromFile( file );  
                }
                catch( Throwable e )
                {
                    final String error = 
                      "Internal error while attempting to build logging configuration from: "
                      + file;
                    throw new KernelException( error, e );
                }
            }
            else
            {
                return config.getChild( "logging" );
            }
        }
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

    private void createInfoListing( 
      StringBuffer buffer, String[] hosts, InitialContext context, KernelCriteria criteria )
    {
        buffer.append( "\n" );
        buffer.append( 
          "\n  ${user.dir} == " 
          + System.getProperty( "user.dir" ) );
        buffer.append( 
          "\n  ${user.home} == " 
          + System.getProperty( "user.home" ) );

        buffer.append( "\n" );
        buffer.append( "\n  ${avalon.repository.cache} == " 
          + context.getInitialCacheDirectory() );
        buffer.append( "\n  ${avalon.repository.hosts} == " );
        String[] ihosts = context.getInitialHosts();
        for( int i=0; i<ihosts.length; i++ )
        {
            if( i>0 ) buffer.append( "," );
            buffer.append( ihosts[i] );
        }

        buffer.append( "\n" );

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
          "\n  ${merlin.logging.config} == " 
          + criteria.getLoggingConfiguration() );

        buffer.append( 
          "\n  ${merlin.logging.implementation} == " 
          + criteria.getLoggingImplementation() );

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
          "\n  ${merlin.audit} == " 
          + criteria.isAuditEnabled() );

        buffer.append( 
          "\n  ${merlin.server} == " 
          + criteria.isServerEnabled() );

        buffer.append( 
          "\n  ${merlin.autostart} == " 
          + criteria.isAutostartEnabled() );

        buffer.append( 
          "\n  ${merlin.code.security.enabled} == " 
          + criteria.isCodeSecurityEnabled() );

        buffer.append( 
          "\n  ${merlin.deployment.timeout} == " 
          + criteria.getDeploymentTimeout() );

        buffer.append( 
          "\n  ${merlin.repository} == " 
          + criteria.getRepositoryDirectory() );

        buffer.append( "\n  ${merlin.repository.hosts} == " );
        for( int i=0; i<hosts.length; i++ )
        {   
            if( i>0 ) buffer.append( "," );  
            buffer.append( StringHelper.toString( hosts[i] ) );
        }

        buffer.append( "\n  ${merlin.deployment} == " );
        URL[] urls = criteria.getDeploymentURLs();
        for( int i=0; i<urls.length; i++ )
        {   
            if( i>0 ) buffer.append( "," );  
            buffer.append( StringHelper.toString( urls[i] ) );
        }
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
                      if( kernel instanceof Disposable )
                      {
                          ((Disposable)kernel).dispose();
                      }
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

    public void printModel( DeploymentModel model )
    {
        StringBuffer buffer = new StringBuffer( "audit report" );
        buffer.append( LINE );
        buffer.append( "\nApplication Model" );
        buffer.append( LINE );
        buffer.append( "\n" );
        printModel( buffer, "  ", model );
        buffer.append( "\n" );
        buffer.append( LINE );
        getLogger().info( buffer.toString() );
    }

    public void printModel( StringBuffer buffer, String lead, DeploymentModel model )
    {
        if( model instanceof ContainmentModel )
        {
            printContainmentModel( buffer, lead, (ContainmentModel) model );
        }
        else if( model instanceof ComponentModel ) 
        {
            printComponentModel( buffer, lead, (ComponentModel) model );
        }
    }

    public void printContainmentModel( StringBuffer buffer, String lead, ContainmentModel model )
    {
        buffer.append( 
          "\n" + lead 
          + "container:" 
          + model 
          + ")" );
        printDeploymentModel( buffer, lead, model );
        DeploymentModel[] models = model.getModels();
        if( models.length > 0 )
        {
            buffer.append( "\n" + lead + "  children:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                printModel( buffer, "    " + lead, m );
            }
        }
        models = model.getStartupGraph();
        if( models.length > 0 )
        {
            buffer.append( "\n" + lead + "  startup:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                buffer.append( "\n" + "    " + lead + (i+1) + ": " + m );
            }
        }
        models = ((ContainmentModel)model).getShutdownGraph();
        if( models.length > 0 )
        {
            buffer.append( "\n" + lead + "  shutdown:" );
            for( int i=0; i<models.length; i++ )
            {
                DeploymentModel m = models[i];
                buffer.append( "\n" + "    " + lead + (i+1) + ": " + m );
            }
        }
    }

    public void printComponentModel( StringBuffer buffer, String lead, ComponentModel model )
    {
        buffer.append( 
          "\n" + lead 
          + "component:" 
          + model + "(" 
          + model.getDeploymentTimeout() 
          + ")" );
        printDeploymentModel( buffer, lead, model );
    }

    public void printDeploymentModel( StringBuffer buffer, String lead, DeploymentModel model )
    {
        DeploymentModel[] providers = model.getProviderGraph();
        DeploymentModel[] consumers = model.getConsumerGraph();

        if(( providers.length == 0 ) && ( consumers.length == 0 ))
        {
            return;
        }

        if( providers.length > 0 ) for( int i=0; i<providers.length; i++ )
        {
            DeploymentModel m = providers[i];
            buffer.append( "\n" + lead + "  <-- " + m );
        }

        if( consumers.length > 0 ) for( int i=0; i<consumers.length; i++ )
        {
            DeploymentModel m = consumers[i];
            buffer.append( "\n" + lead + "  --> " + m );
        }
    }
}
