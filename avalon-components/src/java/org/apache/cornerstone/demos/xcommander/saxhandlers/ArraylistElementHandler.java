/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.xcommander.saxhandlers;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.cornerstone.demos.xcommander.*;
import org.xml.sax.Attributes;

/**
 *
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class ArraylistElementHandler 
    extends AbstractElementHandler 
    implements TypeElementHandler
{
    private ArrayList values = new ArrayList();

    public void addChild( final ElementHandler elementHandler ) 
        throws IllegalArgumentException
    {
        if( elementHandler instanceof TypeElementHandler )
        {
            m_children.add( elementHandler );
        } 
        else
        {
            throw new IllegalArgumentException();
        }
    }

    public void end( final String contents )
    {
        Iterator it = m_children.iterator();
        while ( it.hasNext() )
        {
            TypeElementHandler typeElementHandler = (TypeElementHandler)it.next();
            values.add( typeElementHandler.getTypeValue() );
        }
    }

    public Class getTypeClass()
    {
        return values.getClass();
    }

    public Object getTypeValue()
    {
        return values;
    }

    public String getNamespaceURI()
    {
        return "arraylist";
    }

    public String getLocalName()
    {
        return "arraylist";
    }
}
