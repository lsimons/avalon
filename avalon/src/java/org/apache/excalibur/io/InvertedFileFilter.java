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
 * This takes a file filter as input and inverts the selection.
 * This is used in retrieving files that are not accepted by a filter.
 *
 * @author  Harmeet Bedi <harmeet@kodemuse.com>
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


