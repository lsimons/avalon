/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache;

import java.util.LinkedList;

/**
 * FIXME: Remove or determine good name.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public abstract class ListCachePolicy
    implements ReplacementPolicy
{
    protected LinkedList m_keyList;

    public ListCachePolicy()
    {
        m_keyList = new LinkedList();
    }

    public void add( final Object key )
    {
        m_keyList.addFirst( key );
    }

    public void remove( final Object key )
    {
        m_keyList.remove( key );
    }

    public Object selectVictim()
    {
        return m_keyList.removeLast();
    }
}
