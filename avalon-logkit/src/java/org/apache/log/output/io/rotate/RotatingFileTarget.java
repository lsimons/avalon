/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.io.rotate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log.format.Formatter;
import org.apache.log.output.io.FileTarget;

/**
 * This is a basic Output log target that writes to rotating files.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class RotatingFileTarget
    extends FileTarget
{
    ///The rotation strategy to be used.
    private RotateStrategy      m_rotateStrategy;

    ///The filename strategy to be used.
    private FilenameStrategy    m_filenameStrategy;

    ///Base filename for logging
    private File                m_baseFile;

    /**
     * construct RotatingFileTarget object.
     * By default a time rotating strategy 24 hours, and a file name strategy
     * by append current time in milliseconds is established.
     *
     * @param file the base filename
     * @param formatter Formatter to be used
     */
    public RotatingFileTarget( final File file, 
                               final Formatter formatter,
                               final RotateStrategy rotateStrategy, 
                               final FilenameStrategy filenameStrategy )
        throws IOException
    {
        super( null, false, formatter );

        m_rotateStrategy = rotateStrategy;
        m_filenameStrategy = filenameStrategy;

        if( null != file )
        {
            setFile( file );
            openFile();
        }
    }

    /**
     * Overwrites the m_output value in the super class through
     * assignment of an OutputStreamWriter based on a supplied
     * file path.  Side-effects include the creation of a
     * directory path based relative to the supplied filename.
     *
     * @param baseFile path and filename for log destination
     */
    protected void setFile( final File baseFile )
        throws IOException
    {
        m_baseFile = baseFile;
        final File file = m_filenameStrategy.getLogFileName( m_baseFile );
        setFile( file, false );
    }

    /**
     * Output the log message, and check if rotation is needed.
     */
    protected void write( final String data ) 
    {
        // write the log message
        super.write( data );

        // if rotation is needed, close old File, create new File
        if( m_rotateStrategy.isRotationNeeded( data ) ) 
        {
            close();
            try
            {
                final File file = m_filenameStrategy.getLogFileName( m_baseFile );
                setFile( file, false );
                openFile();
            }
            catch( final IOException ioe )
            {
                error( "Error rotating file", ioe );
            }
        }
    }
}

