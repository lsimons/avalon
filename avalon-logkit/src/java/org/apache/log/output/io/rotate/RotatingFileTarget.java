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
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:mcconnell@osm.net">Stephen McConnell</a>
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class RotatingFileTarget
    extends FileTarget
{
    ///The rotation strategy to be used.
    private RotateStrategy      m_rotateStrategy;

    ///The file strategy to be used.
    private FileStrategy        m_fileStrategy;

    /**
     * Construct RotatingFileTarget object.
     *
     * @param formatter Formatter to be used
     */
    public RotatingFileTarget( final Formatter formatter,
                               final RotateStrategy rotateStrategy, 
                               final FileStrategy fileStrategy )
        throws IOException
    {
        super( null, false, formatter );

        m_rotateStrategy = rotateStrategy;
        m_fileStrategy = fileStrategy;

        rotate();
    }

    protected synchronized void rotate()
        throws IOException
    {
        close();

        final File file = m_fileStrategy.nextFile();
        setFile( file, false );
        openFile();
    }

    /**
     * Output the log message, and check if rotation is needed.
     */
    protected synchronized void write( final String data ) 
    {
        // write the log message
        super.write( data );

        // if rotation is needed, close old File, create new File
        final boolean rotate = 
            m_rotateStrategy.isRotationNeeded( data, getFile() );
        if( rotate ) 
        {
            try { rotate(); }
            catch( final IOException ioe )
            {
                getErrorHandler().error( "Error rotating file", ioe, null );
            }
        }
    }
}

