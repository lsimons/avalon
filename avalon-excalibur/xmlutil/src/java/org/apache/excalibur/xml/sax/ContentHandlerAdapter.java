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

import java.util.Enumeration;

import org.xml.sax.AttributeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * This class is an utility class adapting a SAX version 2.0
 * {@link ContentHandler} to receive SAX version 1.0 events.
 *
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:47:42 $
 */

public class ContentHandlerAdapter
    implements DocumentHandler
{
    private final static String XMLNS = "xmlns";
    private final static String XMLNS_PREFIX = "xmlns:";

    private final ContentHandler m_handler;
    private final NamespaceSupport m_support = new NamespaceSupport();

    public ContentHandlerAdapter( final ContentHandler handler )
    {
        m_handler = handler;
    }

    public void setDocumentLocator( final Locator locator )
    {
        m_handler.setDocumentLocator( locator );
    }

    public void startDocument() throws SAXException
    {
        m_handler.startDocument();
    }

    public void endDocument() throws SAXException
    {
        m_handler.endDocument();
    }

    public void characters( final char ch[],
                            final int start,
                            final int length ) throws SAXException
    {
        m_handler.characters( ch, start, length );
    }

    public void ignorableWhitespace( final char ch[],
                                     final int start,
                                     final int length ) throws SAXException
    {
        m_handler.ignorableWhitespace( ch, start, length );
    }

    public void processingInstruction( final String target,
                                       final String data ) throws SAXException
    {
        m_handler.processingInstruction( target, data );
    }

    public void startElement( final String name,
                              final AttributeList atts ) throws SAXException
    {
        m_support.pushContext();

        for( int i = 0; i < atts.getLength(); i++ )
        {
            final String attributeName = atts.getName( i );
            if( attributeName.startsWith( XMLNS_PREFIX ) )
            {
                m_support.declarePrefix( attributeName.substring( 6 ), atts.getValue( i ) );
            }
            else if( attributeName.equals( XMLNS ) )
            {
                m_support.declarePrefix( "", atts.getValue( i ) );
            }
        }

        final AttributesImpl attributes = new AttributesImpl();
        for( int i = 0; i < atts.getLength(); i++ )
        {
            final String attributeName = atts.getName( i );
            if( !attributeName.startsWith( XMLNS_PREFIX ) && !attributeName.equals( XMLNS ) )
            {
                final String[] parts = m_support.processName( attributeName, new String[ 3 ], true );
                attributes.addAttribute( parts[ 0 ], parts[ 1 ], parts[ 2 ], atts.getType( i ), atts.getValue( i ) );
            }
        }

        final Enumeration e = m_support.getDeclaredPrefixes();
        while( e.hasMoreElements() )
        {
            final String prefix = (String)e.nextElement();
            m_handler.startPrefixMapping( prefix, m_support.getURI( prefix ) );
        }

        final String[] parts = m_support.processName( name, new String[ 3 ], false );
        m_handler.startElement( parts[ 0 ], parts[ 1 ], parts[ 2 ], attributes );
    }

    public void endElement( final String name ) throws SAXException
    {
        final String[] parts = m_support.processName( name, new String[ 3 ], false );
        m_handler.endElement( parts[ 0 ], parts[ 1 ], parts[ 2 ] );

        final Enumeration e = m_support.getDeclaredPrefixes();
        while( e.hasMoreElements() )
        {
            final String prefix = (String)e.nextElement();
            m_handler.endPrefixMapping( prefix );
        }

        m_support.popContext();
    }
}
