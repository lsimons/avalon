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
 * @version CVS $Revision: 1.2 $
 */
public final class FileTimeStampValidity
    implements SourceValidity
{

    private long timeStamp;
    private File file;

    public FileTimeStampValidity( String filename )
    {
        this(new File(filename));
    }

    public FileTimeStampValidity( File file )
    {
        this(file, file.lastModified());
    }

    public FileTimeStampValidity( File file, long  timeStamp )
    {
        this.file = file;
        this.timeStamp = timeStamp;
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
        return (file.lastModified() == this.timeStamp ? 1 : -1);
    }

    public boolean isValid( SourceValidity newValidity )
    {
        if( newValidity instanceof FileTimeStampValidity)
        {
            return this.timeStamp == ( (FileTimeStampValidity)newValidity ).getTimeStamp();
        }
        return false;
    }

    public File getFile()
    {
        return this.file;
    }

    public long getTimeStamp()
    {
        return this.timeStamp;
    }

    public String toString()
    {
        return "FileTimeStampValidity: " + file.getPath() + ": " + this.timeStamp;
    }

}
