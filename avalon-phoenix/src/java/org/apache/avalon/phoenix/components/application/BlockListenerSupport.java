/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.phoenix.BlockEvent;
import org.apache.avalon.phoenix.BlockListener;

/**
 * Manage a set of <code>BlockListener</code> objects and propogate
 * <code>BlockEvent</code> notifications to these listeners.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
final class BlockListenerSupport
{
    //Set of listeners. Must be accessed from synchronized code
    private BlockListener[] m_listeners = new BlockListener[ 0 ];

    /**
     * Add a BlockListener to those requiring notification of
     * <code>BlockEvent</code>s.
     *
     * @param listener the BlockListener
     */
    public synchronized void addBlockListener( final BlockListener listener )
    {
        final BlockListener[] listeners = new BlockListener[ 1 + m_listeners.length ];
        System.arraycopy( m_listeners, 0, listeners, 0, m_listeners.length );
        listeners[ m_listeners.length ] = listener;
        m_listeners = listeners;
    }

    /**
     * Remove a BlockListener from those requiring notification of
     * <code>BlockEvent</code>s.
     *
     * @param listener the BlockListener
     */
    public synchronized void removeBlockListener( final BlockListener listener )
    {
        int index = 0;
        while( index < m_listeners.length )
        {
            if( m_listeners[ index ] == listener ) break;
            index++;
        }

        if( m_listeners.length != index )
        {
            final BlockListener[] listeners = new BlockListener[ m_listeners.length - 1 ];
            System.arraycopy( m_listeners, 0, listeners, 0, index );
            final int length = m_listeners.length - index - 1;
            System.arraycopy( m_listeners, index + 1, listeners, index, length );
        }
    }

    /**
     * Notification that a block has just been added
     * to Server Application.
     *
     * @param event the BlockEvent
     */
    public synchronized void blockAdded( final BlockEvent event )
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].blockAdded( event );
        }
    }

    /**
     * Notification that a block is just about to be
     * removed from Server Application.
     *
     * @param event the BlockEvent
     */
    public synchronized void blockRemoved( final BlockEvent event )
    {
        for( int i = 0; i < m_listeners.length; i++ )
        {
            m_listeners[ i ].blockRemoved( event );
        }
    }
}
