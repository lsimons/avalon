/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.avalon.excalibur.io.IOUtil;

/**
 * Expands a zip file.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ZipExpander
{
    public void expand( final File file, final File directory )
        throws IOException
    {
        expand( file, directory, null );
    }

    public void expand( final File file, final File directory, final FilenameFilter filter )
        throws IOException
    {
        final ZipFile zipFile = new ZipFile( file );

        directory.mkdirs();

        final Enumeration entries = zipFile.entries();
        while( entries.hasMoreElements() )
        {
            final ZipEntry entry = (ZipEntry)entries.nextElement();
            if( entry.isDirectory() ) continue;

            final String name = entry.getName().replace( '/', File.separatorChar );

            //If filter exists and saids 'don't accept' then skip entry
            if( null != filter && !filter.accept( directory, name ) )
            {
                continue;
            }

            //TODO: Do this before filter and use getParentFile(), getName()
            final File destination = new File( directory, name );

            InputStream input = null;
            OutputStream output = null;

            try
            {
                destination.getParentFile().mkdirs();
                output = new FileOutputStream( destination );
                input = zipFile.getInputStream( entry );
                IOUtil.copy( input, output );
            }
            finally
            {
                IOUtil.shutdownStream( input );
                IOUtil.shutdownStream( output );
            }
        }
    }

/*
    private boolean shouldExpandEntry( final String name )
    {
        if( name.startsWith( "META-INF" ) ) return false;
        else return true;
    }
*/
}
