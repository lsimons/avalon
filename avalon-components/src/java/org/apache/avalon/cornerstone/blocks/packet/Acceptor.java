/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Apache Avalon", "Avalon Cornerstone", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
*/

package org.apache.avalon.cornerstone.blocks.packet;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.avalon.cornerstone.services.packet.PacketHandler;
import org.apache.avalon.cornerstone.services.packet.PacketHandlerFactory;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.excalibur.thread.ThreadPool;

/**
 * Support class for the DefaultPacketManager.
 * This manages an individual DatagramSocket.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
class Acceptor
    extends AbstractLogEnabled
    implements Runnable
{
    private final DatagramSocket m_datagramSocket;
    private final PacketHandlerFactory m_handlerFactory;
    private final ThreadPool m_threadPool;
    private final ArrayList m_runners = new ArrayList();

    private Thread m_thread;

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
        synchronized( this )
        {
            if( null != m_thread )
            {
                final Thread thread = m_thread;
                m_thread = null;
                thread.interrupt();

                //Can not join as threads are part of pool
                //and will never finish
                //m_thread.join();

                wait( /*1000*/ );
            }
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

        next:
        while( null != m_thread && !Thread.interrupted() )
        {
            try
            {
                //TODO: packets should really be pooled...
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
                    continue next;
                }

                m_datagramSocket.receive( packet );
                final PacketHandler handler = m_handlerFactory.createPacketHandler();
                final PacketHandlerRunner runner =
                    new PacketHandlerRunner( packet, m_runners, handler );
                setupLogger( runner );
                m_threadPool.execute( runner );
            }
            catch( final InterruptedIOException iioe )
            {
                //Consume exception
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

        synchronized( this )
        {
            notifyAll();
            m_thread = null;
        }
    }
}

class PacketHandlerRunner
    extends AbstractLogEnabled
    implements Runnable
{
    private DatagramPacket m_packet;
    private Thread m_thread;
    private ArrayList m_runners;
    private PacketHandler m_handler;

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
