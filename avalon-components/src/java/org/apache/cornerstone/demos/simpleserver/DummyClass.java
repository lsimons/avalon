/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.simpleserver;

import java.io.Serializable;

/**
 * This is a dummy class to test Store. If this is in a seperate bar
 * from the masterstore classes, reading it from disc will throw a
 * ClassNotFoundException.
 *
 * @author Charles Benett <charles@benett1.demon.co.uk>
 */
public class DummyClass
    implements Serializable
{
    private String    m_name;

    public void setName( final String name )
    {
        m_name = name;
    }

    public String getName()
    {
        return m_name;
    }
}



