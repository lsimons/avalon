/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache;

/**
 * Validate cached object.
 *
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public interface CacheValidator
{
    /**
     * Check validity of cached item.
     *
     * @return true if cached item is valid
     */
    boolean validate( Object key, Object value );
}
