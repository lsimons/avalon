/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.configuration;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAXConfigurationHandler helps build Configurations out of sax events.
 *
 * @author <a href="mailto:fede@apache.org">Federico Barbieri</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 */
public class SAXConfigurationHandler
    extends DefaultHandler
    implements ErrorHandler
{
    private final ArrayList              m_elements        = new ArrayList();
    private Configuration                m_configuration;
    private Locator                      m_locator;

    /**
     * Get the configuration object that was built.
     *
     * @return a <code>Configuration</code> object
     */
    public Configuration getConfiguration()
    {
        return m_configuration;
    }

    /**
     * Clears all data from this configuration handler.
     */
    public void clear()
    {
        m_elements.clear();
        m_locator = null;
    }

    /**
     * Set the document <code>Locator</code> to use.
     *
     * @param locator a <code>Locator</code> value
     */
    public void setDocumentLocator( final Locator locator )
    {
        m_locator = locator;
    }

    /**
     * Handling hook for character data.
     *
     * @param ch a <code>char[]</code> of data
     * @param start offset in the character array from which to start reading
     * @param end length of character data
     * @exception SAXException if an error occurs
     */
    public void characters( final char[] ch, int start, int end )
        throws SAXException
    {
        String value = new String( ch, start, end );

        if( value.equals( "" ) )
        {
            return;
        }

        final DefaultConfiguration configuration =
            (DefaultConfiguration)m_elements.get( m_elements.size() - 1 );

        value = configuration.getValue( "" ) + value;
        configuration.setValue( value );
    }

    /**
     * Handling hook for finishing parsing of an element.
     *
     * @param namespaceURI a <code>String</code> value
     * @param localName a <code>String</code> value
     * @param rawName a <code>String</code> value
     * @exception SAXException if an error occurs
     */
    public void endElement( final String namespaceURI,
                            final String localName,
                            final String rawName )
        throws SAXException
    {
        final int location = m_elements.size() - 1;
        final DefaultConfiguration object = (DefaultConfiguration) m_elements.remove( location );

        // Check for validity
        if ( object.getValue( null ) != null && object.getChildCount() > 0 ) 
        {
            // Could be invalid - the configuration has children and a value.
            // It is valid, however, to have a value consisting of just whitespace.
            // So let's trim the value, and see if we can resolve the conflict that way.
            if ( object.getValue("").trim().equals("") )
            {
                // Resolved!
                object.setValue( null );
            }
            else 
            {
                throw new SAXException( "Not allowed to define mixed content in the " +
                                        "element " + object.getName() + " at " +
                                        object.getLocation() );
            }
        }
        
        if( 0 == location )
        {
            m_configuration = object;
            final String value = m_configuration.getValue( null );
            if( null != value )
            {
                ((DefaultConfiguration)m_configuration).setValue( value.trim() );
            }
        }
    }

    /**
     * Create a new <code>DefaultConfiguration</code> with the specified
     * local name and location.
     *
     * @param localName a <code>String</code> value
     * @param location a <code>String</code> value
     * @return a <code>DefaultConfiguration</code> value
     */
    protected DefaultConfiguration createConfiguration( final String localName,
                                                        final String location )
    {
        return new DefaultConfiguration( localName, location );
    }

    /**
     * Handling hook for starting parsing of an element.
     *
     * @param namespaceURI a <code>String</code> value
     * @param localName a <code>String</code> value
     * @param rawName a <code>String</code> value
     * @param attributes an <code>Attributes</code> value
     * @exception SAXException if an error occurs
     */
    public void startElement( final String namespaceURI,
                              final String localName,
                              final String rawName,
                              final Attributes attributes )
        throws SAXException
    {
        final DefaultConfiguration configuration =
            createConfiguration( rawName, getLocationString() );
        final int size = m_elements.size() - 1;

        if( size > -1 )
        {
            final DefaultConfiguration parent =
                (DefaultConfiguration)m_elements.get( size );

            parent.addChild( configuration );
        }

        m_elements.add( configuration );

        final int attributesSize = attributes.getLength();

        for( int i = 0; i < attributesSize; i++ )
        {
            final String name = attributes.getQName( i );
            final String value = attributes.getValue( i );
            configuration.setAttribute( name, value );
        }
    }

    /**
     * This just throws an exception on a parse error.
     * @param exception the parse error
     * @exception SAXException if an error occurs
     */
    public void error( final SAXParseException exception )
        throws SAXException
    {
        throw exception;
    }

    /**
     * This just throws an exception on a parse error.
     * @param exception the parse error
     * @exception SAXException if an error occurs
     */
    public void warning( final SAXParseException exception )
        throws SAXException
    {
        throw exception;
    }

    /**
     * This just throws an exception on a parse error.
     * @param exception the parse error
     * @exception SAXException if an error occurs
     */
    public void fatalError( final SAXParseException exception )
        throws SAXException
    {
        throw exception;
    }

    /**
     * Returns a string showing the current system ID, line number and column number.
     *
     * @return a <code>String</code> value
     */
    protected String getLocationString()
    {
        if( null == m_locator )
        {
            return "Unknown";
        }
        else
        {
            return
                m_locator.getSystemId() + ":" +
                m_locator.getLineNumber() + ":" +
                m_locator.getColumnNumber();
        }
    }
}
