/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.frame;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.excalibur.lang.DefaultThreadContextPolicy;
import org.apache.avalon.excalibur.lang.ThreadContext;
import org.apache.avalon.excalibur.lang.ThreadContextPolicy;
import org.apache.avalon.excalibur.logger.DefaultLogKitManager;
import org.apache.avalon.excalibur.logger.LogKitManager;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.excalibur.thread.impl.DefaultThreadPool;
import org.apache.avalon.framework.ExceptionUtil;
import org.apache.avalon.framework.Version;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.components.configuration.ConfigurationRepository;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.log.Logger;

/**
 * Manage the "frame" in which Applications operate.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultApplicationFrame
    extends AbstractLoggable
    implements ApplicationFrame, Composable, Configurable, Initializable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultApplicationFrame.class );

    private final static String  DEFAULT_FORMAT =
        "%{time} [%7.7{priority}] (%{category}): %{message}\\n%{throwable}";

    ///Map of thread pools for application
    private HashMap      m_threadPools     = new HashMap();

    //LogKitManager for application
    private LogKitManager   m_logKitManager;

    ///ClassLoader for application
    private ClassLoader  m_classLoader;

    ///Base context for all blocks in application
    private Context      m_context;

    ///Context which application threads must execute in
    private ThreadContext m_threadContext;

    ///Cached version of configuration so that accessible in init() to configure threads
    private Configuration m_configuration;

    //Repository of configuration data to access
    private ConfigurationRepository m_repository;

    private SarMetaData m_metaData;
    private BlockListenerSupport m_listenerSupport;

    public DefaultApplicationFrame( final ClassLoader classLoader, final SarMetaData metaData )
    {
        m_metaData = metaData;
        m_classLoader = classLoader;
    }

    public SarMetaData getMetaData()
    {
        return m_metaData;
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
        //Configure Logging
        final Configuration logs = configuration.getChild( "logs" );
        configureLogKitManager( logs );

        //Cache config to use in building thread pools
        m_configuration = configuration;
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
        m_listenerSupport = new BlockListenerSupport();
        //base context that all block contexts inherit from
        final DefaultContext context = new DefaultContext();
        context.put( BlockContext.APP_NAME, m_metaData.getName() );
        context.put( BlockContext.APP_HOME_DIR, m_metaData.getHomeDirectory() );
        m_context = context;

        final DefaultThreadContextPolicy policy = new DefaultThreadContextPolicy();
        final HashMap map = new HashMap( 1 );
        map.put( ThreadContextPolicy.CLASSLOADER, m_classLoader );
        m_threadContext = new ThreadContext( policy, map );

        //Configure thread pools
        final Configuration threads = m_configuration.getChild( "threads" );
        configureThreadPools( threads );
    }

    /**
     * Add a BlockListener to those requiring notification of
     * <code>BlockEvent</code>s.
     *
     * @param listener the BlockListener
     */
    public void addBlockListener( final BlockListener listener )
    {
        m_listenerSupport.addBlockListener( listener );
    }

    /**
     * Remove a BlockListener from those requiring notification of
     * <code>BlockEvent</code>s.
     *
     * @param listener the BlockListener
     */
    public void removeBlockListener( final BlockListener listener )
    {
        m_listenerSupport.removeBlockListener( listener );
    }

    /**
     * Notification that a block has just been added
     * to Server Application.
     *
     * @param event the BlockEvent
     */
    public void blockAdded( final BlockEvent event )
    {
        m_listenerSupport.blockAdded( event );
    }

    /**
     * Notification that a block is just about to be
     * removed from Server Application.
     *
     * @param event the BlockEvent
     */
    public void blockRemoved( final BlockEvent event )
    {
        m_listenerSupport.blockRemoved( event );
    }

    /**
     * Get ThreadContext for the current application.
     *
     * @return the ThreadContext
     */
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
        return m_logKitManager.getLogger( category );
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
     * Create a BlockContext for a particular Block.
     *
     * @param name the name of the Block
     * @return the created BlockContext
     */
    public BlockContext createBlockContext( final String name )
    {
        final DefaultBlockContext context =
            new DefaultBlockContext( getLogger( name ), this, m_context );
        context.setLogger( getLogger() );
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
     * Setup thread pools based on configuration data.
     *
     * @param configuration the configuration data
     * @exception ConfigurationException if an error occurs
     */
    private void configureThreadPools( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] groups = configuration.getChildren( "thread-group" );

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
            threadPool.setLogger( getLogger() );
            m_threadPools.put( name, threadPool );
        }
        catch( final Exception e )
        {
            final String message = REZ.getString( "frame.error.thread.create", name );
            throw new ConfigurationException( message, e );
        }
    }

    private void configureLogKitManager( final Configuration conf )
        throws ConfigurationException
    {
        final DefaultContext context = new DefaultContext();
        context.put( BlockContext.APP_NAME, m_metaData.getName() );
        context.put( BlockContext.APP_HOME_DIR, m_metaData.getHomeDirectory() );

        try
        {
            final Version version = Version.getVersion( conf.getAttribute( "version", "1.0" ) );

            if ( new Version( 1, 0, 0 ).complies( version ) )
            {
                final SimpleLogKitManager logs = new SimpleLogKitManager();
                setupLogger( logs );
                logs.contextualize( context );
                logs.configure( conf );

                m_logKitManager = logs;
            }
            else
            {
                final DefaultLogKitManager logs = new DefaultLogKitManager();
                setupLogger( logs );
                logs.contextualize( context );
                logs.configure( conf );

                m_logKitManager = logs;
            }
        }
        catch ( final ContextException ce )
        {
            final String message = REZ.getString( "frame.error.log.configure" );
            throw new ConfigurationException( message, ce );
        }
    }
}
