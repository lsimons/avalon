/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities.thread;

import java.util.Hashtable;
import java.util.Iterator;
import org.apache.avalon.atlantis.Facility;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.excalibur.thread.DefaultThreadPool;
import org.apache.excalibur.thread.ThreadPool;
import org.apache.phoenix.engine.facilities.ThreadManager;

/**
 *
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultThreadManager
    extends AbstractLoggable
    implements Facility, ThreadManager, Configurable
{
    protected final Hashtable       m_pools = new Hashtable();

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Configuration[] groups = configuration.getChildren( "thread-group" );
        for( int i = 0; i < groups.length; i++ )
        {
            final Configuration group = groups[ i ];

            final String name = group.getChild( "name" ).getValue();
            final int priority = group.getChild( "priority" ).getValueAsInt( 5 );
            final boolean isDaemon = group.getChild( "is-daemon" ).getValueAsBoolean( false );

            final int minThreads = group.getChild( "min-threads" ).getValueAsInt( 5 );
            final int maxThreads = group.getChild( "max-threads" ).getValueAsInt( 10 );
            final int minSpareThreads = group.getChild( "min-spare-threads" ).
                getValueAsInt( maxThreads - minThreads );

            try
            {
                final DefaultThreadPool threadPool = new DefaultThreadPool( name, maxThreads );
                threadPool.setDaemon( isDaemon );
                setupLogger( threadPool );
                m_pools.put( name, threadPool );
            }
            catch( final Exception e )
            {
                throw new ConfigurationException( "Error creating thread pool " + name,
                                                  e );
            }
        }
    }

    public ThreadPool getDefaultThreadPool()
    {
        return getThreadPool( "default" );
    }

    public ThreadPool getThreadPool( final String name )
    {
        final ThreadPool threadPool = (ThreadPool)m_pools.get( name );

        if( null == threadPool )
        {
            //Should this be a ComponentException ????
            throw new IllegalArgumentException( "No such thread group " + name );
        }

        return threadPool;
    }
}
