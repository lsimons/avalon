/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.phoenix.ApplicationEvent;
import org.apache.avalon.phoenix.ApplicationListener;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * Manage a set of <code>ApplicationListener</code> objects and propogate
 * <code>ApplicationEvent</code> notifications to these listeners.  Not all
 * events pass an Applicationevent parameter.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
final class ApplicationListenerSupport
{
    //Set of listeners. Must be accessed from synchronized code
    private ApplicationListener[] m_listeners = new ApplicationListener[ 0 ];

    /**
     * Add a ApplicationListener to those requiring notification of
     * <code>ApplicationEvent</code>s.
     *
     * @param listener the ApplicationListener
     */
    public synchronized void addApplicationListener( final ApplicationListener listener )
    {
        final ApplicationListener[] listeners = new ApplicationListener[ 1 + m_listeners.length ];
        System.arraycopy( m_listeners, 0, listeners, 0, m_listeners.length );
        listeners[ m_listeners.length ] = listener;
        m_listeners = listeners;
    }

    /**
     * Remove a ApplicationListener from those requiring notification of
     * <code>ApplicationEvent</code>s.
     *
     * @param listener the ApplicationListener
     */
    public synchronized void removeApplicationListener( final ApplicationListener listener )
    {
        int index = 0;
        while( index < m_listeners.length )
        {
            if( m_listeners[ index ] == listener ) break;
            index++;
        }

        if( m_listeners.length != index )
        {
            final ApplicationListener[] listeners = new ApplicationListener[ m_listeners.length - 1 ];
            System.arraycopy( m_listeners, 0, listeners, 0, index );
            final int length = m_listeners.length - index - 1;
            System.arraycopy( m_listeners, index + 1, listeners, index, length );
        }
    }

    void fireApplicationStartingEvent( final SarMetaData metaData )
    {
        final ApplicationEvent event =
            new ApplicationEvent( metaData.getName(), metaData );
        applicationStarting( event );
    }

    /**
     * Notification that the application is starting
     *
     * @param event the ApplicationEvent
     */
    public synchronized void applicationStarting( final ApplicationEvent event ) throws Exception
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].applicationStarting( event );
        }
    }

    /**
     * Notification that the application has started.
     *
     */
    public synchronized void applicationStarted()
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].applicationStarted();
        }
    }

    /**
     * Notification that the application is stopping
     *
     */
    public synchronized void applicationStopping()
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].applicationStopping();
        }
    }

    /**
     * Notification that the application has stopped
     *
     */
    public synchronized void applicationStopped()
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].applicationStopped();
        }
    }

    /**
     * Notification that the application has failed
     *
     */
    public synchronized void applicationFailure( Exception causeOfFailure )
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].applicationFailure( causeOfFailure );
        }
    }

}
