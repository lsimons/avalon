/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.listeners;

import java.rmi.Remote;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.cornerstone.services.rmification.RMIfication;

/**
 * FIXME: INPROGRESS
 *
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public class RMIficationListener
    implements Configurable, BlockListener
{
    private String m_publisherName;
    private RMIfication m_publisher;
    private Map m_publications;
    private List m_queue;

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_publisherName = configuration.getChild( "publisher" ).getValue( "rmification" );

        m_publications = new HashMap();
        final Configuration[] confs = configuration.getChildren( "publish" );
        for ( int i = 0; i < confs.length; i++ )
        {
            final Configuration conf = confs[ i ];

            final String blockName = conf.getAttribute( "remote" );
            final String name = conf.getAttribute( "name", blockName );

            m_publications.put( name, blockName );
        }
    }

    public void blockAdded( final BlockEvent event )
    {
        if ( m_publisherName.equals( event.getName() ) )
        {
            m_publisher = (RMIfication)event.getBlock();
            for ( int i = 0; i < m_queue.size(); i++ )
            {
                final BlockEvent queued = (BlockEvent)m_queue.get( i );
                publishBlock( event );
            }
            m_queue.clear();
            m_queue = null;
        }

        if ( m_publications.containsKey( event.getName() ) )
        {
            if ( event.getBlock() instanceof Remote )
            {
                publishBlock( event );
            }
            else
            {
                //FIXME: throw exception
            }
        }
    }

    public void blockRemoved( final BlockEvent event )
    {
        if ( event.getBlock() instanceof Remote )
        {
            unpublishBlock( event );
        }
    }

    private boolean isPublisherReady()
    {
        return null != m_publisher;
    }

    private void publishBlock( final BlockEvent event )
    {
        if ( !isPublisherReady() )
        {
            m_queue.add( event );
            return;
        }

        final Block block = event.getBlock();
        final String blockName = event.getName();

        final Iterator entries = m_publications.entrySet().iterator();
        while ( entries.hasNext() )
        {
            final Map.Entry entry = (Map.Entry)entries.next();

            if ( entry.getValue().equals( blockName ) )
            {
                try
                {
                    m_publisher.publish( (Remote)block, (String)entry.getKey() );
                }
                catch ( final Exception e )
                {
                    throw new CascadingRuntimeException( "", e );
                }
            }
        }
    }

    private void unpublishBlock( final BlockEvent event )
    {
        final Block block = event.getBlock();
        final String blockName = event.getName();

        final Iterator entries = m_publications.entrySet().iterator();
        while ( entries.hasNext() )
        {
            final Map.Entry entry = (Map.Entry)entries.next();

            if ( entry.getValue().equals( blockName ) )
            {
                try
                {
                    m_publisher.unpublish( (String)entry.getKey() );
                }
                catch ( final Exception e )
                {
                    throw new CascadingRuntimeException( "", e );
                }
            }
        }
    }
}