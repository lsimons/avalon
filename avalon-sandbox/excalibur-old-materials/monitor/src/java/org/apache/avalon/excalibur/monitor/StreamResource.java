/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;

/**
 * Managed Stream based Resource.  This is convenient when you want to dynamically
 * set and get the information from the resource.  For instance, the Resource does
 * not need to be actively monitored if all access to the resource goes through
 * this type of Resource.  It can notify the change as soon as the Writer or
 * OutputStream has been closed.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: StreamResource.java,v 1.2 2001/12/11 09:53:30 jefft Exp $
 */
public abstract class StreamResource extends Resource
{
    /**
     * Required constructor.  The <code>String</code> location is transformed by
     * the specific resource monitor.  For instance, a FileResource will be able
     * to convert a string representation of a path to the proper File object.
     */
    public StreamResource( String location ) throws Exception
    {
        super( location );
    }

    /**
     * Get the Resource contents as an InputStream.
     */
    abstract public InputStream getResourceAsStream() throws IOException;

    /**
     * Get the Resource contents as a Reader.
     */
    abstract public Reader getResourceAsReader() throws IOException;

    /**
     * Set the Resource contents as an OutputStream.
     */
    abstract public OutputStream setResourceAsStream() throws IOException;

    /**
     * Set the Resource contents as a Writer.
     */
    abstract public Writer setResourceAsWriter() throws IOException;

    /**
     * Automatically handle the streamClosedEvent from the ResourceOutputStream
     * and ResourceWriter.
     */
    protected void streamClosedEvent()
    {
        long lastModified = System.currentTimeMillis();

        m_eventSupport.firePropertyChange( Resource.MODIFIED,
                                           new Long( m_previousModified ),
                                           new Long( lastModified ) );

        m_previousModified = lastModified;
    }
}
