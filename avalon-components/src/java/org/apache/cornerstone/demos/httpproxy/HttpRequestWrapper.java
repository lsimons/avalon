/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.httpproxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import org.apache.log.Logger;

/**
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class HttpRequestWrapper
{
    protected final static String     EOF        = "\r\n\r\n";
    protected final static int        SEGLEN     = 2048;

    protected final Logger            m_logger;
    protected String                  m_request;
    protected URL                     m_url;

    protected HttpRequestWrapper( final Logger logger )
    {
        m_logger = logger;
    }

    protected HttpRequestWrapper( final Logger logger, final String request )
        throws IOException
    {
        this( logger );
        setRequest( request );
    }

    protected void setRequest( final String request )
        throws IOException
    {
        m_request = request;

        try
        {
            m_url = new URL( getURLFromHttpHeader() );
        }
        catch( final MalformedURLException mfue )
        {
            m_logger.error( "URL from http header is malformed", mfue );
        }
    }

    public final URL getURL()
    {
        return m_url;
    }

    /**
     * Method getServerInetAddress
     * The server's internet address.
     */
    public final InetAddress getServerInetAddress()
        throws UnknownHostException
    {
        return InetAddress.getByName( m_url.getHost() );
    }

    private String getURLFromHttpHeader()
    {
        try
        {
            int hostStart = m_request.indexOf( " " ) + 1;

            return m_request.substring( hostStart,
                                        m_request.indexOf( " ", hostStart ) );
        }
        catch( final StringIndexOutOfBoundsException sioobe )
        {
            m_logger.error( "Unable to find URL in http header", sioobe );
        }

        return null;
    }

    /**
     * Method getServerName
     * The server's domain name
     */
    public String getServerName()
    {
        return m_url.getHost();
    }

    /**
     * Method getServerPort
     * The port on the server where the http requests should be sent.
     */
    public int getServerPort()
    {
        final int port = m_url.getPort();

        return ((port == -1) ? 80 : port);
    }

    /**
     * Method getNakedHttpRequest
     * Used to forward requests to another proxy server
     */
    public final String getNakedHttpRequest()
    {
        return m_request;
    }

    /**
     * Method getHttpRequest
     * Get the address without the proxy extras
     */
    public String getHttpRequest()
    {
        // strip out proxy info.
        int hostEnd = 1 +
            m_request.indexOf( "/", m_request.indexOf( "//" ) + 2 );

        return
            m_request.substring( 0, m_request.indexOf("http://") ) + "/" +
            m_request.substring( hostEnd, m_request.length() );
    }

    /**
     * Method createHttpRequestWrapper
     * Factory to create the right sub class of HttpRequestWrapper.
     *
     */
    static HttpRequestWrapper createHttpRequestWrapper( final Logger logger,
                                                        final InputStream is )
        throws IOException
    {
        byte[] threeBytes = new byte[3];
        int bytes = is.read( threeBytes );

        if( 3 != bytes )
        {
            throw new RuntimeException( "not three bytes?" );
        }

        final String reqType = new String( threeBytes, 0, bytes );

        if( reqType.equals("POS") )
        {
            return new HttpPostRequestWrapper( logger, is );
        }
        else
        {
            return new HttpGetRequestWrapper( logger, is );
        }
    }
}
