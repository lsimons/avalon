/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.httpproxy;

import java.net.Socket;
import org.apache.log.Logger;

/**
 * This handles an individual incoming request.  It outputs a greeting as html.
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class HttpAuditingProxyHandler
    extends HttpProxyHandler
{
    protected HttpAuditingProxyHandler( final String forwardToAnotherProxy )
    {
        super( forwardToAnotherProxy);
    }

    protected void validateRequest()
        throws HttpRequestValidationException
    {
        // no validate, just log.
        getLogger().info("Connection to " + httpRequestWrapper.getURL());
    }
}

