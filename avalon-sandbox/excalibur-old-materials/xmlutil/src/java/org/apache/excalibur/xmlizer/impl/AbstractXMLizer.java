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
import org.apache.avalon.excalibur.xml.Parser;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.xmlizer.XMLizer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * An abstract XMLizer that can be extended to actually
 * perform some conversion. This class validates that the
 * mimetype is valid and then delegates to implementation
 * defined in subclass.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/11/07 04:43:22 $
 */
public abstract class AbstractXMLizer
    extends AbstractLogEnabled
    implements XMLizer, ThreadSafe, Serviceable
{
    /**
     * The mime type handled by this XMLizer.
     */
    private final String m_mimeType;

    /** The parser used by {@link XMLizer} */
    private Parser m_parser;

    protected AbstractXMLizer( final String mimeType )
    {
        m_mimeType = mimeType;
    }

    /**
     * @avalon.service interface="Parser"
     */
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        m_parser = (Parser)manager.lookup( Parser.ROLE );
    }

    /**
     * Generates SAX events from the given input stream
     * <b>NOTE</b> : if the implementation can produce lexical events, care should be taken
     * that <code>handler</code> can actually be a
     * {@link org.apache.avalon.excalibur.xml.XMLConsumer}
     * that accepts such events or directly implements the
     * LexicalHandler interface!
     *
     * @param stream    the data
     * @param mimeType  the mime-type for the data
     * @param systemID  the URI defining the data (this is optional and can be null)
     */
    public void toSAX( final InputStream stream,
                       final String mimeType,
                       final String systemID,
                       final ContentHandler handler )
        throws SAXException, IOException
    {
        if( null == stream )
        {
            throw new NullPointerException( "stream" );
        }
        if( null == handler )
        {
            throw new NullPointerException( "handler" );
        }

        if( null == mimeType )
        {
            if( getLogger().isDebugEnabled() )
            {
                final String message =
                    "No mime-type for xmlizing " + systemID +
                    ", guessing text/html";
                getLogger().debug( message );
            }
        }
        else if( !mimeType.equalsIgnoreCase( m_mimeType ) )
        {
            if( getLogger().isDebugEnabled() )
            {
                final String message = "Mime-type " + mimeType +
                    "not supported for xmlizing " + systemID +
                    ", guessing text/html";
                getLogger().debug( message );
            }
        }

        toSAX( stream, systemID, handler );
    }

    protected abstract void toSAX( final InputStream stream,
                                   final String systemID,
                                   final ContentHandler handler )
        throws SAXException, IOException;

    protected Parser getParser()
    {
        return m_parser;
    }
}

