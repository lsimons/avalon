/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.helloworldserver;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Component;
import org.apache.cornerstone.services.connection.ConnectionHandler;

/**
 * This handles an individual incoming request.  It outputs a greeting as html.
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @author Federico Barbieri <scoobie@systemy.it>
 * @version 1.0
 */
public class HelloWorldHandler
    extends AbstractLoggable
    implements Component, ConnectionHandler
{
    protected static int        c_counter;

    protected String            m_greeting;

    protected HelloWorldHandler( final String greeting )
    {
        m_greeting = greeting;
    }

    /**
     * Handle a connection.
     * This handler is responsible for processing connections as they occur.
     *
     * @param socket the connection
     * @exception IOException if an error reading from socket occurs
     */
    public void handleConnection( final Socket socket )
        throws IOException
    {
        final String remoteHost = socket.getInetAddress().getHostName();
        final String remoteIP = socket.getInetAddress().getHostAddress();
        final PrintWriter out = new PrintWriter( socket.getOutputStream(), true );

        try
        {
            out.println( "<html><body><b>" + m_greeting + "!</b><br> Requests so far = " +
                         ++c_counter + "<br>" );
            out.println( "you are " + remoteHost + " at " + remoteIP + "<br>" );
            out.println( "</body></html>" );

            socket.close();
        }
        catch( final SocketException se )
        {
            getLogger().debug( "Socket to " + remoteHost + " closed remotely in HelloWorld", se );
        }
        catch( final InterruptedIOException iioe )
        {
            getLogger().debug( "Socket to " + remoteHost + " timeout in HelloWorld", iioe );
        }
        catch( final IOException ioe )
        {
            getLogger().debug( "Exception in HelloWorld handling socket to " + remoteHost ,
                               ioe );
        }
        catch( final Exception e )
        {
            getLogger().debug( "Exception in HelloWorld opening socket", e );
        }
        finally
        {
            try { socket.close(); }
            catch( final IOException ioe )
            {
                getLogger().error( "Exception closing socket ", ioe );
            }
        }

        getLogger().info( "Connection from " + remoteHost + " (" + remoteIP + ")" );
    }
}
