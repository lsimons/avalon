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
package org.apache.excalibur.xml.sax;

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
 * @deprecated Moved to org.apache.excalibur.xml.sax package. Modified to be 
 * thread safe.
 * @author <a href="mailto:dims@yahoo.com">Davanum Srinivas</a>
 *         (Apache Software Foundation, Computer Associates)
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:47:42 $
 */
public class ContentHandlerWrapper
    extends AbstractXMLConsumer
{
    /** The current {@link ContentHandler}. */
    private ContentHandler m_contentHandler;

    /** The optional {@link LexicalHandler} */
    private LexicalHandler m_lexicalHandler;

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
     * Set the {@link ContentHandler} that will receive XML data.
     *
     * @exception IllegalStateException If the {@link ContentHandler}
     *                                  was already set.
     */
    public void setContentHandler( final ContentHandler contentHandler )
        throws IllegalStateException
    {
        if( null != m_contentHandler )
        {
            throw new IllegalStateException();
        }
        m_contentHandler = contentHandler;
    }

    /**
     * Set the {@link LexicalHandler} that will receive XML data.
     *
     * @exception IllegalStateException If the {@link LexicalHandler}
     *                                  was already set.
     */
    public void setLexicalHandler( final LexicalHandler lexicalHandler )
        throws IllegalStateException
    {
        if( null != m_lexicalHandler )
        {
            throw new IllegalStateException();
        }
        m_lexicalHandler = lexicalHandler;
    }

    /**
     * Receive an object for locating the origin of SAX document events.
     */
    public void setDocumentLocator( final Locator locator )
    {
        if( null == m_contentHandler )
        {
            return;
        }
        else
        {
            m_contentHandler.setDocumentLocator( locator );
        }
    }

    /**
     * Receive notification of the beginning of a document.
     */
    public void startDocument()
        throws SAXException
    {
        if( null == m_contentHandler )
        {
            final String message = "ContentHandler not set";
            throw new SAXException( message );
        }
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
        if( null == m_contentHandler )
        {
            final String message = "ContentHandler not set";
            throw new SAXException( message );
        }
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
        if( null != m_lexicalHandler )
        {
            m_lexicalHandler.startDTD( name, publicId, systemId );
        }
    }

    /**
     * Report the end of DTD declarations.
     */
    public void endDTD()
        throws SAXException
    {
        if( null != m_lexicalHandler )
        {
            m_lexicalHandler.endDTD();
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
        if( null != m_lexicalHandler )
        {
            m_lexicalHandler.startEntity( name );
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
        if( null != m_lexicalHandler )
        {
            m_lexicalHandler.endEntity( name );
        }
    }

    /**
     * Report the start of a CDATA section.
     */
    public void startCDATA()
        throws SAXException
    {
        if( null != m_lexicalHandler )
        {
            m_lexicalHandler.startCDATA();
        }
    }

    /**
     * Report the end of a CDATA section.
     */
    public void endCDATA()
        throws SAXException
    {
        if( null != m_lexicalHandler )
        {
            m_lexicalHandler.endCDATA();
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
        if( null != m_lexicalHandler )
        {
            m_lexicalHandler.comment( ch, start, len );
        }
    }
}
