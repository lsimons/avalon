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
public class ArgumentsElementHandler 
    extends AbstractElementHandler
{
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

    public String getNamespaceURI()
    {
        return "arguments";
    }

    public String getLocalName()
    {
        return "arguments";
    }
}
