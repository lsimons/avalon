/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.dom;

import org.apache.avalon.cornerstone.services.dom.DocumentBuilderFactory;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.Block;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Block implementation of the DocumentBuilderFactory service.  That service being
 * a non abstract/static clone of the javax.xml.parsers class of the same name.
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
public class DOMBuilderFactory 
    extends AbstractLogEnabled
    implements Block, Configurable, DocumentBuilderFactory
{
    protected javax.xml.parsers.DocumentBuilderFactory m_documentBuilderFactory;

    public void dispose()
    {
        m_documentBuilderFactory = null;
    }

    public void configure( Configuration configuration )
        throws ConfigurationException
    {

        // org.apache.crimson.jaxp.DocumentBuilderFactoryImpl is an example of a
        // string that's valid (classpath considered) as a parameter on config.xml
        // org.apache.xerces.jaxp.DocumentBuilderFactoryImpl is also valid

        final String domClass =
            configuration.getChild("domClass").getValue();
        try 
        {
            m_documentBuilderFactory =
                (javax.xml.parsers.DocumentBuilderFactory)Class.forName(domClass).newInstance();
        } 
        catch( final ClassNotFoundException cnfe )
        {
            throw new ConfigurationException( "ClassNotFoundException for DOM " + 
                                              "builder factory",
                                              cnfe );
        } 
        catch( final InstantiationException ie )
        {
            throw new ConfigurationException( "InstantiationException for DOM " + 
                                              "builder factory",
                                              ie );
        } 
        catch( final IllegalAccessException ie )
        {
            throw new ConfigurationException( "IllegalAccessException for DOM " + 
                                              "builder factory",
                                              ie );
        }
    }

    public DocumentBuilder newDocumentBuilder()
        throws ParserConfigurationException {
        return m_documentBuilderFactory.newDocumentBuilder();
    }

    public void setNamespaceAware(boolean awareness)
    {
        m_documentBuilderFactory.setNamespaceAware(awareness);
    }

    public void setValidating(boolean validating)
    {
        m_documentBuilderFactory.setValidating(validating);
    }

    public void setIgnoringElementContentWhitespace(boolean whitespace)
    {
        m_documentBuilderFactory.setIgnoringElementContentWhitespace(whitespace);
    }

    public void setExpandEntityReferences(boolean expandEntityRef)
    {
        m_documentBuilderFactory.setExpandEntityReferences(expandEntityRef);
    }

    public void setIgnoringComments(boolean ignoreComments)
    {
        m_documentBuilderFactory.setIgnoringComments(ignoreComments);
    }

    public void setCoalescing(boolean coalescing)
    {
        m_documentBuilderFactory.setCoalescing(coalescing);
    }

    public boolean isNamespaceAware()
    {
        return m_documentBuilderFactory.isNamespaceAware();
    }

    public boolean isValidating()
    {
        return m_documentBuilderFactory.isValidating();
    }

    public boolean isIgnoringElementContentWhitespace()
    {
        return m_documentBuilderFactory.isIgnoringElementContentWhitespace();
    }

    public boolean isExpandEntityReferences()
    {
        return m_documentBuilderFactory.isExpandEntityReferences();
    }

    public boolean isIgnoringComments()
    {
        return m_documentBuilderFactory.isIgnoringComments();
    }

    public boolean isCoalescing()
    {
        return m_documentBuilderFactory.isCoalescing();
    }

    public void setAttribute(String name, Object value)
        throws IllegalArgumentException
    {
        m_documentBuilderFactory.setAttribute(name,value);
    }

    public Object getAttribute(String name)
        throws IllegalArgumentException
    {
        return m_documentBuilderFactory.getAttribute(name);
    }
}

