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
package org.apache.excalibur.instrument.manager.http.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.excalibur.instrument.CounterInstrument;

/**
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/09/08 09:00:46 $
 * @since 4.1
 */
public class HTTPServer
    extends AbstractSocketServer
{
    /** List of registered HTTPURLHandlers. */
    private List m_handlers = new ArrayList();
    
    /** Optimized array of the handler list that lets us avoid synchronization */
    private HTTPURLHandler[] m_handlerArray;
    
    /** Number of requests. */
    private CounterInstrument m_instrumentRequests;
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new HTTPServer.
     *
     * @param port The port on which the server will listen.
     * @param bindAddress The address on which the server will listen for
     *                    connections.
     */
    public HTTPServer( int port, InetAddress bindAddress )
    {
        super( port, bindAddress );
        
        addInstrument( m_instrumentRequests = new CounterInstrument( "requests" ) );
    }
    
    /*---------------------------------------------------------------
     * AbstractSocketServer Methods
     *-------------------------------------------------------------*/
    /**
     * Handle a newly connected socket.  The implementation need not
     *  worry about closing the socket.
     *
     * @param socket Newly connected Socket to be handled.
     */
    protected void handleSocket( Socket socket )
    {
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "handleSocket( " + socket + " ) BEGIN : "
                + Thread.currentThread().getName() );
        }
        
        try
        {
            // As long as we have valid requests, keep the connection open.
            while ( handleRequest( socket.getInputStream(), socket.getOutputStream() ) )
            {
            }
        }
        catch ( java.net.SocketTimeoutException e )
        {
            // The connection simply timed out and closed.
        }
        catch ( java.net.SocketException e )
        {
            // The connection simply timed out and closed.
        }
        catch ( Throwable e )
        {
            getLogger().debug( "Encountered an error processing the request.", e );
        }
        
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( "handleSocket( " + socket + " ) END : "
                + Thread.currentThread().getName() );
        }
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Registers a new HTTP URL Handler with the server.
     *
     * @param handler The handler to register.
     */
    public void registerHandler( HTTPURLHandler handler )
    {
        synchronized( m_handlers )
        {
            m_handlers.add( handler );
            m_handlerArray = null;
        }
    }
    
    private boolean handleRequest( InputStream is, OutputStream os )
        throws IOException
    {
        // We only support the GET method and know nothing of headers so this is easy.
        //  The first line of the request contains the requested url along with any
        //  and all encoded variables.  All of the following lines until a pair of
        //  Line feeds are headers and can be skipped for now.
        
        // Get the actual output stream so we can write the response.
        PrintStream out = new PrintStream( os );
        
        // Read the input header
        BufferedReader r = new BufferedReader( new InputStreamReader( is ) );
        String request = r.readLine();
        if ( request == null )
        {
            // EOF
            return false;
        }
        
        // Read any headers until we get a blank line
        String header;
        do
        {
            header = r.readLine();
        }
        while ( ( header != null ) && ( header.length() > 0 ) );
        
        Throwable error = null;
        
        // Parse the header to make sure it is valid.
        StringTokenizer st = new StringTokenizer( request, " " );
        if ( st.countTokens() == 3 )
        {
            String method = st.nextToken();
            String url = st.nextToken();
            String version = st.nextToken();
            
            if ( method.equals( "GET" ) && version.startsWith( "HTTP/" ) )
            {
                // Extract the path and parameters from the request.
                String path;
                String query = null;
                int pos = url.indexOf( '?' );
                if ( pos > 0 )
                {
                    path = url.substring( 0, pos );
                    
                    if ( pos < url.length() - 1 )
                    {
                        query = url.substring( pos + 1 );
                    }
                }
                else
                {
                    path = url;
                }

                // We now have the path and the params.
                // Look for an HTTPURLHandler which which maps to the path.  Look in
                //  order until one is found.
                
                HTTPURLHandler[] handlers = getHandlers();
                for ( int i = 0; i < handlers.length; i++ )
                {
                    HTTPURLHandler handler = handlers[i];
                    
                    //getLogger().debug( "Test: '" + path + "' starts with '" + handler.getPath() + "'" );
                    if ( path.startsWith( handler.getPath() ) )
                    {
                        // We found it.
                        //getLogger().debug( "  => Matched." );
                        
                        // Decode the query string
                        Map params = new HashMap();
                        if ( query != null )
                        {
                            decodeQuery( params, query, handler.getEncoding() );
                        }
        
                        if ( getLogger().isDebugEnabled() )
                        {
                            getLogger().debug( "Request Path: " + path );
                            getLogger().debug( "  Parameters: " + params.toString() );
                        }
                        
                        m_instrumentRequests.increment();
                        
                        // Create a ByteArrayOutputStream that will be used to get the total number
                        //  bytes that will be written in the response.  This is necessary to set
                        //  the content length in the return headers.
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        boolean ok;
                        // Handle the URL
                        try
                        {
                            handler. handleRequest( path, params, bos );
                            
                            ok = true;
                        }
                        catch ( HTTPRedirect e )
                        {
                            if ( getLogger().isDebugEnabled() )
                            {
                                getLogger().debug( "Redirect to: " + e.getPath() );
                            }
                            
                            byte[] contents = ( "<html><head><title>302 Found</title></head><body>"
                                + "The document has moved <a href='" + e.getPath() + "'>here</a>"
                                + "</body></html>" ).getBytes( handler.getEncoding() );
                                
                            // Write the response.
                            out.println( "HTTP/1.1 302 Found" ); // MOVED_TEMP
                            out.println( "Date: " + new Date() );
                            out.println( "Server: Avalon Instrument Manager HTTP Connector" );
                            out.println( "Content-Length: " + contents.length );
                            out.println( "Location: " + e.getPath() );
                            out.println( "Keep-Alive: timeout=" + ( getSoTimeout() / 1000 ) );
                            out.println( "Connection: Keep-Alive" );
                            out.println( "Content-Type: " + handler.getContentType() );
                            // Make sure that no caching is done by the client
                            out.println( "Pragma: no-cache" );
                            out.println( "Expires: Thu, 01 Jan 1970 00:00:00 GMT" );
                            out.println( "Cache-Control: no-cache" );
                            
                            // Terminate the Headers.
                            out.println( "" );
                            
                            // Write out the actual data.
                            out.write( contents, 0, contents.length );
                            
                            // Flush the output and we are done.
                            out.flush();
                            
                            return true;
                        }
                        catch ( Throwable t )
                        {
                            // Error
                            error = t;
                            ok = false;
                        }
                        
                        if ( ok )
                        {
                            byte[] contents = bos.toByteArray();
                            
                            // Write the response.
                            out.println( "HTTP/1.1 200 OK" );
                            out.println( "Date: " + new Date() );
                            out.println( "Server: Avalon Instrument Manager HTTP Connector" );
                            out.println( "Content-Length: " + contents.length );
                            out.println( "Keep-Alive: timeout=" + ( getSoTimeout() / 1000 ) );
                            out.println( "Connection: Keep-Alive" );
                            out.println( "Content-Type: " + handler.getContentType() );
                            // Make sure that no caching is done by the client
                            out.println( "Pragma: no-cache" );
                            out.println( "Expires: Thu, 01 Jan 1970 00:00:00 GMT" );
                            out.println( "Cache-Control: no-cache" );
                            
                            // Terminate the Headers.
                            out.println( "" );
                            
                            // Write out the actual data.
                            out.write( contents, 0, contents.length );
                            
                            // Flush the output and we are done.
                            out.flush();
                            
                            // Do not close the output stream as it may be reused.
                            
                            return true;
                        }
                    }
                }
            }
        }
        
        // If we get here then the request failed.  Always return 404 for now.
        // Write the response.
        out.println( "HTTP/1.1 404 Not found" );
        out.println( "Date: " + new Date() );
        out.println( "Server: Avalon Instrument Manager HTTP Connector" );
        out.println( "" );
        out.println( "The Requested page does not exist" );
        if ( error !=  null )
        {
            out.println( "---" );
            if ( error instanceof FileNotFoundException )
            {
                out.println( error.getMessage() );
            }
            else
            {
                getLogger().error( "Error servicing request.", error );
                error.printStackTrace( out );
            }
        }
        
        // Flush the output and we are done.
        out.flush();
        
        return false;
    }
    
    public void setParameter( Map params, String param, String value )
    {
        Object old = params.get( param );
        if ( old == null )
        {
            params.put( param, value );
        }
        else
        {
            if ( old instanceof String )
            {
                List list = new ArrayList();
                list.add( old );
                list.add( value );
                params.put( param, list );
            }
            else
            {
                List list = (List)old;
                list.add( value );
            }
        }
    }
    
    private void decodeParameter( Map params, String pair, String encoding )
    {
        int pos = pair.indexOf( '=' );
        if ( pos > 0 )
        {
            try
            {
                String param = URLDecoder.decode( pair.substring( 0, pos ), encoding );
                String value;
                if ( pos < pair.length() - 1 )
                {
                    value = URLDecoder.decode( pair.substring( pos + 1 ), encoding );
                }
                else
                {
                    value = "";
                }
                
                setParameter( params, param, value );
            }
            catch ( UnsupportedEncodingException e )
            {
                throw new IllegalArgumentException( "An unsupported encoding '" + encoding + "' "
                    + "was specified: " + e.getMessage() );
            }
        }
    }
    
    private void decodeQuery( Map params, String query, String encoding )
    {
        StringTokenizer st = new StringTokenizer( query, "&" );
        while ( st.hasMoreTokens() )
        {
            decodeParameter( params, st.nextToken(), encoding );
        }
    }
    
    private HTTPURLHandler[] getHandlers()
    {
        HTTPURLHandler[] handlers = m_handlerArray;
        if ( handlers == null )
        {
            synchronized( m_handlers )
            {
                handlers = new HTTPURLHandler[ m_handlers.size() ];
                m_handlers.toArray( handlers );
                m_handlerArray = handlers;
            }
        }
        
        return handlers;
    }
}

