/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.cpbuilder.metadata;

/**
 * This class defines a specific URL to add to a ClassLoader.
 * It uses the same resolution semantics as the
 * {@link java.security.CodeSource} class. In other words,
 * locations that end with a '/' character are considered to
 * be a directory in which .class files are stored. All other
 * locations are considered to be jar files.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/09/01 00:43:54 $
 */
public class EntryDef
{
    private final String m_location;

    /**
     * Construct a entry with specified location.
     *
     * @param location the location (must not be null)
     */
    public EntryDef( final String location )
    {
        if( null == location )
        {
            throw new NullPointerException( "location" );
        }
        m_location = location;
    }

    /**
     * Return the location associated with Entry.
     *
     * @return  the location associated with Entry.
     */
    public String getLocation()
    {
        return m_location;
    }
}
