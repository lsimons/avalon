/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.kernel;

import java.util.HashMap;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.lang.DefaultThreadContextPolicy;
import org.apache.avalon.excalibur.lang.ThreadContext;
import org.apache.avalon.excalibur.lang.ThreadContextPolicy;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.excalibur.thread.impl.DefaultThreadPool;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.interfaces.ConfigurationRepository;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.log.Hierarchy;
import org.apache.log.Logger;

/**
 * Manage the "frame" in which Applications operate.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class DefaultApplicationContext
    extends AbstractLogEnabled
    implements ApplicationContext, Composable, Configurable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultApplicationContext.class );

    ///Map of thread pools for application
    private HashMap      m_threadPools     = new HashMap();

    //LogKitManager for application
    private Hierarchy    m_hierarchy;

    ///ClassLoader for application
    private ClassLoader  m_classLoader;

    ///ThreadContext for application
    private ThreadContext m_threadContext;

    //Repository of configuration data to access
    private ConfigurationRepository m_repository;

    private SarMetaData m_metaData;

    protected DefaultApplicationContext( final SarMetaData metaData,
                                         final ClassLoader classLoader,
                                         final Hierarchy hierarchy )
    {
        m_metaData = metaData;
        m_classLoader = classLoader;
        m_hierarchy = hierarchy;

        final DefaultThreadContextPolicy policy = new DefaultThreadContextPolicy();
        final HashMap map = new HashMap( 1 );
        map.put( ThreadContextPolicy.CLASSLOADER, m_classLoader );
        m_threadContext = new ThreadContext( policy, map );
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_repository = (ConfigurationRepository)componentManager.lookup( ConfigurationRepository.ROLE );
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
        //Configure thread pools
        final Configuration[] groups =
            configuration.getChild( "threads" ).getChildren( "thread-group" );

        if( groups.length > 0 )
        {
            final String message = REZ.getString( "frame.warn.thread-pools" );
            getLogger().warn( message );
            System.err.println( message );
        }

        for( int i = 0; i < groups.length; i++ )
        {
            configureThreadPool( groups[ i ] );
        }
    }

    public SarMetaData getMetaData()
    {
        return m_metaData;
    }

    public ThreadContext getThreadContext()
    {
        return m_threadContext;
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
        return m_hierarchy.getLoggerFor( category );
    }

    /**
     * Get the Configuration for specified component.
     *
     * @param component the component
     * @return the Configuration
     */
    public Configuration getConfiguration( final String component )
        throws ConfigurationException
    {
        return m_repository.getConfiguration( m_metaData.getName(), component );
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

    private void configureThreadPool( final Configuration configuration )
        throws ConfigurationException
    {
        final String name = configuration.getChild( "name" ).getValue();
        final int priority = configuration.getChild( "priority" ).getValueAsInteger( 5 );
        final boolean isDaemon = configuration.getChild( "is-daemon" ).getValueAsBoolean( false );

        final int minThreads = configuration.getChild( "min-threads" ).getValueAsInteger( 5 );
        final int maxThreads = configuration.getChild( "max-threads" ).getValueAsInteger( 10 );
        final int minSpareThreads = configuration.getChild( "min-spare-threads" ).
            getValueAsInteger( maxThreads - minThreads );

        try
        {
            final DefaultThreadPool threadPool =
                new DefaultThreadPool( name, maxThreads, m_threadContext );
            threadPool.setDaemon( isDaemon );
            threadPool.enableLogging( getLogger() );
            m_threadPools.put( name, threadPool );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "frame.error.thread.create", name );
            throw new ConfigurationException( message, e );
        }
    }
}
