package org.apache.excalibur.xml.dom.test;

import java.io.StringReader;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;
import org.apache.avalon.excalibur.xml.Parser;
import org.apache.excalibur.xml.dom.DOMHandler;
import org.apache.excalibur.xml.dom.DOMHandlerFactory;

public class DefaultDOMHandlerFactoryTestCase extends ExcaliburTestCase
{
    
    private static final String CONTENT = 
        "<?xml version=\"1.0\"?>" + 
        "<test:root xmlns:test=\"http://localhost/test\">" +
            "<test:element1/>" +
            "<test:element2/>" +
        "</test:root>";
    private static final StringReader IN = new StringReader( CONTENT );
    
    public DefaultDOMHandlerFactoryTestCase( String name )
    {
        super( name );
    }
    
    public void testCreateDOMHandler()
    {
        try 
        {
            final Parser parser = (Parser)manager.lookup( Parser.ROLE );
            final DOMHandlerFactory handlerFactory = (DOMHandlerFactory)manager.lookup( DOMHandlerFactory.ROLE );        

            final DOMHandler handler = handlerFactory.createDOMHandler();
            parser.parse( new InputSource( IN ), handler );            
            final Document document = handler.getDocument();
            
            final Element root = document.getDocumentElement();
            this.assertEquals( "Wrong root element", "test:root", root.getNodeName() );
            this.assertEquals( "Wrong namespace uri", "http://localhost/test", root.getNamespaceURI() );
            
            final Node element1 = root.getFirstChild();
            this.assertEquals( "Child is not an element", Document.ELEMENT_NODE, element1.getNodeType() );
            this.assertEquals( "Wrong first element", "test:element1", element1.getNodeName() );
            
            final Node element2 = root.getLastChild();
            this.assertEquals( "Child is not an element", Document.ELEMENT_NODE, element2.getNodeType() );
            this.assertEquals( "Wrong last element", "test:element2", element2.getNodeName() );                        
        }
        catch ( ComponentException e )
        {
            this.fail( "Failed to lookup components: " + e.getMessage() );
        }
        catch ( Exception e )
        {
            this.fail( "Failed to create handler: " + e.getMessage() );
        }
    }
    
}
