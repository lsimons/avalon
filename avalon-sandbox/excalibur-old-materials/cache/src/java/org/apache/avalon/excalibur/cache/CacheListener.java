/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache;

import java.util.EventListener;

/**
 * Receive notifications about <code>Cache</code>.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public interface CacheListener
    extends EventListener
{
    /**
     * Added new item.
     *
     * @param event the cache event
     */
    void added( CacheEvent event );

    /**
     * Removed item.
     *
     * @param event the cache event
     */
    void removed( CacheEvent event );
}
