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
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

/**
 * Communication link to an URL for the <code>sar</code> protocol. This is a
 * read-only connection.
 *
 * @author <a href="mailto:mirceatoma@home.com">Mircea Toma</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/10/27 17:02:17 $
 */
class SarURLConnection 
    extends URLConnection
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( SarURLConnection.class );

    private static final char SEPARATOR = '|';
    private JarInputStream m_input;
    private URL m_nestedURL;
    private String m_entryName;
    private final ArrayList m_entryNames = new ArrayList();

    private JarEntry m_entry;
    private Manifest m_manifest;

    /**
     * Creates new SarURLConnection.
     */
    public SarURLConnection( URL url ) throws MalformedURLException
    {
        super( url );
        parseSpec();
    }

    /**
     * Parse the specs for a given url.
     */
    private void parseSpec() throws MalformedURLException
    {
        final String spec = url.getFile();

        int separator = spec.lastIndexOf( SEPARATOR );
        if (separator == -1)
        {
            final String message =
                REZ.getString( "parse-url", String.valueOf( SEPARATOR ), spec );
            throw new MalformedURLException( message );
        }

        m_nestedURL = new URL( spec.substring( 0, separator++ ) );
        m_entryName = spec.substring( ++separator, spec.length() );
    }

    /**
     * Opens a communications link to the resource referenced by this URL,
     * if such a connection has not already been established.
     */
    public void connect() throws IOException
    {
        if (connected) return;

        m_input = new JarInputStream( m_nestedURL.openStream() );
        m_manifest = m_input.getManifest();

        // check if entry is directory or file
        if ( m_entryName.endsWith( "/" ) || ( m_entryName.length() == 0 ) )
        {
            while ( ( m_entry = m_input.getNextJarEntry() ) != null )
            {
                m_entryNames.add( m_entry.getName() );
            }

            connected = true;
        }
        else
        {
            while ( ( m_entry = m_input.getNextJarEntry() ) != null )
            {
                final String entryName = m_entry.getName();

                if ( m_entryName.equals( entryName ) )
                {
                    connected = true;
                    ifModifiedSince = m_entry.getTime();
                    return;
                }
            }
        }
    }

    /**
     * Get the input stream that reads from this open connection.
     * @return the InputStream.
     */
    public InputStream getInputStream() throws IOException
    {
        connect();
        return m_input;
    }

    /**
     * Get the Manifest of the jar containing the read resource.
     * @return the Manifest
     */
    public Manifest getManifest() throws IOException
    {
        connect();
        return m_manifest;
    }

    /**
     * Get the JarEntry info of the the read resource.
     * @return the JarEntry. <code>Null</code> if entry is a directory.
     */
    public JarEntry getJarEntry() throws IOException
    {
        connect();
        return m_entry;
    }

    /**
     * Get the entry names if the resource is a directory.
     * @return the entry name list. Zero size array if entry is not a directory.
     */
    public String[] list() throws IOException
    {
        connect();

        final ArrayList entryNames = new ArrayList();

        for ( int i = 0; i < m_entryNames.size(); i++ )
        {
            final String entryName = (String) m_entryNames.get( i );

            if ( entryName.startsWith( m_entryName ) )
            {
                String name = entryName.substring(m_entryName.length(), entryName.length());

                // select only entries within current level
                if ( ( ( name.indexOf("/") == -1 ) ||
                       ( name.indexOf( "/" ) == name.length() - 1 ) ) &&
                     ( name.length() != 0 ) )
                {
                    entryNames.add( name );
                }
            }
        }

        return (String[]) entryNames.toArray( new String[0] );
    }
}
