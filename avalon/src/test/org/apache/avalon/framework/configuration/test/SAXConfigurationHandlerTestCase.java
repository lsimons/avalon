/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Apache Avalon", "Avalon Excalibur", "Avalon
    Framework" and "Apache Software Foundation"  must not be used to endorse
    or promote products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache 
 Software Foundation, please see <http://www.apache.org/>.
 
*/
package org.apache.avalon.framework.configuration.test;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.NamespacedSAXConfigurationHandler;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
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
     *     &lt;emptyElement/&gt;
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
        final String emptylocal = "emptyElement";
        final String emptyraw = emptylocal;
        
        final AttributesImpl emptyAttributes  = new AttributesImpl();

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
        handler.startElement( rootURI, emptylocal, emptyraw, emptyAttributes );
        handler.endElement( rootURI, emptylocal, emptyraw );
        handler.endElement( null, null, rootraw);
        handler.endPrefixMapping( "child" );
        handler.endDocument();

        final Configuration configuration = handler.getConfiguration();
        assertEquals( attValue, configuration.getAttribute(attqName));
        assertEquals( childvalue, configuration.getChild(childraw).getValue());
        assertEquals( "", configuration.getChild(childraw).getNamespace() );
        assertEquals( rootraw, configuration.getName());
        assertEquals( "test", configuration.getChild(emptyraw).getValue( "test" ) );
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





