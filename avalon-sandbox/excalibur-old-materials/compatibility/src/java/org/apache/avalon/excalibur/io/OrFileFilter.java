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
 * Accepts a selection if it is acceptable to either of two {@link FilenameFilter}s.
 * This takes two {@link FilenameFilter}s as input.
 *
 * <p>Eg., to print all directories or <code>*.gif</code> files in the current directory:</p>
 *
 * <pre>
 * File dir = new File(".");
 * String[] files = dir.list( new OrFileFilter(
 *         new DirectoryFileFilter(),
 *         new ExtensionFileFilter(".gif")
 *         )
 *     );
 * for ( int i=0; i&lt;files.length; i++ )
 * {
 *     System.out.println(files[i]);
 * }
 * </pre>
 *
 * @author Harmeet Bedi <harmeet@kodemuse.com>
 * @version CVS $Revision: 1.1 $ $Date: 2003/03/11 13:29:17 $
 * @since 4.0
 */
public class OrFileFilter
    implements FilenameFilter
{
    private final FilenameFilter m_filter1;
    private final FilenameFilter m_filter2;

    public OrFileFilter( final FilenameFilter filter1, final FilenameFilter filter2 )
    {
        m_filter1 = filter1;
        m_filter2 = filter2;
    }

    public boolean accept( final File file, final String name )
    {
        return m_filter1.accept( file, name ) || m_filter2.accept( file, name );
    }
}



