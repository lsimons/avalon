/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.xcommander.saxhandlers;

import org.apache.cornerstone.demos.xcommander.*;
import org.xml.sax.Attributes;

/**
 *
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class StringElementHandler 
    extends AbstractElementHandler 
    implements TypeElementHandler
{
    private String value;

    public void addChild( final ElementHandler elementHandler ) 
        throws IllegalArgumentException
    {
        throw new IllegalArgumentException();
    }

    public void end( final String contents )
    {
        value = new String( contents );
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
        return "string";
    }

    public String getLocalName()
    {
        return "string";
    }
}
