/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.transport.autopublishing;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.ApplicationEvent;
import org.apache.avalon.phoenix.ApplicationListener;
import org.apache.avalon.phoenix.BlockEvent;
import org.apache.excalibur.altrmi.server.AltrmiPublisher;
import org.apache.excalibur.altrmi.server.PublicationException;

/**
 * Publishes so configured services automatically after block start().
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.14 $
 */
public class AutoPublisher extends AbstractLogEnabled
    implements Configurable, ApplicationListener
{
    private String m_publisherName;
    private AltrmiPublisher m_altrmiPublisher;
    private Map m_publications;
    private Vector m_events = new Vector();

    /**
     * @phoenix:configuration-schema type="relax-ng"
     */
    public void configure( final Configuration configuration ) throws ConfigurationException
    {

        m_publisherName = configuration.getChild( "publisher" ).getValue( "altrmification" );
        m_publications = new HashMap();

        final Configuration[] confs = configuration.getChildren( "publish" );

        for( int i = 0; i < confs.length; i++ )
        {
            final Configuration conf = confs[ i ];
            final String blockName = conf.getAttribute( "block" );
            final String publishAsName = conf.getAttribute( "publishAsName" );
            final String interfaceToPublish = conf.getAttribute( "interfaceToPublish" );

            m_publications.put( blockName, new PublicationInfo( publishAsName, interfaceToPublish ) );
        }
    }

    public void blockAdded( final BlockEvent event )
    {
        if( m_publisherName.equals( event.getName() ) )
        {
            m_altrmiPublisher = (AltrmiPublisher)event.getBlock();
        }

        if( m_publications.containsKey( event.getName() ) )
        {
            m_events.add( event );

            if( getLogger().isDebugEnabled() )
                getLogger().debug( "Will publish block: " + event.getName() );
        }
    }

    public void blockRemoved( final BlockEvent event )
    {
    }

    public void applicationStarting( ApplicationEvent event ) throws Exception
    {
    }

    public void applicationStarted()
    {

        for( int i = 0; i < m_events.size(); i++ )
        {
            final BlockEvent event = (BlockEvent)m_events.elementAt( i );
            final Object block = event.getBlock();
            PublicationInfo pi = (PublicationInfo)m_publications.get( event.getName() );

            try
            {
                m_altrmiPublisher.publish( block, pi.getPublishAsName(),
                    this.getClass().getClassLoader().loadClass( pi.getInterfaceToPublish() ) );
            }
            catch( PublicationException e )
            {
                getLogger().error( "Some problem auto-publishing", e );

                throw new CascadingRuntimeException( "Some problem auto-publishing", e );
            }
            catch( ClassNotFoundException e )
            {
                throw new CascadingRuntimeException(
                    "Interface specified in config.xml ('interfaceToPublish' attribte) not found",
                    e );
            }
        }
    }

    public void applicationStopping()
    {
        for( int i = 0; i < m_events.size(); i++ )
        {
            BlockEvent event = (BlockEvent)m_events.elementAt( i );

            if( m_publications.containsKey( event.getName() ) )
            {
                final Object block = event.getBlock();
                PublicationInfo pi = (PublicationInfo)m_publications.get( event.getName() );

                try
                {
                    m_altrmiPublisher.unPublish( block, pi.getPublishAsName() );
                }
                catch( PublicationException e )
                {
                    throw new CascadingRuntimeException( "Some problem un-auto-publishing", e );
                }
            }
        }
    }

    public void applicationStopped()
    {
        m_events.clear();

        if( getLogger().isDebugEnabled() ) getLogger().debug( "Cleared event list" );
    }

    public void applicationFailure( Exception e )
    {
    }
}
