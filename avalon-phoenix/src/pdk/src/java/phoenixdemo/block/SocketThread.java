
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package phoenixdemo.block;



import phoenixdemo.server.PDKDemoServerImpl;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;


/**
 * Class SocketThread
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.2 $
 */
public class SocketThread extends Thread {

    private PDKDemoServerImpl mPDKDemoServerImpl;
    private ServerSocket svr;

    protected SocketThread(PDKDemoServerImpl pdkDemoServerImpl, int port) {

        mPDKDemoServerImpl = pdkDemoServerImpl;

        try {
            svr = new ServerSocket(port);
        } catch (IOException ioe) {
            throw new RuntimeException(
                "Unable to open listening port.  It is probably already being listened to.");
        }
    }

    /**
     * Method run
     *
     *
     */
    public void run() {

        while (true) {
            try {
                ConnectionThread ct = new ConnectionThread(svr.accept());

                ct.start();
            } catch (IOException ioe) {
                System.out.println("Some problem with getting a socket for the connetion.");
            }
        }
    }

    /**
     * Class ConnectionThread
     *
     *
     * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
     * @version $Revision: 1.2 $
     */
    class ConnectionThread extends Thread {

        private Socket mSocket;

        private ConnectionThread(Socket socket) {
            mSocket = socket;
        }

        /**
         * Method run
         *
         *
         */
        public void run() {
            mPDKDemoServerImpl.processSocket(mSocket);
        }
    }
}
