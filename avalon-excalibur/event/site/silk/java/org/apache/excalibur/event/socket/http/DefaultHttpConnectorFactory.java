/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.http;

import java.io.IOException;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.seda.StageManager;

/**
 * An implementation of the {@link HttpConnectorFactory} interface
 * representing a factory for SEDA {@link HttpConnector} http 
 * connectors. A connector acts as a facade to the SEDA style dispatch
 * mechanism. This is a component that has to be installed into the 
 * system to be used. It can be configured in the following manner:
 * <p>
 * <m_code><pre>
 *   &lt;component&gt;
 *     &lt;sinks handler-sink="stage-name"/&gt;
 *   &lt;/component&gt;
 * </pre></m_code> 
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class DefaultHttpConnectorFactory extends AbstractLogEnabled
    implements HttpConnectorFactory, Initializable, Serviceable
{
    /** The components service manager. */
    private ServiceManager m_serviceManager;
    
    /** The connector handler */
    private HttpConnectorHandler m_handler = null;
    
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
            final String role = HttpConnectorHandler.ROLE;
            m_handler = (HttpConnectorHandler)manager.lookup(role);
        }
        finally
        {
            m_serviceManager.release(stageManager);
        }
    }

    //------------------------- Serviceable implementation
    /**
     * @see Serviceable#service(ServiceManager)
     */
    public void service(ServiceManager serviceManager)
    {
        m_serviceManager = serviceManager;
    }

    //------------------------- HttpConnectorFactory implementation
    /**
     * @see HttpConnectorFactory#createHttpConnector(Sink)
     */
    public HttpConnector createHttpConnector(
        int port, Context context, Sink queue) 
            throws IOException, SinkException
    {
        final HttpConnector connector = 
            new DefaultHttpConnector(queue, port, -1, context);
        return connector;
    }

    /**
     * @see HttpConnectorFactory#createHttpConnector(Sink, int)
     */
    public HttpConnector createHttpConnector(
        int port, Context context, int threshold, Sink queue) 
            throws IOException, SinkException
    {
        final HttpConnector connector = 
            new DefaultHttpConnector(queue, port, threshold, context);
        return connector;
    }
    
    //------------------------- DefaultHttpConnectorFactory inner classes
    /**
     * An inner class implementation of the HttpConnector 
     * interface.
     * @since Sep 26, 2002
     * 
     * @author <a href="mailto:schierma@users.sourceforge.net">schierma</a>
     */
    private final class DefaultHttpConnector implements HttpConnector
    {
        /** The client sink */
        private final Sink m_queue;
        
        /** Whether the connector is closed */
        private boolean m_closed;
        
        /** The context for the connector */
        private final Context m_context;
        
        //------------------------- DefaultHttpConnector constructors
        /**
         * Creates a default HttpConnector with the specified 
         * sink and write clog threshold.
         * @since Sep 26, 2002
         * 
         * @param queue
         *  The client sink
         * @param threshold
         *  The write clog threshold for the server socket
         */
        public DefaultHttpConnector(
            Sink queue, int port, int threshold, Context context)
                throws SinkException, IOException
        {
            super();
            m_queue = queue;
            m_context = context;
            
            m_handler.open(new OpenRequest(this, port, threshold));
            m_closed = false;
        }

        //------------------------- HttpConnector implementation
        /**
         * @see HttpConnector#close()
         */
        public void close() throws SinkException
        {
            if(!m_closed)
            {
                m_handler.close(new CloseRequest(this));
                m_closed = true;
            }
        }

        /**
         * @see HttpConnector#getCompletionQueue()
         */
        public Sink getCompletionQueue()
        {
            return m_queue;
        }
        
        /**
         * @see HttpConnector#getContext()
         */
        public Context getContext()
        {
            return m_context;
        }

    }
}
