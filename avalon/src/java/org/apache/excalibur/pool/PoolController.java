/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool;

/**
 * This is the interface you implement if you want to control how Pools capacity
 * changes overtime.
 *
 * It gets called everytime that a Pool tries to go below or above it's minimum or maximum.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface PoolController
{
    /**
     * Called when a Pool reaches it's minimum.
     *
     * Return the number of elements to increase minimum and maximum by.
     *
     * @return the element increase
     */
    int grow();

    /**
     * Called when a pool reaches it's maximum.
     *
     * Returns the number of elements to decrease mi and max by.
     *
     * @return the element decrease
     */
    int shrink();
}
