/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.pool;

/**
 * This interface standardizes the behaviour of a recyclable object.
 * A recyclable object is defined as an object that can be used to
 * encapsulate another object without being altered by its content.
 * Therefore, a recyclable object may be recycled and reused many times.
 *
 * This is helpful in cases where recyclable objects are continously
 * created and destroied, causing a much greater amount of garbage to
 * be collected by the JVM garbage collector. By making it recyclable,
 * it is possible to reduce the GC execution time thus incrementing the
 * overall performance of a process and decrementing the chance of
 * memory overflow.
 *
 * Every implementation must provide their own method to allow this
 * recyclable object to be reused by setting its content.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Recyclable
    extends Poolable
{
    /**
     * This method should be implemented to remove all costly resources
     * in object. These resources can be object references, database connections,
     * threads etc. What is categorised as "costly" resources is determined on
     * a case by case analysis.
     */
    void recycle();
}
