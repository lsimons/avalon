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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.xml.sax.SAXException;

/**
 * Test that the <code>Configuration</code>s built by
 * <code>DefaultConfigurationBuilder</code> meet the stated API contracts.
 *
 * @author <a href="mailto:jefft@apache.org">Jeff Turner</a>
 * @author <a href="mailto:rantene@hotmail.com">Ran Tene</a>
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 */
public final class DefaultConfigurationBuilderTestCase
    extends TestCase
{

    private DefaultConfigurationBuilder m_builder;
    private DefaultConfigurationBuilder m_nsBuilder;
    private File m_file;
    private File m_nsFile;
    private File m_testDirectory;
    private final String m_simpleFileName = "config_simple.xml";
    private final String m_nsFileName = "config_namespaces.xml";
    private final String m_path = "test/framework/io/";

    private final String simpleXML =
    "<?xml version=\"1.0\" ?>"+
    "<config boolAttr=\"true\" floatAttr=\"1.32\">"+
    "   <elements-a>"+
    "       <element name=\"a\"/>"+
    "   </elements-a>"+
    "   <elements-b>"+
    "       <element name=\"b\"/> "+
    "   </elements-b>"+
    "   <elements-b type=\"type-b\"/>"+
    "   <elements-c>"+
    "   true"+
    "   </elements-c>"+
    "</config>";

    /**
     * These assertions apply when the default builder is used to create a
     * Configuration from <code>simpleXML</code>, ie namespace
     * support is disabled.
     */
    private void simpleAssertions(Configuration conf)
        throws ConfigurationException
    {
        assertEquals( "config", conf.getName() );
        assertEquals( "getNamespace() should default to \"\"", "", conf.getNamespace() );
        try {
            String value = conf.getValue();
            fail( "Should throw a ConfigurationException, as this element"+
                    "contains child elements, not a value" );
        } catch ( ConfigurationException e )
        {}

        Configuration[] children;
        children = conf.getChildren();
        assertEquals( 4, children.length );
        assertEquals( "elements-a", children[0].getName() );
        assertEquals( "elements-b", children[1].getName() );
        assertEquals( "b", children[1].getChild("element", false).getAttribute("name") );
        assertEquals( "elements-b", children[2].getName() );
        assertEquals( "elements-c", children[3].getName() );

        final String[] attrNames = conf.getAttributeNames();
        assertEquals( 2, attrNames.length );
        assertEquals( "default", conf.getAttribute("nonexistent", "default") );
        assertEquals( true, conf.getAttributeAsBoolean("boolAttr") );
        assertEquals( (float)1.32, conf.getAttributeAsFloat("floatAttr"), 0.0 );

        // Check that the auto-node-creation feature is working correctly.
        assertEquals(
                     "When a non-existent child is requested, a blank node should be created",
                     "nonexistent",
                     conf.getChild( "nonexistent" ).getName()
                     );
        assertEquals(
                     "When a non-existent child is requested, a blank node should be created",
                     "baz",
                     conf.getChild( "foo" ).getChild("bar").getChild("baz").getName()
                     );
        try {
            String value = conf.getChild("nonexistent").getValue();
            fail( "Auto-created child nodes should not have a value" );
        } catch ( ConfigurationException e )
        {}
        assertEquals( "Turning auto-node-creation off failed", null, conf.getChild( "nonexistent", false )
                    );
        assertEquals( "Standard getChild() lookup failed", "elements-b", conf.getChild( "elements-b", false ).getName() );
        assertEquals( "Boolean value surrounded by whitespace failed", true, conf.getChild("elements-c").getValueAsBoolean( false ) );
        assertEquals( "A value-containing element should have no child nodes", 0, conf.getChild("elements-c").getChildren().length );
    }

    private final String nsXML =
    "<?xml version=\"1.0\" ?>"+
    "<conf:config"+
    "       boolAttr=\"true\" floatAttr=\"1.32\""+
    "       xmlns:conf=\"http://conf.com\" xmlns:a=\"http://a.com\" xmlns:b=\"http://b.com\" xmlns:c=\"http://c.com\" xmlns:d=\"http://d.com\" xmlns:e=\"http://e.com\">"+
    "   <a:elements-a>"+
    "       <c:element name=\"a\"/>"+
    "   </a:elements-a>"+
    "   <elements-b xmlns=\"http://defaultns.com\">"+
    "       <element name=\"b\"/> "+
    "   </elements-b>"+
    "   <b:elements-b type=\"type-b\"/>"+
    "   <elements-c>"+
    "   true"+
    "   </elements-c>"+
    "   <d:element>d:element</d:element>"+
    "   <e:element>e:element</e:element>"+        
    "</conf:config>";
    /*
    "<?xml version=\"1.0\"?>"+
    "<my-system version='1.3' xmlns:doc=\"http://myco.com/documentation\">"+
    "   <doc:desc>This is a highly fictitious config file</doc:desc>"+
    "   <widget name=\"fooWidget\" initOrder=\"1\" threadsafe=\"true\"/>"+
    "</my-system>";
    */

    /**
     * These assertions apply when the default builder is used to create a
     * Configuration from <code>nsXML</code>, ie namespace support is disabled,
     * but the XML uses namespaces. 
     */
    private void simpleAssertionsNS(Configuration conf)
        throws ConfigurationException
    {
        assertEquals( "conf:config", conf.getName() );
        assertEquals( "getNamespace() should default to \"\"", "", conf.getNamespace() );
        try {
            String value = conf.getValue();
            fail( "Should throw a ConfigurationException, as this element"+
                    "contains child elements, not a value" );
        } catch ( ConfigurationException e )
        {}

        Configuration[] children;
        children = conf.getChildren();
        assertEquals( 6, children.length );
        assertEquals( "a:elements-a", children[0].getName() );
        assertEquals( "elements-b", children[1].getName() );
        assertEquals( "b", children[1].getChild("element", false).getAttribute("name") );
        assertEquals( "b:elements-b", children[2].getName() );
        assertEquals( "elements-c", children[3].getName() );

        final String[] attrNames = conf.getAttributeNames();
        assertEquals( 8, attrNames.length );
        assertEquals( "true", conf.getAttribute("boolAttr", null) );
        assertEquals( true, conf.getAttributeAsBoolean("boolAttr") );
        assertEquals( (float)1.32, conf.getAttributeAsFloat("floatAttr"), 0.0 );
        assertEquals( "http://conf.com", conf.getAttribute("xmlns:conf") );
        assertEquals( "http://a.com", conf.getAttribute("xmlns:a") );
        assertEquals( "http://b.com", conf.getAttribute("xmlns:b") );
        assertEquals( "http://c.com", conf.getAttribute("xmlns:c") );

        // Check that the auto-node-creation feature is working correctly.
        assertEquals(
                     "When a non-existent child is requested, a blank node should be created",
                     "nonexistent",
                     conf.getChild( "nonexistent" ).getName()
                     );
        assertEquals(
                     "When a non-existent child is requested, a blank node should be created",
                     "baz",
                     conf.getChild( "foo" ).getChild("bar").getChild("baz").getName()
                     );
        try {
            String value = conf.getChild("nonexistent").getValue();
            fail( "Auto-created child nodes should not have a value" );
        } catch ( ConfigurationException e )
        {}
        assertEquals( "Turning auto-node-creation off failed", null, conf.getChild( "nonexistent", false )
                    );
        assertEquals( "Standard getChild() lookup failed", "b:elements-b", conf.getChild( "b:elements-b", false ).getName() );
        assertEquals( "Boolean value surrounded by whitespace failed", true, conf.getChild("elements-c").getValueAsBoolean( false ) );
        assertEquals( "A value-containing element should have no child nodes", 0, conf.getChild("elements-c").getChildren().length );
        
        assertEquals( "d:element", conf.getChild("d:element").getValue() );
        assertEquals( "e:element", conf.getChild("e:element").getValue() );
    }


    /**
     * These assertions apply when the namespace-enabled builder is used to
     * create a Configuration from <code>nsXML</code>, ie namespace support is
     * enabled, and the XML uses namespaces. 
     */
    private void nsAssertions(Configuration conf)
        throws ConfigurationException
    {
        assertEquals( "config", conf.getName() );
        assertEquals( "Namespace not set correctly", "http://conf.com", conf.getNamespace() );
        try {
            String value = conf.getValue();
            fail( "Should throw a ConfigurationException, as this element"+
                    "contains child elements, not a value" );
        } catch ( ConfigurationException e )
        {}

        Configuration[] children;
        children = conf.getChildren();
        assertEquals( 6, children.length );
        assertEquals( "elements-a", children[0].getName() );
        assertEquals( "http://a.com", children[0].getNamespace() );
        assertEquals( "elements-b", children[1].getName() );
        assertEquals( "http://defaultns.com", children[1].getNamespace() );
        assertEquals( "b", children[1].getChild("element", false).getAttribute("name") );
        assertEquals( "elements-b", children[2].getName() );
        assertEquals( "http://b.com", children[2].getNamespace() );
        assertEquals( "elements-c", children[3].getName() );
        assertEquals( "", children[3].getNamespace() );

        final String[] attrNames = conf.getAttributeNames();
        assertEquals( 2, attrNames.length ); // the other 4 are xmlns and so shouldn't appear
        assertEquals( "true", conf.getAttribute("boolAttr", null) );
        assertEquals( true, conf.getAttributeAsBoolean("boolAttr") );
        assertEquals( (float)1.32, conf.getAttributeAsFloat("floatAttr"), 0.0 );

        // Check that the auto-node-creation feature is working correctly.
        assertEquals(
                     "When a non-existent child is requested, a blank node should be created",
                     "nonexistent",
                     conf.getChild( "nonexistent" ).getName()
                     );
        assertEquals(
                     "When a non-existent child is requested, a blank node should be created",
                     "baz",
                     conf.getChild( "foo" ).getChild("bar").getChild("baz").getName()
                     );
        try {
            String value = conf.getChild("nonexistent").getValue();
            fail( "Auto-created child nodes should not have a value" );
        } catch ( ConfigurationException e )
        {}
        assertEquals( "Turning auto-node-creation off failed", null, conf.getChild( "nonexistent", false )
                    );
        assertEquals( "Standard getChild() lookup failed", "elements-b", conf.getChild( "elements-b", false ).getName() );
        assertEquals( "Boolean value surrounded by whitespace failed", true, conf.getChild("elements-c").getValueAsBoolean( false ) );
        assertEquals( "A value-containing element should have no child nodes", 0, conf.getChild("elements-c").getChildren().length );
    }


    public DefaultConfigurationBuilderTestCase()
    {
        this("DefaultConfigurationBuilder Test Case");
    }

    public DefaultConfigurationBuilderTestCase( final String name )
    {
        super( name );
        m_testDirectory = (new File( m_path)).getAbsoluteFile();
        if( !m_testDirectory.exists() )
        {
            m_testDirectory.mkdirs();
        }
    }

    protected void setUp()
        throws Exception
    {
        m_file = new File( m_testDirectory, m_simpleFileName );
        m_nsFile = new File( m_testDirectory, m_nsFileName );
        FileWriter writer = new FileWriter( m_file );
        writer.write( simpleXML );
        writer.close();
        writer = new FileWriter( m_nsFile );
        writer.write( nsXML );
        writer.close();

    }

    protected  void tearDown()
        throws Exception
    {
        /*FileWriter writer = new FileWriter( m_file );
        writer.write( "" );
        writer.close();
        writer = new FileWriter( m_nsFile );
        writer.write( "" );
        writer.close();*/
        m_builder = null;
        m_nsBuilder = null;
    }


    public void testBuildFromFileName()
        throws Exception
    {
        m_builder = new DefaultConfigurationBuilder();
        m_nsBuilder = new DefaultConfigurationBuilder(true); // switch on namespace support
        Configuration conf = m_builder.buildFromFile( m_path + m_simpleFileName );
        simpleAssertions( conf );
        conf = m_builder.buildFromFile( m_path + m_nsFileName );
        simpleAssertionsNS( conf );
        conf = m_nsBuilder.buildFromFile( m_path + m_nsFileName );
        nsAssertions( conf );
    }

    public void testBuildFromFile()
        throws Exception
    {
        m_builder = new DefaultConfigurationBuilder();
        m_nsBuilder = new DefaultConfigurationBuilder(true); // switch on namespace support
        Configuration conf =  m_builder.buildFromFile( m_file );
        simpleAssertions( conf );
        conf = m_builder.buildFromFile( m_nsFile );
        simpleAssertionsNS( conf );
        conf = m_nsBuilder.buildFromFile( m_nsFile );
        nsAssertions( conf );
    }

    public void testBuild()
        throws Exception
    {
        m_builder = new DefaultConfigurationBuilder();
        m_nsBuilder = new DefaultConfigurationBuilder(true); // switch on namespace support
        Configuration conf =  m_builder.build( m_file.toURL().toString() );
        simpleAssertions( conf );
        conf = m_builder.buildFromFile( m_nsFile );
        simpleAssertionsNS( conf );
        conf = m_nsBuilder.buildFromFile( m_nsFile );
        nsAssertions( conf );
    }

    private final String spaceTrimmingCheckXML =
        "<?xml version=\"1.0\" ?>"+
        " <config>"+
        "   <trimmed-item>\n"+
        "    value     \n"+
        "   </trimmed-item>\n"+
        "   <preserved-item xml:space='preserve'>\n"+
        " a space&#13; a CR, then a trailing space </preserved-item>\n"+
        "   <first-level-item xml:space='preserve'>\n"+
        "      <second-level-preserved> whitespace around </second-level-preserved>\n"+
        "   </first-level-item>\n"+
        "   <trimmed-again-item>\n"+
        "    value     \n"+
        "   </trimmed-again-item>\n"+
        "</config>";
    
    /**
     * Checks that whitespace is normally stripped but preserved if
     * space preserving processing instructions are present.
     */
    public void testSpaceTrimming()
        throws Exception
    {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        InputStream in = new ByteArrayInputStream( spaceTrimmingCheckXML.getBytes() );
        Configuration conf = builder.build( in );
        assertEquals( "Value is trimmed by default",
                      "value",
                      conf.getChild( "trimmed-item" ).getValue() );
        assertEquals( "After trimming turned off value is preserved",
                      "\n a space\r a CR, then a trailing space ",
                      conf.getChild( "preserved-item" ).getValue() );
        assertEquals( "Trimming two levels deep works too",
                      " whitespace around ",
                      conf.getChild( "first-level-item" )
                      .getChild( "second-level-preserved" ).getValue() );
        assertEquals( "Trimming turned back on",
                      "value",
                      conf.getChild( "trimmed-again-item" ).getValue() );
    }

    
    private final String mixedContentXML =
        "<?xml version=\"1.0\" ?>"+
        "<a>a<a/></a>"
        ;
    public void testMixedContentDetection()
        throws Exception
    {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        InputStream in = new ByteArrayInputStream( mixedContentXML.getBytes() );
        try
        {
            builder.build( in );
            fail ("Must fail on mixed content");
        } catch ( SAXException e )
        {}
    }
}
