/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.dom;

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.component.Component;

public class DefaultDOMSerializer 
    extends AbstractLogEnabled 
    implements DOMSerializer, Component
{    
    private final TransformerFactory m_factory = TransformerFactory.newInstance();
        
    public void serialize( Document document, ContentHandler contentHandler, LexicalHandler lexicalHandler ) throws SAXException 
    {        
        try 
        {
            final Transformer transformer = m_factory.newTransformer();
            final DOMSource source = new DOMSource( document );
            final SAXResult result = new SAXResult( contentHandler );
            result.setLexicalHandler( lexicalHandler );

            transformer.transform( source, result );
        }
        catch ( TransformerConfigurationException e ) 
        {
            getLogger().error( "Cannot create transformer", e );
            throw new SAXException( e );
        }
        catch ( TransformerException e )
        {
            getLogger().error( "Cannot serialize document", e );
            throw new SAXException( e );
        }
    }    
}
