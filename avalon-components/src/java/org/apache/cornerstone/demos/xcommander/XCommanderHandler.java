/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.xcommander;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.SocketException;
import org.apache.avalon.activity.Initializable;
import org.apache.avalon.component.Component;
import org.apache.avalon.logger.AbstractLoggable;
import org.apache.cornerstone.services.connection.ConnectionHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This handles an individual incoming XCommander request.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class XCommanderHandler
    extends AbstractLoggable
    implements Component, Initializable, ConnectionHandler, CommandHandler
{
    protected final static String DEFAULT_PARSER = "org.apache.xerces.parsers.SAXParser";
    protected final static String PARSER =
        System.getProperty("org.xml.sax.parser", DEFAULT_PARSER );
    protected XMLReader m_parser;
    protected XCommanderServer m_parent;

    protected BufferedReader m_in = null;
    protected BufferedWriter m_out = null;

    public XCommanderHandler( XCommanderServer parentServer )
    {
        m_parent = parentServer;
    }

    public void initialize() throws Exception
    {
        // create a SAX2 Parser from the org.xml.sax.parser system property,
        // or else from the DEFUALT_PARSER.
        try
        {
            m_parser = XMLReaderFactory.createXMLReader( PARSER );
            //m_parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            DefaultSAXHandler saxHandler = new DefaultSAXHandler( this );
            m_parser.setContentHandler( saxHandler );
            m_parser.setErrorHandler( saxHandler );
        }
        catch( final SAXException se )
        {
            getLogger().error( "Unable to setup SAX parser: " + se );
        }
    }

    public void handleCommand( String type, String identifier, Object result )
    {
        if(result instanceof GlobalResult)
        {
            System.out.println( "Sending result " + result + " to main server." );
            m_parent.handleCommand( type, identifier, result );
        }
        else
        {
            getLogger().info( "handling command: " + identifier +
                              " - sending results: " + result.toString() );
            if( m_out != null )
            {
                String results = "<?xml version=\"1.0\" ?>\n";
                results +=
                    "<results type=\"" + type + "\" identifier=\"" + identifier + "\">\n";
                results += result.toString() + "\n</results>";

                try
                {
                    final char[] end = new char[1];
                    end[0] = '\u0000';
                    m_out.write( results ); // should be valid xml
                    m_out.write( end );
                    m_out.flush();
                }
                catch( final SocketException se )
                {
                    getLogger().warn( "Socket closed remotely.", se );
                }
                catch( final InterruptedIOException iioe )
                {
                    getLogger().warn( "Socket timeout.", iioe );
                }
                catch( IOException ioe )
                {
                    getLogger().warn( "Exception handling socket:" + ioe.getMessage(), ioe );
                }
                catch( final Exception e )
                {
                    getLogger().warn( "Exception on socket: " + e.getMessage(), e );
                }
            }
            else
            {
                getLogger().info( "Exception: Socket not available." );
            }
        }
    }

    public Class getCommand( final String commandName )
    {
        return m_parent.getCommand( commandName );
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
        String remoteHost = null;
        String remoteIP = null;

        try
        {
            m_in =
                new BufferedReader( new InputStreamReader( socket.getInputStream() ), 1024 );
            m_out =
                new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ) );

            remoteHost = socket.getInetAddress().getHostName();
            remoteIP = socket.getInetAddress().getHostAddress();

            getLogger().info( "Connection from " + remoteHost + " ( " + remoteIP + " )" );

            // notify XCommanderServer of the new client
            m_parent.addClient(this);

            // read the input. When a zero byte is encountered,
            // pass the input to the SAX parser.
            String inputLine;
            int streamResult;
            char buf[] = new char[1];
            do
            {
                inputLine = "";

                do
                {
                    streamResult = m_in.read( buf, 0, 1 );
                    inputLine += buf[0];
                }
                while( buf[0] != '\u0000' );

                inputLine = inputLine.substring(0,(inputLine.length()-1));

                if( -1 != inputLine.indexOf( "<command" ) )
                {
                    if( !inputLine.startsWith( "<?xml " ) )
                    {
                        inputLine = "<?xml version=\"1.0\" ?> " + inputLine;
                    }
                    m_parser.parse( new InputSource( new StringReader( inputLine ) ) );
                }
            }
            while( streamResult != -1 );

            //Finish
            m_out.flush();

            // we lost the client; notify the server
            m_parent.removeClient( this );

            m_out.close();
            m_in.close();
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

            try { m_parent.removeClient( this ); }
            catch( final Exception e )
            {
            }
        }
    }
}

