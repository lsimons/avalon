/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.excalibur.xml.impl;

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
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:47:42 $
 */
public final class XercesParser
    extends AbstractLogEnabled
    implements org.apache.excalibur.xml.sax.SAXParser, org.apache.excalibur.xml.dom.DOMParser,
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
