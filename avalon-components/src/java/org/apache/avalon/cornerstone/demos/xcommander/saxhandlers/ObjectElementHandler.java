/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.demos.xcommander.saxhandlers;

import org.apache.avalon.cornerstone.demos.xcommander.*;
import org.xml.sax.Attributes;

/**
 *
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class ObjectElementHandler 
    extends AbstractElementHandler 
    implements TypeElementHandler
{
    private ConstructorElementHandler ceh;
    private Object obj;
    private String type;

    public void addChild( final ElementHandler elementHandler ) 
        throws IllegalArgumentException
    {
        if( elementHandler instanceof ConstructorElementHandler )
        {
            ceh = (ConstructorElementHandler)elementHandler;
        } 
        else
        {
            throw new IllegalArgumentException();
        }
    }

    public void start( final Attributes attributes )
    {
        type = attributes.getValue( "", "class" );
    }

    public void end( final String contents )
    {
        try
        {
            // try to find the specified class
            Class handlerClass = Class.forName( type );
            if( ceh != null )
            {
                // create an object from the settings supplied by ceh
                obj = handlerClass.getConstructor( ceh.getArgumentTypes() ).
                    newInstance( ceh.getArguments() );
            }
        } 
        catch( final Exception e )
        {
        }
    }

    public Class getTypeClass()
    {
        return obj.getClass();
    }

    public Object getTypeValue()
    {
        return obj;
    }

    public String getNamespaceURI()
    {
        return "object";
    }

    public String getLocalName()
    {
        return "object";
    }
}
