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

package org.apache.avalon.logging.logkit;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException ;
import java.lang.reflect.Method ;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.avalon.logging.provider.LoggingCriteria;
import org.apache.avalon.logging.provider.LoggingException;
import org.apache.avalon.logging.provider.LoggingManager;
import org.apache.avalon.logging.data.CategoriesDirective;
import org.apache.avalon.logging.data.CategoryDirective;
import org.apache.avalon.logging.logkit.factory.FileTargetFactory;
import org.apache.avalon.logging.logkit.factory.StreamTargetFactory;
import org.apache.avalon.logging.logkit.factory.MulticastTargetFactory;
import org.apache.avalon.logging.logkit.factory.PluginTargetFactory;

import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Factory;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.excalibur.configuration.ConfigurationUtil;

import org.apache.log.LogTarget;
import org.apache.log.output.io.StreamTarget;
import org.apache.log.format.Formatter;

/**
 * The DefaultLoggingFactory provides support for the establishment of a 
 * new logging system using LogKit as the implementation.
 */
public class DefaultLoggingFactory implements Factory
{
    //--------------------------------------------------------------------------
    // static
    //--------------------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultLoggingFactory.class );

    private static final FormatterFactory FORMATTER = 
      new DefaultFormatterFactory();

    //--------------------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------------------

    private final ClassLoader m_classloader;
    private final InitialContext m_context;
    
    //--------------------------------------------------------------------------
    // state
    //--------------------------------------------------------------------------

    private Logger m_logger;
    private File m_basedir;
    private LogTargetFactoryManager m_factories;
    private LogTargetManager m_targets;
    private LogTargetFactoryBuilder m_builder;

    //--------------------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------------------

   /**
    * Creation of a new default factory.
    * @param context the repository inital context
    * @param classloader the factory classloader
    */
    public DefaultLoggingFactory( InitialContext context, ClassLoader classloader )
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
        return new DefaultLoggingCriteria( m_context );
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
    * Creation of a new logging manager using the supplied criteria.
    *
    * @param map the parameters 
    * @return the logging manager instance
    * @exception Exception if an error occurs
    */
    public Object create( final Map map ) throws Exception
    {
        if( null == map )
        {
            throw new NullPointerException( "map" );
        }

        final LoggingCriteria criteria = getLoggingCriteria( map );

        //
        // get the logging system configuration, base directory
        // and bootstrap logger and setup the primary managers
        //

        final Configuration config = criteria.getConfiguration();
        m_logger = setUpBootstrapLogger( criteria, config );
        m_basedir = criteria.getBaseDirectory();

        Map factoriesMap = new HashMap();
        m_factories = new DefaultLogTargetFactoryManager( factoriesMap );

        Map targetsMap = new HashMap();
        m_targets = new DefaultLogTargetManager( targetsMap );

        m_builder = 
          new DefaultLogTargetFactoryBuilder( 
            m_context, m_classloader, m_logger, m_basedir, 
            m_factories, m_targets );

        //
        // setup the logging targets
        //

        final Configuration targetsConfig = config.getChild( "targets" );
        setupTargets( factoriesMap, targetsMap, targetsConfig );

        //
        // setup the logging categories directive
        //

        CategoriesDirective categories = 
          getCategoriesDirective( config.getChild( "categories" ), true );

        //
        // setup the internal logging channel name
        //

        String internal = 
          config.getChild( "logger" ).getAttribute( "name", "logger" );

        //
        // get the debug policy
        //

        boolean debug = criteria.isDebugEnabled();

        //
        // create a logkit logging mananager
        //

        LoggingManager manager = 
          new DefaultLoggingManager( m_logger, m_targets, categories, internal, debug );

        //
        // setup the default log target
        //

        return manager;
    }

    private LoggingCriteria getLoggingCriteria( Map map )
    {
        if( map instanceof LoggingCriteria )
        {
            return (LoggingCriteria) map;
        }
        else
        {
            final String error = 
              REZ.getString( 
                "factory.bad-criteria", 
                map.getClass().getName() );
            throw new IllegalArgumentException( error );
        }
    }

    private Logger setUpBootstrapLogger( LoggingCriteria criteria, Configuration config )
    {
        if( config.getAttribute( "debug", "false" ).equals( "true" ) )
        {
            return new ConsoleLogger( ConsoleLogger.LEVEL_DEBUG );
        }
        else
        {
            return criteria.getBootstrapLogger();
        }
    }

   /**
    * Setup of the log targets declared in the logging configuration.
    * @param logger the logging channel to log establishment events
    * @param manager the log factory manager from which target factories 
    *    are resolved
    * @param config the log targets configuration
    */
    private void setupTargets( final Map factories, final Map targets, final Configuration config )
      throws LoggingException
    {
        Configuration[] children = config.getChildren();
        for( int i = 0; i < children.length; i++ )
        {
            Configuration child = children[i];
            final String id = getTargetId( child );
            try
            {
                final LogTarget target = createLogTarget( factories, id, child );
                targets.put( id, target );
                final String message = 
                  REZ.getString( "target.notice.add", id );
                m_logger.debug( message );
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "target.notice.fail", id );
                m_logger.error( error, e );
                throw new LoggingException( error, e );
            }
        }
    }

   /**
    * Create a new log target using a supplied configuration.
    * @param manager the log target factory manager
    * @param config the target configuration
    * @return the logging target
    * @exception Exception if an error occurs during factory creation
    */ 
    private LogTarget createLogTarget( Map factories, final String id, final Configuration config )
      throws LoggingException
    {
        final String key = getTargetFactoryKey( config );
        final LogTargetFactory factory = getLogTargetFactory( factories, key );
        return factory.createTarget( config );
    }

    private LogTargetFactory getLogTargetFactory( Map factories, String key )
      throws LoggingException
    {
        final LogTargetFactory factory = m_factories.getLogTargetFactory( key );
        if( factory != null )
        {
            return factory;
        }
        else
        {
            Class clazz = getLogTargetFactoryClass( key );
            LogTargetFactory newFactory = 
              m_builder.buildLogTargetFactory( clazz );
            factories.put( key, newFactory );
            return newFactory;
        }
    }

    private Class getLogTargetFactoryClass( final String key )
      throws LoggingException
    {
        if( key.equals( "file" ) )
        {
            return FileTargetFactory.class;
        }
        else if( key.equals( "stream" ) )
        {
            return StreamTargetFactory.class;
        }
        else if( key.equals( "multicast" ) )
        {
            return MulticastTargetFactory.class;
        }
        else if( key.equals( "target" ) )
        {
            return PluginTargetFactory.class;
        }
        else
        {
            final String message = 
              REZ.getString( "factory.error.unknown", key );
            throw new LoggingException( message );
        }
    }

   /**
    * Return the factory key declared by a log target configuration.
    * @param the target configuration
    * @return the factory key
    */
    private String getTargetFactoryKey( Configuration config )
      throws LoggingException
    { 
        return config.getName();
    }

   /**
    * Return the id assigned to a target.
    * @param config the target configuration
    * @return the target id
    * @exception LoggingException if the id is not declared
    */
    private String getTargetId( Configuration config )
      throws LoggingException
    { 
        try
        {
            return config.getAttribute( "id" );
        }
        catch( ConfigurationException e )
        {
            final String listing = ConfigurationUtil.list( config );
            final String error = 
              REZ.getString( 
                "target.error.missing-id", 
                listing );
            throw new LoggingException( error );
        }
    }

    private CategoriesDirective getCategoriesDirective( Configuration config )
      throws LoggingException
    {
        return getCategoriesDirective( config, false );
    }

    private CategoriesDirective getCategoriesDirective( 
      Configuration config, boolean root )
      throws LoggingException
    {
        final String name = getCategoryName( config, root );
        final String priority = config.getAttribute( "priority", null );
        final String target = config.getAttribute( "target", null );
        CategoryDirective[] categories = 
          getCategoryDirectives( config );
        return new CategoriesDirective( name, priority, target, categories );
    }

    private String getCategoryName( Configuration config, boolean root )
      throws LoggingException
    {
        if( root ) return "";
        final String name = config.getAttribute( "name", null );
        if( null != name ) return name;

        final String error = 
          REZ.getString( "target.error.missing-category-name" );
        throw new LoggingException( error );
    }

    private CategoryDirective[] getCategoryDirectives( Configuration config )
      throws LoggingException
    {
        ArrayList list = new ArrayList();
        Configuration[] children = config.getChildren();
        for( int i=0; i<children.length; i++ )
        {
            Configuration child = children[i];
            if( child.getName().equals( "category" ) )
            {
                CategoryDirective directive = 
                  getCategoryDirective( child );
                list.add( directive );
            }
            else if( child.getName().equals( "categories" ) )
            {
                CategoriesDirective directive = 
                  getCategoriesDirective( child );
                list.add( directive );
            }
        }
        return (CategoryDirective[]) list.toArray( new CategoryDirective[0] );
    }

    private CategoryDirective getCategoryDirective( Configuration config ) 
      throws LoggingException
    {
        final String name = getCategoryName( config, false );
        final String priority = config.getAttribute( "priority", null );
        final String target = config.getAttribute( "target", null );
        return new CategoryDirective( name, priority, target );
    }

    //--------------------------------------------------------------------------
    // junk
    //--------------------------------------------------------------------------

   /**
    * Return the identitying key associated with the log target factory.
    * @param config the log target factory configuration
    * @return the unique key
    */
    /*
    private String getFactoryKey( Configuration config )
      throws LoggingException
    {
        try
        {
            return config.getAttribute( "type" );
        }
        catch( ConfigurationException e )
        {
            final String listing = ConfigurationUtil.list( config );
            final String error = 
              REZ.getString( 
                "target.error.missing-type", 
                listing );
           throw new LoggingException( error );
        }
    }
    */


   /**
    * Return the class attribute from a factory element.
    * @param config the target factory configuration
    * @return the target classname
    * @exception LoggingException if the class attribute is not declared
    */
    /*
    private String getFactoryClassname( Configuration config )
      throws LoggingException
    { 
        try
        {
            return config.getAttribute( "class" );
        }
        catch( ConfigurationException e )
        {
            final String listing = ConfigurationUtil.list( config );
            final String error = 
              REZ.getString( 
                "target.error.missing-class", 
                listing );
            throw new LoggingException( error );
        }
    }
    */

   /**
    * Load a factory class using a supplied factory classname.
    * @param factory the factory classname
    * @return the factory class
    * @exception LoggingException if a factory class loading error occurs
    */
    /*
    protected Class loadFactoryClass( String classname )
        throws LoggingException
    {
        try
        {
            return m_classloader.loadClass( classname );
        }
        catch( ClassNotFoundException e )
        {
            final String error = 
              REZ.getString( 
                "target.error.class-not-found", 
                classname );
            throw new LoggingException( error, e );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "target.error.class-load", 
                classname );
            throw new LoggingException( error, e );
        }
    }
    */

   /**
    * Create a new logging target factory instance.
    */
    /*
    private LogTargetFactory createLogTargetFactory( Configuration config ) 
      throws LoggingException
    {
        String classname = getFactoryClassname( config );
        Class clazz = loadFactoryClass( classname );
        return buildLogTargetFactoryViaConstructor( clazz );
    }
    */

    /**
     * Create the log target factories.
     *
     * @param config the factory configuration element.
     * @throws LoggingException if an error occurs in factor directive parsing
     */
    /*
    private void setupTargetFactories( 
      final Map factories, final Configuration config )
      throws LoggingException
    {
        Configuration[] children = config.getChildren();
        for( int i = 0; i < children.length; i++ )
        {
            Configuration child = children[i];
            if( child.getName().equalsIgnoreCase( "factory" ) )
            {
                final String key = getFactoryKey( child );
                try
                {
                    LogTargetFactory factory =
                      createLogTargetFactory( child );
                    factories.put( key, factory );
                }
                catch( LoggingException e )
                {
                    final String error = 
                     REZ.getString( "factory.target-factory.load.error", key );
                    m_logger.error( error, e );
                }
            }
            else
            {
                final String name = child.getName();
                final String listing = ConfigurationUtil.list( child ); 
                final String error = 
                  REZ.getString( 
                    "factory.target-factory.unknown-element", 
                    name, 
                    listing );
                m_logger.error( error );
            }
        }
    }
    */

}