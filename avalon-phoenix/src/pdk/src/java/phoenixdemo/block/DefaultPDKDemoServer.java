
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package phoenixdemo.block;



import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.phoenix.Block;

import phoenixdemo.api.PDKDemoServer;

import phoenixdemo.server.PDKDemoServerImpl;


/**
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class DefaultPDKDemoServer extends AbstractLoggable
        implements Block, PDKDemoServer, Configurable, Startable, Initializable {

    protected int mPort;
    protected PDKDemoServerImpl mPDKServer;
    protected SocketThread mSocketThread;

    /**
     * Method configure
     *
     *
     * @param configuration
     *
     * @throws ConfigurationException
     *
     */
    public void configure(final Configuration configuration) throws ConfigurationException {
        mPort = configuration.getChild("port").getValueAsInteger(7777);
    }

    /**
     * Method initialize
     *
     *
     * @throws Exception
     *
     */
    public void initialize() throws Exception {
        mPDKServer = new PDKDemoServerImpl();
    }

    /**
     * Method start
     *
     *
     * @throws Exception
     *
     */
    public void start() throws Exception {

        mSocketThread = new SocketThread(mPDKServer, mPort);

        mSocketThread.start();
        System.out.println("Server started on port " + mPort);
    }

    /**
     * Method stop
     *
     *
     * @throws Exception
     *
     */
    public void stop() throws Exception {

        mSocketThread = new SocketThread(mPDKServer, mPort);

        mSocketThread.interrupt();

        mSocketThread = null;

        System.out.println("Server stopped on port " + mPort);
    }
}
