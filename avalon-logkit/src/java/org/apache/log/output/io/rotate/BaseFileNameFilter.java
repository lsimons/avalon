/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.log.output.io.rotate;

import java.io.File;
import java.io.FilenameFilter;

class BaseFileNameFilter
    implements FilenameFilter
{
    private String m_baseFileName;

    BaseFileNameFilter( final String baseFileName )
    {
        m_baseFileName = baseFileName;
    }

    public boolean accept( File file, String name )
    {
        return ( name.startsWith( m_baseFileName ) );
    }
}
