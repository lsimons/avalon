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
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/22 10:06:04 $
 */
public class XercesParser
    extends AbstractLogEnabled
    implements Parser, ErrorHandler, SingleThreaded
{

    /** the SAX Parser */
    final SAXParser parser;

    public XercesParser()
        throws SAXException
    {
        this.parser = new SAXParser();

        this.parser.setFeature( "http://xml.org/sax/features/validation", false );
        this.parser.setFeature( "http://xml.org/sax/features/namespaces", true );
        this.parser.setFeature( "http://xml.org/sax/features/namespace-prefixes",
                                true );
    }

    public void parse( InputSource in, ContentHandler consumer )
        throws SAXException, IOException
    {
        if( consumer instanceof LexicalHandler )
        {
            this.parse( in, consumer, (LexicalHandler)consumer );
        }
        else
        {
            this.parse( in, consumer, null );
        }
    }

    /**
     * Parse the <code>InputSource</code> and send
     * SAX events to the content handler and
     * the lexical handler.
     */
    public void parse( InputSource in,
                       ContentHandler contentHandler,
                       LexicalHandler lexicalHandler )
        throws SAXException, IOException
    {
        if( null != lexicalHandler )
        {
            this.parser.setProperty( "http://xml.org/sax/properties/lexical-handler",
                                     lexicalHandler );
        }
        this.parser.setErrorHandler( this );
        this.parser.setContentHandler( contentHandler );
        this.parser.parse( in );
    }

    /**
     * Parses a new Document object from the given InputSource.
     */
    public Document parseDocument( InputSource input )
        throws SAXException, IOException
    {
        DOMParser parser = null;

        try
        {
            parser = new DOMParser();

            parser.setFeature( "http://xml.org/sax/features/validation", false );
            parser.setFeature( "http://xml.org/sax/features/namespaces", true );
            parser.setFeature( "http://xml.org/sax/features/namespace-prefixes",
                               true );

            parser.parse( input );
        }
        catch( Exception pce )
        {
            getLogger().error( "Could not build DocumentBuilder", pce );
            return null;
        }

        return parser.getDocument();
    }

    /**
     * Return a new <code>Document</code>.
     */
    public Document createDocument() throws SAXException
    {
        return new DocumentImpl();
    }

    /**
     * Receive notification of a recoverable error.
     */
    public void error( SAXParseException e )
        throws SAXException
    {
        throw new SAXException( "Error parsing " + e.getSystemId() + " (line " +
                                e.getLineNumber() + " col. " + e.getColumnNumber() +
                                "): " + e.getMessage(), e );
    }

    /**
     * Receive notification of a fatal error.
     */
    public void fatalError( SAXParseException e )
        throws SAXException
    {
        throw new SAXException( "Fatal error parsing " + e.getSystemId() + " (line " +
                                e.getLineNumber() + " col. " + e.getColumnNumber() +
                                "): " + e.getMessage(), e );
    }

    /**
     * Receive notification of a warning.
     */
    public void warning( SAXParseException e )
        throws SAXException
    {
        throw new SAXException( "Warning parsing " + e.getSystemId() + " (line " +
                                e.getLineNumber() + " col. " + e.getColumnNumber() +
                                "): " + e.getMessage(), e );
    }
}
