/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.httpproxy;

import java.net.URL;
import java.util.StringTokenizer;

/**
 * This handles an individual incoming request.  It outputs a greeting as html.
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class HttpFilteringProxyHandler
    extends HttpProxyHandler
{
    // used for callbacks
    protected HttpFilteringProxyServer   proxyServer;

    /**
     * Constructor HttpFilteringProxyHandler
     *
     */
    protected HttpFilteringProxyHandler( final HttpFilteringProxyServer proxyServer,
                                         final String forwardToAnotherProxy )
    {
        super( forwardToAnotherProxy );

        // used for callbacks
        this.proxyServer = proxyServer;
    }

    /**
     * Method validateRequest
     * Check to see whether domain name in blocked list.
     *
     * @throws HttpRequestValidationException
     *
     */
    protected void validateRequest()
        throws HttpRequestValidationException
    {
        final String domainName = httpRequestWrapper.getServerName();

        if( !proxyServer.domainAllowed( domainName ) )
        {
            throw new HttpBlockedDomainException( domainName );
            // perhaps this should be:
            // 1) test for image as mime type
            // 2) return 1x1 gif instead.
        }
    }

    /**
     * Method getOutgoingHttpRequest
     * Overrides that of parent, adding cookie removal functionality for the
     * filtering proxy server.
     *
     * @param anotherProxy
     *
     * @return
     *
     */
    public String getOutgoingHttpRequest( final boolean anotherProxy )
    {
        final String domainName = httpRequestWrapper.getServerName();
        final String httpRequest = super.getOutgoingHttpRequest( anotherProxy );

        if( !proxyServer.cookieAllowed( domainName ) )
        {
            final String request =
                getHttpRequestWithoutCookie( httpRequest, httpRequestWrapper.getURL() );
            return request.trim() + "\r\n\r\n";
        }
        else
        {
            return httpRequest;
        }
    }

    private String getHttpRequestWithoutCookie( final String httpRequest, final URL url )
    {
        final StringBuffer newHttpRequest = new StringBuffer();
        final StringTokenizer rqstTokens = new StringTokenizer( httpRequest, "\n\r" );

        while( rqstTokens.hasMoreTokens() )
        {
            final String line = rqstTokens.nextToken();

            if( !line.startsWith("Cookie:") )
            {
                newHttpRequest.append( line ).append( "\r\n" );
            }
            else
            {
                getLogger().info( "Cookie supressed for url :" + url.toString() );
            }
        }

        return newHttpRequest.toString().trim() + "\n\r";
    }
}
