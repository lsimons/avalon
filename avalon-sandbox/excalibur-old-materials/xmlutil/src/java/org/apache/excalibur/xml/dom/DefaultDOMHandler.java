/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.dom;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.avalon.excalibur.xml.ContentHandlerWrapper;

/**
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/08/16 10:42:36 $
 */
public class DefaultDOMHandler 
    extends ContentHandlerWrapper
    implements DOMHandler
{    
    private final Document m_document;
    private final boolean m_ignoreWhitespaces;
    private final boolean m_ignoreComments;
    
    public DefaultDOMHandler( TransformerHandler handler, Document document, boolean ignoreComments, boolean ignoreWhitespaces )
    {
        super( handler, handler );
        m_document = document;
        m_ignoreComments = ignoreComments;
        m_ignoreWhitespaces = ignoreWhitespaces;
        
        handler.setResult( new DOMResult( m_document ) );
    }
    
    public Document getDocument()
    {
        return m_document;
    }
    
    public void ignorableWhitespace( final char[] ch, final int start, final int len )
    throws SAXException
    {
        if ( !m_ignoreWhitespaces ) super.ignorableWhitespace( ch, start, len );        
    }
    
    public void comment( final char[] ch, final int start, final int len )
    throws SAXException
    {
        if ( !m_ignoreComments ) super.comment( ch, start, len );
    }
}
