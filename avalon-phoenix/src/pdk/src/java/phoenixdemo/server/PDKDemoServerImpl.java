
/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package phoenixdemo.server;



import phoenixdemo.api.PDKDemoServer;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * Class PDKDemoServerImpl
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1 $
 */
public class PDKDemoServerImpl implements PDKDemoServer {

    /**
     * Constructor PDKDemoServerImpl
     *
     *
     */
    public PDKDemoServerImpl() {}

    /**
     * Method processSocket
     *
     *
     * @param socket
     *
     */
    public void processSocket(Socket socket) {

        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            String st = null;

            try {
                st = (String) ois.readObject();
            } catch (ClassNotFoundException cnfe) {}

            System.out.println("String passed = " + st);
            ois.close();
            socket.close();
        } catch (IOException ioe) {
            System.out.println("Unexpected IO Exception");
        }
    }

    /**
     * Method main
     *
     *
     * @param args
     *
     * @throws IOException
     *
     */
    public static void main(String[] args) throws IOException {

        PDKDemoServerImpl svr = new PDKDemoServerImpl();
        ServerSocket serverSocket = new ServerSocket(7654);

        System.out.println("PDK Demo listening on port " + 7654);
        System.out.println("Ctrl-C to exit");

        while (true) {
            svr.processSocket(serverSocket.accept());
        }
    }
}
