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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributeListImpl;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * This class is an utility class adapting a SAX version 1.0
 * {@link DocumentHandler} to receive SAX version 2.0 events.
 *
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 12:47:42 $
 */
public class DocumentHandlerAdapter
    implements ContentHandler
{
    private final static String XMLNS = "xmlns";
    private final static String XMLNS_PREFIX = "xmlns:";
    private final static String CDATA = "CDATA";
    private final DocumentHandler m_documentHandler;
    private final NamespaceSupport m_support = new NamespaceSupport();
    private boolean m_contextPushed = false;

    /**
     * Create a new <code>ContentHandlerWrapper</code> instance.
     */
    public DocumentHandlerAdapter( final DocumentHandler documentHandler )
    {
        m_documentHandler = documentHandler;
    }

    /**
     * Receive an object for locating the origin of SAX document events.
     */
    public void setDocumentLocator( final Locator locator )
    {
        m_documentHandler.setDocumentLocator( locator );
    }

    /**
     * Receive notification of the beginning of a document.
     */
    public void startDocument() throws SAXException
    {
        m_documentHandler.startDocument();
    }

    /**
     * Receive notification of the end of a document.
     */
    public void endDocument()
        throws SAXException
    {
        m_documentHandler.endDocument();
    }

    /**
     * Begin the scope of a prefix-URI Namespace mapping.
     */
    public void startPrefixMapping( final String prefix, final String uri ) throws SAXException
    {
        if( !m_contextPushed )
        {
            m_support.pushContext();
            m_contextPushed = true;
        }

        m_support.declarePrefix( prefix, uri );
    }

    /**
     * End the scope of a prefix-URI mapping.
     */
    public void endPrefixMapping( final String prefix ) throws SAXException
    {
        //do nothing
    }

    /**
     * Receive notification of the beginning of an element.
     */
    public void startElement( final String uri,
                              final String loc,
                              final String raw,
                              final Attributes a ) throws SAXException
    {
        if( !m_contextPushed )
        {
            m_support.pushContext();
        }
        m_contextPushed = false;

        final String name = getTagName( loc, raw, uri );

        final AttributeListImpl attributeList = new AttributeListImpl();
        for( int i = 0; i < a.getLength(); i++ )
        {
            String attributeName = a.getQName( i );
            if( ( attributeName == null ) || ( attributeName.length() == 0 ) )
            {
                final String attributeNamespaceURI = a.getURI( i );
                final String attributeLocalName = a.getLocalName( i );
                if( attributeNamespaceURI.length() == 0 )
                {
                    attributeName = attributeLocalName;
                }
                else
                {
                    final String prefix = m_support.getPrefix( attributeNamespaceURI );
                    if( prefix == null )
                    {
                        throw new SAXException( "No attribute prefix for namespace URI: " + attributeNamespaceURI );
                    }
                    attributeName = prefix + ':' + attributeLocalName;
                }
            }
            attributeList.addAttribute( attributeName, a.getType( i ), a.getValue( i ) );
        }

        final Enumeration e = m_support.getDeclaredPrefixes();
        while( e.hasMoreElements() )
        {
            final String prefix = (String)e.nextElement();
            if( prefix.length() == 0 )
            {
                attributeList.addAttribute( XMLNS, CDATA, uri );
            }
            else
            {
                attributeList.addAttribute( XMLNS_PREFIX + prefix, CDATA, uri );
            }
        }

        m_documentHandler.startElement( name, attributeList );
    }

    /**
     * Receive notification of the end of an element.
     */
    public void endElement( final String uri,
                            final String loc,
                            final String raw ) throws SAXException
    {
        final String name = getTagName( loc, raw, uri );
        m_documentHandler.endElement( name );
        m_support.popContext();
    }

    /**
     * Receive notification of character data.
     */
    public void characters( final char[] ch,
                            final int start,
                            final int len ) throws SAXException
    {
        m_documentHandler.characters( ch, start, len );
    }

    /**
     * Receive notification of ignorable whitespace in element content.
     */
    public void ignorableWhitespace( final char[] ch,
                                     final int start,
                                     final int len ) throws SAXException
    {
        m_documentHandler.ignorableWhitespace( ch, start, len );
    }

    /**
     * Receive notification of a processing instruction.
     */
    public void processingInstruction( final String target,
                                       final String data ) throws SAXException
    {
        m_documentHandler.processingInstruction( target, data );
    }

    /**
     * Receive notification of a skipped entity.
     *
     * @param name The name of the skipped entity.  If it is a  parameter
     *             entity, the name will begin with '%'.
     */
    public void skippedEntity( final String name ) throws SAXException
    {
        //do nothing
    }

    private String getTagName( final String loc, final String raw, final String uri ) throws SAXException
    {
        if( raw != null && raw.length() > 0 )
        {
            return raw;
        }
        else
        {
            final String prefix = getTagPrefix( uri );
            return ( ( prefix.length() == 0 ) ? "" : ( prefix + ':' ) ) + loc;
        }
    }

    private String getTagPrefix( final String uri ) throws SAXException
    {
        if( m_support.getPrefix( uri ) == null )
        {
            if( ( uri == null ) || ( uri.length() < 1 ) )
            {
                return "";
            }
            else
            {
                final String defaultURI = m_support.getURI( "" );
                if( ( defaultURI != null ) && defaultURI.equals( uri ) )
                {
                    return ""; // default namespace
                }
                else
                {
                    throw new SAXException( "No element prefix for namespace URI: " + uri );
                }
            }
        }
        else
        {
            return m_support.getPrefix( uri );
        }
    }
}
