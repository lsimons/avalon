/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.frame;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.avalon.excalibur.thread.DefaultThreadPool;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.logger.AvalonFormatter;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.output.io.FileTarget;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * Manage the "frame" in which Applications operate.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultApplicationFrame
    extends AbstractLoggable
    implements ApplicationFrame, Contextualizable, Configurable, Initializable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultPolicy.class );

    private final static String  DEFAULT_FORMAT =
        "%{time} [%7.7{priority}] <<%{category}>> (%{context}): %{message}\\n%{throwable}";

    ///Name of application
    private String       m_name;

    ///Base directory of applications working directory
    private File         m_baseDirectory;

    ///Hierarchy of Application logging
    private Hierarchy    m_logHierarchy    = new Hierarchy();

    ///Map of thread pools for application
    private HashMap      m_threadPools     = new HashMap();

    ///Policy for application
    private Policy       m_policy;

    ///ClassLoader for application
    private ClassLoader  m_classLoader;

    ///Classpath for application
    private URL[]        m_classPath;

    ///Base context for all blocks in application
    private Context      m_context;

    public void contextualize( final Context context )
        throws ContextException
    {
        m_name = (String)context.get( "app.name" );
        m_baseDirectory = (File)context.get( "app.home" );
        m_classPath = (URL[])context.get( "app.class.path" );
    }

    /**
     * Configure frame.
     *
     * @param configuration the configuration data
     * @exception ConfigurationException if an error occurs
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        //Configure policy
        final Configuration policy = configuration.getChild( "policy" );
        configurePolicy( policy );

        //Configure thread pools
        final Configuration threads = configuration.getChild( "threads" );
        configureThreadPools( threads );

        //Configure Logging
        final Configuration logs = configuration.getChild( "logs" );
        final Configuration[] targets = logs.getChildren( "log-target" );
        final HashMap targetSet = configureTargets( targets );
        final Configuration[] categories = logs.getChildren( "category" );
        configureCategories( categories, targetSet );
    }

    /**
     * Initialize frame.
     * This involves creating and preparing the ClassLoader.
     *
     * @exception Exception if an error occurs
     */
    public void initialize()
        throws Exception
    {
        final ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        m_classLoader = new PolicyClassLoader( m_classPath, parentClassLoader, m_policy );

        //base contxt that all block contexts inherit from
        final DefaultContext context = new DefaultContext();
        context.put( BlockContext.APP_NAME, m_name );
        context.put( BlockContext.APP_HOME_DIR, m_baseDirectory );
        m_context = context;
    }

    /**
     * Get ClassLoader for the current application.
     *
     * @return the ClassLoader
     */
    public ClassLoader getClassLoader()
    {
        return m_classLoader;
    }

    /**
     * Get logger with category for application.
     * Note that this name may not be the absolute category.
     *
     * @param category the logger category
     * @return the Logger
     */
    public Logger getLogger( final String category )
    {
        return m_logHierarchy.getLoggerFor( category );
    }

    /**
     * Create a BlockContext for a particular Block.
     *
     * @param name the name of the Block
     * @return the created BlockContext
     */
    public BlockContext createBlockContext( final String name )
    {
        final DefaultBlockContext context =
            new DefaultBlockContext( getLogger(), this, m_context );
        context.put( BlockContext.NAME, name );
        context.makeReadOnly();
        return context;
    }

    /**
     * Retrieve thread pool by name.
     *
     * @param name the name of thread pool
     * @return the threadpool
     * @exception IllegalArgumentException if the name of thread pool is
     *            invalid or named pool does not exist
     */
    public ThreadPool getThreadPool( final String name )
    {
        final ThreadPool threadPool = (ThreadPool)m_threadPools.get( name );

        if( null == threadPool )
        {
            final String message = REZ.getString( "frame.error.thread.missing", name );
            throw new IllegalArgumentException( message );
        }

        return threadPool;
    }

    /**
     * Retrieve the default thread pool.
     *
     * @return the thread pool
     */
    public ThreadPool getDefaultThreadPool()
    {
        return getThreadPool( "default" );
    }

    /**
     * Setup policy based on configuration data.
     *
     * @param configuration the configuration data
     * @exception ConfigurationException if an error occurs
     */
    private void configurePolicy( final Configuration configuration )
        throws ConfigurationException
    {
        final DefaultPolicy policy = new DefaultPolicy( m_baseDirectory );
        policy.setLogger( getLogger() );
        policy.configure( configuration );
        m_policy = policy;
    }

    /**
     * Setup thread pools based on configuration data.
     *
     * @param configuration the configuration data
     * @exception ConfigurationException if an error occurs
     */
    private void configureThreadPools( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] groups = configuration.getChildren( "thread-group" );
        for( int i = 0; i < groups.length; i++ )
        {
            final Configuration group = groups[ i ];

            final String name = group.getChild( "name" ).getValue();
            final int priority = group.getChild( "priority" ).getValueAsInteger( 5 );
            final boolean isDaemon = group.getChild( "is-daemon" ).getValueAsBoolean( false );

            final int minThreads = group.getChild( "min-threads" ).getValueAsInteger( 5 );
            final int maxThreads = group.getChild( "max-threads" ).getValueAsInteger( 10 );
            final int minSpareThreads = group.getChild( "min-spare-threads" ).
                getValueAsInteger( maxThreads - minThreads );

            try
            {
                final DefaultThreadPool threadPool = new DefaultThreadPool( name, maxThreads );
                threadPool.setDaemon( isDaemon );
                threadPool.setLogger( getLogger() );
                m_threadPools.put( name, threadPool );
            }
            catch( final Exception e )
            {
                final String message = REZ.getString( "frame.error.thread.create", name );
                throw new ConfigurationException( message, e );
            }
        }
    }

    /**
     * Configure a set of logtargets based on config data.
     *
     * @param targets the target configuration data
     * @return a Map of target-name to target
     * @exception ConfigurationException if an error occurs
     */
    private HashMap configureTargets( final Configuration[] targets )
        throws ConfigurationException
    {
        final HashMap targetSet = new HashMap();

        for( int i = 0; i < targets.length; i++ )
        {
            final Configuration target = targets[ i ];
            final String name = target.getAttribute( "name" );
            String location = target.getAttribute( "location" ).trim();
            final String format = target.getAttribute( "format", null );

            if( '/' == location.charAt( 0 ) )
            {
                location = location.substring( 1 );
            }

            final AvalonFormatter formatter = new AvalonFormatter();

            ///If format specified then setup formatter appropriately
            if( null != format ) formatter.setFormat( format );
            else formatter.setFormat( DEFAULT_FORMAT );

            //Specify output location for logging
            final File file = new File( m_baseDirectory, location );

            //Setup logtarget
            FileTarget logTarget = null;
            
            try
            {
                logTarget = new FileTarget( file.getAbsoluteFile(), false, formatter );
            }
            catch( final IOException ioe )
            {
                final String message = REZ.getString( "frame.error.log.create", file );
                throw new ConfigurationException( message, ioe );
            }

            targetSet.put( name, logTarget );
        }

        return targetSet;
    }

    /**
     * COnfigure Logging categories.
     *
     * @param categories configuration data for categories
     * @param targets a hashmap containing the already existing taregt
     * @exception ConfigurationException if an error occurs
     */
    private void configureCategories( final Configuration[] categories, final HashMap targets )
        throws ConfigurationException
    {
        for( int i = 0; i < categories.length; i++ )
        {
            final Configuration category = categories[ i ];
            final String name = category.getAttribute( "name", "" );
            final String target = category.getAttribute( "target" );
            final String priorityName = category.getAttribute( "priority" );

            final Logger logger = getLogger( name );

            final LogTarget logTarget = (LogTarget)targets.get( target );
            if( null == target )
            {
                final String message = REZ.getString( "frame.error.target.locate", target );
                throw new ConfigurationException( message );
            }

            final Priority priority = Priority.getPriorityForName( priorityName );
            if( !priority.getName().equals( priorityName ) )
            {
                final String message = REZ.getString( "frame.error.priority.unknown", priorityName );
                throw new ConfigurationException( message );
            }

            if( name.equals( "" ) )
            {
                m_logHierarchy.setDefaultPriority( priority );
                m_logHierarchy.setDefaultLogTarget( logTarget );
            }
            else
            {
                logger.setPriority( priority );
                logger.setLogTargets( new LogTarget[] { logTarget } );
            }
        }
    }
}
