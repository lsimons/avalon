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
public class RotatingFileOutputLogTarget
    extends FileTarget
{
    ///The rotation strategy to be used.
    private RotateStrategy      m_rotateStrategy;

    ///The filename strategy to be used.
    private FilenameStrategy    m_filenameStrategy;

    /**
     * construct RotatingFileOutputLogTarget object.
     * By default a time rotating strategy 24 hours, and a file name strategy
     * by append current time in milliseconds is established.
     *
     * @param file the base filename
     * @param formatter Formatter to be used
     */
    public RotatingFileOutputLogTarget( final File file, final Formatter formatter )
        throws IOException
    {
        super( null, false, formatter );

        m_rotateStrategy = new RotateStrategyByTime( 1000 * 60 * 60 * 24 );
        m_filenameStrategy = new FilenameStrategyUniqueLogFile();

        if( null != file )
        {
            setFile( file );
        }
    }

    /**
     * Set rotation strategy by time.
     *
     * @param timeInterval rotate ever timer-interval seconds
     */
    public void setRotateStrategyByTimeSeconds( final long timeInterval )
    {
        m_rotateStrategy = new RotateStrategyByTime( timeInterval * 1000 );
    }

    /**
     *  Set rotation strategy by size.
     *
     *  @param maxSize rotate if log file has more than max-size KB
     */
    public void setRotateStrategyBySizeKB( final long maxSize )
    {
        m_rotateStrategy = new RotateStrategyBySize( maxSize * 1024 );
    }

    /**
     * Set log filename strategy using revolving filename suffix.
     */
    public void setFilenameStrategyRevolvingLogFile()
    {
        m_filenameStrategy = new FilenameStrategyRevolvingLogFile( m_filenameStrategy );
    }

    /**
     * Set log filename strategy using time filename suffix.
     */
    public void setFilenameStrategyUniqueLogFile()
    {
        m_filenameStrategy = new FilenameStrategyUniqueLogFile( m_filenameStrategy );
    }

    /**
     * Overwrites the m_output value in the super class through
     * assignment of an OutputStreamWriter based on a supplied
     * file path.  Side-effects include the creation of a
     * directory path based relative to the supplied filename.
     *
     * @param filename path and filename for log destination
     */
    protected void setFile( final File filename )
        throws IOException
    {
        m_filenameStrategy.setBaseFileName( filename );
        openFile( m_filenameStrategy.getLogFileName() );
    }

    /**
     * open 'calculated' file name.
     *
     * @param file the name calculated by FilenameStrategy object
     */
    protected void openFile( final File file ) 
    {
        try
        {
            setFile( file, false );
            openFile();
        }
        catch( final IOException ioe )
        {
            error( "Error opening file " + file, ioe );
        }
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
            openFile( m_filenameStrategy.getLogFileName() );
        }
    }
}

