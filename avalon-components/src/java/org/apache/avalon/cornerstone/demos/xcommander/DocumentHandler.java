/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.demos.xcommander;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.avalon.cornerstone.demos.xcommander.saxhandlers.CommandElementHandler;
import org.apache.avalon.cornerstone.demos.xcommander.saxhandlers.EmptyElementHandler;
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
 * This is not a real element handler, but rather it represents the start of
 * an xml document.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class DocumentHandler 
    implements ElementHandler
{
    protected ArrayList children = new ArrayList();

    /** does not do anything */
    public void start( final Attributes attributes )
    {
    }

    /** does not do anything */
    public void end( final String contents )
    {
    }

    /** a DocumentHandler takes CommandElementHandlers only. */
    public void addChild( final ElementHandler elementHandler ) 
        throws IllegalArgumentException
    {
        if( elementHandler instanceof CommandElementHandler )
        {
            children.add( elementHandler );
        } 
        else
        {
            throw new IllegalArgumentException();
        }
    }

    public void addEmptyChild( final EmptyElementHandler emptyElementHandler )
    {
        children.add( emptyElementHandler );
    }

    public boolean removeChild( ElementHandler elementHandler )
    {
        if( children.contains( elementHandler ) )
        {
            return children.remove( elementHandler );
        }
        return false;
    }

    public ArrayList getChildren()
    {
        return children;
    }

    /** This method checks whether two elements are of an equal type. It
     *  checks for similar namespaceURIs and localNames, and also
     *  if the type attributes (when available) are equal. */
    public boolean equalTypes( final ElementHandler elementHandler )
    {
        if((elementHandler.getNamespaceURI() == this.getNamespaceURI()) &&
           (elementHandler.getLocalName() == this.getLocalName())) 
        {
            return true;
        }
        return false;
    }

    /** calls equalTypes */
    public boolean equals( final Object object )
    {
        if( object instanceof ElementHandler )
        {
            return equalTypes( (ElementHandler)object );
        }
        return false;
    }

    /** @return the string "DocumentHandler" */
    public String getNamespaceURI()
    {
        return "DocumentHandler";
    }

    /** @return the string "DocumentHandler" */
    public String getLocalName()
    {
        return "DocumentHandler";
    }

    /** Calls {@link org.apache.avalon.cornerstone.demos.xcommander.saxhandlers.CommandElementHandler#executeCommand(CommandHandler) executeCommand()} on every CommandElementHandler.
     *
     * @param ch The CommandHandler which is to receive the results of this document's XCommands.
     */
    public void executeCommand( final CommandHandler commandHandler ) 
        throws SAXException
    {
        final Iterator it = children.iterator();
        while( it.hasNext() )
        {
            final ElementHandler elementHandler = (ElementHandler)it.next();
            if( elementHandler instanceof CommandElementHandler )
            {
                final CommandElementHandler commandElementHandler = 
                    (CommandElementHandler)elementHandler;
                commandElementHandler.executeCommand( commandHandler );
            }
        }
    }
}
