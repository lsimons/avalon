/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple single threaded synchronous echo server implementation
 * to test with.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class SyncServer
{

    //-------------------------- SyncServer specific implementation
    /**
     * Application entry point.
     * @since Sep 19, 2002
     */
    public static void main(String[] args) throws Exception
    {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Listening on 8080...");
        Socket socket = server.accept();
        System.out.println("Accept AsyncConnection...");
        BufferedReader in =
            new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line = "";
        while (in.ready())
        {
            line += in.readLine();
            line += "\n";
        }
        //in.close();

        System.out.println("Echo Server received message: " + line);
        OutputStream out = socket.getOutputStream();
        System.out.println("Echo Server writes message: " + line);
        out.write(line.getBytes());
        out.flush();
        socket.close();
        server.close();
        System.out.println("Echo Server shutdown...");
    }

}
