/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.collections;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Enumeration wrapper for iterator.
 *
 * @deprecated use org.apache.commons.collections.IteratorEnumeration instead
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/03/22 12:31:43 $
 * @since 4.0
 */
public final class IteratorEnumeration
    implements Enumeration
{
    protected Iterator m_iterator;

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

