/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.demos.xcommander.xcommands;

import org.apache.avalon.cornerstone.demos.xcommander.XCommand;

/**
 * This simply returns the string passed to it in the constructor.
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class Echo 
    implements XCommand
{
    private String m_string;

    public Echo()
    {
        this( "Echo!" );
    }

    public Echo( Object o )
    {
        m_string = (String)o;
    }

    public Echo( String s )
    {
        m_string = s;
    }

    public String toString()
    {
        return "<string>" + m_string + "</string>";
    }

    public String toXml()
    {
        return this.toString();
    }
}
