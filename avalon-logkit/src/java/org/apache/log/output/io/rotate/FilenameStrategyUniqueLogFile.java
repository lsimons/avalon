/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.io.rotate;

import java.io.File;
import java.io.IOException;

/**
 * strategy for naming log files based on appending time suffix
 *
 * @author <a href="mailto:bh22351@i-one.at">Bernhard Huber</a>
 */
public class FilenameStrategyUniqueLogFile 
    implements FilenameStrategy
{
    ///the base file name.
    private File m_baseFileName;

    public FilenameStrategyUniqueLogFile() 
    {
        setBaseFileName( new File(FilenameStrategy.BASE_FILE_NAME_DEFAULT) );
    }

    public FilenameStrategyUniqueLogFile( final FilenameStrategy fs ) 
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

    public FilenameStrategyUniqueLogFile( final File baseFileName ) 
    {
        m_baseFileName = baseFileName;
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
        sb.append( m_baseFileName );
        sb.append( getCurrentValue() );
        return new File( sb.toString() );
    }

    private String getCurrentValue() 
    {
        final long time = System.currentTimeMillis();
        return String.valueOf( time );
    }
}

