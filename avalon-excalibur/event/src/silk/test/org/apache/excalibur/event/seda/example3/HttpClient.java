/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.seda.example3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;


/**
 * Simple threaded synchronous client implementation to test with.
 * Writes to port 8080.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class HttpClient implements Runnable
{
    /** The thread index */
    private final int m_index;
    /** Host name */
    private final String m_url;
    
    //-------------------------- HttpClient constructors
    /**
     * Creates the client with the specified index.
     * @since Sep 19, 2002
     * 
     * @param index
     *  The thread index for this client
     */
    public HttpClient(int index, String url)
    {
        super();
        m_url = url;
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
                final URL url = new URL(m_url);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                
                con.connect();
                
                final OutputStream out = 
                    new BufferedOutputStream(con.getOutputStream());
                
                final BufferedInputStream fileInput = 
                    new BufferedInputStream(new FileInputStream(
                        "C:/eclipse/workspace/common-libs/java-j2ee/servlet.jar"));
                
                int read = 0; 
                int j = 0;
                for(; (read = fileInput.read()) != -1; j++)
                {
                    out.write(read);
                }
                fileInput.close();
                
                //System.out.println("[" + m_index +"]"+ i + " Posted " + j + " bytes.");
                
                out.flush();
                //out.close();
                //System.out.println("Send. Now receive");
                final BufferedReader in =
                    new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                
                String line = null;
                while((line = in.readLine()) != null)
                {
                    //System.out.println(line);
                }
                
                if( i % 500 == 0 )
                {
                    System.out.println("[" + m_index +"]"+ i + " done!");
                }
                
                //in.close();
                con.disconnect();
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
        String url = "http://localhost";
        try
        {
            url = args[0];
        }
        catch(Throwable e)
        {
            // ignore
        }
         
        for(int i = 0; i < 10; i++)
        {
            new Thread(new HttpClient(i, url)).start();
        }
    }
}