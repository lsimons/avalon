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
package org.apache.avalon.facilities.console.impl;

import java.io.IOException;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.avalon.facilities.console.Console;
import org.apache.avalon.facilities.console.ConsoleCommand;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;


/**
 * @avalon.component name="console-impl" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.facilities.console.Console"
 */
public class ConsoleImpl extends AbstractLogEnabled
    implements Console, Runnable, Startable, Parameterizable, Initializable
{
    private int         m_Port;
    private ServerSocket m_ServerSocket;
    private Thread      m_Thread;
    private Hashtable   m_Commands;
    private ArrayList   m_Interpreters;
    private String      m_Welcome;    
    
    public void parameterize( Parameters params )
        throws ParameterException
    {
        m_Port = params.getParameterAsInteger( "port", 3333 );
        m_Welcome = params.getParameter( "welcome-text", "type 'help' form more info." );
    }
    
    public void initialize()
        throws Exception
    {
        m_Interpreters = new ArrayList();
        m_Commands = new Hashtable();
        m_ServerSocket = new ServerSocket( m_Port );
    }

    public void start()
    {
        /* start new thread for monitoring and updating. */
        getLogger().info( "Console started." );
        m_Thread = new Thread( this );
        m_Thread.start();
    }

    public void stop()
    {
        try
        {
            m_Thread.interrupt();
            m_ServerSocket.close();
        } catch( IOException e )
        {}
    }

    public void dispose()
    {
        getLogger().info("Console disposed.");
    }

    public void addCommand( ConsoleCommand cmd )
    {
        getLogger().info( "Added " + cmd.getName() + " command." );
        m_Commands.put( cmd.getName(), cmd );
    }
    
    public void removeCommand( ConsoleCommand cmd )
    {
        m_Commands.remove( cmd.getName() );
    }
    
    public void removeCommand( String commandname )
    {
        m_Commands.remove( commandname );
    }
    
    public ConsoleCommand getCommand( String name )
    {
        return (ConsoleCommand) m_Commands.get( name );
    }
    
    public Collection getCommandNames()
    {
        return m_Commands.keySet();
    }
    
    public void run()
    {
        boolean running = true;
        while( running )
        {
            try
            {
                Socket socket = m_ServerSocket.accept();
                CommandInterpreter intp = new CommandInterpreter( socket, m_Welcome, this );
                m_Interpreters.add( intp );
                intp.start();
            } catch( IOException e )
            {
                running = false;
            } catch( Exception e )
            {
                getLogger().warn( "", e );
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
}
