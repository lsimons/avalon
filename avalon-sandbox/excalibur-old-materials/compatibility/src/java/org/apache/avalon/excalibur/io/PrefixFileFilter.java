/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * This filters filenames for a certain prefix.
 *
 * <p>Eg., to print all files and directories in the current directory whose name starts with</p>
 * <code>foo</code>:
 *
 * <pre>
 * File dir = new File(".");
 * String[] files = dir.list( new PrefixFileFilter("foo"));
 * for ( int i=0; i&lt;files.length; i++ )
 * {
 *     System.out.println(files[i]);
 * }
 * </pre>
 *
 *
 * @author  Federico Barbieri <fede@apache.org>
 * @author Serge Knystautas <sergek@lokitech.com>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/03/22 12:31:43 $
 * @since 4.0
 */
public class PrefixFileFilter
    implements FilenameFilter
{
    private String[] m_prefixs;

    public PrefixFileFilter( final String[] prefixs )
    {
        m_prefixs = prefixs;
    }

    public PrefixFileFilter( final String prefix )
    {
        m_prefixs = new String[]{prefix};
    }

    public boolean accept( final File file, final String name )
    {
        for( int i = 0; i < m_prefixs.length; i++ )
        {
            if( name.startsWith( m_prefixs[ i ] ) )
            {
                return true;
            }
        }
        return false;
    }
}
