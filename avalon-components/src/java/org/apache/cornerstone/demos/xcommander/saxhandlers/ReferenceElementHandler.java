/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.xcommander.saxhandlers;

import org.xml.sax.Attributes;

import org.apache.cornerstone.demos.xcommander.*;

/**
 *
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class ReferenceElementHandler 
    extends AbstractElementHandler 
    implements TypeElementHandler 
{
    private Object value = null;

    public void addChild( ElementHandler eh ) 
        throws IllegalArgumentException
    {
            throw new IllegalArgumentException();
    }

    public void end( String contents )
    {
        ClassLoader cl = getClass().getClassLoader();
        try
        {
            value = cl.loadClass( contents );
        } 
        catch( final ClassNotFoundException cnfe )
        {
            value = cl.getResource( contents );
        }
    }

    public Class getTypeClass()
    {
        return value.getClass();
    }

    public Object getTypeValue()
    {
        return value;
    }

    public String getNamespaceURI()
    {
        return "reference";
    }

    public String getLocalName()
    {
        return "reference";
    }
}
