/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.sax;

import java.io.IOException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
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
 * @author <a href="mailto:fumagalli@exoffice.com">Pierpaolo Fumagalli</a>
 *         (Apache Software Foundation, Exoffice Technologies)
 * @version CVS $Revision: 1.1 $ $Date: 2003/01/14 09:39:37 $
 */
public final class XercesParser
    extends AbstractLogEnabled
    implements Parser, org.apache.excalibur.xml.dom.Parser,
                ErrorHandler, ThreadSafe, Initializable
{
    public void initialize()
        throws Exception
    {
        final String message =
            "WARNING: XercesParser has been deprecated in favour of " +
            "JaxpParser. Please use JaxpParser unless it is incompatible" +
            "with your environment";
        getLogger().warn( message );
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
        final SAXParser parser = createSAXParser();

        if( null != lexicalHandler )
        {
            parser.setProperty( "http://xml.org/sax/properties/lexical-handler",
                                lexicalHandler );
        }
        parser.setErrorHandler( this );
        parser.setContentHandler( contentHandler );
        parser.parse( in );
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
