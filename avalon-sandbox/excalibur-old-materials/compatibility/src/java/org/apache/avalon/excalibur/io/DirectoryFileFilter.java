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
 * This filter accepts <code>File</code>s that are directories.
 * <p>Eg., here is how to print out a list of the current directory's subdirectories:</p>
 *
 * <pre>
 * File dir = new File(".");
 * String[] files = dir.list( new DirectoryFileFilter() );
 * for ( int i=0; i&lt;files.length; i++ )
 * {
 *     System.out.println(files[i]);
 * }
 * </pre>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3 $ $Date: 2003/03/22 12:31:43 $
 * @since 4.0
 */
public class DirectoryFileFilter
    implements FilenameFilter
{
    public boolean accept( final File file, final String name )
    {
        return new File( file, name ).isDirectory();
    }
}


