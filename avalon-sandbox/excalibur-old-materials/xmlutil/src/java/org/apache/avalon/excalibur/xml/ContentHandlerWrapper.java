/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * This class is an utility class &quot;wrapping&quot; around a SAX version 2.0
 * {@link ContentHandler} and forwarding it those events received throug
 * its {@link XMLConsumer}s interface.
 * <br>
 *
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 *         (Apache Software Foundation, Computer Associates)
 * @version CVS $Revision: 1.2 $ $Date: 2002/07/07 07:19:49 $
 */
public class ContentHandlerWrapper
    extends AbstractXMLConsumer
    implements Recyclable
{
    /** The current <code>ContentHandler</code>. */
    protected ContentHandler contentHandler;

    /** The optional <code>LexicalHandler</code> */
    protected LexicalHandler lexicalHandler;

    /**
     * Create a new <code>ContentHandlerWrapper</code> instance.
     */
    public ContentHandlerWrapper()
    {
    }

    /**
     * Create a new <code>ContentHandlerWrapper</code> instance.
     */
    public ContentHandlerWrapper( final ContentHandler contentHandler )
    {
        setContentHandler( contentHandler );
    }

    /**
     * Create a new <code>ContentHandlerWrapper</code> instance.
     */
    public ContentHandlerWrapper( final ContentHandler contentHandler,
                                  final LexicalHandler lexicalHandler )
    {
        setContentHandler( contentHandler );
        setLexicalHandler( lexicalHandler );
    }

    /**
     * Set the <code>ContentHandler</code> that will receive XML data.
     *
     * @exception IllegalStateException If the <code>ContentHandler</code>
     *                                  was already set.
     */
    public void setContentHandler( final ContentHandler contentHandler )
        throws IllegalStateException
    {
        if( null != this.contentHandler )
        {
            throw new IllegalStateException();
        }
        this.contentHandler = contentHandler;
    }

    /**
     * Set the <code>LexicalHandler</code> that will receive XML data.
     *
     * @exception IllegalStateException If the <code>LexicalHandler</code>
     *                                  was already set.
     */
    public void setLexicalHandler( final LexicalHandler lexicalHandler )
        throws IllegalStateException
    {
        if( null != this.lexicalHandler )
        {
            throw new IllegalStateException();
        }
        this.lexicalHandler = lexicalHandler;
    }

    public void recycle()
    {
        contentHandler = null;
        lexicalHandler = null;
    }

    /**
     * Receive an object for locating the origin of SAX document events.
     */
    public void setDocumentLocator( final Locator locator )
    {
        if( null == contentHandler )
        {
            return;
        }
        else
        {
            contentHandler.setDocumentLocator( locator );
        }
    }

    /**
     * Receive notification of the beginning of a document.
     */
    public void startDocument()
        throws SAXException
    {
        if( null == contentHandler )
        {
            final String message = "ContentHandler not set";
            throw new SAXException( message );
        }
        contentHandler.startDocument();
    }

    /**
     * Receive notification of the end of a document.
     */
    public void endDocument()
        throws SAXException
    {
        contentHandler.endDocument();
    }

    /**
     * Begin the scope of a prefix-URI Namespace mapping.
     */
    public void startPrefixMapping( final String prefix,
                                    final String uri )
        throws SAXException
    {
        if( null == contentHandler )
        {
            final String message = "ContentHandler not set";
            throw new SAXException( message );
        }
        contentHandler.startPrefixMapping( prefix, uri );
    }

    /**
     * End the scope of a prefix-URI mapping.
     */
    public void endPrefixMapping( final String prefix )
        throws SAXException
    {
        contentHandler.endPrefixMapping( prefix );
    }

    /**
     * Receive notification of the beginning of an element.
     */
    public void startElement( final String uri,
                              final String loc,
                              final String raw,
                              final Attributes a )
        throws SAXException
    {
        contentHandler.startElement( uri, loc, raw, a );
    }

    /**
     * Receive notification of the end of an element.
     */
    public void endElement( final String uri,
                            final String loc,
                            final String raw )
        throws SAXException
    {
        contentHandler.endElement( uri, loc, raw );
    }

    /**
     * Receive notification of character data.
     */
    public void characters( final char[] ch,
                            final int start,
                            final int len )
        throws SAXException
    {
        contentHandler.characters( ch, start, len );
    }

    /**
     * Receive notification of ignorable whitespace in element content.
     */
    public void ignorableWhitespace( final char[] ch,
                                     final int start,
                                     final int len )
        throws SAXException
    {
        contentHandler.ignorableWhitespace( ch, start, len );
    }

    /**
     * Receive notification of a processing instruction.
     */
    public void processingInstruction( final String target,
                                       final String data )
        throws SAXException
    {
        contentHandler.processingInstruction( target, data );
    }

    /**
     * Receive notification of a skipped entity.
     *
     * @param name The name of the skipped entity.  If it is a  parameter
     *             entity, the name will begin with '%'.
     */
    public void skippedEntity( final String name )
        throws SAXException
    {
        contentHandler.skippedEntity( name );
    }

    /**
     * Report the start of DTD declarations, if any.
     *
     * @param name The document type name.
     * @param publicId The declared public identifier for the external DTD
     *                 subset, or null if none was declared.
     * @param systemId The declared system identifier for the external DTD
     *                 subset, or null if none was declared.
     */
    public void startDTD( final String name,
                          final String publicId,
                          final String systemId )
        throws SAXException
    {
        if( null != lexicalHandler )
        {
            lexicalHandler.startDTD( name, publicId, systemId );
        }
    }

    /**
     * Report the end of DTD declarations.
     */
    public void endDTD()
        throws SAXException
    {
        if( null != lexicalHandler )
        {
            lexicalHandler.endDTD();
        }
    }

    /**
     * Report the beginning of an entity.
     *
     * @param name The name of the entity. If it is a parameter entity, the
     *             name will begin with '%'.
     */
    public void startEntity( final String name )
        throws SAXException
    {
        if( null != lexicalHandler )
        {
            lexicalHandler.startEntity( name );
        }
    }

    /**
     * Report the end of an entity.
     *
     * @param name The name of the entity that is ending.
     */
    public void endEntity( final String name )
        throws SAXException
    {
        if( null != lexicalHandler )
        {
            lexicalHandler.endEntity( name );
        }
    }

    /**
     * Report the start of a CDATA section.
     */
    public void startCDATA()
        throws SAXException
    {
        if( null != lexicalHandler )
        {
            lexicalHandler.startCDATA();
        }
    }

    /**
     * Report the end of a CDATA section.
     */
    public void endCDATA()
        throws SAXException
    {
        if( null != lexicalHandler )
        {
            lexicalHandler.endCDATA();
        }
    }

    /**
     * Report an XML comment anywhere in the document.
     *
     * @param ch An array holding the characters in the comment.
     * @param start The starting position in the array.
     * @param len The number of characters to use from the array.
     */
    public void comment( final char[] ch,
                         final int start,
                         final int len )
        throws SAXException
    {
        if( null != lexicalHandler )
        {
            lexicalHandler.comment( ch, start, len );
        }
    }

}
