/*
 * Copyright 1997-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.console;

import java.io.IOException;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class ConsoleImpl
    implements Runnable
{
    static final private String CONSOLE_LISTEN_THREAD_NAME = "Console-Listen";
    static private Logger m_Logger;

    private int m_Port;
    private ServerSocket m_ServerSocket;
    private Thread m_Thread;
    private ArrayList m_Interpreters;

    static
    {
        m_Logger = Logger.getLogger( ConsoleImpl.class );
    }

    public ConsoleImpl()
        throws IOException
    {
        m_Interpreters = new ArrayList();
    }


    public void initialize( Map props )
    {
        if( props != null )
        {
            try
            {
                m_Port = Integer.parseInt( (String) props.get( "Port" ) );
            } catch( NumberFormatException e )
            {} // ignore
        }
        if( m_Port == 0 )
            m_Port = 3333;
        try
        {
            m_ServerSocket = new ServerSocket( m_Port );
        } catch( IOException e )
        {
            m_Logger.warn( "Could not open the server socket.", e );
        }
    }

    public void start()
    {
        /* start new thread for monitoring and updating. */
        m_Logger.info( "Console started." );
        m_Thread = new Thread( this, CONSOLE_LISTEN_THREAD_NAME );
        m_Thread.start();
//        fireStartedEvent();
    }

    public void stop()
    {
        try
        {
            m_Thread.interrupt();
            m_ServerSocket.close();
        } catch( IOException e )
        {}
//        fireStoppedEvent();
    }

    public void dispose()
    {
        m_Logger.info("Console disposed.");
//        fireDisposedEvent();
    }

    public void run()
    {
        boolean running = true;
        while( running )
        {
            try
            {
                Socket socket = m_ServerSocket.accept();
                CommandInterpreter intp = new CommandInterpreter( socket );
                m_Interpreters.add( intp );
                intp.start();
            } catch( IOException e )
            {
                running = false;
            } catch( Exception e )
            {
                m_Logger.warn( "", e );
                try
                {
                    Thread.sleep( 60000 );
                } catch( InterruptedException f )
                {
                    running = false;
                }
            }
        }
        Iterator list = m_Interpreters.iterator();
        while( list.hasNext() )
        {
            CommandInterpreter intp = (CommandInterpreter) list.next();
            intp.interrupt();
        }
    }

    public String getName()
    {
        return "console";
    }
}
