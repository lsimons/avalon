/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xmlizer.impl;

import java.io.IOException;
import java.io.InputStream;
import org.apache.avalon.excalibur.component.ExcaliburComponentSelector;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.xmlizer.XMLizer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Converter for transforming any input stream with a given mime-type
 * into SAX events.
 * This component acts like a selector. All XMLizer can "register"
 * themselfes for a given mime-type and this component forwards
 * the transformation to the registered on.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.8 $ $Date: 2002/11/07 05:11:07 $
 */
public final class XMLizerImpl
    extends ExcaliburComponentSelector
    implements XMLizer, ThreadSafe
{
    /** The default mimeType used when no mimeType is given */
    private final String m_defaultMimeType;

    public XMLizerImpl()
    {
        this( "text/xml" );
    }

    public XMLizerImpl( final String defaultMimeType )
    {
        m_defaultMimeType = defaultMimeType;
    }

    /**
     * Generates SAX events from the given input stream
     * <b>NOTE</b> : if the implementation can produce lexical events, care should be taken
     * that <code>handler</code> can actually be a
     * {@link org.apache.avalon.excalibur.xml.XMLConsumer} that accepts such
     * events or directly implements the LexicalHandler interface!
     * @param stream    the data
     * @param specifiedMimeType  the mime-type for the data
     * @param systemID  the URI defining the data (this is optional and can be null)
     * @throws ComponentException if no suitable converter is found
     */
    public void toSAX( final InputStream stream,
                       final String specifiedMimeType,
                       final String systemID,
                       final ContentHandler handler )
        throws SAXException, IOException, ComponentException
    {
        if( null == stream )
        {
            throw new NullPointerException( "stream" );
        }
        if( null == handler )
        {
            throw new NullPointerException( "handler" );
        }

        String mimeType = specifiedMimeType;
        if( null == mimeType )
        {
            if( getLogger().isDebugEnabled() )
            {
                final String message =
                    "No mime-type for xmlizing " + systemID +
                    ", guessing " + m_defaultMimeType;
                getLogger().debug( message );
            }
            mimeType = m_defaultMimeType;
        }

        if( !hasComponent( mimeType ) )
        {
            final String message = "No XMLizer registered for mimeType " + mimeType;
            throw new ComponentException( message );
        }

        final XMLizer realXMLizer = (XMLizer)select( mimeType );
        try
        {
            realXMLizer.toSAX( stream, mimeType, systemID, handler );
        }
        finally
        {
            release( realXMLizer );
        }
    }
}

