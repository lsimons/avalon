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
public class HttpBlockedDomainException 
    extends HttpRequestValidationException
{
    /**
     * Constructor HttpBlockedDomainException
     * The domain in question is not allowed to be connected to given the
     * rules of the filter.
     */
    public HttpBlockedDomainException( final String domainName )
    {
        super( "Access to " + domainName + "has been blocked" );
    }
}

