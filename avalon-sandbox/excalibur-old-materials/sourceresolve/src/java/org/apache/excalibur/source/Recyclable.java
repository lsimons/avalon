/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.source;


/**
 * This interface standardizes the behaviour of a recyclable object.
 * A recyclable object is defined as an object that can be used to
 * encapsulate another object without being altered by its content.
 * Therefore, a recyclable object may be recycled and reused many times.
 *
 * This is helpful in cases where recyclable objects are continously
 * created and destroyed, causing a much greater amount of garbage to
 * be collected by the JVM garbage collector. By making it recyclable,
 * it is possible to reduce the GC execution time, thus incrementing the
 * overall performance of a process and decrementing the chance of
 * memory overflow.
 *
 * Every implementation must provide their own method to allow this
 * recyclable object to be reused by setting its content.
 *
 * @author <a href="mailto:stefano@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/09 05:30:02 $
 * @since 4.0
 */
public interface Recyclable
{
    /**
     * This method should be implemented to reset the object to its "ready"
     * state.  That means that all JDBC connectinos have been properly taken
     * care of, any threads that are spawned during use need to be reclaimed,
     * etc.
     */
    void recycle();
}
