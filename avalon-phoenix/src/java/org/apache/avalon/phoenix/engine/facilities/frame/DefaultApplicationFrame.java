/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.facilities.frame;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.avalon.excalibur.io.ExtensionFileFilter;
import org.apache.avalon.excalibur.thread.DefaultThreadPool;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.atlantis.Facility;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.engine.facilities.ApplicationFrame;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.format.AvalonFormatter;
import org.apache.log.output.FileOutputLogTarget;

/**
 * Manage the "frame" in which Applications operate.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultApplicationFrame
    extends AbstractLoggable
    implements ApplicationFrame, Contextualizable, Configurable, Initializable
{
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

    public void contextualize( final Context context )
        throws ContextException
    {
        m_name = (String)context.get( "name" );
        m_baseDirectory = (File)context.get( "directory" );
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
        final URL[] classPath = getClassPath();
        final ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        m_classLoader = new PolicyClassLoader( classPath, parentClassLoader, m_policy );
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
            throw new IllegalArgumentException( "No such thread group " + name );
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
                throw new ConfigurationException( "Error creating thread pool " + name,
                                                  e );
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

            //Setup logtarget
            final FileOutputLogTarget logTarget = new FileOutputLogTarget();
            logTarget.setFormatter( formatter );

            //Specify output location for logging
            final File file = new File( m_baseDirectory, location );
            try { logTarget.setFilename( file.getAbsolutePath() ); }
            catch( final IOException ioe )
            {
                throw new ConfigurationException( "Error initializing log files", ioe );
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
                throw new ConfigurationException( "Unable to locate target " + target );
            }

            final Priority priority = Priority.getPriorityForName( priorityName );
            if( !priority.getName().equals( priorityName ) )
            {
                throw new ConfigurationException( "Unknown logging priority " + priorityName );
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

    /**
     * Get Classpath for application.
     *
     * @return the list of URLs in ClassPath
     */
    private URL[] getClassPath()
    {
        final File blockDir = new File( m_baseDirectory, "blocks" );
        final File libDir = new File( m_baseDirectory, "lib" );

        final ArrayList urls = new ArrayList();
        getURLs( urls, blockDir, new String[] { ".bar" } );
        getURLs( urls, libDir, new String[] { ".jar", ".zip" } );

        return (URL[])urls.toArray( new URL[0] );
    }

    /**
     * Add all matching files in directory to url list.
     *
     * @param urls the url list
     * @param directory the directory to scan
     * @param extentions the list of extensions to match
     * @exception MalformedURLException if an error occurs
     */
    private void getURLs( final ArrayList urls, final File directory, final String[] extensions )
    {
        final ExtensionFileFilter filter = new ExtensionFileFilter( extensions );
        final File[] files = directory.listFiles( filter );
        if( null == files ) return;
        for( int i = 0; i < files.length; i++ )
        {
            try { urls.add( files[ i ].toURL() ); }
            catch( final MalformedURLException mue )
            {
                //should never occur
            }
        }
    }
}
