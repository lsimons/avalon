/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache;

/**
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public abstract class AbstractCacheStore
    implements CacheStore
{
    public boolean isFull()
    {
        return size() >= capacity();
    }
}
