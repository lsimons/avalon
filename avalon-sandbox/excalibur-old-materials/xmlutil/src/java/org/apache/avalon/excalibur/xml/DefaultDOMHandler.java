/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.TransformerHandler;

/**
 * @author <a href="mailto:mirceatoma@apache.org">Mircea Toma</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/06 18:16:53 $
 */
public class DefaultDOMHandler 
    extends ContentHandlerWrapper
    implements DOMHandler
{    
    private final DOMResult m_result;
    private final boolean m_ignoreWhitespaces;
    private final boolean m_ignoreComments;
    
    public DefaultDOMHandler( TransformerHandler handler, DOMResult result , boolean ignoreComments, boolean ignoreWhitespaces )
    {
        super( handler, handler );
        m_result = result;
        m_ignoreComments = ignoreComments;
        m_ignoreWhitespaces = ignoreWhitespaces;
    }
    
    public Document getDocument()
    {
        return (Document)m_result.getNode();
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
