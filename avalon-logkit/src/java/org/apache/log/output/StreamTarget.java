/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.log.Formatter;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;

/**
 * A basic target that writes to an OutputStream.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class StreamTarget
    extends AbstractOutputTarget
{
    ///OutputStream we are writing to
    private OutputStream   m_outputStream;

    /**
     * Constructor that writes to a stream and uses a particular formatter.
     *
     * @param outputStream the OutputStream to write to
     * @param formatter the Formatter to use
     */
    public StreamTarget( final OutputStream outputStream, final Formatter formatter )
    {
        super( formatter );

        if( null != outputStream )
        {
            setOutputStream( outputStream );
        }
    }

    /**
     * Set the output stream.
     * Close down old stream and write tail if appropriate.
     *
     * @param outputStream the new OutputStream
     */
    protected synchronized void setOutputStream( final OutputStream outputStream )
    {
       if( null == outputStream )
       {
           throw new NullPointerException( "outputStream property must not be null" );
       }
       
       if( null != m_outputStream )
       {
           close();
       }
       
       m_outputStream = outputStream;
       open();
    }

    /**
     * Abstract method that will output event.
     *
     * @param data the data to be output
     */
    protected void write( final String data )
    {
        //Cache method local version
        //so that can be replaced in another thread
        final OutputStream outputStream = m_outputStream;

        if( null == outputStream )
        {
            error( "Attempted to write data '" + data + "' to Null OutputStream", null );
            return;
        }

        try
        {
            //TODO: We should be able to specify encoding???
            outputStream.write( data.getBytes() );
            outputStream.flush();
        }
        catch( final IOException ioe )
        {
            error( "Error writing data '" + data + "' to OutputStream", ioe );
        }
    }

    /**
     * Shutdown target.
     * Attempting to write to target after close() will cause errors to be logged.
     *
     */
    public synchronized void close()
    {
        super.close();

        final OutputStream outputStream = m_outputStream;
        m_outputStream = null;

        try
        {
            if( null != outputStream )
            {
                outputStream.close();
            }
        }
        catch( final IOException ioe )
        {
            error( "Error closing OutputStream", ioe );
        }
    }
}
