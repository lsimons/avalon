/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.httpproxy;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.Component;
import org.apache.cornerstone.services.connection.ConnectionHandler;

/**
 * This handles an individual incoming request.  It returns a bytes etc from remote server.
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public abstract class HttpProxyHandler
    extends AbstractLoggable
    implements Component, ConnectionHandler
{
    protected Socket              clientSocket;
    protected Socket              serverSocket;
    protected String              clientHost;
    protected String              clientIP;
    protected String              forwardToAnotherProxy;
    protected HttpRequestWrapper  httpRequestWrapper;

    protected HttpProxyHandler( final String forwardToAnotherProxy )
    {
        this.forwardToAnotherProxy = forwardToAnotherProxy;
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
        this.clientSocket = socket;
        clientHost = clientSocket.getInetAddress().getHostName();
        clientIP = clientSocket.getInetAddress().getHostAddress();

        try
        {
            httpRequestWrapper =
                HttpRequestWrapper.createHttpRequestWrapper( getLogger(),
                                                             clientSocket.getInputStream() );

            validateRequest();
            forwardRequest();
        }
        catch( final HttpRequestValidationException hrve )
        {
            write403Page();
            getLogger().info( hrve.getMessage() );
        }
        catch( final SocketException se )
        {
            getLogger().debug( "Socket to " + clientHost + " closed remotely.", se );
        }
        catch( final InterruptedIOException iioe )
        {
            getLogger().debug( "Socket to " + clientHost + " timeout.", iioe );
        }
        catch( final IOException ioe )
        {
            getLogger().debug( "Exception in proxy handling socket to " + clientHost, ioe );
        }
        catch( final Exception e )
        {
            getLogger().debug( "Exception in proxy opening socket", e );
        }
        finally
        {
            try { clientSocket.close(); }
            catch( final IOException ioe )
            {
                getLogger().error( "Exception closing client socket ", ioe );
            }

            if( null != serverSocket )
            {
                try { serverSocket.close(); }
                catch( final IOException ioe )
                {
                    getLogger().error( "Exception closing server socket ", ioe );
                }
            }
        }
    }

    protected Socket makeOutgoingSocket()
        throws IOException
    {
        if( !forwardToAnotherProxy.equals("") )
        {
            int colon = forwardToAnotherProxy.indexOf(':');
            String toName = forwardToAnotherProxy.substring(0, colon);
            int toPort = Integer.
                parseInt( forwardToAnotherProxy.substring( colon + 1,
                                                           forwardToAnotherProxy.length() ) );

            return new Socket( toName, toPort );
        }
        else
        {
            return new Socket( httpRequestWrapper.getServerInetAddress(),
                               httpRequestWrapper.getServerPort() );
        }
    }

    public String getOutgoingHttpRequest( final boolean anotherProxy )
    {
        if( anotherProxy )
        {
            return httpRequestWrapper.getNakedHttpRequest().trim() + "\r\n\r\n";
        }
        else
        {
            return httpRequestWrapper.getHttpRequest().trim() + "\r\n\r\n";
        }
    }

    public final void forwardRequest()
        throws IOException
    {
        String request = getOutgoingHttpRequest( ( !forwardToAnotherProxy.equals("") ) );

        try
        {
            serverSocket = makeOutgoingSocket();

            serverSocket.getOutputStream().write( request.getBytes() );

            byte[] page = new byte[2048];
            int pLength;
            BufferedOutputStream bufToClient =
                new BufferedOutputStream( clientSocket.getOutputStream() );
            InputStream serverIS = serverSocket.getInputStream();

            do
            {
                pLength = serverIS.read( page );

                if( -1 != pLength )
                {
                    bufToClient.write( page, 0, pLength );
                }
            }
            while( -1 != pLength );

            bufToClient.close();
        }
        catch( final Exception e )
        {
            // general catch is deliberate, see writeErrorPage(..)
            writeErrorPage( e );
        }
    }

    private void writeErrorPage( final Exception e )
        throws IOException
    {
        final PrintWriter output = new PrintWriter( clientSocket.getOutputStream() );

        output.println( "<html><body>Unable to reach <b>" + httpRequestWrapper.getServerName() +
                        ":" + httpRequestWrapper.getServerPort() + "</b> at the moment." );
        output.println( "<br />This Message is bought to you by the Avalon demo proxy server." );
        output.println( "<br />If you had a direct connection to the net, you would not see " +
                        "this message, your browser would instead tell you it could not reach" +
                        " the site.<br />");

        if( e instanceof UnknownHostException )
        {
            output.println( "<br />The probable cause is that the domain name does not exist," +
                            " of the route to it is severed." );
        }
        else if( e instanceof ConnectException )
        {
            output.println( "<br />The probable cause is that the machine at domain name is" +
                            " not running a service at the port number in question (HTTP or" +
                            " any other)." );
        }
        else
        {
            output.println( "<br />The cause is unknown, this may help though: " +
                            e.getMessage() );
        }

        output.println( "</body></head>" );
        output.flush();
        output.close();
    }

    // Block the resource, buy using http code 403.
    // "403 Forbidden Resource is not available, regardless of authorization."
    private void write403Page()
    {
        try
        {
            final PrintWriter output = new PrintWriter( clientSocket.getOutputStream() );

            output.println( "<html><head><title>Blocked</title></head>" +
                            "<body>Blocked</body></html>" );
            output.flush();
            output.close();
        }
        catch( final IOException ioe ) {}
    }
    protected abstract void validateRequest()
        throws HttpRequestValidationException;
}
