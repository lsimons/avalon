/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.tools.protocols;

import java.net.URLStreamHandlerFactory;
import java.net.URLStreamHandler;
import org.apache.avalon.phoenix.tools.protocols.sar.Handler;

/**
 * Factory for custom URL stream protocol handlers.
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/10/14 00:41:09 $
 */
public class DefaultURLStreamHandlerFactory implements URLStreamHandlerFactory
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
        Handler handler = null;
        
        if ( "sar".equals( protocol ) )
        {
            handler = new Handler();
        }
        
        return handler;
    }
    
}
