/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.httpproxy;

/**
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public interface HttpFilteringProxyServer
    extends HttpProxyServer
{
    /**
     * Method blockAllContentFrom No requests will reach this site.
     */
    void blockAllContentFrom( String domainName, boolean onOff );

    /**
     * Method allowCookiesFrom Allows Cookies to be sent to this site.
     * This is kinda redundant with some of the features of Netscape 4.x and 6.x
     */
    void allowCookiesFrom( String domainName, boolean onOff );

    /**
     * Method domainAllowed Check to see whether the appl domain should be blocked
     */
    boolean domainAllowed( String domainName );

    /**
     * Method cookieAllowed Check to see whether the appl domain can be sent cookies
     */
    boolean cookieAllowed( String domainName );

    /**
     * List the domains that are completely blocked.
     */
    String[] getBlockedDomains();

    /**
     * List the domains for which cookies are not sent.
     */
    String[] getCookieSuppressedDomains();
}
