/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket;

import java.io.IOException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkClosedException;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.SinkFullException;
import org.apache.excalibur.event.ext.DequeueInterceptor;
import org.apache.excalibur.event.ext.DequeueInterceptorSource;
import org.apache.excalibur.event.seda.NoSuchSinkException;
import org.apache.excalibur.event.seda.SinkMap;
import org.apache.excalibur.event.seda.Stage;
import org.apache.excalibur.nbio.AsyncConnectionManager;
import org.apache.excalibur.nbio.AsyncSelectable;
import org.apache.excalibur.nbio.AsyncSelectableReader;
import org.apache.excalibur.nbio.AsyncSelectableWriter;
import org.apache.excalibur.nbio.AsyncSelection;
import org.apache.excalibur.nbio.AsyncSelector;

/**
 * Write stage used to handle socket write events. The default 
 * queue for this stage must be the read sink stage.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public abstract class AbstractAsyncSocketHandlerBase extends AbstractLogEnabled
    implements AsyncSocketHandlerBase, Stage, 
        Serviceable, Disposable, Configurable, Initializable
{
    /** The handler service's configuration */
    protected Configuration m_configuration = null;
    
    /** The IO connection manager instance */
    protected AsyncConnectionManager m_connectionManager = null;
    
    /** A selector for write channel events */
    protected AsyncSelector m_writeSelector = null;
    
    /** A selector for read channel events */
    protected AsyncSelector m_readSelector = null;
    
    /** The service manager and selector managing this component. */
    protected ServiceManager m_serviceManager;

    /** The write sink. */
    protected Sink m_writeSink = null;
    
    /** The read sink. */
    protected Sink m_readSink = null;
    
    /** Number of times to try to finish a socket write */
    private int m_tryWriteSpin = SocketConstants.TRYWRITE_SPIN;
    /** Maximum number of write reqs on a socket to process at once */
    private int m_maxWriteRequests = SocketConstants.MAX_WRITE_REQS_PER_SOCKET;
    /** Number of empty writes after which write-ready mask is disabled.
        If set to -1, no disable will occur. */
    private int m_threshold = SocketConstants.WRITE_MASK_DISABLE_THRESHOLD;
    /** Maximum number of bytes to try writing at once; -1 if no limit */
    private int m_maxWriteLength = SocketConstants.MAX_WRITE_LEN;

    //-------------------------- AbstractAsyncSocketHandlerBase abstract methods
    /**
     * Creates an incoming packet from the passed in socket 
     * state with the specific length.
     * @since Sep 24, 2002
     * 
     * @param length
     *  The length of the packet.
     * @param socketState
     *  The state of the socket with the read buffer
     * @return Object
     *  The created incoming packet
     */
    protected abstract Object createIncomingPacket(
        ReadWriteSocketState socketState, final int length);
        
    //-------------------------- AsyncSocketHandlerBase implementation
    /**
     * @see AsyncDatagramSocketHandler#flush(FlushRequest)
     */
    public void flush(FlushRequest flush)
    {
        final ReadWriteSocketState sockState = flush.getSocketState();
        addRequest(flush, sockState);
    }

    /**
     * @see AsyncSocketHandler#close(CloseRequest)
     */
    public void close(CloseRequest close)
    {
        final ReadWriteSocketState sockState = close.getSocketState();
        // If there is no pending outgoing data, do immediate close
        if (!sockState.isWriteOutStanding())
        {
            close(sockState, close.getCompletionQueue());
        }
        else
        {
            // Queue it up
            addRequest(close, sockState);
        }
    }

    /**
     * @see AsyncSocketHandler#startWrite(WriteRequest)
     */
    public void startWrite(WriteRequest write)
    {
        final ReadWriteSocketState sockState = write.getSocketState();
        // If already closed, just drop it
        if (!sockState.isClosed())
        {
            //Adding write req to sockState
            if (!addRequest(write, sockState))
            {
                // Couldn't enqueue: this connection is clogged
                Sink completionQueue =
                    write.getBufferElement().getCompletionQueue();
                if (completionQueue != null)
                {
                    completionQueue = 
                        sockState.getConnection().getCompletionQueue();
                }
                if (completionQueue != null)
                {
                    final ConnectionCloggedEvent sce =
                        new ConnectionCloggedEvent(
                            write.getConnection(),
                            write.getBufferElement());
                    completionQueue.tryEnqueue(sce);
                }
            }
        }
    }

    /**
     * @see AsyncSocketHandler#startRead(ReadRequest)
     */
    public void startRead(ReadRequest start)
    {
        final ReadWriteSocketState sockState = start.getSocketState();
        synchronized (sockState)
        {
            // This must be synchronized with close() 
            if (sockState.isClosed())
            {
                // May have been closed already
                if(getLogger().isDebugEnabled())
                {
                    getLogger().debug("Socket already closed");
                }
                return;
            }

            sockState.setCompletionQueue(start.getCompletionQueue());
            sockState.setReadClogTries(start.getCloggedReadAttempts());
            
            final AsyncSelectable channel = sockState.getSelectable();
            final AsyncSelection key = m_readSelector.register(channel);
            key.subscribeRead(true);
            sockState.setReadSelectionKey(key);

            if (key != null)
            {
                key.attach(sockState);
            }
            else
            {
                if (getLogger().isErrorEnabled())
                {
                    getLogger().error("Register returned null selection key");
                }
            }
        }
    }

    /**
     * @see AsyncDatagramSocketHandler#read(AsyncSelection)
     */
    public void read(AsyncSelection selectionKey)
    {
        final Object attach = selectionKey.attachment();
        final ReadWriteSocketState socketState = (ReadWriteSocketState) attach;
        // When using SelectSource, we need this guard, 
        // since after closing a socket we may have 
        // outstanding read events still in the queue
        if (socketState.isClosed())
        {
            if(getLogger().isDebugEnabled())
            {
                getLogger().debug("Socket already closed");
            }
            return;
        }
        
        final Object clogged = socketState.getCloggedReadElement();
        if (clogged != null)
        {
            // First try to drain the clogged element
            try
            {
                socketState.getCompletionQueue().enqueue(clogged);
            }
            catch (SinkFullException qfe)
            {
                // Nope, still clogged
                if (socketState.hasCloggedReadAttemptsExceeded())
                {
                    if (getLogger().isWarnEnabled())
                    {
                        getLogger().warn(
                            "Read attempts exceeded, dropping " + clogged);
                    }
                    socketState.resetCloggedReadAttempts();
                }
                else
                {
                    socketState.incrementCloggedReadAttempts();
                    // Try again later
                    return;
                }
            }
            catch (SinkException sce)
            {
                // Whoops - user went away - just drop
                close(socketState, null);
            }
        }
        else
        {
            readRaw(socketState);
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
        
        m_writeSelector = m_connectionManager.createAsyncSelector();
        m_readSelector = m_connectionManager.createAsyncSelector();
        
        final DequeueInterceptorSource readSource = 
            (DequeueInterceptorSource)m_readSink;
        final DequeueInterceptor readInterceptor = 
            new AsyncSelectorDequeueInterceptor(m_readSelector);
        ContainerUtil.enableLogging(readInterceptor, getLogger());
        readSource.setDequeueInterceptor(readInterceptor);

        final DequeueInterceptorSource writeSource = 
            (DequeueInterceptorSource)m_writeSink;
        final DequeueInterceptor writeInterceptor = 
            new AsyncSelectorDequeueInterceptor(m_writeSelector);
        ContainerUtil.enableLogging(writeInterceptor, getLogger());
        writeSource.setDequeueInterceptor(writeInterceptor);
    }

    //-------------------------- Initializable implementation
    /**
     * @see Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        m_connectionManager = (AsyncConnectionManager) 
            m_serviceManager.lookup(AsyncConnectionManager.ROLE);

        m_tryWriteSpin = m_configuration.getAttributeAsInteger(
            "write-spin", m_tryWriteSpin);
        m_maxWriteRequests = m_configuration.getAttributeAsInteger(
            "max-write-requests", m_maxWriteRequests);
        m_threshold = m_configuration.getAttributeAsInteger(
            "write-disable-threshold", m_threshold);
        m_maxWriteLength = m_configuration.getAttributeAsInteger(
            "write-length", m_maxWriteLength);
    }

    //-------------------------- Disposable implementation
    /**
     * @see Disposable#dispose()
     */
    public void dispose()
    {
        m_writeSelector.close();
        m_readSelector.close();
        m_serviceManager.release(m_connectionManager);
    }
    
    //-------------------------- Configurable implementation
    /**
     * @see Configurable#configure(Configuration)
     */
    public void configure(Configuration configuration)
        throws ConfigurationException
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

    //--------------------------- DefaultWritingStage specific implementation
    /**
     * Processes as many as possible socket requests
     * from the socket state.
     * @since Aug 27, 2002
     * 
     * @param sockState
     *  The socket state containing the socket requests
     */
    protected void handleSocketState(ReadWriteSocketState sockState) //throws IOException
    {
        // Socket already closed; just forget about it
        if (!sockState.isClosed())
        {
            if (!sockState.isWriteOutStanding())
            {
                disableSocket(sockState);
            }
            else
            {
                // Avoid doing too many things on each socket
                final int max = m_maxWriteRequests;

                // this can happen if someone closes the socket 
                // while we are processing writes.
                for (int processed = 0; processed < max; processed++)
                {
                    final Object request = sockState.peekWriteRequest();
                    if (!handleRequest(sockState, request))
                    {
                        return;
                    }
                }
            }
        }
    }

    /**
     * Processes the request and delegates to either
     * {@link #handleFlush(ReadWriteSocketState, FlushRequest)} or
     * {@link #handleWrite(ReadWriteSocketState, WriteRequest)}, or
     * closes the connection upon a close request.
     * @since Aug 27, 2002
     * 
     * @param request
     *  The request to dispatch
     * @param sockState
     *  The socket state with the logical connections 
     *  internal information
     */
    private boolean handleRequest(ReadWriteSocketState sockState, Object request)
    {
        if (request == null
            || request.equals(sockState.getCurrentWriteRequest()))
        {
            // Skip if locked
            return false;
        }
        else if (request instanceof WriteRequest)
        {
            // Handle write request
            final WriteRequest write = (WriteRequest) request;
            return handleWrite(sockState, write);
        }
        else if (request instanceof FlushRequest)
        {
            final FlushRequest flush = (FlushRequest) request;
            return handleFlush(sockState, flush);
        }
        else if (request instanceof CloseRequest)
        {
            final CloseRequest close = (CloseRequest) request;
            // OK - by the time we have the lock we can claim 
            // the close is done
            close(sockState, close.getCompletionQueue());
            return false;
        }
        else
        {
            if(getLogger().isDebugEnabled())
            {
                getLogger().debug("Invalid incoming request: " + request);
            }
            throw new IllegalArgumentException(
                "Invalid incoming request: " + request);
        }
    }

    /**
     * Initiates a flush on the connection represented
     * by the passed in socket state.
     * @since Aug 27, 2002
     * 
     * @param sockState
     *  The socket state representing the connection to
     *  be flushed
     * @param flush
     *  The flush request providing a completion queue.
     */
    private boolean handleFlush(ReadWriteSocketState sockState, FlushRequest flush)
    {
        // OK - by the time we have the lock we can claim
        // the flush is done
        if (flush.getCompletionQueue() != null)
        {
            // JRVB: added check to avoid NullPointerException
            final AsyncConnection con = flush.getConnection();
            final ConnectionFlushedEvent event =
                new ConnectionFlushedEvent(con);
            flush.getCompletionQueue().tryEnqueue(event);
        }

        // Clear the request
        if (!sockState.isClosed())
        {
            sockState.popWriteRequest();
            sockState.setWriteRequestCompleted();
            return true;
        }
        return false;
    }

    /**
     * Initiates a write on the connection represented
     * by the passed in socket state.
     * @since Aug 27, 2002
     * 
     * @param sockState
     *  The socket state representing the connection to
     *  be written
     * @param write
     *  The write request providing a completion queue
     *  and the data to be written.
     */
    private boolean handleWrite(ReadWriteSocketState sockState, WriteRequest write)
    {
        final Buffer buf = write.getBufferElement();

        if (sockState.getCurrentWriteRequest() == null)
        {
            sockState.setCurrentWriteRequest(write);
            final int offset = buf.getOffset();
            final byte[] data = buf.getData();
            final int size = buf.getSize();

            sockState.allocateWriteBuffer(data, offset, size);
        }

        boolean done = false;
        try
        {
            // Try hard to finish this packet
            for (int i = 0; i < m_tryWriteSpin && !done; i++)
            {
                done = tryWrite(sockState);
            }
        }
        catch (SinkClosedException e)
        {
            if(getLogger().isErrorEnabled())
            {
                getLogger().error("Connection closed during write. ", e);
            }
            // OK, the socket closed underneath us
            // XXX MDW: Taking this out for now - expect the SinkClosedEvent
            // to be pushed up when read() fails

            //SinkIF cq = wreq.buf.getCompletionQueue();
            //if (cq != null) {
            //  SinkClosedEvent sce = new SinkClosedEvent(wreq.conn);
            //  cq.enqueue_lossy(sce);
            //}
        }

        if (done)
        {
            // Finished this write
            sockState.setWriteRequestCompleted();

            // Send completion upcall
            Sink completionQueue = buf.getCompletionQueue();
            if (completionQueue != null)
            {
                completionQueue = 
                    sockState.getConnection().getCompletionQueue();
            }
            if (completionQueue != null)
            {
                final AsyncConnection con = sockState.getConnection();
                final ConnectionDrainedEvent event =
                    new ConnectionDrainedEvent(con, buf);
                completionQueue.tryEnqueue(event);
            }

            // Clear the request
            if (!sockState.isClosed())
            {
                sockState.popWriteRequest();
                return true;
            }
        }
        // Don't want to process anything else
        return false;
    }

    /**
     * Tries to disable the socket represented by the 
     * passed in socket state object. Checks the configured
     * threshold and then manipulates the selection key.
     * @since Aug 27, 2002
     * 
     * @param sockState
     *  The socket state representing the internal state
     *  of the connection.
     */
    private void disableSocket(ReadWriteSocketState sockState)
    {
        sockState.incrementEmptyWritesCount();

        final int threshold = m_threshold;
        if (threshold != -1 && sockState.getEmptyWritesCount() >= threshold)
        {
            final AsyncSelection key = sockState.getWriteSelectionKey();
            key.subscribeWrite(false);
        }
    }

    /**
     * Adds the passed in write request to the socket's 
     * state. If the writing has not started yet it will 
     * set up writing on the socket channel.
     * @since Aug 27, 2002
     * 
     * @param socketRequest
     *  The request to be added to the socket's state
     * @param socketState
     *  The socket state where the request is queued for 
     *  handling.
     */
    private boolean addRequest(Object socketRequest, ReadWriteSocketState socketState)
    {
        // This is synchronized with close() to avoid a race with close()
        // removing from vector while this method is being called.
        synchronized (socketState)
        {
            if (socketState.isClosed())
            {
                if(getLogger().isDebugEnabled())
                {
                    getLogger().debug("Socket already closed.");
                }
                return false;
            }

            if (!socketState.isWriteStarted())
            {
                final AsyncSelectable channel = socketState.getSelectable();
                final AsyncSelection key = m_writeSelector.register(channel);
                key.subscribeWrite(true);

                if (key == null)
                {
                    if (getLogger().isWarnEnabled())
                    {
                        getLogger().warn("Registration returned null key");
                    }
                    return false;
                }

                socketState.setWriteSelectionKey(key);
                key.attach(socketState);

                //m_ActiveWriteSocketsCount++;

                socketState.setWriteStarted();
            }
            else if (!socketState.isWriteOutStanding())
            {
                socketState.resetEmptyWritesCount();
                // enable writing
                final AsyncSelection key = socketState.getWriteSelectionKey();
                key.subscribeWrite(true);

                //m_ActiveWriteSocketsCount++;
            }

            if (socketState.hasCloggedWriteAttemptsExceeded())
            {
                if (getLogger().isWarnEnabled())
                {
                    getLogger().warn(
                        "Write Clog threshold exceeded, dropping "
                            + socketRequest);
                }

                if (socketRequest instanceof CloseRequest)
                {
                    // Do immediate close: Assume socket is clogged
                    final CloseRequest closeRequest =
                        (CloseRequest) socketRequest;
                    close(socketState, closeRequest.getCompletionQueue());
                    return true;
                }
                return false;
            }

            socketState.pushWriteRequest(socketRequest);
            socketState.incrementCloggedWriteAttempts();
            return true;
        }
    }

    /**
     * Closes the socket associated with the socket state.
     * Any success evants are enqueued in the specified sink.
     * @since Sep 18, 2002
     * 
     * @param state
     *  The inner state of the socket
     * @param closeEventQueue
     *  The sink to enqueue success events to.
     */
    protected void close(ReadWriteSocketState state, Sink closeEventQueue)
    {
        // This is synchronized to avoid close() interfering with
        // addRequest
        synchronized (state)
        {
            if (!state.isClosed())
            {
                try
                {
                    final AsyncSelection write = state.getWriteSelectionKey();
                    final AsyncSelection read = state.getReadSelectionKey();
                    final AsyncSelectable channel = state.getSelectable();
                    if(null != write)
                    {
                        write.close();
                    }
                    if(null != read)
                    {
                        read.close();
                    }
                    if(null != channel)
                    {
                        channel.close();
                    }
                    // Close the state and eliminate write queue
                    // This introduces a race condition with addWriteRequest() 
                    // need to serialize close() with other queue operations 
                    // on the socket.
                    state.setClosed();
                }
                catch (IOException e)
                {
                    if(getLogger().isErrorEnabled())
                    {
                        getLogger().error("Exception during closing.", e);
                    }
                    // ignore
                }

                if (closeEventQueue != null)
                {
                    final ConnectionClosedEvent closeEvent =
                        new ConnectionClosedEvent(state.getConnection());
                    closeEventQueue.tryEnqueue(closeEvent);
                }
            }
        }
    }
    
    /**
     * Tries to write to the socket represented by the
     * socket state object the byte buffer also in the 
     * state object.
     * @since Aug 27, 2002
     * 
     * @param socketState
     *  The state object contianing the byte buffer to
     *  write and the rest of the socket information.
     */
    protected boolean tryWrite(ReadWriteSocketState socketState)
        throws SinkClosedException
    {
        try
        {
            final int target = socketState.getCurrentWriteLengthTarget();
            final int offset = socketState.getCurrentWriteOffset();

            int tryLen = target - offset;
            if (m_maxWriteLength != -1)
            {
                tryLen = Math.min(tryLen, m_maxWriteLength);
            }

            final byte[] buffer = socketState.getWriteByteBuffer();
            final AsyncSelectableWriter channel = 
                (AsyncSelectableWriter)socketState.getSelectable();
            
            int written = channel.write(buffer, offset, tryLen);
            return socketState.advanceCurrentWriteOffset(written);
        }
        catch (IOException e)
        {
            if(getLogger().isErrorEnabled())
            {
                getLogger().error("IOException during attempt to write.", e);
            }
            
            // Assume this is because socket was already closed
            close(socketState, null);
            throw new SinkClosedException(
                "IOException during attempt to write. ", e);
        }
    }
        
    /** 
     * Performs a read operation using the passed in state
     * of the socket. Enqueues an incoming packet into the
     * read completion queue attached with the scoket state.
     * In the case the sink is full the packet is delivered 
     * later and set as the clogged element in the state.
     * @since Aug 27, 2002
     * 
     * @param socketState
     *  The state object representing the logical socket's
     *  internals.
     */
    protected void readRaw(ReadWriteSocketState socketState)
    {
        try
        {
            final AsyncSelectableReader channel = 
                (AsyncSelectableReader)socketState.getSelectable();
            final int length =
                channel.read(socketState.getReadByteBuffer());
        
            if (length > 0)
            {
                final Object packet =
                    createIncomingPacket(socketState, length);
        
                try
                {
                    socketState.getCompletionQueue().enqueue(packet);
                    //socketState.getReadByteBuffer().rewind();
                }
                catch (SinkFullException e)
                {
                    socketState.setCloggedReadElement(packet);
                    socketState.resetCloggedReadAttempts();
                }
                catch (SinkException e)
                {
                    // User has gone away
                    close(socketState, null);
                }
            }
            else if (length == 0)
            {
                // Sometimes we get an error return result from
                // poll() which causes an attempted read here, but no
                // IOException. For now I am going to just drop the "null"
                // packet - on Linux it seems that certain TCP errors can
                // trigger this.
                if (getLogger().isWarnEnabled())
                {
                    getLogger().warn("Got empty read on socket");
                }
            }
            else if (length < 0)
            {
                // Read failed - assume socket is dead
                close(socketState, socketState.getCompletionQueue());
            }
        }
        catch (IOException e)
        {
            // Read failed - assume socket is dead
            close(socketState, socketState.getCompletionQueue());
        }
    }
}