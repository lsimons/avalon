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
import java.net.Socket;

/**
 * Simple threaded synchronous client implementation to test with.
 * Writes to port 8080.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class SyncClient implements Runnable
{
    /** The thread index */
    private final int m_index;
    /** Host name */
    private final String m_host;
    /** Port number */
    private final int m_port;
    
    //-------------------------- HttpClient constructors
    /**
     * Creates the client with the specified index.
     * @since Sep 19, 2002
     * 
     * @param index
     *  The thread index for this client
     */
    public SyncClient(int index, String host, int port)
    {
        super();
        m_host = host;
        m_port = port;
        m_index = index;
    }
    
    //-------------------------- Runnable implementation
    /**
     * @see Runnable#run()
     */
    public void run()
    {
        for(int i = 0; true; i++)
        {
            try
            {
                Socket client = new Socket(m_host, m_port);
                OutputStream out = client.getOutputStream();
                out.write(("Hello world.\r\n").getBytes());
                out.flush();
                //out.close();
                //System.out.println("Send. Now receive");
                BufferedReader in =
                    new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                System.out.println("Done [" + m_index +"]"+ i + ":"  + in.readLine());
                //in.close();
                client.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    //-------------------------- HttpClient specific implementation
    /**
     * Application entry point.
     * @since Sep 19, 2002
     */
    public static void main(String[] args) throws Exception
    {
        String host = "localhost";
        int port = 8080;
        
        try
        {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        catch(Throwable e)
        {
            // ignore
        }
         
        for(int i = 0; i < 10; i++)
        {
            new Thread(new SyncClient(i, host, port)).start();
        }
    }
}