/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.configuration;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Utility class used to load Configuration trees from XML files.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2002/05/20 11:40:30 $
 */
public class ConfigurationBuilder
{
    private static final DTDInfo[] c_dtdInfo = new DTDInfo[]
    {
        new DTDInfo( "-//PHOENIX/Block Info DTD Version 1.0//EN",
                     "http://jakarta.apache.org/phoenix/blockinfo_1_0.dtd",
                     "org/apache/avalon/phoenix/tools/blockinfo.dtd" ),
        new DTDInfo( "-//PHOENIX/Assembly DTD Version 1.0//EN",
                     "http://jakarta.apache.org/phoenix/assembly_1_0.dtd",
                     "org/apache/avalon/phoenix/tools/assembly.dtd" )
    };

    private static final DTDResolver c_resolver =
        new DTDResolver( c_dtdInfo, ConfigurationBuilder.class.getClassLoader() );

    /**
     * Private constructor to block instantiation.
     */
    private ConfigurationBuilder()
    {
    }

    /**
     * Utility method to create a new XML reader.
     */
    private static XMLReader createXMLReader()
        throws SAXException, ParserConfigurationException
    {
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware( false );
        final SAXParser saxParser = saxParserFactory.newSAXParser();
        return saxParser.getXMLReader();
    }

    /**
     * Internally sets up the XMLReader
     */
    private static void setupXMLReader( final XMLReader reader,
                                        final SAXConfigurationHandler handler )
    {
        reader.setEntityResolver( c_resolver );
        reader.setContentHandler( handler );
        reader.setErrorHandler( handler );
    }

    /**
     * Build a configuration object using an URI
     */
    public static Configuration build( final String uri )
        throws SAXException, ParserConfigurationException, IOException, ConfigurationException
    {
        return build( new InputSource( uri ) );
    }

    /**
     * Build a configuration object using an XML InputSource object
     */
    public static Configuration build( final InputSource input )
        throws SAXException, ParserConfigurationException, IOException, ConfigurationException
    {
        final XMLReader reader = createXMLReader();
        final SAXConfigurationHandler handler = new SAXConfigurationHandler();
        setupXMLReader( reader, handler );
        reader.parse( input );
        return handler.getConfiguration();
    }
}
