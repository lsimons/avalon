/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.naming.rmi.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.MarshalledObject;
import java.rmi.server.UnicastRemoteObject;

import org.apache.avalon.excalibur.naming.DefaultNameParser;
import org.apache.avalon.excalibur.naming.DefaultNamespace;
import org.apache.avalon.excalibur.naming.memory.MemoryContext;

/**
 * This is a simple test name server and should NOT be used in a production system.
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class Main
        implements Runnable
{
    public static void main( final String[] args )
            throws Exception
    {
        boolean debug = true;

        if( args.length > 0 )
        {
            if( "-q".equals( args[0] ) )
            {
                debug = false;
            }
        }

        final Main main = new Main( debug );
        main.start();
        main.accept();
    }

    private final boolean m_debug;
    private RMINamingProviderImpl m_server;
    private ServerSocket m_serverSocket;
    private MarshalledObject m_serverStub;
    private boolean m_running;
    private boolean m_initialized;

    public Main( boolean debug )
    {
        m_debug = debug;
    }

    public Main()
    {
        this( true );
    }

    public void init()
            throws Exception
    {
        if( m_initialized ) return;

        try
        {
            if( m_debug ) System.out.println( "Starting server on port " + 1977 );
            m_serverSocket = new ServerSocket( 1977 );
            m_initialized = true;
        }
        catch( final IOException ioe )
        {
            if( m_debug ) System.out.println( "Failed starting server" );
            throw ioe;
        }
    }

    public void start()
            throws Exception
    {
        init();
        export();
    }

    public void export()
            throws Exception
    {
        final DefaultNameParser parser = new DefaultNameParser();
        final DefaultNamespace namespace = new DefaultNamespace( parser );
        final MemoryContext context = new MemoryContext( namespace, null, null );
        m_server = new RMINamingProviderImpl( context );

        // Start listener
        try
        {
            // Export server
            if( m_debug ) System.out.println( "Exporting RMI object on port " + 1099 );
            m_serverStub =
                    new MarshalledObject( UnicastRemoteObject.exportObject( m_server, 1099 ) );
        }
        catch( final IOException ioe )
        {
            if( m_debug ) System.out.println( "Failed exporting object" );
            throw ioe;
        }
    }

    public void dispose()
            throws Exception
    {
        if( m_debug ) System.out.println( "Shutting down server" );
        m_running = false;
        final ServerSocket serverSocket = m_serverSocket;
        m_serverSocket = null;
        serverSocket.close();
        if( m_debug ) System.out.println( "Server shutdown" );
    }

    public void stop()
            throws Exception
    {
        if( m_debug ) System.out.println( "Stopping" );
        m_running = false;
        if( m_debug ) System.out.println( "Unexporting object" );
        UnicastRemoteObject.unexportObject( m_server, true );
        m_serverStub = null;
        if( m_debug ) System.out.println( "Server stopped" );
    }

    public void accept()
    {
        m_running = true;
        while( m_running )
        {
            // Accept a connection
            try
            {
                final Socket socket = m_serverSocket.accept();
                if( m_debug ) System.out.println( "Accepted Connection" );
                final ObjectOutputStream output =
                        new ObjectOutputStream( socket.getOutputStream() );

                output.writeObject( m_serverStub );

                socket.close();
            }
            catch( final IOException ioe )
            {
                if( !m_running ) break;
                ioe.printStackTrace();
            }
        }
    }

    public void run()
    {
        accept();
    }
}
