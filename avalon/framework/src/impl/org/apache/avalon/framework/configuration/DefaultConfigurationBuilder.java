/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * A SAXConfigurationBuilder builds configurations via SAX2 compliant parser.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultConfigurationBuilder
{
    private SAXConfigurationHandler               m_handler;
    private XMLReader                             m_parser;

    /**
     * Create a Configuration Builder with a default XMLReader.
     */
    public DefaultConfigurationBuilder()
    {
        //yaya the bugs with some compilers and final variables ..
        try
        {
            final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
//            saxParserFactory.setNamespaceAware(true);
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            this.setParser(saxParser.getXMLReader());
        }
        catch( final Exception se )
        {
            throw new Error( "Unable to setup SAX parser" + se );
        }
    }

    /**
     * Create a Configuration Builder with your own XMLReader.
     */
    public DefaultConfigurationBuilder( XMLReader parser )
    {
        this.setParser(parser);
    }

    /**
     * Internally sets up the XMLReader
     */
    private void setParser(XMLReader parser)
    {
        m_parser = parser;

        m_handler = getHandler();

        m_parser.setContentHandler( m_handler );
        m_parser.setErrorHandler( m_handler );
    }

    /**
     * Get a SAXConfigurationHandler for your configuration reading.
     */
    protected SAXConfigurationHandler getHandler()
    {
        return new SAXConfigurationHandler();
    }

    /**
     * Build a configuration object from a file using a filename.
     */
    public Configuration buildFromFile( final String filename )
        throws SAXException, IOException, ConfigurationException
    {
        return buildFromFile( new File( filename ) );
    }

    /**
     * Build a configuration object from a file using a File object.
     */
    public Configuration buildFromFile( final File file )
        throws SAXException, IOException, ConfigurationException
    {
        synchronized(this)
        {
            m_handler.clear();
            m_parser.parse( file.toURL().toString() );
            return m_handler.getConfiguration();
        }
    }

    /**
     * Build a configuration object using an InputStream.
     */
    public Configuration build( final InputStream inputStream )
        throws SAXException, IOException, ConfigurationException
    {
        return build( new InputSource( inputStream ) );
    }

    /**
     * Build a configuration object using an URI
     */
    public Configuration build( final String uri )
        throws SAXException, IOException, ConfigurationException
    {
        return build( new InputSource( uri ) );
    }

    /**
     * Build a configuration object using an XML InputSource object
     */
    public Configuration build( final InputSource input )
        throws SAXException, IOException, ConfigurationException
    {
        synchronized(this)
        {
            m_handler.clear();
            m_parser.parse( input );
            return m_handler.getConfiguration();
        }
    }
}
