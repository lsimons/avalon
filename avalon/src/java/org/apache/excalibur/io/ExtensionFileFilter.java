/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * This filters files based on the extension (what the filename
 * ends with).  This is used in retrieving all the files of a
 * particular type.
 *
 * @author  Federico Barbieri <fede@apache.org>
 * @author Serge Knystautas <sergek@lokitech.com>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ExtensionFileFilter
    implements FilenameFilter
{
    private String[]           m_extensions;

    public ExtensionFileFilter( final String[] extensions )
    {
        m_extensions = extensions;
    }

    public ExtensionFileFilter( final String extension )
    {
        m_extensions = new String[] { extension };
    }

    public boolean accept( final File file, final String name )
    {
        for( int i = 0; i < m_extensions.length; i++ )
        {
            if( name.endsWith( m_extensions[ i ] ) ) return true;
        }
        return false;
    }
}


