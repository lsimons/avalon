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
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceValidity;

/**
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version $Id: SourceResource.java,v 1.9 2003/01/14 07:45:10 cziegeler Exp $
 */
public final class SourceResource
    extends StreamResource
{
    /** The wrapped source object */
    private final Source m_source;

    /** The last validity object */
    private SourceValidity m_validity;

    /**
     * Instantiate the SourceResource
     */
    public SourceResource( final Source source )
        throws Exception
    {
        super( source.getSystemId() );

        m_source = source;
        setPreviousModified( System.currentTimeMillis() );
        m_validity = source.getValidity();
    }

    /**
     * Determines the last time this resource was modified
     */
    public long lastModified()
    {
        if( null == m_validity )
        {
            return System.currentTimeMillis();
        }
        else
        {
            int valid = m_validity.isValid();
            boolean isValid = false;
            SourceValidity newVal = null;
            if ( valid == 0 ) {
                m_source.discardValidity();
                newVal = m_source.getValidity();
                if( newVal != null)
                {
                    valid = m_validity.isValid( newVal );
                    isValid = (valid == 1);
                }
            } else {
                isValid = (valid == 1);
            }
            if ( isValid ) {
                return getPreviousModified();
            } else {
                if ( null == newVal ) {
                    m_source.discardValidity();
                    m_validity = m_source.getValidity();
                }
                return System.currentTimeMillis();
            }
        }
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public InputStream getResourceAsStream()
        throws IOException
    {
        try
        {
            return m_source.getInputStream();
        }
        catch( SourceException se )
        {
            throw new IOException( "SourceException: " + se.getMessage() );
        }
    }

    /**
     * Sets the resource value with a Writer
     */
    public Reader getResourceAsReader()
        throws IOException
    {
        return new InputStreamReader( getResourceAsStream() );
    }

    /**
     * Sets the resource value with an OutputStream
     */
    public OutputStream setResourceAsStream()
        throws IOException
    {
        throw new IOException( "setResourceAsStream() not supported for URLResource" );
    }

    /**
     * Sets the resource value with a Writer
     */
    public Writer setResourceAsWriter()
        throws IOException
    {
        throw new IOException( "setResourceAsWriter() not supported for URLResource" );
    }

    public Source getSource()
    {
        return m_source;
    }
}
