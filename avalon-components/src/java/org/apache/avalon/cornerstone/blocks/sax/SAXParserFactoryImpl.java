/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.blocks.sax;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.Block;
import org.apache.avalon.cornerstone.services.sax.SAXParserFactory;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.parsers.ParserConfigurationException;


/**
 * Block implementation of the SAXParserFactory service.  That service being
 * a non abstract/static clone of the javax.xml.parsers class of the same name.
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
public class SAXParserFactoryImpl
    extends AbstractLogEnabled
    implements Block, Configurable, SAXParserFactory
{
    protected javax.xml.parsers.SAXParserFactory m_saxParserFactory;

    public void dispose()
    {
        m_saxParserFactory = null;
    }

    public void configure( Configuration configuration )
        throws ConfigurationException
    {

        // org.apache.crimson.jaxp.SAXParserFactoryImpl is an example of a
        // string that's valid (classpath considered) as a parameter on config.xml

        final String saxClass =
            configuration.getChild("saxClass").getValue();
        try 
        {
            m_saxParserFactory =
                (javax.xml.parsers.SAXParserFactory)Class.forName(saxClass).newInstance();
        } 
        catch( final ClassNotFoundException cnfe )
        {
            throw new ConfigurationException( "ClassNotFoundException for SAX " +
                                              "parser factory",
                                              cnfe );
        } 
        catch( final InstantiationException ie )
        {
            throw new ConfigurationException( "InstantiationException for SAX " +
                                              "parser factory",
                                              ie );
        } 
        catch( final IllegalAccessException ie )
        {
            throw new ConfigurationException( "IllegalAccessException for SAX " +
                                              "parser factory",
                                              ie );
        }
    }

    public void setNamespaceAware(boolean awareness) {
        m_saxParserFactory.setNamespaceAware(awareness);
    }

    public void setValidating(boolean validating) {
        m_saxParserFactory.setValidating(validating);
    }

    public boolean isNamespaceAware() {
        return m_saxParserFactory.isNamespaceAware();
    }

    public boolean isValidating() {
        return m_saxParserFactory.isValidating();
    }

    public void setFeature(String name, boolean value)
            throws ParserConfigurationException,
            SAXNotRecognizedException, SAXNotSupportedException {
        m_saxParserFactory.setFeature(name,value);
    }

    public boolean getFeature(String name)
            throws ParserConfigurationException, SAXNotRecognizedException,
            SAXNotSupportedException {
        return m_saxParserFactory.getFeature(name);
    }
}

