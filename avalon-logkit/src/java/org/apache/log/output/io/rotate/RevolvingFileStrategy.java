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
public class RevolvingFileStrategy
    implements FileStrategy
{
    ///revolving suffix formatting pattern. ie. "'.'000000"
    private final static String      PATTERN = "'.'000000";

    ///current revolving suffix
    private long           m_rotation = 1;

    ///the base file name.
    private File           m_baseFileName;

    ///starting revolving value. ie. 0
    private long           m_rotationMinValue = 0;

    ///max revolving value. ie 1000
    private long           m_maxRotations = 1000;

    ///a revolving suffix formatter
    private DecimalFormat  m_decimalFormat;

    private File    m_baseFile;

    public RevolvingFileStrategy( final File baseFile )
    {
        m_baseFile = baseFile;
        m_rotation = m_rotationMinValue;
        m_decimalFormat = new DecimalFormat( PATTERN );
    }

    /**
     * Calculate the real file name from the base filename.
     *
     * @return File the calculated file name
     */
    public File nextFile() 
    {
        final StringBuffer sb = new StringBuffer();
        final FieldPosition fp = new FieldPosition( NumberFormat.INTEGER_FIELD );
        sb.append( m_baseFile );

        final StringBuffer result = m_decimalFormat.format( m_rotation, sb, fp );
        m_rotation += 1;

        if( m_rotation >= m_maxRotations ) 
        {
            m_rotation = m_rotationMinValue;
        }

        return new File( result.toString() );
    }
}

