package org.apache.avalon.excalibur.xml;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.TransformerHandler;

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
