/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.collections;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Enumeration wrapper for iterator.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class IteratorEnumeration
    implements Enumeration
{
    protected Iterator       m_iterator;

    public IteratorEnumeration( final Iterator iterator )
    {
        m_iterator = iterator;
    }

    public boolean hasMoreElements()
    {
        return m_iterator.hasNext();
    }

    public Object nextElement()
    {
        return m_iterator.next();
    }
}

