/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Data written to this stream is forwarded to a stream that has been associated
 * with this thread.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:31:43 $
 */
public final class DemuxOutputStream
    extends OutputStream
{
    private final InheritableThreadLocal m_streams = new InheritableThreadLocal();

    /**
     * Bind the specified stream to the current thread.
     *
     * @param output the stream to bind
     */
    public OutputStream bindStream( final OutputStream output )
    {
        final OutputStream stream = getStream();
        m_streams.set( output );
        return stream;
    }

    /**
     * Closes stream associated with current thread.
     *
     * @throws IOException if an error occurs
     */
    public void close()
        throws IOException
    {
        final OutputStream output = getStream();
        if( null != output )
        {
            output.close();
        }
    }

    /**
     * Flushes stream associated with current thread.
     *
     * @throws IOException if an error occurs
     */
    public void flush()
        throws IOException
    {
        final OutputStream output = getStream();
        if( null != output )
        {
            output.flush();
        }
    }

    /**
     * Writes byte to stream associated with current thread.
     *
     * @param ch the byte to write to stream
     * @throws IOException if an error occurs
     */
    public void write( final int ch )
        throws IOException
    {
        final OutputStream output = getStream();
        if( null != output )
        {
            output.write( ch );
        }
    }

    /**
     * Utility method to retrieve stream bound to current thread (if any).
     */
    private OutputStream getStream()
    {
        return (OutputStream)m_streams.get();
    }
}
