/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * Communication link to an URL for the <code>sar</code> protocol. This is a
 * read-only connection.
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 * @version CVS $Revision: 1.5 $ $Date: 2001/10/28 11:31:05 $
 */
class SarURLConnection
    extends URLConnection
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( SarURLConnection.class );

    private JarEntry m_entry;
    private JarFile m_jar;

    /**
     * Creates new SarURLConnection.
     */
    public SarURLConnection( final JarFile jar, final URL url )
        throws MalformedURLException
    {
        super( url );
        m_jar = jar;
        useCaches = false;
    }

    /**
     * Opens a communications link to the resource referenced by this URL,
     * if such a connection has not already been established.
     */
    public void connect() throws IOException
    {
        if (connected) return;

        final String name = url.getPath().substring( 1 );
        m_entry = m_jar.getJarEntry( name );
        if( null == m_entry )
        {
            throw new IOException( "No entry named " + name + " in .sar file." );
        }

        ifModifiedSince = m_entry.getTime();

        connected = true;
    }

    /**
     * Get the input stream that reads from this open connection.
     *
     * @return the InputStream.
     */
    public InputStream getInputStream() 
        throws IOException
    {
        connect();
        return m_jar.getInputStream( m_entry );
    }
}
