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

/**
 * This class is an utility class proxying a SAX version 2.0
 * {@link ContentHandler} and forwarding the events to it.
 * <br>
 *
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/02/27 09:24:46 $
 */
public class ContentHandlerProxy
    implements ContentHandler
{
    /** The current {@link ContentHandler}. */
    private final ContentHandler m_contentHandler;

    /**
     * Create a new <code>ContentHandlerWrapper</code> instance.
     */
    public ContentHandlerProxy( final ContentHandler contentHandler )
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
