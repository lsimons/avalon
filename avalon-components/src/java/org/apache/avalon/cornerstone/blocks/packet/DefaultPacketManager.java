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

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.avalon.cornerstone.services.packet.PacketHandlerFactory;
import org.apache.avalon.cornerstone.services.packet.PacketManager;
import org.apache.avalon.cornerstone.services.threads.ThreadManager;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * This is the service through which PacketManagement occurs.
 *
 * @phoenix:block
 * @phoenix:service name="org.apache.avalon.cornerstone.services.packet.PacketManager"
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public class DefaultPacketManager
    extends AbstractLogEnabled
    implements PacketManager, Serviceable, Disposable
{
    private HashMap m_acceptors = new HashMap();
    private ThreadManager m_threadManager;

    /**
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.threads.ThreadManager"
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_threadManager = (ThreadManager)serviceManager.lookup( ThreadManager.ROLE );
    }

    public void dispose()
    {
        final Iterator names = ( (HashMap)m_acceptors.clone() ).keySet().iterator();
        while( names.hasNext() )
        {
            final String name = (String)names.next();
            try
            {
                disconnect( name );
            }
            catch( final Exception e )
            {
                getLogger().warn( "Error disconnecting " + name, e );
            }
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

        //Make sure timeout is specified for socket.
        if( 0 == socket.getSoTimeout() )
        {
            socket.setSoTimeout( 500 );
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
        connect( name, socket, handlerFactory, m_threadManager.getDefaultThreadPool() );
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
