/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A SAXConfigurationBuilder builds configurations via SAX2 compliant parser.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultConfigurationBuilder
    implements ConfigurationBuilder
{
    protected final static String                 DEFAULT_PARSER =
        "org.apache.xerces.parsers.SAXParser";
    protected final static String                 PARSER =
        System.getProperty("org.xml.sax.parser", DEFAULT_PARSER );

    protected SAXConfigurationHandler             m_handler;
    protected XMLReader                           m_parser;

    public DefaultConfigurationBuilder()
    {
        this( PARSER );
    }

    public DefaultConfigurationBuilder( final String parserClass )
    {
        //yaya the bugs with some compilers and final variables ..
        m_handler = getHandler();
        try
        {
            m_parser = XMLReaderFactory.createXMLReader( parserClass );
            //m_parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            m_parser.setContentHandler( m_handler );
            m_parser.setErrorHandler( m_handler );
        }
        catch( final SAXException se )
        {
            throw new Error( "Unable to setup SAX parser" + se );
        }
    }

    protected SAXConfigurationHandler getHandler()
    {
        return new SAXConfigurationHandler();
    }

    public Configuration build( final String resource )
        throws SAXException, IOException, ConfigurationException
    {
        final InputStream input = new FileInputStream( resource );

        try { return build( input ); }
        finally
        {
            try { input.close(); }
            catch( final IOException ioe ) {}
        }
    }

    public Configuration build( final InputStream inputStream )
        throws SAXException, IOException, ConfigurationException
    {
        final InputSource inputSource = new InputSource( inputStream );
        return build( inputSource );
    }

    public Configuration build( final InputSource input )
        throws SAXException, IOException, ConfigurationException
    {
        m_handler.clear();
        m_parser.parse( input );
        return m_handler.getConfiguration();
    }
}
