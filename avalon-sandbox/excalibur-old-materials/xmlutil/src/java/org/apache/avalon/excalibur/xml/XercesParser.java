/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml;

import java.io.IOException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.SingleThreaded;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.parsers.SAXParser;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;

/**
 *
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation, Exoffice Technologies)
 * @version CVS $Revision: 1.3 $ $Date: 2002/07/09 13:57:57 $
 */
public class XercesParser
    extends AbstractLogEnabled
    implements Parser, ErrorHandler, SingleThreaded
{
    /** the SAX Parser */
    private SAXParser m_parser;

    public XercesParser()
        throws SAXException
    {
        m_parser = createSAXParser();
    }

    public void parse( final InputSource in,
                       final ContentHandler consumer )
        throws SAXException, IOException
    {
        if( consumer instanceof LexicalHandler )
        {
            parse( in, consumer, (LexicalHandler)consumer );
        }
        else
        {
            parse( in, consumer, null );
        }
    }

    /**
     * Parse the {@link InputSource} and send
     * SAX events to the content handler and
     * the lexical handler.
     */
    public void parse( final InputSource in,
                       final ContentHandler contentHandler,
                       final LexicalHandler lexicalHandler )
        throws SAXException, IOException
    {
        final SAXParser parser = getSAXParser();

        if( null != lexicalHandler )
        {
            parser.setProperty( "http://xml.org/sax/properties/lexical-handler",
                                lexicalHandler );
        }
        parser.setErrorHandler( this );
        parser.setContentHandler( contentHandler );
        parser.parse( in );

        //Note it is a deliberate choice to make sure that
        //the parser is only released when a successful parse
        //has occured. If an exception is generated the
        //parser becomes fubar so we need to let it go into
        //the void to be garbage collected
        releaseSAXParser( parser );
    }

    /**
     * Parses a new Document object from the given {@link InputSource}.
     */
    public Document parseDocument( final InputSource input )
        throws SAXException, IOException
    {
        try
        {
            final DOMParser parser = new DOMParser();
            parser.setFeature( "http://xml.org/sax/features/validation", false );
            parser.setFeature( "http://xml.org/sax/features/namespaces", true );
            parser.setFeature( "http://xml.org/sax/features/namespace-prefixes",
                               true );

            parser.parse( input );

            return parser.getDocument();
        }
        catch( final Exception e )
        {
            final String message = "Could not build DocumentBuilder";
            getLogger().error( message, e );
            return null;
        }
    }

    /**
     * Return a new {@link Document}.
     */
    public Document createDocument()
        throws SAXException
    {
        return new DocumentImpl();
    }

    /**
     * Receive notification of a recoverable error.
     */
    public void error( final SAXParseException spe )
        throws SAXException
    {
        final String message =
            "Error parsing " + spe.getSystemId() + " (line " +
            spe.getLineNumber() + " col. " + spe.getColumnNumber() +
            "): " + spe.getMessage();
        throw new SAXException( message, spe );
    }

    /**
     * Receive notification of a fatal error.
     */
    public void fatalError( final SAXParseException spe )
        throws SAXException
    {
        final String message =
            "Fatal error parsing " + spe.getSystemId() + " (line " +
            spe.getLineNumber() + " col. " + spe.getColumnNumber() +
            "): " + spe.getMessage();
        throw new SAXException( message, spe );
    }

    /**
     * Receive notification of a warning.
     */
    public void warning( final SAXParseException spe )
        throws SAXException
    {
        final String message =
            "Warning parsing " + spe.getSystemId() + " (line " +
            spe.getLineNumber() + " col. " + spe.getColumnNumber() +
            "): " + spe.getMessage();
        throw new SAXException( message, spe );
    }

    /**
     * Aquire a parser from the pool of {@link SAXParser} objects.
     *
     * @return the parser
     */
    private SAXParser getSAXParser()
        throws SAXException
    {
        return m_parser;
    }

    /**
     * Utility method to release parser back into pool.
     *
     * @param parser the parser
     */
    private void releaseSAXParser( final SAXParser parser )
    {
        m_parser = parser;
    }

    /**
     * Utility method to create a SAXParser.
     *
     * @return new SAXParser
     * @throws SAXException if unable to create parser
     */
    private SAXParser createSAXParser()
        throws SAXException
    {
        final SAXParser parser = new SAXParser();
        parser.setFeature( "http://xml.org/sax/features/validation", false );
        parser.setFeature( "http://xml.org/sax/features/namespaces", true );
        parser.setFeature( "http://xml.org/sax/features/namespace-prefixes",
                           true );
        return parser;
    }
}
