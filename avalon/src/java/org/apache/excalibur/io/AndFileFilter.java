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
 * This takes two file fiters as input. Accepts a selection only if it is
 * accpetable to both the input filters
 *
 * @author  Harmeet Bedi <harmeet@kodemuse.com>
 */
public class AndFileFilter
    implements FilenameFilter
{
    private final FilenameFilter m_filter1;
    private final FilenameFilter m_filter2;

    public AndFileFilter( FilenameFilter filter1, FilenameFilter filter2 )
    {
        m_filter1 = filter1;
        m_filter2 = filter2;
    }

    public boolean accept( final File file, final String name )
    {
        return m_filter1.accept( file, name ) && m_filter2.accept( file, name );
    }
}


