/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.dom;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/09/03 20:29:53 $
 */
public class DefaultDOMHandlerFactory
    extends AbstractLogEnabled
    implements DOMHandlerFactory, Initializable, Component, ThreadSafe
{
    private final SAXTransformerFactory m_transformerFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
    private final DocumentBuilderFactory m_documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder m_documentBuilder;

    public void initialize() throws Exception
    {
        m_documentBuilder = m_documentBuilderFactory.newDocumentBuilder();
    }

    public DOMHandler createDOMHandler() throws Exception
    {
        final Document document = m_documentBuilder.newDocument();
        return createDOMHandler(document);
    }
    
    public DOMHandler createDOMHandler(Document document) throws Exception {
        final TransformerHandler transformerHandler = m_transformerFactory.newTransformerHandler();
        return new DefaultDOMHandler( transformerHandler, document );
    }    
}
