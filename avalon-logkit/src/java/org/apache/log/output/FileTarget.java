/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.log.Formatter;

/**
 * A basic target that writes to a File.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class FileTarget
    extends StreamTarget
{
    ///File we are writing to
    private File   m_file;

    /**
     * COnstruct file target to write to a file with a formatter. 
     *
     * @param file the file to write to
     * @param append true if file is to be appended to, false otherwise
     * @param formatter the Formatter
     * @exception IOException if an error occurs
     */
    public FileTarget( final File file, final boolean append, final Formatter formatter )
        throws IOException
    {
        super( null, formatter );

        if( null != file )
        {
            setFile( file, append );
        }
    }

    /**
     * Set the file for this target.
     * This method will attempt to create directories below file and 
     * append to it if specified.
     *
     * @param file the file to write to
     * @param append true if file is to be appended to, false otherwise
     * @exception IOException if directories can not be created or file can not be opened
     */
    protected synchronized void setFile( final File file, final boolean append )
        throws IOException
    {
        if( null == file )
        {
            throw new NullPointerException( "file property must not be null" );
        }

        final File parent = file.getParentFile();
        if( !parent.exists() )
        {
            parent.mkdirs();
        }

        final FileOutputStream outputStream = 
            new FileOutputStream( file.getPath(), append );

        super.close();
        setOutputStream( outputStream );
        open();
        m_file = file;
    }

    /**
     * Retrieve file associated with target.
     * This allows subclasses to access file object.
     *
     * @return the output File
     */
    protected File getFile()
    {
        return m_file;
    }
}
