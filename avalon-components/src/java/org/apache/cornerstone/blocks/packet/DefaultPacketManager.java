/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.packet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.Disposable;
import org.apache.avalon.util.thread.ThreadPool;
import org.apache.cornerstone.services.packet.PacketHandler;
import org.apache.cornerstone.services.packet.PacketHandlerFactory;
import org.apache.cornerstone.services.packet.PacketManager;
import org.apache.phoenix.Block;
import org.apache.phoenix.BlockContext;

/**
 * This is the service through which PacketManagement occurs.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultPacketManager
    extends AbstractLoggable
    implements Block, PacketManager, Contextualizable, Disposable
{
    protected BlockContext        m_context;
    protected HashMap             m_acceptors        = new HashMap();

    public void contextualize( final Context context )
    {
        m_context = (BlockContext)context;
    }
    
    public void dispose()
        throws Exception
    {
        final Iterator names = ((HashMap)m_acceptors.clone()).keySet().iterator();
        while( names.hasNext() )
        {
            final String name = (String)names.next();
            disconnect( name );
        }
    }

    /**
     * Start managing a DatagramSocket.
     * Management involves accepting packets and farming them out to threads 
     * from pool to be handled.
     *
     * @param name the name of acceptor
     * @param socket the DatagramSocket from which to 
     * @param handlerFactory the factory from which to aquire handlers
     * @param threadPool the thread pool to use
     * @exception Exception if an error occurs
     */
    public synchronized void connect( final String name, 
                                      final DatagramSocket socket,
                                      final PacketHandlerFactory handlerFactory,
                                      final ThreadPool threadPool )
        throws Exception
    {
        if( null != m_acceptors.get( name ) )
        {
            throw new IllegalArgumentException( "Acceptor already exists with name " + 
                                                name );
        }

        final Acceptor acceptor = new Acceptor( socket, handlerFactory, threadPool );
        setupLogger( acceptor );
        m_acceptors.put( name, acceptor );
        threadPool.execute( acceptor );
    }
    
    /**
     * Start managing a DatagramSocket. 
     * This is similar to other connect method except that it uses default thread pool.
     *
     * @param name the name of DatagramSocket
     * @param socket the DatagramSocket from which to 
     * @param handlerFactory the factory from which to aquire handlers
     * @exception Exception if an error occurs
     */
    public synchronized void connect( final String name, 
                                      final DatagramSocket socket, 
                                      final PacketHandlerFactory handlerFactory )
        throws Exception
    {
        connect( name, socket, handlerFactory, m_context.getDefaultThreadPool() );
    }
    
    /**
     * This shuts down all handlers and socket, waiting for each to gracefully shutdown.
     *
     * @param name the name of packet
     * @exception Exception if an error occurs
     */
    public synchronized void disconnect( final String name )
        throws Exception
    {
        disconnect( name, false );
    }
    
    /**
     * This shuts down all handlers and socket. 
     * If tearDown is true then it will forcefully shutdown all acceptors and try 
     * to return as soon as possible. Otherwise it will behave the same as 
     * void disconnect( String name );
     *
     * @param name the name of acceptor
     * @param tearDown if true will forcefully tear down all handlers
     * @exception Exception if an error occurs
     */
    public synchronized void disconnect( final String name, final boolean tearDown )
        throws Exception
    {
        final Acceptor acceptor = (Acceptor)m_acceptors.remove( name );
        if( null == acceptor )
        {
            throw new IllegalArgumentException( "No such acceptor with name " +
                                                name );
        }

        //TODO: Stop ignoring tearDown
        acceptor.dispose();
    }
}
