/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source.impl.validity;

import java.io.File;
import org.apache.excalibur.source.SourceValidity;

/**
 * A validation object for time-stamps.
 *
 * @author: <a href="mailto:volker.schmitt@basf-it-services.com">Volker Schmitt</a>
 * @version CVS $Revision: 1.3 $
 */
public final class FileTimeStampValidity
    implements SourceValidity
{
    private long m_timeStamp;
    private File m_file;

    public FileTimeStampValidity( final String filename )
    {
        this( new File( filename ) );
    }

    public FileTimeStampValidity( final File file )
    {
        this( file, file.lastModified() );
    }

    public FileTimeStampValidity( final File file,
                                  final long timeStamp )
    {
        m_file = file;
        m_timeStamp = timeStamp;
    }

    /**
     * Check if the component is still valid.
     * If <code>0</code> is returned the isValid(SourceValidity) must be
     * called afterwards!
     * If -1 is returned, the component is not valid anymore and if +1
     * is returnd, the component is valid.
     */
    public int isValid()
    {
        return ( m_file.lastModified() == m_timeStamp ? 1 : -1 );
    }

    public boolean isValid( final SourceValidity newValidity )
    {
        if( newValidity instanceof FileTimeStampValidity )
        {
            final long timeStamp =
                ( (FileTimeStampValidity)newValidity ).getTimeStamp();
            return ( m_timeStamp == timeStamp );
        }
        return false;
    }

    public File getFile()
    {
        return this.m_file;
    }

    public long getTimeStamp()
    {
        return this.m_timeStamp;
    }

    public String toString()
    {
        return "FileTimeStampValidity: " + m_file.getPath() + ": " + this.m_timeStamp;
    }
}
