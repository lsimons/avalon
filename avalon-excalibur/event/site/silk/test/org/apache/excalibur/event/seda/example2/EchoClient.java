/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example2;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.seda.StageManager;
import org.apache.excalibur.event.socket.tcp.AsyncSocket;
import org.apache.excalibur.event.socket.tcp.AsyncSocketFactory;
import org.apache.excalibur.fortress.ContainerManager;
import org.apache.excalibur.fortress.DefaultContainerManager;
import org.apache.excalibur.fortress.container.DefaultContainer;
import org.apache.excalibur.fortress.util.ContextBuilder;
import org.apache.excalibur.fortress.util.ContextManager;

/**
 * Tests the stage container default implementation 
 * by setting up a server using the nio tcp package.
 * The server will listen on port 8080.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class EchoClient
{
    private static final long EXECUTION_TIME = 10000000000L;

    protected static final int THREADS = 10;
    protected static final int TEST_SIZE = 5000;

    /** A logger to log messages to */
    private static Logger m_logger = null;
    /** The container manager */
    private static ContainerManager m_containerManager = null;
    /** The context manager */
    private static ContextManager m_contextManager = null;
    /** The container instance */
    private static DefaultContainer m_container = null;
    
    public static final Object lock = new Object();

    //------------------------- HttpConnector specific implementation
    /**
     * Main entry method for the application.
     * @since Sep 16, 2002
     * 
     * @throws Exception
     *  If an error ocurrs
     */
    public static final void main(String[] args) throws Exception
    {
        setup();
                
        // Runs the example
        run("localhost", 8080);

        m_containerManager.dispose();
        m_contextManager.dispose();
    }

    /** 
     * Sets up the container for the example application
     * @since Sep 16, 2002
     * 
     * @throws Exception
     *  If an error ocurrs
     */
    public static void setup() throws Exception
    {
        // create a new context builder to set the context of the container
        final ContextBuilder contextBuilder = new ContextBuilder();

        // container class is the stage container to test
        contextBuilder.setContainerClass(DefaultContainer.class.getName());
        contextBuilder.setContextDirectory("./");
        contextBuilder.setWorkDirectory("./");
        contextBuilder.setLoggerCategory(null);
        //contextBuilder.setComponentManagerParent(kernelManager);
        contextBuilder.setContextClassLoader(
            Thread.currentThread().getContextClassLoader());

        contextBuilder.setContainerConfiguration(
            "resource://org/apache/excalibur/event/seda/example2/EchoClient.xconf");
        contextBuilder.setLoggerManagerConfiguration(
            "resource://org/apache/excalibur/event/seda/example2/EchoClient.xlog");

        m_contextManager =
            new ContextManager(contextBuilder.getContext(), null);

        // initialize the context manager
        m_contextManager.initialize();
        // then set the context manager to be used by the container's manager
        m_containerManager = new DefaultContainerManager(m_contextManager);
        // init the manager
        m_containerManager.initialize();

        m_container = (DefaultContainer) m_containerManager.getContainer();

        m_logger = m_containerManager.getLogger();
    }

    /**
     * Runs the example by providing an initial event.
     * The stages inside the container will then
     * communicate and print something to the logger.
     * @since Sep 16, 2002
     * 
     * @throws Exception
     *  If an error ocurrs
     */
    public static void run(String host, int port) throws Exception
    {
        final ServiceManager manager = m_container.getServiceManager();
        final StageManager stageManager = 
            (StageManager) manager.lookup(StageManager.ROLE);
        
        final Sink sendSink = 
            (Sink)stageManager.getServiceManager().lookup("send-stage");

        final String role = AsyncSocketFactory.ROLE;
        final AsyncSocketFactory factory =
            (AsyncSocketFactory) manager.lookup(role);
        final AsyncSocket socket = factory.createSocket(host, port, sendSink);
        
        m_logger.info("++++++++++++++++++++++++++++++++++++++++++++");
        m_logger.info("Socket waiting ...");
        m_logger.info("++++++++++++++++++++++++++++++++++++++++++++");
        
        // wait some time and have all stages execute
        synchronized(lock)
        {
            try
            {
                lock.wait(EXECUTION_TIME);
            }
            catch(InterruptedException e)
            {
                // ignore
            }
        }

        m_logger.info("++++++++++++++++++++++++++++++++++++++++++++");
        m_logger.info("Socket stopped ...");
        m_logger.info("++++++++++++++++++++++++++++++++++++++++++++");

        manager.release(factory);
    }
}