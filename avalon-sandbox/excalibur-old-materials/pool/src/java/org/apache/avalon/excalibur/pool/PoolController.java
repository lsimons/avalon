/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.pool;

/**
 * This is the interface you implement if you want to control how Pools capacity
 * changes overtime.
 *
 * It gets called everytime that a Pool tries to go below or above it's minimum or maximum.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 05:09:04 $
 * @since 4.0
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
