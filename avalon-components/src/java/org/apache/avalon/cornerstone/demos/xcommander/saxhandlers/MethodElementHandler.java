/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.demos.xcommander.saxhandlers;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.avalon.cornerstone.demos.xcommander.*;
import org.xml.sax.Attributes;

/**
 *
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class MethodElementHandler 
    extends AbstractElementHandler
{
    protected ArgumentsElementHandler aeh;
    protected String name;

    public void addChild( final ElementHandler elementHandler ) 
        throws IllegalArgumentException
    {
        if( elementHandler instanceof ArgumentsElementHandler )
        {
            aeh = (ArgumentsElementHandler)elementHandler;
        } else
        {
            throw new IllegalArgumentException();
        }
    }

    public void start( final Attributes attributes )
    {
        name = attributes.getValue( "", "name" );
    }

    public String getName()
    {
        return name;
    }

    public Class[] getArgumentTypes()
    {
        if ( aeh != null )
        {
            ArrayList types = new ArrayList();
            Iterator it = aeh.getChildren().iterator();
            while( it.hasNext() )
            {
                TypeElementHandler teh = (TypeElementHandler)it.next();
                types.add(teh.getTypeClass());
            }
            return (Class[])types.toArray(new Class[0]);
        }
        else
        {
            return new Class[0];
        }
    }

    public Object[] getArguments()
    {
        if ( aeh != null )
        {
            ArrayList values = new ArrayList();
            Iterator it = aeh.getChildren().iterator();
            while( it.hasNext() )
            {
                TypeElementHandler teh = (TypeElementHandler)it.next();
                values.add(teh.getTypeValue());
            }
            return values.toArray();
        } 
        else
        {
            return new Object[0];
        }
    }

    public String getNamespaceURI()
    {
        return "method";
    }

    public String getLocalName()
    {
        return "method";
    }
}
