/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
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
            if( "-q".equals( args[ 0 ] ) )
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
