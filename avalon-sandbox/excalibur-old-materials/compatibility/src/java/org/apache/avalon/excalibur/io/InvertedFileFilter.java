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
 * This takes a <code>FilenameFilter<code> as input and inverts the selection.
 * This is used in retrieving files that are not accepted by a filter.
 *
 * <p>
 * Eg., here is how one could use <code>InvertedFileFilter</code> in conjunction with
 * {@link org.apache.avalon.excalibur.io.ExtensionFileFilter} to print all files not ending in
 * <code>.bak</code> or <code>.BAK</code> in the current directory:
 * </p>
 *
 * <pre>
 * File dir = new File(".");
 * String[] files = dir.list(
 *     new InvertedFileFilter(
 *         new ExtensionFileFilter( new String[]{".bak", ".BAK"} )
 *         )
 *     );
 * for ( int i=0; i&lt;files.length; i++ )
 * {
 *     System.out.println(files[i]);
 * }
 * </pre>
 *
 * @author Harmeet Bedi <harmeet@kodemuse.com>
 * @version CVS $Revision: 1.3 $ $Date: 2003/03/22 12:31:43 $
 * @since 4.0
 */
public class InvertedFileFilter
    implements FilenameFilter
{
    private final FilenameFilter m_originalFilter;

    public InvertedFileFilter( final FilenameFilter originalFilter )
    {
        m_originalFilter = originalFilter;
    }

    public boolean accept( final File file, final String name )
    {
        return !m_originalFilter.accept( file, name );
    }
}


