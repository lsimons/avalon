/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.configuration.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

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
}
