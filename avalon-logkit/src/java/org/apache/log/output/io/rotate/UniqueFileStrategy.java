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
public class UniqueFileStrategy 
    implements FileStrategy
{
    private File    m_baseFile;

    public UniqueFileStrategy( final File baseFile )
    {
        m_baseFile = baseFile;
    }

    /**
     * Calculate the real file name from the base filename.
     *
     * @return File the calculated file name
     */
    public File nextFile() 
    {
        final StringBuffer sb = new StringBuffer();
        sb.append( m_baseFile );
        sb.append( System.currentTimeMillis() );
        return new File( sb.toString() );
    }
}

