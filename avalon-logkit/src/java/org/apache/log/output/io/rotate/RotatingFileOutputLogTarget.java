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
    /**
     * The rotation strategy to be used.
     */
    RotateStrategy rotateStrategy;

    /**
     * The filename strategy to be used.
     */
    FilenameStrategy filenameStrategy;

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

        rotateStrategy = new RotateStrategyByTime( 1000 * 60 * 60 * 24 );
        filenameStrategy = new FilenameStrategyUniqueLogFile();

        if( null != file )
        {
            setFile( file );
        }
    }

    /**
     * set rotation strategy by time
     * @param time_interval rotate ever timer-interval seconds
     */
    public void setRotateStrategyByTimeSeconds( long timeInterval )
    {
        rotateStrategy = new RotateStrategyByTime( timeInterval * 1000 );
    }

    /**
     *  set rotation strategy by size
     *  @param max_size rotate if log file has more than max-size KB
     */
    public void setRotateStrategyBySizeKB( final long maxSize )
    {
        rotateStrategy = new RotateStrategyBySize( maxSize * 1024 );
    }

    /**
     * set log filename strategy using revolving filename suffix.
     */
    public void setFilenameStrategyRevolvingLogFile()
    {
        filenameStrategy = new FilenameStrategyRevolvingLogFile( filenameStrategy );
    }

    /**
     * set log filename strategy using time filename suffix.
     */
    public void setFilenameStrategyUniqueLogFile()
    {
        filenameStrategy = new FilenameStrategyUniqueLogFile( filenameStrategy );
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
        filenameStrategy.setBaseFileName( filename );
        openFile( filenameStrategy.getLogFileName() );
    }

    /**
     * open 'calculated' file name.
     *
     * @param log_file_name the name calculated by FilenameStrategy object
     */
    protected void openFile( final File log_file_name ) {
        try {
            setFile( log_file_name, false );
            openFile();
        } catch (IOException ioe) {
            // fix me what do do now?
        }
    }

    /**
     * output the log message, and check if rotation is needed.
     */
    protected void write( final String data ) {
        // write the log message
        super.write( data );
        // if rotation is needed, close old FileWriter, create new FileWriter
        if (rotateStrategy.isRotationNeeded( data )) {
            close();
            openFile( filenameStrategy.getLogFileName() );
        }
    }
}

