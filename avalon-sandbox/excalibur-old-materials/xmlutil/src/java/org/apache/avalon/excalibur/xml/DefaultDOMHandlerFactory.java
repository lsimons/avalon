/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.dom.DOMResult;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/06 18:16:53 $
 */
public class DefaultDOMHandlerFactory extends AbstractLogEnabled implements DOMHandlerFactory, Configurable, Initializable, Component, ThreadSafe
{   
    private final SAXTransformerFactory m_transformerFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
    private final DocumentBuilderFactory m_documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder m_documentBuilder;
    private boolean m_ignoreWhitespaces;
    private boolean m_ignoreComments;

    public void configure( Configuration configuration ) throws ConfigurationException
    {
        m_ignoreWhitespaces = configuration.getChild( "ignore-whitespaces", true ).getValueAsBoolean( false );
        m_ignoreComments = configuration.getChild( "ignore-comments", true ).getValueAsBoolean( false );
    }

    public void initialize() throws Exception    
    {
        m_documentBuilder = m_documentBuilderFactory.newDocumentBuilder();
    }
    
    public DOMHandler createDOMHandler() throws Exception
    {
        final Document document = m_documentBuilder.newDocument();
        final DOMResult result = new DOMResult( document );
        final TransformerHandler transformerHandler = m_transformerFactory.newTransformerHandler();
        transformerHandler.setResult( result );
        
        return new DefaultDOMHandler( transformerHandler, result, m_ignoreComments, m_ignoreWhitespaces );
    }        
        
}
