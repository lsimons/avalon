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
 * Strategy for naming log files based on appending a revolving suffix.
 *
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class RevolvingFileStrategy
    implements FileStrategy
{
    ///revolving suffix formatting pattern. ie. "'.'000000"
    private final static String PATTERN = "'.'000000";

    ///a revolving suffix formatter
    private DecimalFormat m_decimalFormat;

    ///current revolving suffix
    private int m_rotation;

    ///max revolving value.
    private int m_maxRotations;

    ///the base file name.
    private File m_baseFile;

    public RevolvingFileStrategy( final File baseFile,
                                  final int initialRotation,
                                  final int maxRotations )
    {
        m_decimalFormat = new DecimalFormat( PATTERN );

        m_baseFile = baseFile;
        m_rotation = initialRotation;
        m_maxRotations = maxRotations;

        if( -1 == initialRotation )
        {
            ///TODO: Scan filesystem to get current number
        }

        if( -1 == m_maxRotations )
        {
            m_maxRotations = Integer.MAX_VALUE;
        }

        if( m_rotation > m_maxRotations ) m_rotation = m_maxRotations;
        if( m_rotation < 0 ) m_rotation = 0;
    }

    public RevolvingFileStrategy( final File baseFile, final int maxRotations )
    {
        this( baseFile, -1, maxRotations );
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

        if( m_rotation >= m_maxRotations ) m_rotation = 0;

        return new File( result.toString() );
    }
}

