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
import org.apache.avalon.framework.configuration.Namespace;
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

    /**
     * Test the ContentHandler.  The XML created should look like this:
     *
     * <pre>
     *   &lt;rawName attqName="attValue"&gt;
     *     &lt;child:localName xmlns:child="namespaceURI"&gt;value&lt;/child:localName&gt;
     *   &lt;/rawName&gt;
     * </pre>
     */
    public void testHandling() throws Exception
    {
        final String rootURI = "";
        final String rootlocal = "rawName";
        final String rootraw = "rawName";
        final String childURI = "namespaceURI";
        final String childlocal = "localName";
        final String childraw = "child:" + childlocal;
        final String childvalue = "value";
        final String attqName = "attqName";
        final String attValue = "attValue";

        final AttributesImpl attributes  = new AttributesImpl();
        attributes.addAttribute(null,null,attqName,
                                "CDATA",attValue);

        final AttributesImpl childAttributes  = new AttributesImpl();

        m_handler.startDocument();
        m_handler.startElement( rootURI, rootlocal, rootraw, attributes );
        m_handler.startPrefixMapping( "child", childURI );
        m_handler.startElement( childURI,
                                childlocal,
                                childraw,
                                childAttributes );

        m_handler.characters( childvalue.toCharArray(), 0, childvalue.length() );
        m_handler.endElement( childURI, childlocal, childraw );
        m_handler.endPrefixMapping( "child" );
        m_handler.endElement( null, null, rootraw);
        m_handler.endDocument();

        final Configuration configuration = m_handler.getConfiguration();
        assertEquals( attValue, configuration.getAttribute(attqName));
        assertEquals( childvalue, configuration.getChild(childlocal).getValue());
        assertEquals( Namespace.getNamespace("child", childURI), configuration.getChild(childlocal).getNamespace() );
        assertEquals( rootraw, configuration.getName());
    }
}





