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
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.phoenix.tools.protocols.sar.SarURLConnection;

/**
 * This is the connection factory for the <code>sar</code> protocol.
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/10/27 15:21:47 $
 */
final class SarURLStreamHandler
    extends URLStreamHandler
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( SarURLStreamHandler.class );

    /**
     * Creates a new URLConnection.
     *
     * @param url the URL where to connect.
     * @return the URLConnection.
     */
    public URLConnection openConnection( final URL url )
        throws IOException
    {
        if ( "sar".equals( url.getProtocol() ) )
        {
            return new SarURLConnection( url );
        }

        final String message = REZ.getString( "create-connection", url.getProtocol() );
        throw new IOException( message );
    }
}
