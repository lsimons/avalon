/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import java.io.IOException;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.seda.StageManager;

/**
 * An implementation of the {@link AsyncServerSocketFactory} interface
 * representing a factory for SEDA {@link AsyncServerSocket} server
 * sockets. A server socket acts as a facade to the SEDA style dispatch
 * mechanism. This is a component that has to be installed into the 
 * system to be usable. It can be configured in the following manner:
 * <p>
 * <m_code><pre>
 *   &lt;component&gt;
 *     &lt;sinks listen-sink="stage-name"/&gt;
 *   &lt;/component&gt;
 * </pre></m_code>
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultAsyncServerSocketFactory
    extends AbstractLogEnabled
    implements AsyncServerSocketFactory, Serviceable, Initializable
{
    /** The components service manager. */
    private ServiceManager m_serviceManager;

    /** The server socket handler */
    private AsyncServerSocketHandler m_handler = null;

    //-------------------------- ITcpServerSocketFactory implementation
    /**
     * @see ServerSocketFactory#createServerSocket(int, Sink)
     */
    public AsyncServerSocket createServerSocket(int port, Sink sink)
        throws IOException, SinkException
    {
        return new DefaultAsyncServerSocket(port, sink);
    }

    /**
     * @see ServerSocketFactory#createServerSocket(int, Sink, int)
     */
    public AsyncServerSocket createServerSocket(
        int port,
        Sink sink,
        int threshold)
        throws IOException, SinkException
    {
        return new DefaultAsyncServerSocket(port, sink, threshold);
    }

    //-------------------------- Serviceable implementation
    /**
     * @see Serviceable#service(ServiceManager)
     */
    public void service(ServiceManager serviceManager)
    {
        m_serviceManager = serviceManager;
    }

    //------------------------- Initializable implementation
    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        final StageManager stageManager =
            (StageManager) m_serviceManager.lookup(StageManager.ROLE);
        try
        {
            final ServiceManager manager = stageManager.getServiceManager();
            final String role = AsyncServerSocketHandler.ROLE;
            m_handler = (AsyncServerSocketHandler)manager.lookup(role);
        }
        finally
        {
            m_serviceManager.release(stageManager);
        }
    }

    //-------------------------- DefaultAsyncServerSocketFactory inner classes
    /** 
     * This class represents an asynchronous server socket.
     * An application creates a AsyncServerSocket to listen 
     * for incoming TCP connections on a given port; when 
     * a connection is received, a AsyncTcpConnection object is 
     * pushed to the Sink associated with the AsyncServerSocket. 
     * The AsyncTcpConnection is then used for communication.
     *
     * @version $Revision: 1.1 $
     * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
     */
    final class DefaultAsyncServerSocket implements AsyncServerSocket
    {
        /** Internal state used by the Socket implementation */
        private ListenSocketState m_listenSockState;

        /** The port on which to accept socket connections */
        private final int m_serverPort;

        //-------------------------- DefaultAsyncServerSocket constructors
        /**
         * Open a server socket listening on the given port. 
         * When a connection arrives, a {@link AsyncTcpConnection}
         * will be posted to the given completionQueue. If the 
         * server socket dies, an {@link ServerSocketClosedEvent}
         * will be posted instead.
         * @since May 21, 2002)
         * 
         * @param serverPort
         *  The port on which to accept socket connections
         * @param completionQueue
         *  The queue on which events are enqueued
         * @throws IOException 
         *  When an IOException ocurrs.
         */
        DefaultAsyncServerSocket(int serverPort, Sink completionQueue)
            throws IOException, SinkException
        {
            m_serverPort = serverPort;
            m_handler.listen(
                new ListenRequest(this, serverPort, completionQueue, -1));
        }

        /**
         * Open a server socket listening on the given port. 
         * When a connection arrives, a {@link AsyncTcpConnection}
         * will be posted to the given completion queue. If the 
         * server socket dies, a {@link ServerSocketClosedEvent} 
         * will be posted instead.
         * @since May 21, 2002)
         * 
         * @param serverPort
         *  The port at which to accept socket connections
         * @param completionQueue 
         *  The queue on which events are enqueued
         * @param writeClogThreshold 
         *  The maximum number of outstanding write requests to a 
         *  connection established using this socket before a
         *  {@link SinkCloggedEvent} is pushed onto the completion 
         *  queue for that connection. The default value is 
         *  <m_code>-1</m_code>, which indicates that no 
         *  {@link SinkCloggedEvents} will be generated.
         * @throws IOException 
         *  When an IOException ocurrs.
         */
        DefaultAsyncServerSocket(
            int serverPort,
            Sink completionQueue,
            int writeClogThreshold)
            throws IOException, SinkException
        {
            m_serverPort = serverPort;
            m_handler.listen(
                new ListenRequest(
                    this,
                    serverPort,
                    completionQueue,
                    writeClogThreshold));
        }

        //-------------------------- AsyncServerSocket implementation
        /**
         * @see AsyncServerSocket#suspendAccept()
         */
        public void suspendAccept() throws SinkException
        {
            m_handler.suspend(
                new SuspendAcceptRequest(this, m_listenSockState));
        }

        /**
         * @see AsyncServerSocket#resumeAccept()
         */
        public void resumeAccept() throws SinkException
        {
            m_handler.resume(
                new ResumeAcceptRequest(this, m_listenSockState));
        }

        /**
         * @see AsyncServerSocket#getPort()
         */
        public int getPort()
        {
            return m_serverPort;
        }

        /**
         * @see AsyncServerSocket#getLocalPort()
         */
        public int getLocalPort()
        {
            if (m_listenSockState != null)
            {
                return m_listenSockState.getLocalPort();
            }
            return -1;
        }

        /**
         * @see AsyncServerSocket#close()
         */
        public void close() throws SinkException
        {
            m_handler.close(
                new ServerSocketCloseRequest(this, m_listenSockState));
        }

        //-------------------------- DefaultAsyncServerSocket specific implementation
        /**
         * Allows to set the internal server socket's state.
         * @since May 21, 2002)
         * 
         * @param listenSockState
         *  The internal server socket's listening state.
         */
        void setListenSockState(ListenSocketState listenSockState)
        {
            m_listenSockState = listenSockState;
        }
    }

}