/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceValidity;

/**
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version $Id: SourceResource.java,v 1.1 2002/04/19 09:05:37 cziegeler Exp $
 */
public final class SourceResource extends StreamResource
{
    /** The wrapped source object */
    private final Source source;

    /** The last validity object */
    private SourceValidity validity;

    /**
     * Instantiate the SourceResource
     */
    public SourceResource( Source source )
        throws Exception
    {
        super( source.getSystemId() );

        this.source = source;
        m_previousModified = System.currentTimeMillis();
        this.validity = source.getValidity();
    }

    /**
     * Determines the last time this resource was modified
     */
    public long lastModified()
    {
        if( this.validity == null )
        {
            return System.currentTimeMillis();
        }
        else
        {
            SourceValidity newVal = this.source.getValidity();
            if( newVal != null && this.validity.isValid( newVal ) == true )
            {
                return m_previousModified;
            }
            else
            {
                this.validity = newVal;
                return System.currentTimeMillis();
            }
        }
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public InputStream getResourceAsStream() throws IOException
    {
        return this.source.getInputStream();
    }

    /**
     * Sets the resource value with a Writer
     */
    public Reader getResourceAsReader() throws IOException
    {
        return new InputStreamReader( this.getResourceAsStream() );
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public OutputStream setResourceAsStream() throws IOException
    {
        throw new IOException( "setResourceAsStream() not supported for URLResource" );
    }

    /**
     * Sets the resource value with a Writer
     */
    public Writer setResourceAsWriter() throws IOException
    {
        throw new IOException( "setResourceAsWriter() not supported for URLResource" );
    }
}
