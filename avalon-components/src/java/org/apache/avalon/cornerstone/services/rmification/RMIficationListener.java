/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.rmification;

import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;

/**
 * FIXME: INPROGRESS and NOT TESTED
 * Publish blocks via RMI.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 * @version $Revision: 1.4 $
 */
public class RMIficationListener
    extends AbstractLogEnabled
    implements Configurable, BlockListener
{
    /** <code>RMIfication</code> block's name */
    private String          m_publisherName;
    /** <code>RMIfication</code> block */
    private RMIfication     m_publisher;
    /** publications */
    private Map             m_publications;
    /** delayed events */
    private List            m_delayedEvents;

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_publisherName =
            configuration.getChild( "publisher" ).getValue( "rmification" );

        m_publications = new HashMap();
        final Configuration[] confs = configuration.getChildren( "publish" );
        for ( int i = 0; i < confs.length; i++ )
        {
            final Configuration conf = confs[ i ];

            final String blockName = conf.getAttribute( "block" );
            final String name = conf.getAttribute( "name", blockName );

            m_publications.put( name, blockName );
        }

        m_delayedEvents = new ArrayList();
    }

    public void blockAdded( final BlockEvent event )
    {
        final String blockName = event.getName();

        if ( m_publisherName.equals( blockName ) )
        {
            if ( getLogger().isDebugEnabled() )
            {
                final String message = "Found publisher block " + blockName;
                getLogger().debug( message );
            }

            m_publisher = (RMIfication)event.getBlock();

            processDelayedEvents();
        }

        if ( m_publications.containsValue( blockName ) )
        {
            publishBlock( event );
        }
    }

    public void blockRemoved( final BlockEvent event )
    {
        if ( m_publications.containsValue( event.getName() ) )
        {
            unpublishBlock( event );
        }
    }

    private boolean isPublisherReady()
    {
        return null != m_publisher;
    }

    private void processDelayedEvents()
    {
        final Iterator delayedEvents = m_delayedEvents.iterator();
        while ( delayedEvents.hasNext() )
        {
            publishBlock( (BlockEvent)delayedEvents.next() );
        }
        m_delayedEvents.clear();
        m_delayedEvents = null;
    }

    private void publishBlock( final BlockEvent event )
    {
        if ( ! isPublisherReady() )
        {
            m_delayedEvents.add( event );
            return;
        }

        final Block block = event.getBlock();
        final String blockName = event.getName();

        final Iterator entries = m_publications.entrySet().iterator();
        while ( entries.hasNext() )
        {
            final Map.Entry entry = (Map.Entry)entries.next();
            final String publicationName = (String)entry.getKey();

            if ( entry.getValue().equals( blockName ) )
            {
                try
                {
                    m_publisher.publish( (Remote)block, publicationName );

                }
                catch ( final Exception e )
                {
                    final String message =
                        "Fail to publish " + publicationName;
                    throw new CascadingRuntimeException( message, e );
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
            final String publicationName = (String)entry.getKey();

            if ( entry.getValue().equals( blockName ) )
            {
                try
                {
                    m_publisher.unpublish( publicationName );
                }
                catch ( final Exception e )
                {
                    final String message =
                        "Fail to unpublish " + publicationName;
                    throw new CascadingRuntimeException( message, e );
                }
            }
        }
    }
}
