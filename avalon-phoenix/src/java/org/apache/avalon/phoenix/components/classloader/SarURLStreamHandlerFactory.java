/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * Factory for <code>sar:</code> URL stream protocol handler.
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 * @version CVS $Revision: 1.2 $ $Date: 2001/10/27 15:20:38 $
 */
class SarURLStreamHandlerFactory
    implements URLStreamHandlerFactory
{
    /**
     * Creates a new <code>URLStreamHandler</code> instance with the specified
     * protocol.
     *
     * @param protocol the protocol ("<code>ftp</code>", "<code>http</code>",
     * "<code>nntp</code>", etc.).
     * @return  a <code>URLStreamHandler</code> for the specific protocol.
     * @see java.net.URLStreamHandler
     */
    public URLStreamHandler createURLStreamHandler( String protocol )
    {
        if ( "sar".equals( protocol ) )
        {
            return new SarURLStreamHandler();
        }

        return null;
    }
}
