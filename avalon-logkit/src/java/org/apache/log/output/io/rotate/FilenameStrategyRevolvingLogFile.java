/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.io.rotate;

import java.io.File;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;

/**
 * strategy for naming log files based on appending revolving suffix.
 *
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class FilenameStrategyRevolvingLogFile
    implements FilenameStrategy
{
    ///revolving suffix formatting pattern. ie. "'.'000000"
    private final static String      PATTERN = "'.'000000";

    ///current revolving suffix
    private long           m_rotation = 1;

    ///the base file name.
    private File           m_baseFileName;

    ///starting revolving value. ie. 1
    private long           m_rotationMinValue = 1;

    ///max revolving value. ie 1000
    private long           m_maxRotations = 1000;

    ///a revolving suffix formatter
    private DecimalFormat  m_decimalFormat;

    public FilenameStrategyRevolvingLogFile() 
    {
        m_rotation = m_rotationMinValue;
        m_decimalFormat = new DecimalFormat( PATTERN );
        setBaseFileName( new File(FilenameStrategy.BASE_FILE_NAME_DEFAULT) );
    }

    public FilenameStrategyRevolvingLogFile( FilenameStrategy fs ) 
    {
        this();
        if( null != fs )
        {
            final File bfn = fs.getBaseFileName();
            if( null != bfn )
            {
                setBaseFileName( bfn );
            }
        }
    }

    public FilenameStrategyRevolvingLogFile( final File baseFileName ) 
    {
        setBaseFileName( baseFileName );
    }

    public File getBaseFileName() 
    {
        return m_baseFileName;
    }

    public void setBaseFileName( final File baseFileName ) 
    {
        m_baseFileName = baseFileName;
    }

    /**
     * Calculate the real file name from the base filename.
     *
     * @return File the calculated file name
     */
    public File getLogFileName() 
    {
        final StringBuffer sb = new StringBuffer();
        final FieldPosition fp = new FieldPosition( NumberFormat.INTEGER_FIELD );
        sb.append( m_baseFileName );

        final StringBuffer result = m_decimalFormat.format( m_rotation, sb, fp );
        m_rotation += 1;

        if( m_rotation >= m_maxRotations ) 
        {
            m_rotation = m_rotationMinValue;
        }

        return new File( result.toString() );
    }
}

