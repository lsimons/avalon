/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.cache;

import java.util.EventListener;

/**
 * @author <a href="mailto:colus@isoft.co.kr">Eung-ju Park</a>
 */
public interface CacheListener
    extends EventListener
{
    /**
     * Added new item.
     */
    void added( CacheEvent event );

    /**
     * Removed item.
     */
    void removed( CacheEvent event );
}
