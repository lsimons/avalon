/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.configuration.test;

import java.util.List;
import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
import org.apache.avalon.framework.configuration.NamespacedSAXConfigurationHandler;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Test the basic public methods of SAXConfigurationHandlerTestCase.
 *
 * @author <a href="mailto:rantene@hotmail.com">Ran Tene</a>
 */
public final class SAXConfigurationHandlerTestCase extends TestCase
{
    public SAXConfigurationHandlerTestCase()
    {
        this("SAXConfigurationHandler Test Case ");
    }

    public SAXConfigurationHandlerTestCase( final String name )
    {
        super( name );
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
    public void testDefaultHandling() throws Exception
    {
        SAXConfigurationHandler handler = new SAXConfigurationHandler( );

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
        attributes.addAttribute("",attqName,attqName,
                                "CDATA",attValue);

        final AttributesImpl childAttributes  = new AttributesImpl();
        childAttributes.addAttribute("", "child", "xmlns:child", "CDATA", childURI);

        handler.startDocument();
        handler.startPrefixMapping( "child", childURI );
        handler.startElement( rootURI, rootlocal, rootraw, attributes );
        handler.startElement( childURI,
                                childlocal,
                                childraw,
                                childAttributes );

        handler.characters( childvalue.toCharArray(), 0, childvalue.length() );
        handler.endElement( childURI, childlocal, childraw );
        handler.endElement( null, null, rootraw);
        handler.endPrefixMapping( "child" );
        handler.endDocument();

        final Configuration configuration = handler.getConfiguration();
        assertEquals( attValue, configuration.getAttribute(attqName));
        assertEquals( childvalue, configuration.getChild(childraw).getValue());
        assertEquals( "", configuration.getChild(childraw).getNamespace() );
        assertEquals( rootraw, configuration.getName());
    }

    public void testNamespaceHandling() throws Exception
    {
        SAXConfigurationHandler handler = new NamespacedSAXConfigurationHandler( );

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
        attributes.addAttribute("",attqName,attqName,
                                "CDATA",attValue);

        final AttributesImpl childAttributes  = new AttributesImpl();
        childAttributes.addAttribute("", "child", "xmlns:child", "CDATA", childURI);

        handler.startDocument();
        handler.startPrefixMapping( "child", childURI );
        handler.startElement( rootURI, rootlocal, rootraw, attributes );
        handler.startElement( childURI,
                                childlocal,
                                childraw,
                                childAttributes );

        handler.characters( childvalue.toCharArray(), 0, childvalue.length() );
        handler.endElement( childURI, childlocal, childraw );
        handler.endElement( null, null, rootraw);
        handler.endPrefixMapping( "child" );
        handler.endDocument();

        final Configuration configuration = handler.getConfiguration();
        assertEquals( attValue, configuration.getAttribute(attqName));
        assertEquals( childvalue, configuration.getChild(childlocal).getValue());
        assertEquals( childURI, configuration.getChild(childlocal).getNamespace() );
        assertEquals( rootraw, configuration.getName());
    }
}





