/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002,2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.avalon.testserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandler;
import org.apache.avalon.cornerstone.services.connection.ConnectionHandlerFactory;
import org.apache.avalon.cornerstone.services.connection.ConnectionManager;
import org.apache.avalon.cornerstone.services.packet.PacketHandler;
import org.apache.avalon.cornerstone.services.packet.PacketHandlerFactory;
import org.apache.avalon.cornerstone.services.packet.PacketManager;
import org.apache.avalon.cornerstone.services.scheduler.PeriodicTimeTrigger;
import org.apache.avalon.cornerstone.services.scheduler.Target;
import org.apache.avalon.cornerstone.services.scheduler.TimeScheduler;
import org.apache.avalon.cornerstone.services.scheduler.TimeTrigger;
import org.apache.avalon.cornerstone.services.sockets.ServerSocketFactory;
import org.apache.avalon.cornerstone.services.sockets.SocketManager;
import org.apache.avalon.cornerstone.services.store.ObjectRepository;
import org.apache.avalon.cornerstone.services.store.Store;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.testserver.classes.ClassesTest;
import org.apache.avalon.testserver.extension1.ExtensionTestClass1;
import org.apache.avalon.testserver.extension2.ExtensionTestClass2;

/**
 * This is a demo block used to demonstrate a simple server using Avalon. The
 * server listens on a port specified in .confs. All commands are one line
 * commands. It understands three commands: PUT, COUNT, LIST.
 * <br>PUT <string> stores the given string on the file system
 * <br>COUNT counts the number of strings stored
 * <br>LIST responds with all the strings, one per line.
 *
 * @phoenix:block
 *
 * @author Charles Benett <charles@benett1.demon.co.uk>
 * @author Federico Barbieri <fede@apache.org>
 * @author Peter Donald
 */
public class SimpleServer
    extends AbstractLogEnabled
    implements Contextualizable, Serviceable, Configurable, Initializable,
    ConnectionHandlerFactory, ConnectionHandler, Target, PacketHandler, PacketHandlerFactory
{
    private TimeScheduler m_timeScheduler;
    private Configuration m_configuration;
    private SocketManager m_socketManager;
    private ConnectionManager m_connectionManager;
    private Store m_testStore;
    private ObjectRepository m_repository;
    private PacketManager m_packetManager;

    private PrintWriter m_out;
    private int m_count;
    private BlockContext m_context;

    public void contextualize( final Context context )
    {
        m_context = (BlockContext)context;
    }

    protected final BlockContext getBlockContext()
    {
        return m_context;
    }

    /**
     *
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.store.Store"
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.connection.ConnectionManager"
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.sockets.SocketManager"
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.packet.PacketManager"
     * @phoenix:dependency name="org.apache.avalon.cornerstone.services.scheduler.TimeScheduler"
     *
     */
    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        m_testStore = (Store)serviceManager.lookup( Store.ROLE );
        m_socketManager = (SocketManager)serviceManager.lookup( SocketManager.ROLE );
        m_connectionManager = (ConnectionManager)serviceManager.lookup( ConnectionManager.ROLE );
        m_timeScheduler = (TimeScheduler)serviceManager.lookup( TimeScheduler.ROLE );
        m_packetManager = (PacketManager)serviceManager.lookup( PacketManager.ROLE );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    public void initialize()
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

        final int port = m_configuration.getChild( "port" ).getValueAsInteger();
        getLogger().info( "Want to open port on:" + port );

        final ServerSocketFactory factory =
            m_socketManager.getServerSocketFactory( "plain" );
        final ServerSocket serverSocket = factory.createServerSocket( port );

        m_connectionManager.connect( "DemoListener", serverSocket, this );

        getLogger().info( "Got socket" );

        getLogger().info( "Testing access to Extension in same .sar ..." );
        ExtensionTestClass1.doSomeThing();
        getLogger().info( "...successful test" );

        getLogger().info( "Testing access to Extension from central extension repository..." );
        ExtensionTestClass2.doSomeThing();
        getLogger().info( "...successful test" );

        getLogger().info( "Creating datagram socket..." );
        final DatagramSocket datagramSocket = new DatagramSocket( 5053 );
        getLogger().info( "...successful" );

        getLogger().info( "Connecting datagram socket..." );
        m_packetManager.connect( "udp/5053", datagramSocket, this );
        getLogger().info( "...successful" );

        getLogger().info( "Testing a class loaded from SAR-INF/classes..." );
        ClassesTest.doClassesTest();
        getLogger().info( "...successful" );

        testResource( "/README.txt" );
        testResource( "README.txt" );
        testResource( "SAR-INF/config.xml" );
        testResource( "SAR-INF/lib/cornerstone.jar" );

        getLogger().info( "Testing loading a class from a non-standard ClassLoader tree" );
        final ClassLoader classLoader =
            getBlockContext().getClassLoader( "cltest" );
        try
        {
            classLoader.loadClass( "org.apache.avalon.testserver.cltest.CLTest" );
        }
        catch( final ClassNotFoundException cnfe )
        {
            final String message =
                "Failed to load class CLTest from cltest classloader";
            throw new Exception( message );
        }

        getLogger().info( "...Demo init" );
    }

    private void testResource( String resourceName ) throws Exception
    {
        final InputStream resourceAsStream =
            m_context.getResourceAsStream( resourceName );
        if( null == resourceAsStream )
        {
            final String message = "Unable to load resource named: " + resourceName;
            throw new Exception( message );
        }
        else
        {
            final String message =
                "Loaded resource named " + resourceName +
                " as " + resourceAsStream;
            System.out.println( message );
        }
    }

    /**
     * Construct an appropriate PacketHandler.
     *
     * @return the new PacketHandler
     * @exception Exception if an error occurs
     */
    public PacketHandler createPacketHandler()
        throws Exception
    {
        return this;
    }

    /**
     * Handle a datgram packet.
     * This handler is responsible for processing packets as they occur.
     *
     * @param packet the packet
     * @exception IOException if an error reading from socket occurs
     * @exception ProtocolException if an error handling connection occurs
     */
    public void handlePacket( final DatagramPacket packet )
        throws IOException, ProtocolException
    {
        System.out.println( "Pickety pickety pop - got a packet!" );
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
     * Release a previously created ConnectionHandler e.g. for spooling.
     */
    public void releaseConnectionHandler( ConnectionHandler connectionHandler )
    {
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
                new PrintWriter( new BufferedOutputStream( socket.getOutputStream() ), true );

            remoteHost = socket.getInetAddress().getHostName();
            remoteIP = socket.getInetAddress().getHostAddress();

            getLogger().info( "Connection from " + remoteHost + " ( " + remoteIP + " )" );

            //Greet connection
            m_out.println( "Welcome to the Avalon Demo Server!" );

            // Handle connection
            while( parseCommand( in.readLine() ) )
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
            try
            {
                socket.close();
            }
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

        System.out.println( "[" + getBlockContext().getName() + "] Target triggered: " + triggerName );
    }

    private boolean parseCommand( String command )
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
            argument = fullcommand.substring( command.length() + 1 );
        }

        if( command.equalsIgnoreCase( "TEST" ) )
        {
            m_out.println( "You said 'TEST'" );

            final DummyClass write = new DummyClass();
            write.setName( argument );

            try
            {
                m_repository.put( argument, write );
            }
            catch( final Exception e )
            {
                getLogger().warn( "Exception putting into repository: " + e );
            }

            m_out.println( "Dummy written, trying for read" );
            try
            {
                m_repository.list();
            }
            catch( Exception e )
            {
                getLogger().warn( "Exception reading from repository: " + e, e );
            }

            DummyClass read = null;
            try
            {
                read = (DummyClass)m_repository.get( argument );
            }
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
                String txt = (String)m_repository.get( k );
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
                it.next();
                c = c + 1;
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


