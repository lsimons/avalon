/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.blocks.packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Component;
import org.apache.excalibur.thread.ThreadPool;
import org.apache.cornerstone.services.packet.PacketHandler;
import org.apache.cornerstone.services.packet.PacketHandlerFactory;

/**
 * Support class for the DefaultPacketManager.
 * This manages an individual DatagramSocket.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class Acceptor
    extends AbstractLoggable
    implements Component, Runnable
{
    protected final DatagramSocket          m_datagramSocket;
    protected final PacketHandlerFactory    m_handlerFactory;
    protected final ThreadPool              m_threadPool;
    protected final ArrayList               m_runners          = new ArrayList();

    protected Thread                           m_thread;

    public Acceptor( final DatagramSocket datagramSocket,
                     final PacketHandlerFactory handlerFactory,
                     final ThreadPool threadPool )
    {
        m_datagramSocket = datagramSocket;
        m_handlerFactory = handlerFactory;
        m_threadPool = threadPool;
    }

    public void dispose()
        throws Exception
    {
        if( null != m_thread )
        {
            m_thread.interrupt();
            m_thread.join( /* 1000 ??? */ );
            m_thread = null;
        }

        final Iterator runners = m_runners.iterator();
        while( runners.hasNext() )
        {
            final PacketHandlerRunner runner = (PacketHandlerRunner)runners.next();
            runner.dispose();
        }

        m_runners.clear();
    }

    public void run()
    {
        m_thread = Thread.currentThread();

        while( !Thread.interrupted() )
        {
            try
            {
                //TODO: packets hould really be pooled...
                DatagramPacket packet = null;

                try
                {
                    final int size = m_datagramSocket.getReceiveBufferSize();
                    final byte[] buffer = new byte[ size ];
                    packet = new DatagramPacket( buffer, size );
                }
                catch( final IOException ioe )
                {
                    getLogger().error( "Failed to get receive buffer size for datagram socket",
                                       ioe );
                }

                m_datagramSocket.receive( packet );
                final PacketHandler handler = m_handlerFactory.createPacketHandler();
                final PacketHandlerRunner runner =
                    new PacketHandlerRunner( packet, m_runners, handler );
                setupLogger( runner );
                m_threadPool.execute( runner );
            }
            catch( final IOException ioe )
            {
                getLogger().error( "Exception accepting connection", ioe );
            }
            catch( final Exception e )
            {
                getLogger().error( "Exception executing runner", e );
            }
        }
    }
}

class PacketHandlerRunner
    extends AbstractLoggable
    implements Runnable, Component
{
    protected DatagramPacket  m_packet;
    protected Thread          m_thread;
    protected ArrayList       m_runners;
    protected PacketHandler   m_handler;

    PacketHandlerRunner( final DatagramPacket packet,
                         final ArrayList runners,
                         final PacketHandler handler )
    {
        m_packet = packet;
        m_runners = runners;
        m_handler = handler;
    }

    public void dispose()
        throws Exception
    {
        if( null != m_thread )
        {
            m_thread.interrupt();
            m_thread.join( /* 1000 ??? */ );
            m_thread = null;
        }
    }

    public void run()
    {
        try
        {
            m_thread = Thread.currentThread();
            m_runners.add( this );

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Starting connection on " + m_packet );
            }

            m_handler.handlePacket( m_packet );

            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Ending connection on " + m_packet );
            }
        }
        catch( final Exception e )
        {
            getLogger().warn( "Error handling packet", e );
        }
        finally
        {
            m_runners.remove( this );
        }
    }
}
