/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.demos.xcommander.saxhandlers;

import java.util.ArrayList;
import org.apache.avalon.cornerstone.demos.xcommander.*;
import org.xml.sax.Attributes;

/**
 * Provides default implementations of the functions in ElementHandler.
 * Define the start() and end() methods to create basic functionality.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public abstract class AbstractElementHandler 
    implements ElementHandler 
{
    protected ArrayList m_children = new ArrayList();

    public void start( final Attributes attributes )
    {
    }

    public void end( final String contents )
    {
    }

    /** Override this method if you wish to be able to refuse the addition
     *  of an ElementHandler. */
    public void addChild( final ElementHandler elementHandler ) 
        throws IllegalArgumentException
    {
        m_children.add( elementHandler );
    }

    public void addEmptyChild( final EmptyElementHandler emptyElementHandler )
    {
        m_children.add( emptyElementHandler );
    }

    public boolean removeChild( final ElementHandler elementHandler )
    {
        if( m_children.contains( elementHandler ) )
        {
            return m_children.remove( elementHandler );
        }
        return false;
    }

    public ArrayList getChildren()
    {
        return m_children;
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
            return equalTypes( (ElementHandler)object );
        return false;
    }

    public String getNamespaceURI()
    {
        return "default";
    }

    public String getLocalName()
    {
        return "default";
    }
}
