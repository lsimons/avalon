/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.configuration.test;

import java.util.List;
import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Test the basic public methods of SAXConfigurationHandlerTestCase.
 *
 * @author <a href="mailto:rantene@hotmail.com">Ran Tene</a>
 */
public final class SAXConfigurationHandlerTestCase extends TestCase
{
    private SAXConfigurationHandler m_handler;
        
    public SAXConfigurationHandlerTestCase()
    {
        this("SAXConfigurationHandler Test Case ");
    }

    public SAXConfigurationHandlerTestCase( final String name )
    {
        super( name );
    }

    public void setUp()
    {
        m_handler = new SAXConfigurationHandler( );
    }

    public void tearDowm()
    {
        m_handler = null;
    }

    public void testHandling() throws Exception
    {
        final String namespaceURI = "namespaceURI";
        final String localName = "localName";
        final String rawName = "rawName";
        final String value = "value";
        final String attUri = "attUri";
        final String attLocalName = "attLocalName";
        final String attqName = "attqName";
        final String attType = "attType";
        final String attValue = "attValue";
        final String childfix = "child";

        final AttributesImpl attributes  = new AttributesImpl();
        attributes.addAttribute(attUri,attLocalName,attqName,
                                attType,attValue);

        final AttributesImpl childAttributes  = new AttributesImpl();
        m_handler.startDocument();
        m_handler.startElement( namespaceURI, localName, rawName, attributes );
        m_handler.startElement( namespaceURI + childfix,  
                                localName + childfix,
                                rawName + childfix, 
                                childAttributes );

        m_handler.characters( value.toCharArray(), 0, value.length() );
        m_handler.endElement( namespaceURI + childfix, localName + childfix, rawName + childfix );
        m_handler.endElement( namespaceURI, localName, rawName);
        m_handler.endDocument();

        final Configuration configuration = m_handler.getConfiguration();
        assertEquals( attValue, configuration.getAttribute(attqName));
        assertEquals( value, configuration.getChild(rawName+childfix).getValue());
        assertEquals( rawName, configuration.getName());
    }
}





