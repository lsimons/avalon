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
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.xmlizer.XMLizer;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Converter for transforming an input stream contain text/xml data
 * to SAX events.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/07/10 08:59:10 $
 */
public final class TextXMLizer
    extends AbstractLogEnabled
    implements XMLizer, ThreadSafe, Composable
{
    private static final String XML_MIME_TYPE = "text/xml";

    /**
     * The parser to use.
     */
    private Parser m_parser;

    public void compose( final ComponentManager manager )
    {
        m_parser = (Parser)manager.lookup( Parser.ROLE );
    }

    /**
     * Generates SAX events from the given input stream
     * <b>NOTE</b> : if the implementation can produce lexical events, care should be taken
     * that <code>handler</code> can actually be a
     * {@link org.apache.avalon.excalibur.xml.XMLConsumer} that accepts such
     * events or directly implements the LexicalHandler interface!
     * @param stream    the data
     * @param mimeType  the mime-type for the data
     * @param systemID  the URI defining the data (this is optional and can be null)
     * @throws ComponentException if no suitable converter is found
     */
    public void toSAX( final InputStream stream,
                       final String mimeType,
                       final String systemID,
                       final ContentHandler handler )
        throws SAXException, IOException, ComponentException
    {
        if( null == stream )
        {
            final String message = "Stream must not be null.";
            throw new ComponentException( message );
        }
        if( null == handler )
        {
            final String message = "Handler must not be null.";
            throw new ComponentException( message );
        }

        if( null == mimeType )
        {
            if( getLogger().isDebugEnabled() )
            {
                final String message =
                    "No mime-type for xmlizing " + systemID +
                    ", guessing text/xml";
                getLogger().debug( message );
            }
        }
        else if( !mimeType.equalsIgnoreCase( XML_MIME_TYPE ) )
        {
            if( getLogger().isDebugEnabled() )
            {
                final String message = "Mime-type " + mimeType +
                    "not supported for xmlizing " + systemID +
                    ", guessing text/xml";
                getLogger().debug( message );
            }
        }

        final InputSource inputSource = new InputSource( stream );
        if( null != systemID )
        {
            inputSource.setSystemId( systemID );
        }

        m_parser.parse( inputSource, handler );
    }
}