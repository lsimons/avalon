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
 * @version CVS $Revision: 1.2 $ $Date: 2002/07/07 07:11:45 $
 */
public class TextXMLizer
    extends AbstractLogEnabled
    implements XMLizer, ThreadSafe, Composable
{
    /** The component manager */
    private ComponentManager m_manager;

    /**
     * Composable interface
     */
    public void compose( final ComponentManager manager )
    {
        m_manager = manager;
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
        else if( !mimeType.equalsIgnoreCase( "text/xml" ) )
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

        final Parser parser = (Parser)m_manager.lookup( Parser.ROLE );
        try
        {
            parser.parse( inputSource, handler );
        }
        finally
        {
            m_manager.release( parser );
        }
    }

}

