/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
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
 * This class is an utility class &quot;wrapping&quot; around a SAX version 2.0
 * {@link ContentHandler} and forwarding the events to it.
 *
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/10/15 23:25:09 $
 */

public class DocumentHandlerAdapter implements DocumentHandler
{    
    private final static String XMLNS = "xmlns";
    private final static String XMLNS_PREFIX = "xmlns:";
    private final ContentHandler m_handler;
    private final NamespaceSupport m_support = new NamespaceSupport();
    
    public DocumentHandlerAdapter(ContentHandler handler)
    {
        m_handler = handler;
    }
    
    public void setDocumentLocator(Locator locator)
    {
        m_handler.setDocumentLocator(locator);
    }
    
    public void startDocument() throws SAXException
    {
        m_handler.startDocument();
    }
    
    public void endDocument() throws SAXException
    {
        m_handler.endDocument();
    }
    
    public void characters(char ch[], int start, int length) throws SAXException
    {
        m_handler.characters(ch, start, length);
    }
    
    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException
    {
        m_handler.ignorableWhitespace(ch, start, length);
    }
    
    public void processingInstruction(String target, String data) throws SAXException
    {
        m_handler.processingInstruction(target, data);
    }
    
    public void startElement(String name, AttributeList atts) throws SAXException
    {
        m_support.pushContext();
        
        for (int i = 0; i < atts.getLength(); i++)
        {
            final String attributeName = atts.getName(i);
            if (attributeName.startsWith(XMLNS_PREFIX))
            {
                m_support.declarePrefix(attributeName.substring(6), atts.getValue(i));
            }
            else if (attributeName.equals(XMLNS))
            {
                m_support.declarePrefix("", atts.getValue(i));
            }
        }
        
        final AttributesImpl attributes = new AttributesImpl();
        for (int i = 0; i < atts.getLength(); i++)
        {
            final String attributeName = atts.getName(i);
            if (!attributeName.startsWith(XMLNS_PREFIX) && !attributeName.equals(XMLNS))
            {
                final String[] parts = m_support.processName(name, new String[3], true);
                attributes.addAttribute(parts[0], parts[1], parts[2], atts.getType(i), atts.getValue(i));
            }
        }
        
        final Enumeration e = m_support.getDeclaredPrefixes();
        while(e.hasMoreElements())
        {
            final String prefix = (String)e.nextElement();
            m_handler.startPrefixMapping(prefix, m_support.getURI(prefix));
        }
        
        final String[] parts = m_support.processName(name, new String[3], false);
        m_handler.startElement(parts[0], parts[1], parts[2], attributes);
    }
    
    public void endElement(String name) throws SAXException
    {
        final String[] parts = m_support.processName(name, new String[3], false);
        m_handler.endElement(parts[0], parts[1], parts[2]);
        
        final Enumeration e = m_support.getDeclaredPrefixes();
        while(e.hasMoreElements())
        {
            final String prefix = (String)e.nextElement();
            m_handler.endPrefixMapping(prefix);
        }
        
        m_support.popContext();
    }
}
