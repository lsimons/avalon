/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.threads;

import java.util.HashMap;
import org.apache.avalon.cornerstone.services.threads.ThreadManager;
import org.apache.excalibur.threadcontext.ThreadContext;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.excalibur.thread.impl.DefaultThreadPool;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.Block;

/**
 * Default implementation of ThreadManager.
 *
 * @phoenix:service name="org.apache.avalon.cornerstone.services.threads.ThreadManager"
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class DefaultThreadManager
    extends AbstractLogEnabled
    implements Block, ThreadManager, Configurable
{
    ///Map of thread pools for application
    private HashMap m_threadPools = new HashMap();

    /**
     * Setup thread pools based on configuration data.
     *
     * @param configuration the configuration data
     * @exception ConfigurationException if an error occurs
     */
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final ThreadContext threadContext = ThreadContext.getThreadContext();

        final Configuration[] groups = configuration.getChildren( "thread-group" );
        for( int i = 0; i < groups.length; i++ )
        {
            configureThreadPool( groups[ i ], threadContext );
        }
    }

    private void configureThreadPool( final Configuration configuration,
                                      final ThreadContext threadContext )
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
                new DefaultThreadPool( name, maxThreads, threadContext );
            threadPool.setDaemon( isDaemon );
            threadPool.enableLogging( getLogger() );
            m_threadPools.put( name, threadPool );
        }
        catch( final Exception e )
        {
            final String message = "Error creating ThreadPool named " + name;
            throw new ConfigurationException( message, e );
        }
    }

    /**
     * Retrieve a thread pool by name.
     *
     * @param name the name of thread pool
     * @return the threadpool
     * @exception IllegalArgumentException if the name of thread pool is
     *            invalid or named pool does not exist
     */
    public ThreadPool getThreadPool( final String name )
        throws IllegalArgumentException
    {
        final ThreadPool threadPool = (ThreadPool)m_threadPools.get( name );

        if( null == threadPool )
        {
            final String message = "Unable to locate ThreadPool named " + name;
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
}
