/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.ext.DequeueInterceptor;
import org.apache.excalibur.event.ext.DequeueInterceptorSource;
import org.apache.excalibur.event.seda.NoSuchSinkException;
import org.apache.excalibur.event.seda.SinkMap;
import org.apache.excalibur.event.seda.Stage;
import org.apache.excalibur.event.socket.AsyncSelectorDequeueInterceptor;
import org.apache.excalibur.event.socket.ReadWriteSocketState;
import org.apache.excalibur.event.socket.SocketConstants;
import org.apache.excalibur.nbio.AsyncConnectionManager;
import org.apache.excalibur.nbio.AsyncSelectableServerSocket;
import org.apache.excalibur.nbio.AsyncSelectableSocket;
import org.apache.excalibur.nbio.AsyncSelection;
import org.apache.excalibur.nbio.AsyncSelector;

/**
 * Represents an implementation of an asynchronous server socket
 * SEDA event handler.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultAsyncServerSocketHandler extends AbstractLogEnabled
    implements AsyncServerSocketHandler, Stage, 
        Serviceable, Disposable, Configurable, Initializable
{
    /** The IO connection manager instance */
    protected AsyncConnectionManager m_connectionManager = null;

    /** A selector for channel events */
    private AsyncSelector m_selector = null;
    
    /** The service manager and selector managing this component. */
    private ServiceManager m_serviceManager;

    /** The configuration for this handler */
    private Configuration m_configuration;
    
    /** The write sink. */
    private Sink m_writeSink = null;
    
    /** The read sink. */
    private Sink m_readSink = null;
    
    /** The listen sink. */
    private Sink m_listenSink = null;
    
    /** Maximum number of accepts to process at once */
    private int m_maxAcceptsAtOnce = SocketConstants.MAX_ACCEPTS_AT_ONCE;
    
    /** The size of the internal read buffer in bytes */
    private int m_readBufferSize = SocketConstants.READ_BUFFER_SIZE;

    //------------------------ AsyncServerSocketHandler implementation
    /**
     * @see AsyncServerSocketHandler#accept(AsyncSelection)
     */
    public void accept(AsyncSelection selectionKey) throws IOException
    {
        final ListenSocketState listenState =
            (ListenSocketState) selectionKey.attachment();
        selectionKey.clear();
            
        // Try to do as many accepts as we can in one go
        for (int i = 0; i < m_maxAcceptsAtOnce; i++)
        {
            if (listenState.isClosed())
            {
                if(getLogger().isInfoEnabled())
                {
                    getLogger().info("Channel closed in accept.");
                }
                break;
            }

            final AsyncSelectableServerSocket serverChannel = 
                listenState.getServerSocketChannel();
            final AsyncServerSocket serverSocket = listenState.getServerSocket();
            
            try
            {
                final AsyncSelectableSocket channel = serverChannel.accept();
                if (channel == null)
                {
                    break;
                }

                //channel.configureBlocking(false);

                //final Socket socket = channel.socket();
                final InetAddress address = channel.getInetAddress();
                final int port = channel.getPort();
                final Sink queue = listenState.getCompletionQueue();
                
                final DefaultAsyncTcpConnection con =
                    new DefaultAsyncTcpConnection(
                        m_writeSink, m_readSink, queue, serverSocket, address, port);
                
                // create a new socket state for communication
                final int threshold = listenState.getWriteClogThreshold();
                final ReadWriteSocketState socketState =
                    new ReadWriteSocketState(con, channel, threshold, m_readBufferSize);
    
                con.setSockState(socketState);
                
                try
                {
                    listenState.getCompletionQueue().enqueue(con);
                }
                catch (SinkException e)
                {
                    if(getLogger().isErrorEnabled())
                    {
                        getLogger().error("Couldn't enqueue new connection", e);
                    }
                }
                //return connection;
            }
            catch (SocketException e)
            {
                if (getLogger().isErrorEnabled())
                {
                    getLogger().error("SocketException during accept.", e);
                }
                //break;
                throw e;
            }
            catch (IOException e)
            {
                if (getLogger().isInfoEnabled())
                {
                    getLogger().info("IOException during accept.", e);
                }
                
                final ServerSocketClosedEvent dead =
                    new ServerSocketClosedEvent(serverSocket);

                listenState.getCompletionQueue().tryEnqueue(dead);
                listenState.getSelectionKey().close();
                
                // re-throw IOException
                throw e;
            }
        }

        if(getLogger().isDebugEnabled())
        {
            getLogger().debug("No more sockets to serve...");
        }
    }
    
    /**
     * @see AsyncServerSocketHandler#close(ServerSocketCloseRequest)
     */
    public void close(ServerSocketCloseRequest closeRequest)
    {
        final ListenSocketState listenSockState =
            closeRequest.getListenSocketState();
        
        // OK for lss to be null if closed down already
        if (listenSockState != null && !listenSockState.isClosed())
        {
            listenSockState.getSelectionKey().close();
            try
            {
                listenSockState.getServerSocketChannel().close();
            }
            catch (IOException e)
            {
                if (getLogger().isErrorEnabled())
                {
                    getLogger().error("IOException during closing.", e);
                }
                // Ignore
            }
        
            listenSockState.setClosed();
        
            final AsyncServerSocket serverSocket =
                listenSockState.getServerSocket();
            final ServerSocketClosedEvent closed =
                new ServerSocketClosedEvent(serverSocket);
            
            listenSockState.getCompletionQueue().tryEnqueue(closed);
        }
    }
    
    /**
     * @see AsyncServerSocketHandler#resume(ResumeAcceptRequest)
     */
    public void resume(ResumeAcceptRequest resumeRequest)
    {
        final ListenSocketState listenSockState =
            resumeRequest.getListenSocketState();
        
        if (listenSockState == null)
        {
            if (getLogger().isErrorEnabled())
            {
                getLogger().error("Socket state is null.");
            }
            throw new NullPointerException(
                "ResumeAcceptRequest for server socket "
                    + resumeRequest.getServerSocket()
                    + " with null ListenSocketState!");
        }
        
        if(!listenSockState.isClosed())
        {
            final AsyncSelection key = listenSockState.getSelectionKey();
            key.subscribeAccept(true);
        }
    }
    
    /**
     * @see AsyncServerSocketHandler#resume(ResumeAcceptRequest)
     */
    public void suspend(SuspendAcceptRequest suspendRequest)
    {
        
        final ListenSocketState listenSockState =
            suspendRequest.getListenSocketState();
        
        if (listenSockState == null)
        {
            if (getLogger().isErrorEnabled())
            {
                getLogger().error("Socket state is null.");
            }
            throw new NullPointerException(
                "SuspendAcceptRequest for server socket "
                    + suspendRequest.getServerSocket()
                    + " with null ListenSocketState!");
        }
        
        if(!listenSockState.isClosed())
        {
            final AsyncSelection key = listenSockState.getSelectionKey();
            key.subscribeAccept(false);
        }
    }

    /**
     * @see AsyncServerSocketHandler#listen(ListenRequest)
     */
    public void listen(ListenRequest listen)
    {
        try
        {
            final AsyncSelectableServerSocket channel = 
                m_connectionManager.createAsyncSelectableServerSocket();
            channel.open();
            //channel.configureBlocking(false);
            channel.bind(listen.getPort());
        
            final AsyncSelection key = m_selector.register(channel);
            key.subscribeAccept(true);
            final ListenSocketState listenState =
                new ListenSocketState(listen, key, channel);
        
            key.attach(listenState);
        
            /////////////////////////////////////////////////////////
            final AsyncServerSocket socket = listen.getServerSocket();
            ((DefaultAsyncServerSocketFactory.DefaultAsyncServerSocket)socket)
                .setListenSockState(listenState);
            /////////////////////////////////////////////////////////
        
            listen.getCompletionQueue().tryEnqueue(
                new ListenSuccessEvent(listen.getServerSocket()));
        }
        catch (IOException e)
        {
            // Can't create socket - probably because the address was 
            // already in use
            final String message =
                "Cannot create socket. " + e.getMessage();
            
            if (getLogger().isErrorEnabled())
            {
                getLogger().error(message, e);
            }
            
            final ListenFailedEvent event =
                new ListenFailedEvent(listen.getServerSocket(), message);
        
            listen.getCompletionQueue().tryEnqueue(event);
        }
    }

    //-------------------------- Stage implementation
    /**
     * @see Stage#setSinkMap(SinkMap)
     */
    public void setSinkMap(SinkMap map) throws NoSuchSinkException
    {
        final Configuration sink = m_configuration.getChild("sinks", true);
        
        final String write = sink.getAttribute("write-sink", "write");
        m_writeSink = map.getSink(write);
        final String read = sink.getAttribute("read-sink", "read");
        m_readSink = map.getSink(read);
        final String listen = sink.getAttribute("listen-sink", "listen");
        m_listenSink = map.getSink(listen);

        m_selector = m_connectionManager.createAsyncSelector();
        
        final DequeueInterceptorSource listenSource = 
            (DequeueInterceptorSource)m_listenSink;
        final DequeueInterceptor interceptor = 
            new AsyncSelectorDequeueInterceptor(m_selector);
        ContainerUtil.enableLogging(interceptor, getLogger());
        listenSource.setDequeueInterceptor(interceptor);
    }
    
    //-------------------------- Initializable implementation
    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        m_connectionManager = (AsyncConnectionManager) 
            m_serviceManager.lookup(AsyncConnectionManager.ROLE);
        
        m_readBufferSize = m_configuration.getAttributeAsInteger(
            "read-length", m_readBufferSize);
        m_maxAcceptsAtOnce = m_configuration.getAttributeAsInteger(
            "max-accepts", m_maxAcceptsAtOnce);
    }

    //-------------------------- Disposable implementation
    /**
     * @see Disposable#dispose()
     */
    public void dispose()
    {
        m_selector.close();
        m_serviceManager.release(m_connectionManager);
    }
    
    //-------------------------- Configurable implementation
    /**
     * @see Configurable#configure(Configuration)
     */
    public void configure(Configuration configuration)
    {
        m_configuration = configuration;
    }

    //-------------------------- Serviceable implementation
    /**
     * @see Serviceable#service(ServiceManager)
     */
    public void service(ServiceManager serviceManager)
    {
        m_serviceManager = serviceManager;
    }
}