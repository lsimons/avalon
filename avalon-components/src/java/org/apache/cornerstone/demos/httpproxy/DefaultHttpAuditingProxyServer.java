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
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class DefaultHttpAuditingProxyServer
    extends AbstractHttpProxyServer
    implements HttpAuditingProxyServer
{
    public DefaultHttpAuditingProxyServer()
    {
        super("Auditing");
    }

    /**
     * Method createHttpProxyHandler
     * Factory method, overriding that of parent, to create the right handler for
     * an individual request.
     */
    protected HttpProxyHandler newHttpProxyHandler()
    {
        return new HttpAuditingProxyHandler( m_forwardToAnotherProxy );
    }
}
