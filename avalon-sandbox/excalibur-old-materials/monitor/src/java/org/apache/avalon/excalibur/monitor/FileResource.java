/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Managed File based Resource.  This is convenient when you want to dynamically
 * set and get the information from the resource.  For instance, the Resource does
 * not need to be actively monitored if all access to the resource goes through
 * this type of Resource.  It can notify the change as soon as the Writer or
 * OutputStream has been closed.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: FileResource.java,v 1.3 2002/03/16 00:05:41 donaldp Exp $
 */
public class FileResource extends StreamResource
{
    private final File m_file;

    /**
     * Instantiate the FileResource
     */
    public FileResource( String resource )
        throws Exception
    {
        this( new File( resource ) );
    }

    public FileResource( File resource )
        throws Exception
    {
        super( resource.getCanonicalPath() );

        m_file = resource;
        m_previousModified = m_file.lastModified();
    }

    /**
     * Determines the last time this resource was modified
     */
    public long lastModified()
    {
        return m_file.lastModified();
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public InputStream getResourceAsStream() throws IOException
    {
        return new FileInputStream( m_file );
    }

    /**
     * Sets the resource value with a Writer
     */
    public Reader getResourceAsReader() throws IOException
    {
        return new FileReader( m_file );
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public OutputStream setResourceAsStream() throws IOException
    {
        return new ResourceOutputStream( new FileOutputStream( m_file ), this );
    }

    /**
     * Sets the resource value with a Writer
     */
    public Writer setResourceAsWriter() throws IOException
    {
        return new ResourceWriter( new FileWriter( m_file ), this );
    }
}
