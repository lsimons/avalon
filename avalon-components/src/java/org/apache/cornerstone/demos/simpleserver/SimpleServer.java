/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.simpleserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Component;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.Initializable;
import org.apache.phoenix.Block;
import org.apache.cornerstone.services.store.Store;
import org.apache.cornerstone.services.store.ObjectRepository;
import org.apache.cornerstone.services.sockets.SocketManager;
import org.apache.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.cornerstone.services.connection.ConnectionHandler;
import org.apache.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.cornerstone.services.connection.ConnectionManager;
import org.apache.cornerstone.services.scheduler.TimeScheduler;
import org.apache.cornerstone.services.scheduler.Target;
import org.apache.cornerstone.services.scheduler.CronTimeTrigger;
import org.apache.cornerstone.services.scheduler.PeriodicTimeTrigger;
import org.apache.cornerstone.services.scheduler.TimeTrigger;

/**
 * This is a demo block used to demonstrate a simple server using Avalon. The
 * server listens on a port specified in .confs. All commands are one line
 * commands. It understands three commands: PUT, COUNT, LIST.
 * <br>PUT <string> stores the given string on the file system
 * <br>COUNT counts the number of strings stored
 * <br>LIST responds with all the strings, one per line.
 *
 * @author Charles Benett <charles@benett1.demon.co.uk>
 * @author Federico Barbieri <fede@apache.org>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class SimpleServer 
    extends AbstractLoggable 
    implements Block, SimpleService, Composer, Configurable, Initializable,
    ConnectionHandlerFactory, ConnectionHandler, Target
{
    protected TimeScheduler           m_timeScheduler;
    protected Configuration           m_configuration;
    protected SocketManager           m_socketManager;
    protected ConnectionManager       m_connectionManager;
    protected Store                   m_testStore;
    protected ObjectRepository        m_repository;

    protected PrintWriter             m_out;
    protected int                     m_count;

    public void compose( final ComponentManager componentManager )
        throws ComponentManagerException
    {
        m_testStore = 
            (Store)componentManager.lookup( "org.apache.cornerstone.services.store.Store" );

        m_socketManager = (SocketManager)componentManager.
            lookup( "org.apache.cornerstone.services.sockets.SocketManager" );

        m_connectionManager = (ConnectionManager)componentManager.
            lookup( "org.apache.cornerstone.services.connection.ConnectionManager" );

        m_timeScheduler = (TimeScheduler)componentManager.
            lookup( "org.apache.cornerstone.services.scheduler.TimeScheduler" );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    public void init() 
        throws Exception
    {
        getLogger().info( "init Demo ..." );

        final Configuration repConf = m_configuration.getChild( "repository" );
        getLogger().info( "Want to use repository in:" + 
                          repConf.getAttribute( "destinationURL" ) );
        m_repository = (ObjectRepository)m_testStore.select( repConf );
        getLogger().info( "Got repository" );

        TimeTrigger trigger = null;

        trigger = new PeriodicTimeTrigger( -1, 2 * 1000 );
        m_timeScheduler.addTrigger( "try", trigger, this );

        trigger = new PeriodicTimeTrigger( 9 * 1000, -1 );
        m_timeScheduler.addTrigger( "do", trigger, this );

        //trigger = new CronTimeTrigger( -1, -1, -1, -1, -1, false );
        //m_timeScheduler.addTrigger( "cron-trigger", trigger, this );

        final int port = m_configuration.getChild( "port" ).getValueAsInt();
        getLogger().info( "Want to open port on:" + port );

        final ServerSocketFactory factory = 
            m_socketManager.getServerSocketFactory( "plain" );
        final ServerSocket serverSocket = factory.createServerSocket( port );

        m_connectionManager.connect( "DemoListener", serverSocket, this );

        getLogger().info( "Got socket" );

        getLogger().info( "...Demo init" );
    }

    /**
     * Construct an appropriate ConnectionHandler.
     *
     * @return the new ConnectionHandler
     * @exception Exception if an error occurs
     */
    public ConnectionHandler createConnectionHandler()
        throws Exception
    {
        //Can return this because the ConnectionHandler is thread safe
        return this;
    }    

    /**
     * Handle a connection.
     * This handler is responsible for processing connections as they occur.
     *
     * @param socket the connection
     * @exception IOException if an error reading from socket occurs
     * @exception ProtocolException if an error handling connection occurs
     */
    public void handleConnection( final Socket socket )
        throws IOException, ProtocolException
    {
        String remoteHost = null;
        String remoteIP = null;

        try
        {
            final BufferedReader in = 
                new BufferedReader( new InputStreamReader( socket.getInputStream() ), 1024 );
            m_out = 
                new PrintWriter( new BufferedOutputStream( socket.getOutputStream()), true );

            remoteHost = socket.getInetAddress().getHostName();
            remoteIP = socket.getInetAddress().getHostAddress();

            getLogger().info( "Connection from " + remoteHost + " ( " + remoteIP + " )" );
           
            //Greet connection
            m_out.println( "Welcome to the Avalon Demo Server!" );
           
            // Handle connection
            while( parseCommand( in.readLine()) )
            {
                // timeServer.resetAlarm(this.toString());
            }
           
            //Finish
            m_out.flush();
            socket.close();
        } 
        catch( final SocketException se )
        {
            getLogger().info( "Socket to " + remoteHost + " closed remotely." );
        } 
        catch( final InterruptedIOException iioe )
        {
            getLogger().info( "Socket to " + remoteHost + " timeout." );
        }
        catch( IOException ioe ) 
        {
            getLogger().info( "Exception handling socket to " + remoteHost + ":" + 
                              ioe.getMessage() );
        } 
        catch( final Exception e )
        {
            getLogger().info( "Exception on socket: " + e.getMessage() );
        } 
        finally
        {
            try { socket.close(); } 
            catch( final IOException ioe )
            {
                getLogger().error( "Exception closing socket: " + ioe.getMessage() );
            }
        }
    }

    public void targetTriggered( final String triggerName )
    {
        if( triggerName.equals( "do" ) )
        {
            try
            {
                m_timeScheduler.removeTrigger( "try" );
            } 
            catch( final Exception e )
            {
                e.printStackTrace();
            }
        }

        System.out.println( "Target triggered: " + triggerName );
    }

    protected boolean parseCommand( String command )
        throws Exception 
    {
        if( null == command )
        {
            return false;
        }

        getLogger().info( "Command received: " + command );

        StringTokenizer commandLine = new StringTokenizer( command.trim() );
        int arguments = commandLine.countTokens();
        String argument = null;
        if( 0 == arguments ) 
        {
            return true;
        }

        String fullcommand = command;
        command = commandLine.nextToken();
        if( arguments > 1 ) 
        {
            argument = fullcommand.substring(command.length() + 1);
        }
       
        if( command.equalsIgnoreCase( "TEST" ) )
        {
            m_out.println( "You said 'TEST'" );

            final DummyClass write = new DummyClass();
            write.setName( argument );

            try { m_repository.put( argument, write ); } 
            catch( final Exception e )
            {
                getLogger().warn( "Exception putting into repository: " + e );
            }

            m_out.println( "Dummy written, trying for read" );
            try  { final Iterator it = m_repository.list(); } 
            catch( Exception e )
            {
                getLogger().warn( "Exception reading from repository: " + e, e );
            }

            DummyClass read = null;
            try { read = (DummyClass) m_repository.get(argument); } 
            catch( final Exception e )
            {
                getLogger().warn( "Exception reading from repository: " + e, e );
            }

            m_out.println( "Recovered: " + read.getName() );
            return true;
        } 
        else if( command.equalsIgnoreCase( "PUT" ) )
        {
            m_out.println( "You said 'PUT'" );
            final String key = "AMsg" + ++m_count;
            m_repository.put( key, argument );
            return true;
        }
        else if( command.equalsIgnoreCase( "LIST" ) ) 
        {
            m_out.println( "You said 'LIST'" );

            final Iterator it = m_repository.list();
            
            while( it.hasNext() )
            {
                String k = (String)it.next();
                String txt = (String) m_repository.get( k );
                m_out.println( "Msg " + k + " was " + txt );
            }
            m_out.println( "That's All folks!" );
            return true;
        } 
        else if( command.equalsIgnoreCase( "COUNT" ) )
        {
            m_out.println( "You said 'COUNT'" );
            Iterator it = m_repository.list();
            int c = 0;
            
            while( it.hasNext() )
            {
                Object ignore = it.next();
                c=c+1;
            }

            m_out.println( "Number of messages in repository is: " + c );
            return true;
        } 
        else
        {
            m_out.println( "Only valid commands are: PUT, LIST or COUNT." );
            return true;
        }
    }
}


