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
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

/**
 * Test the basic public methods of DefaultConfigurationBuilder.
 *
 * @author <a href="mailto:rantene@hotmail.com">Ran Tene</a>
 */
public final class DefaultConfigurationBuilderTestCase
    extends TestCase
{

    private DefaultConfigurationBuilder m_builder;
    private File m_file;
    private File m_testDirectory;
    private final String m_fileName = "configTest.xml";
    private final String m_path = "test/framework/io/";

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
        m_builder = new DefaultConfigurationBuilder();
        String xmlString = "<?xml version=\"1.0\" ?>\n";
        xmlString += "<config>";
        xmlString += "<elements-a>";
        xmlString += "<element name=\"a\"/>";
        xmlString += "</elements-a>";
        xmlString += "<elements-b>";
        xmlString += "<element name=\"b\"/> ";
        xmlString += "</elements-b>";
        xmlString += "<elements-b type=\"type-b\"/>";
        xmlString += "</config>";
        m_file = new File( m_testDirectory, m_fileName );
        final FileWriter writer = new FileWriter( m_file );
        writer.write( xmlString );
        writer.flush();
    }

    protected  void tearDown()
        throws Exception
    {
        final FileWriter writer = new FileWriter( m_file );
        writer.write( "" );
        writer.flush();
        m_builder = null;
    }

    public void testBuildFromFileName()
        throws Exception
    {
        final Configuration conf =  m_builder.buildFromFile( m_path + m_fileName );
        assertEquals( "config", conf.getName() );
        assertEquals( "elements-a", conf.getChild( "elements-a", false ).getName() );

    }

    public void testBuildFromFile()
        throws Exception
    {
        final Configuration conf =  m_builder.buildFromFile( m_file );
        assertEquals( "config", conf.getName() );
        assertEquals( "elements-a", conf.getChild( "elements-a", false ).getName() );
    }

    public void testBuild()
        throws Exception
    {
        final Configuration conf =  m_builder.build( m_file.toURL().toString() );
        assertEquals( "config", conf.getName() );
        assertEquals( "elements-b", conf.getChild( "elements-b", false ).getName() );

        final Configuration[] children = conf.getChildren( "elements-b" );
        assertEquals( "type-b", children[1].getAttribute( "type" ) );
    }
}






