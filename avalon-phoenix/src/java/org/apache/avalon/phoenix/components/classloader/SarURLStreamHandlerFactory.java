/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * Implementation and "factory" and for <code>sar:</code> URL stream
 * protocol handler.
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 * @version CVS $Revision: 1.6 $ $Date: 2001/10/27 22:47:10 $
 */
class SarURLStreamHandlerFactory
    extends URLStreamHandler
    implements URLStreamHandlerFactory
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( SarURLStreamHandlerFactory.class );

    private static final String PROTOCOL = "sar";

    /**
     * Creates a new <code>URLStreamHandler</code> instance with the specified
     * protocol.
     *
     * @param protocol the protocol ("<code>ftp</code>", "<code>http</code>",
     * "<code>nntp</code>", etc.).
     * @return  a <code>URLStreamHandler</code> for the specific protocol.
     * @see java.net.URLStreamHandler
     */
    public URLStreamHandler createURLStreamHandler( final String protocol )
    {
        if( PROTOCOL.equals( protocol ) )
        {
            return this;
        }

        return null;
    }

    /**
     * Creates a new URLConnection.
     *
     * @param url the URL where to connect.
     * @return the URLConnection.
     */
    public URLConnection openConnection( final URL url )
        throws IOException
    {
        if( PROTOCOL.equals( url.getProtocol() ) )
        {
            return new SarURLConnection( url );
        }

        final String message = REZ.getString( "create-connection", url.getProtocol() );
        throw new IOException( message );
    }
}
