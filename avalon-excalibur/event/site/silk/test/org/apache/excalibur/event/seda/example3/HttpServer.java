/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example3;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.seda.StageManager;
import org.apache.excalibur.event.socket.http.HttpConnector;
import org.apache.excalibur.event.socket.http.HttpConnectorFactory;
import org.apache.excalibur.fortress.ContainerManager;
import org.apache.excalibur.fortress.DefaultContainerManager;
import org.apache.excalibur.fortress.container.DefaultContainer;
import org.apache.excalibur.fortress.util.ContextBuilder;
import org.apache.excalibur.fortress.util.ContextManager;


/**
 * A simple stage driven http server that serves files in a
 * configured directory. This server is just an example. It's
 * not safe to be used in production, since it serves all files 
 * in the specified directory.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class HttpServer
{
    private static final long EXECUTION_TIME = 100000000;
    
    /** A logger to log messages to */
    private static Logger m_logger = null;
    /** The container manager */
    private static ContainerManager m_containerManager = null;
    /** The context manager */
    private static ContextManager m_contextManager = null;
    /** The container instance */
    private static DefaultContainer m_container = null;

    //------------------------- HttpServer specific implementation
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
        run(80);

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
            "resource://org/apache/excalibur/event/seda/example3/HttpServer.xconf");
        contextBuilder.setLoggerManagerConfiguration(
            "resource://org/apache/excalibur/event/seda/example3/HttpServer.xlog");

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
    public static void run(int port) throws Exception
    {
        final ServiceManager manager = m_container.getServiceManager();
        final StageManager stageManager = 
            (StageManager) manager.lookup(StageManager.ROLE);
        
        final Sink handlerSink = 
            (Sink)stageManager.getServiceManager().lookup("handler-stage");

        final String role = HttpConnectorFactory.ROLE;
        final HttpConnectorFactory factory =
            (HttpConnectorFactory) manager.lookup(role);
            
        final Context context = new DefaultContext();
        final HttpConnector httpConnector = 
            factory.createHttpConnector(port, context, handlerSink);
        
        m_logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++");
        m_logger.info("Waiting for http requests on port " + port + "...");
        m_logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++");
        
        // wait some time and have all stages execute
        Thread.sleep(EXECUTION_TIME);

        m_logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++");
        m_logger.info("Http server stopped!");
        m_logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}