/*
 * Copyright (c) 2001 by Matt Welsh and The Regents of the University of 
 * California. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */

/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.avalon.framework.CascadingError;
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkClosedException;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.ext.AbstractSimpleSink;
import org.apache.excalibur.event.seda.NoSuchSinkException;
import org.apache.excalibur.event.seda.SinkMap;
import org.apache.excalibur.event.seda.Stage;
import org.apache.excalibur.event.socket.AbstractAsyncSocketErrorEvent;
import org.apache.excalibur.event.socket.AsyncConnection;
import org.apache.excalibur.event.socket.Buffer;
import org.apache.excalibur.event.socket.ConnectionCloggedEvent;
import org.apache.excalibur.event.socket.ConnectionClosedEvent;
import org.apache.excalibur.event.socket.ConnectionDrainedEvent;
import org.apache.excalibur.event.socket.ConnectionFlushedEvent;
import org.apache.excalibur.event.socket.SocketInputStream;
import org.apache.excalibur.event.socket.tcp.AsyncServerSocket;
import org.apache.excalibur.event.socket.tcp.AsyncServerSocketFactory;
import org.apache.excalibur.event.socket.tcp.AsyncTcpConnection;
import org.apache.excalibur.event.socket.tcp.IncomingPacket;
import org.apache.excalibur.event.socket.tcp.ListenSuccessEvent;

/**
 * A DefaultHttpConnectorHandler is a stage handler which accepts 
 * incoming connections and dispatches HttpConnections and DefaultHttpRequest
 * to the associated connectors. When a connector is closed, a 
 * HttpConnectorClosedEvent is pushed.
 *
 * @version $Revision: 1.1 $
 * @author Matt Welsh (mdw@cs.berkeley.edu)
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultHttpConnectorHandler extends AbstractLogEnabled
    implements HttpConnectorHandler, Stage, Serviceable, Configurable
{
    /** The handler service's configuration */
    private Configuration m_configuration = null;

    /** The handler service's manager */
    private ServiceManager m_serviceManager = null;

    /** The socket with which the http server is listening */
    protected AsyncServerSocket m_serverSocket = null;

    /** THis handler's event queue */
    protected Sink m_serverSink = null;

    /** AsyncTcpConnection -> DefaultHttpConnection mapping */
    private final Map m_connectionTable = new HashMap();
    /** AsyncTcpConnection -> DefaultHttpConnection mapping */
    private final Map m_connectorTable = new HashMap();

    //-------------------------- Serviceable implementation
    /**
     * @see Serviceable#service(ServiceManager)
     */
    public void service(ServiceManager serviceManager)
    {
        m_serviceManager = serviceManager;
    }

    //-------------------------- Stage implementation
    /**
     * @see Stage#setSinkMap(SinkMap)
     */
    public void setSinkMap(SinkMap map) throws NoSuchSinkException
    {
        final Configuration sink = m_configuration.getChild("sinks", true);

        final String server = sink.getAttribute("handler-sink", "handler");
        m_serverSink = map.getSink(server);
    }

    //-------------------------- Configurable implementation
    /**
     * @see Configurable#configure(Configuration)
     */
    public void configure(Configuration configuration)
    {
        m_configuration = configuration;
    }

    //-------------------------- HttpConnector implementation
    /**
     * @see HttpConnectorHandler#close(CloseRequest)
     */
    public void close(CloseRequest request)
    {
        final HttpConnector connector = request.getConnector();
        final Sink queue = connector.getCompletionQueue();
        final Iterator iter = m_connectorTable.keySet().iterator();
        while(iter.hasNext())
        {
            final Object key = iter.next();
            if(m_connectorTable.get(key).equals(connector))
            {
                m_connectorTable.remove(key);
                queue.tryEnqueue(new HttpConnectorClosedEvent(connector));
                return;
            }
        }
    }
    
    /**
     * @see HttpConnectorHandler#open(OpenRequest)
     */
    public void open(OpenRequest request) throws SinkException, IOException
    {
        final int listenPort = request.getPort();
        final int writeThreshold = request.getWriteClogThreshold();
        final HttpConnector connector = request.getConnector();

        // setup end point for listening
        try
        {
            final String role = AsyncServerSocketFactory.ROLE;
            final AsyncServerSocketFactory serverFactory =
                (AsyncServerSocketFactory) m_serviceManager.lookup(role);
            try
            {
                final AsyncServerSocket serverSocket =
                    serverFactory.createServerSocket(
                        listenPort, m_serverSink, writeThreshold);
                m_connectorTable.put(serverSocket, connector);
            }
            finally
            {
                // forward any IOException
                m_serviceManager.release(serverFactory);
            }
        }
        catch (ServiceException e)
        {
            throw new CascadingRuntimeException(
                "No server socket factory installed.", e);
        }

    }
    
    /**
     * @see HttpConnectorHandler#receive(IncomingPacket)
     */
    public void receive(IncomingPacket packet) throws IOException
    {
        final AsyncTcpConnection connection = packet.getConnection();
        final DefaultHttpConnection httpConnection =
            (DefaultHttpConnection) m_connectionTable.get(connection);

        // Connection may have been closed
        if (httpConnection != null)
        {
            try
            {
                httpConnection.parsePacket(packet);
            }
            catch (IOException e)
            {
                if (getLogger().isErrorEnabled())
                {
                    getLogger().error(
                        "Got IOException during packet processing",
                        e);
                }
                throw e;
                // XXX Should close connection
            }
        }
    }

    /**
     * @see HttpConnectorHandler#accept(AsyncTcpConnection)
     */
    public void accept(AsyncTcpConnection connection)
        throws SinkClosedException, SinkException
    {
        final AsyncServerSocket socket = connection.getServerSocket();
        final HttpConnector connector = 
            (HttpConnector)m_connectorTable.get(socket);
        
        if(connector != null)
        {
            final Sink sink = connector.getCompletionQueue();
            
            final DefaultHttpConnection httpConnection =
                new DefaultHttpConnection(connection, connector);
            // Push to user
            sink.tryEnqueue(httpConnection);
            //connection.read();
            
            m_connectionTable.put(connection, httpConnection);
        }
    }

    /**
     * @see HttpConnectorHandler#socketError(AbstractAsyncSocketErrorEvent)
     */
    public void socketError(AbstractAsyncSocketErrorEvent event)
    {
        if (getLogger().isErrorEnabled())
        {
            getLogger().error("Error on server socket. " + event.getMessage());
        }
        
        final HttpConnector connector = 
            (HttpConnector)m_connectorTable.get(event.getSocket());
        
        if(connector != null)
        {
            final Sink sink = connector.getCompletionQueue();
            sink.tryEnqueue(event);
        }
    }

    /**
     * @see HttpConnectorHandler#connectionDrained(ConnectionDrainedEvent)
     */
    public void connectionDrained(ConnectionDrainedEvent qel)
    {
    }

    /**
     * @see HttpConnectorHandler#connectionFlushed(ConnectionFlushedEvent)
     */
    public void connectionFlushed(ConnectionFlushedEvent event)
    {
        final AsyncConnection connection = event.getConnection();
        // Some connection is clogged; tell the user 
        final DefaultHttpConnection httpConnection =
            (DefaultHttpConnection) m_connectionTable.get(connection);
        if (httpConnection != null)
        {
            httpConnection.getCompletionQueue().tryEnqueue(
                new ConnectionFlushedEvent(httpConnection));
        }
    }

    /**
     * @see HttpConnectorHandler#connectionClogged(ConnectionCloggedEvent)
     */
    public void connectionClogged(ConnectionCloggedEvent event)
    {
        final AsyncConnection connection = event.getConnection();
        // Some connection is clogged; tell the user 
        final DefaultHttpConnection httpConnection =
            (DefaultHttpConnection) m_connectionTable.get(connection);
        if (httpConnection != null)
        {
            httpConnection.getCompletionQueue().tryEnqueue(
                new ConnectionCloggedEvent(httpConnection, null));
        }
    }

    /**
     * @see HttpConnectorHandler#connectionClosed(HttpConnectorClosedEvent)
     */
    public void connectionClosed(ConnectionClosedEvent event)
    {
        final AsyncConnection connection = event.getConnection();
        // Some connection closed; tell the user 
        final DefaultHttpConnection httpConnection =
            (DefaultHttpConnection) m_connectionTable.get(connection);
        if (httpConnection != null)
        {
            httpConnection.getCompletionQueue().tryEnqueue(
                new ConnectionClosedEvent(httpConnection));
            cleanupConnection(httpConnection);
        }
    }

    /**
     * @see HttpConnectorHandler#listenSuccess(ListenSuccessEvent)
     */
    public void listenSuccess(ListenSuccessEvent event)
    {
        final AsyncServerSocket socket = event.getServerSocket();
        final HttpConnector connector = 
            (HttpConnector)m_connectorTable.get(socket);
        
        if(connector != null)
        {
            final Sink sink = connector.getCompletionQueue();
            sink.tryEnqueue(event);
        }
    }

    //-------------------------- DefaultHttpConnectorHandler specific implementation
    /**
     * Cleans up the connection by removing it from the 
     * connection association table.
     * @since Sep 25, 2002
     * 
     * @param m_httpConnection
     *  The default Http Connection containing the connection
     *  which acts as a key to remove the association
     */
    void cleanupConnection(DefaultHttpConnection httpConnection)
    {
        m_connectionTable.remove(httpConnection.getAsyncTcpConnection());
    }

    /**
     * Returns the serverSocket.
     * @return AsyncServerSocket
     */
    public AsyncServerSocket getServerSocket()
    {
        return m_serverSocket;
    }

    /** 
     * Suspend acceptance of new connections on this server.
     * This m_request will not be effective immediately.
     * @since Sep 25, 2002
     */
    public void suspendAccept() throws SinkException
    {
        m_serverSocket.suspendAccept();
    }

    /** 
     * Resume acceptance of new connections on this server.
     * This m_request will not be effective immediately.
     * @since Sep 25, 2002
     */
    public void resumeAccept() throws SinkException
    {
        m_serverSocket.resumeAccept();
    }

    //----------------------------- DefaultHttpConnectorHandler inner classes
    /**
     * This is a inner class which reads HTTP m_request packets.
     * An instance of this class is fed IncomingPackets (via the 
     * <tt>parsePacket</tt> method). When a complete packet has been
     * read, an DefaultHttpRequest is pushed to the corresponding Sink.
     * This is the bulk of the HTTP protocol implementation.
     * @since Sep 25, 2002
     * 
     * @author Matt Welsh
     * @author <a href="mailto:schierma@users.sourceforge.net">schierma</a>
     */
    final class HttpPacketReader
    {
        private static final int STATE_START = 0;
        private static final int STATE_HEADER = 1;
        private static final int STATE_CONTENT = 2;
        private static final int STATE_DONE = 3;

        private int m_currentState;
        private SocketInputStream m_inputStream;
        private StreamTokenizer m_streamTokenizer;

        private String m_request;
        private String m_url;
        private int m_httpVersion;
        private Map m_header;
        private DefaultHttpConnection m_httpConnection;
        private final Sink m_completionQueue;
        private byte[] m_payload;
        
        private int m_position = 0;

        /**
         * Create an HttpPacketReader with the given DefaultHttpConnection
         * and completion queue.
         * @since Sep 25, 2002
         * 
         * @param completionQueue
         *  The queue to enqueue requests to.
         * @param conn
         *  The connection
         */
        HttpPacketReader(DefaultHttpConnection connection, Sink completionQueue)
        {
            m_httpConnection = connection;
            m_inputStream = new SocketInputStream();
            m_completionQueue = completionQueue;
            reset();
        }

        /**
         * Parse the given packet; returns <code>true</code> if 
         * a complete HTTP request has been received and parsed.
         * @since Sep 27, 2002
         */
        boolean parsePacket(IncomingPacket pkt) throws IOException
        {
            m_inputStream.addPacket(pkt);

            // parsing state machine
            int origstate;
            do
            {
                origstate = m_currentState;

                switch (m_currentState)
                {
                    case STATE_START :
                        m_currentState = parseURL();
                        break;
                    case STATE_HEADER :
                        m_currentState = accumulateHeader();
                        break;
                    case STATE_CONTENT :
                        m_currentState = accumulateContent();
                        break;
                    case STATE_DONE :
                        enqueueRequest();
                        reset();
                        return true;
                    default :
                        throw new CascadingError("Bad state", null);
                }
            }
            while (m_currentState != origstate);

            return false;
        }

        /**
         * Reset the internal State of the packet reader.
         * @since Sep 25, 2002
         */
        private void reset()
        {
            m_currentState = STATE_START;
            m_inputStream.clear();

            final Reader reader = new InputStreamReader(m_inputStream);
            m_streamTokenizer = new StreamTokenizer(reader);
            m_streamTokenizer.resetSyntax();
            m_streamTokenizer.wordChars((char) 0, (char) 255);
            m_streamTokenizer.whitespaceChars('\u0000', '\u0020');
            m_streamTokenizer.eolIsSignificant(true);

            m_request = null;
            m_url = null;
            m_header = null;
            m_httpVersion = 0;
            m_payload = null;
            m_position = 0;
        }

        /**
         * Parse the first line of the request header.
         * @since Sep 25, 2002
         * 
         * @return int
         *  The currentState for the tState machine
         * @throws IOException
         *  If an error occurrs reading from a stream
         */
        private int parseURL() throws IOException
        {
            m_inputStream.mark(0);

            final String req = nextWord();
            m_url = nextWord();
            final String ver = nextWord();

            if ((req == null) || (m_url == null) || (ver == null))
            {
                m_inputStream.reset();
                return STATE_START;
            }
            else
            {
                m_request = req;
                if (ver.equals("HTTP/1.0"))
                {
                    m_httpVersion = DefaultHttpRequest.HTTPVER_10;
                    final String tmp = nextWord(); // Throw away EOL
                    return STATE_HEADER;
                }
                else if (ver.equals("HTTP/1.1"))
                {
                    m_httpVersion = DefaultHttpRequest.HTTPVER_11;
                    final String tmp = nextWord(); // Throw away EOL
                    return STATE_HEADER;
                }
                else
                {
                    if (!ver.equals(HttpConstants.CRLF))
                    {
                        throw new IOException(
                            "Unknown HTTP version in m_request: "
                                + m_httpVersion);
                    }
                    m_httpVersion = DefaultHttpRequest.HTTPVER_09;
                    return STATE_DONE;
                }
            }
        }

        /**
         * Accumulate header lines into the internal vector.
         * @since Sep 25, 2002
         * 
         * @return int
         *  The currentState for the State machine
         * @throws IOException
         *  If an error occurrs reading from a stream
         */
        private int accumulateHeader() throws IOException
        {
            while (true)
            {
                final String line = nextLine();
                if (line == null)
                {
                    // End of buffer
                    return STATE_HEADER;
                }
                else if (!line.equals(""))
                {
                    if (m_header == null)
                    {
                        m_header = new HashMap(1);
                    }
                    
                    final int position = line.indexOf(':');
                    if(position != -1)
                    {
                        final String key = line.substring(0, position).trim();
                        final String value = line.substring(position + 1).trim();
                        m_header.put(key, value);
                    }
                    else
                    {
                        if(getLogger().isErrorEnabled())
                        {
                            getLogger().error("Malformed header: " + line);
                        }
                    }
                }
                else
                {
                    return STATE_CONTENT;
                }
            }
        }
        
        /**
         * Accumulate content bytes into the payload buffer.
         * @since Sep 25, 2002
         * 
         * @return int
         *  The currentState for the state machine
         * @throws IOException
         *  If an error occurrs reading from a stream
         */
        private int accumulateContent() throws IOException
        {
            if(m_payload == null)
            {
                final String length = (String)m_header.get("Content-Length");
                if(!m_request.startsWith("POST") || length == null)
                {
                    return STATE_DONE;
                }
                
                try
                {
                    final int size = Integer.parseInt(length);
                    m_payload = new byte[size];
                }
                catch (NumberFormatException e)
                {
                    return STATE_DONE;
                }
            }
            
            while (true)
            {
                final int read = m_inputStream.read(
                    m_payload, m_position, m_payload.length - m_position);
                
                if(getLogger().isDebugEnabled())
                {
                    getLogger().debug("Number of bytes read: " + read);
                }
                
                m_position += Math.max(read, 0);
                
                if (read <= 0)
                {
                    // End of buffer
                    return STATE_CONTENT;
                }
                else if (m_position == m_payload.length)
                {
                    return STATE_DONE;
                }
            }
        }

        /**
         * Process the m_header, possibly pushing an DefaultHttpRequest to 
         * the user.
         * @since Sep 25, 2002
         * 
         * @throws IOException
         *  If an error occurrs reading from a stream
         */
        private void enqueueRequest() throws IOException
        {
            final Buffer payload;
            if(m_payload == null)
            {
                payload = null;
            }
            else
            {
                payload = new Buffer(m_payload);
            }
            
            final DefaultHttpRequest req =
                new DefaultHttpRequest(
                    m_httpConnection,
                    m_request,
                    m_url,
                    m_httpVersion,
                    m_header,
                    payload);

            // Pushing req to user
            if (!m_completionQueue.tryEnqueue(req))
            {
                if (getLogger().isErrorEnabled())
                {
                    getLogger().warn("Could not enqueue request " + req);
                }
            }
        }

        /**
         * Read the next whitespace-delimited word from the 
         * packet.
         * @since Sep 25, 2002
         * 
         * @return String
         *  The next whitespace-delimited word from the packet.
         * @throws IOException
         *  In case of an error reading from the stream.
         */
        private String nextWord() throws IOException
        {
            while (true)
            {
                int type = m_streamTokenizer.nextToken();
                switch (type)
                {
                    case StreamTokenizer.TT_EOL :
                        return HttpConstants.CRLF;
                    case StreamTokenizer.TT_EOF :
                        return null;
                    case StreamTokenizer.TT_WORD :
                        return m_streamTokenizer.sval;
                    case StreamTokenizer.TT_NUMBER :
                        return Double.toString(m_streamTokenizer.nval);
                    default :
                        continue;
                }
            }
        }

        /**
         * Read the next line from the packet.
         * @since Sep 25, 2002
         *
         * @return String
         *  The next line from the packet.
         * @throws IOException
         *  In case of an error reading from the stream.
         */
        private String nextLine() throws IOException
        {
            String line = new String("");
            boolean first = true;

            while (true)
            {
                switch (m_streamTokenizer.nextToken())
                {
                    case StreamTokenizer.TT_EOL :
                        return line;
                    case StreamTokenizer.TT_EOF :
                        return null;
                    case StreamTokenizer.TT_WORD :
                        if (first)
                        {
                            line = m_streamTokenizer.sval;
                            first = false;
                        }
                        else
                        {
                            line += " " + m_streamTokenizer.sval;
                        }
                        break;
                    case StreamTokenizer.TT_NUMBER :
                        if (first)
                        {
                            line = Double.toString(m_streamTokenizer.nval);
                            first = false;
                        }
                        else
                        {
                            line += " "
                                + Double.toString(m_streamTokenizer.nval);
                        }
                        break;
                    default :
                        continue;
                }
            }
        }
    }

    /**
     * This class represents a single HTTP connection. When a 
     * DefaultHttpConnectorHandler receives a connection, a DefaultHttpConnection 
     * is pushed to the user. To send HTTP responses to a client, 
     * you can enqueue an AbstractHttpResponse object on the 
     * corresponding DefaultHttpConnection.
     * @see HttpConnection
     * @see DefaultHttpRequest
     * @see AbstractHttpResponse
     *
     * @author Matt Welsh
     * @author <a href="mailto:schierma@users.sourceforge.net">schierma</a>
     */
    final class DefaultHttpConnection implements HttpConnection, HttpConstants
    {
        /** The Connector */
        private final HttpConnector m_connector;
        
        /** The underlying tcp/ip connection */
        private AsyncTcpConnection m_tcpConnection;

        /** The packet reader helper class */
        private HttpPacketReader m_packetReader;

        /** To associate an arbitrary data object with this connection. */
        public Object userTag;
        
        /** The associated completion queue (client) */
        private Sink m_completionQueue = null;

        //---------------------------- DefaultHttpConnection constructors
        /**
         * Create a DefaultHttpConnection with the given TCP 
         * connection and completion queue.
         * @since Sep 25, 2002
         * 
         * @param tcpConnection
         *  The underlying tcp/ip connection
         * @param connector
         *  The connector
         */
        DefaultHttpConnection(
            AsyncTcpConnection tcpConnection, HttpConnector connector)
        {
            m_tcpConnection = tcpConnection;
            m_connector = connector;
            m_completionQueue = connector.getCompletionQueue();
            m_packetReader = new HttpPacketReader(this, m_completionQueue);
        }

        //---------------------------- HttpConnection
        /**
         * @see HttpConnection#write(AbstractHttpResponse)
         */
        public void write(AbstractHttpResponse response) throws SinkException
        {
            Buffer bufarr[] = response.getBuffers();
            write(bufarr);
        }
        
        /**
         * @see HttpConnection#getConnector()
         */
        public final HttpConnector getConnector()
        {
            return m_connector;
        }

        //---------------------------- AsyncConnection implementation
        /**
         * @see AsyncConnection#close()
         */
        public void close() throws SinkException
        {
            // XXX For now, allow a connection to be closed multiple 
            // times. Tricky bit below: Provide anonymous Sink as 
            // 'completionQueue' which we re-enqueue onto user 
            // completionQueue as appropriate ConnectionDrainedEvent!

            cleanupConnection(this);
            m_tcpConnection.setCompletionQueue(new AbstractSimpleSink()
            {
                public void enqueue(Object event) throws SinkException
                {
                    m_completionQueue.enqueue(
                        new ConnectionDrainedEvent(
                            DefaultHttpConnection.this,
                            event));
                }
            });
            m_tcpConnection.close();
        }

        /**
         * @see AsyncConnection#flush(Sink)
         */
        public void flush() throws SinkException
        {
            m_tcpConnection.flush();
        }

        /**
         * @see AsyncConnection#getAddress()
         */
        public InetAddress getAddress()
        {
            return m_tcpConnection.getAddress();
        }

        /**
         * @see AsyncConnection#getPort()
         */
        public int getPort()
        {
            return m_tcpConnection.getPort();
        }

        /**
         * @see AsyncConnection#read(Sink, int)
         */
        public void read(int readAttempts)
            throws SinkException, SinkClosedException
        {
            m_tcpConnection.read(readAttempts);
        }

        /**
         * @see AsyncConnection#read(Sink)
         */
        public void read() 
            throws SinkException, SinkClosedException
        {
            m_tcpConnection.read();
        }

        /**
         * @see AsyncConnection#write(Buffer)
         */
        public void write(Buffer bufferElement)
            throws SinkException, SinkClosedException
        {
            m_tcpConnection.write(bufferElement);
        }

        /**
         * @see AsyncConnection#write(Buffer[])
         */
        public void write(Buffer[] bufarr)
            throws SinkException, SinkClosedException
        {
            m_tcpConnection.write(bufarr);
        }

        /**
         * @see AsyncConnection#getCompletionQueue()
         */
        public Sink getCompletionQueue()
        {
            return m_completionQueue;
        }

        /**
         * @see AsyncConnection#setCompletionQueue(Sink)
         */
        public void setCompletionQueue(Sink completionQueue)
        {
            if(completionQueue != null)
            {
                m_completionQueue = completionQueue;
            }
        }

        //---------------------- DefaultHttpConnection specific implementation
        /**
         * Parse the data contained in the given TCP packet
         * using the packet reader state machine.
         * @since Sep 26, 2002
         * 
         * @param packet
         *  The packet to be parsed with the statemachine
         * @throws IOException
         *  If the packet could not be parsed
         */
        void parsePacket(IncomingPacket packet) throws IOException
        {
            m_packetReader.parsePacket(packet);
        }

        /**
         * Return the AsyncTcpConnection associated with this
         * connection.
         * @since Sep 26, 2002
         * 
         * @return AsyncTcpConnection
         *  The AsyncTcpConnection associated with this connection
         */
        AsyncTcpConnection getAsyncTcpConnection()
        {
            return m_tcpConnection;
        }
    }
}
