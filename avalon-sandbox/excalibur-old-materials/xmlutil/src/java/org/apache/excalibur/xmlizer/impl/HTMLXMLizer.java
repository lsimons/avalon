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
import java.io.StringWriter;
import java.io.StringReader;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.avalon.excalibur.xml.Parser;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.xmlizer.XMLizer;
import org.w3c.tidy.Tidy;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Converter for transforming an input stream contain text/html data
 * to SAX events.
 * This class uses jtidy.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.10 $ $Date: 2002/08/04 03:20:33 $
 */
public final class HTMLXMLizer
    extends AbstractLogEnabled
    implements XMLizer, ThreadSafe, Composable
{
    private static final String HTML_MIME_TYPE = "text/html";

    /** Used for converting DOM -> SAX */
    private static final Properties c_format = createFormatProperties();

    /** The parser used by {@link XMLizer} */
    private Parser m_parser;

    public void compose( final ComponentManager manager )
        throws ComponentException
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
                    ", guessing text/html";
                getLogger().debug( message );
            }
        }
        else if( !mimeType.equalsIgnoreCase( HTML_MIME_TYPE ) )
        {
            if( getLogger().isDebugEnabled() )
            {
                final String message = "Mime-type " + mimeType +
                    "not supported for xmlizing " + systemID +
                    ", guessing text/html";
                getLogger().debug( message );
            }
        }

        final Tidy xhtmlconvert = new Tidy();
        xhtmlconvert.setXmlOut( true );
        xhtmlconvert.setXHTML( true );
        xhtmlconvert.setShowWarnings( false );

        final StringWriter writer = new StringWriter();
        try
        {
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperties( c_format );
            final DOMSource domSource = new DOMSource( xhtmlconvert.parseDOM( stream, null ) );
            final StreamResult streamResult = new StreamResult( writer );
            transformer.transform( domSource, streamResult );
        }
        catch( final TransformerException te )
        {
            final String message = "Exception during transformation.";
            throw new SAXException( message, te );
        }

        final InputSource inputSource =
            new InputSource( new StringReader( writer.toString() ) );
        if( null != systemID )
            inputSource.setSystemId( systemID );

        m_parser.parse( inputSource, handler );
    }

    /**
     * Utility method to create format properties for XMLizer.
     */
    private static Properties createFormatProperties()
    {
        final Properties format = new Properties();
        format.put( OutputKeys.METHOD, "xml" );
        format.put( OutputKeys.OMIT_XML_DECLARATION, "no" );
        format.put( OutputKeys.INDENT, "yes" );
        return format;
    }
}

