/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.EOFException;
import org.apache.log.Priority;
import org.apache.log.Logger;

/**
 * This class is useful to redirect standard 
 * output or standard error to a Logger.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class OutputStreamLogger
    extends OutputStream
{    
    ///Logger that we log to
    private final Logger        m_logger;

    ///Log level we log to
    private final Priority      m_priority;

    ///The buffered output so far
    private final StringBuffer  m_output    = new StringBuffer();

    ///Flag set to true once stream closed
    private boolean             m_closed;

    public OutputStreamLogger( final Logger logger, 
                               final Priority priority )
    {
        m_logger = logger;
        m_priority = priority;
    }

    /**
     * Shutdown stream.
     *
     */
    public void close()
        throws IOException
    {
        write( '\n' );
        m_closed = true;
    }

    public void write( final int data ) 
        throws IOException
    {
        if( true == m_closed ) 
        {
            throw new EOFException( "OutputStreamLogger closed" );
        }
        
        if( '\n' == data )
        {
            m_logger.log( m_priority, m_output.toString() );
            m_output.setLength( 0 );
        }
        else
        {
            //Should we properly convert char using locales etc??
            m_output.append( (char)data );
        }
    }

}
