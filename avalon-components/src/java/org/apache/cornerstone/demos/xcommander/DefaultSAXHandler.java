/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.xcommander;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.cornerstone.demos.xcommander.saxhandlers.AbstractElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.ArgumentsElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.ArraylistElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.BooleanElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.CommandElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.ConstructorElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.DoubleElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.EmptyElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.FloatElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.IntElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.MethodElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.ObjectElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.ReferenceElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.StringElementHandler;
import org.apache.cornerstone.demos.xcommander.saxhandlers.TypeElementHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <p>A SAX2 ContentHandler and ErrorHandler which parses an xml command
 * document and then executes it. DefaultSAXHandler returns the results of
 * the method called by the xml command document to it's parent by calling
 * the parent's handleCommand( Object o ) method.</p>
 * <p>A sample xml command can be found <a href="doc-files/sample-command.xml">here</a>.<br />
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class DefaultSAXHandler 
    extends DefaultHandler 
    implements ErrorHandler
{
    // stack object containing element handlers in use
    private ElementHandlerStack stack = new ElementHandlerStack();
    // list containing element handlers
    private ArrayList handlers = new ArrayList();
    // container used for character data
    private CharArrayWriter contents;

    // method called to provide results of xml command
    private CommandHandler parent;

    /**
     * @param ch The supplied CommandHandler will receive the results of all XCommands.
     */
    DefaultSAXHandler( CommandHandler ch ) 
    {
        parent = ch;

        contents = new CharArrayWriter();

        handlers.add( CommandElementHandler.class );
        handlers.add( ConstructorElementHandler.class );
        handlers.add( MethodElementHandler.class );
        handlers.add( ArgumentsElementHandler.class );

        handlers.add( ObjectElementHandler.class );
        handlers.add( ReferenceElementHandler.class );
        handlers.add( ArraylistElementHandler.class );
        handlers.add( StringElementHandler.class );
        handlers.add( IntElementHandler.class );
        handlers.add( BooleanElementHandler.class );
        handlers.add( FloatElementHandler.class );
        handlers.add( DoubleElementHandler.class );
    }

    // SAX2 ContentHandler Functions
    public void startDocument() 
        throws SAXException
    {
        // the root handler is created...
        stack.push( new DocumentHandler() );
    }

    public void endDocument() 
        throws SAXException
    {
        final DocumentHandler documentHandler = (DocumentHandler)stack.pop();
        documentHandler.executeCommand( parent );
    }

    public void startElement( final String namespaceURI, 
                              final String localName,
                              final String qName, 
                              final Attributes atts ) 
        throws SAXException
    {
        ElementHandler el = null;

        // try to find the correct handler class and create an instance from it...
        Iterator it = handlers.iterator();
        while( it.hasNext() )
        {
            Class currClass = (Class)it.next();
            Object currObj;
            try
            {
                currObj = currClass.newInstance();
            }
            catch ( Exception e )
            {
                throw new 
                    SAXException( "Could not instantiate a class in DefaultSAXHandler.handlers",
                                  e );
            }

            ElementHandler eh = (ElementHandler)currObj;
            if( eh.getLocalName().equals(localName) )
            {
                // we've got a correct ElementHandler!
                el = eh;
                break;
            }
        }

        if( null == el )
        {
            // couldn't find an element anywhere.....
            throw new SAXException( "No handler for xml element <" + localName + "> found." );
        }
        
        try
        {
            stack.peek().addChild( el );
            stack.push( el );
            el.start( atts );
        } 
        catch( final IllegalArgumentException iae )
        {
            // the parent will not accept elements of this type.
            // we will insert an 'empty' child, so further child
            // elements will be added to the empty child which
            // does nothing.
            EmptyElementHandler eeh = new EmptyElementHandler();
            stack.peek().addEmptyChild( eeh );
            stack.push( eeh );
            eeh.start( atts );
        }
    }

    public void endElement( final String namespaceURI, 
                            final String localName,
                            final String qName ) 
        throws SAXException
    {
        stack.peek().end( contents.toString() );
        contents.reset();
        stack.pop();
    }

    public void characters( final char[] ch, final int start, final int length ) 
        throws SAXException
    {
        contents.write( ch, start, length );
    }

    // SAX2 ErrorHandler Functions
    public void warning( final SAXParseException spe ) 
        throws SAXException
    {
    }

    public void error( final SAXParseException spe ) 
        throws SAXException
    {
    }

    public void fatalError( final SAXParseException spe ) 
        throws SAXException
    {
    }

    /** This is simply a safe wrapper for a LinkedList that implements a simple stack. */
    public class ElementHandlerStack 
    {
        private LinkedList m_list = new LinkedList();

        public boolean isEmpty()
        {
            return m_list.isEmpty();
        }

        public void push( final ElementHandler elementHandler )
        {
            m_list.addLast( elementHandler );
        }

        public ElementHandler pop()
        {
            return (ElementHandler)m_list.removeLast();
        }

        public ElementHandler peek()
        {
            return (ElementHandler)m_list.getLast();
        }
    }
}
