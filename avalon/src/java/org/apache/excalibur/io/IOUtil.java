/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.io;

import java.io.*;

/**
 * This class provides basic facilities for manipulating io streams.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class IOUtil
{
    /**
     * Private constructor to prevent instantiation.
     */
    private IOUtil()
    {
    }

    public static void shutdownStream( final OutputStream output )
    {
        if( null == output ) return;

        try { output.close(); }
        catch( final IOException ioe ) {}
    }

    public static void shutdownStream( final InputStream input )
    {
        if( null == input ) return;

        try { input.close(); }
        catch( final IOException ioe ) {}
    }

    /**
     * Copy stream-data from source to destination.
     */
    public static void copy( final InputStream source, final OutputStream destination )
        throws IOException
    {
        try
        {
            final BufferedInputStream input = new BufferedInputStream( source );
            final BufferedOutputStream output = new BufferedOutputStream( destination );

            final int BUFFER_SIZE = 1024 * 4;
            final byte[] buffer = new byte[ BUFFER_SIZE ];

            while( true )
            {
                final int count = input.read( buffer, 0, BUFFER_SIZE );
                if( -1 == count ) break;

                // write out those same bytes
                output.write( buffer, 0, count );
            }

            //needed to flush cache
            output.flush();
        }
        finally
        {
            shutdownStream( source );
            shutdownStream( destination );
        }
    }
}
