/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * This class is an utility class &quot;wrapping&quot; around a SAX version 2.0
 * {@link ContentHandler} and forwarding the events to it.
 * <br>
 *
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/10/13 03:20:14 $
 */
public class ContentHandlerAdapter implements ContentHandler
{
    /** The current {@link ContentHandler}. */
    private final ContentHandler m_contentHandler;

    /**
     * Create a new <code>ContentHandlerWrapper</code> instance.
     */
    public ContentHandlerAdapter(final ContentHandler contentHandler)
    {
        m_contentHandler = contentHandler;
    }

    /**
     * Receive an object for locating the origin of SAX document events.
     */
    public void setDocumentLocator( final Locator locator )
    {
        m_contentHandler.setDocumentLocator( locator );
    }

    /**
     * Receive notification of the beginning of a document.
     */
    public void startDocument()
        throws SAXException
    {
        m_contentHandler.startDocument();
    }

    /**
     * Receive notification of the end of a document.
     */
    public void endDocument()
        throws SAXException
    {
        m_contentHandler.endDocument();
    }

    /**
     * Begin the scope of a prefix-URI Namespace mapping.
     */
    public void startPrefixMapping( final String prefix,
                                    final String uri )
        throws SAXException
    {
        m_contentHandler.startPrefixMapping( prefix, uri );
    }

    /**
     * End the scope of a prefix-URI mapping.
     */
    public void endPrefixMapping( final String prefix )
        throws SAXException
    {
        m_contentHandler.endPrefixMapping( prefix );
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
        m_contentHandler.startElement( uri, loc, raw, a );
    }

    /**
     * Receive notification of the end of an element.
     */
    public void endElement( final String uri,
                            final String loc,
                            final String raw )
        throws SAXException
    {
        m_contentHandler.endElement( uri, loc, raw );
    }

    /**
     * Receive notification of character data.
     */
    public void characters( final char[] ch,
                            final int start,
                            final int len )
        throws SAXException
    {
        m_contentHandler.characters( ch, start, len );
    }

    /**
     * Receive notification of ignorable whitespace in element content.
     */
    public void ignorableWhitespace( final char[] ch,
                                     final int start,
                                     final int len )
        throws SAXException
    {
        m_contentHandler.ignorableWhitespace( ch, start, len );
    }

    /**
     * Receive notification of a processing instruction.
     */
    public void processingInstruction( final String target,
                                       final String data )
        throws SAXException
    {
        m_contentHandler.processingInstruction( target, data );
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
        m_contentHandler.skippedEntity( name );
    }
}
