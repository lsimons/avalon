/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.collections;

/**
 * A Buffer is an ordered list of objects, that does not support querying or
 * direct access to the elements.  It is basically a First In/First Out (FIFO)
 * buffer.  It is useful in both pooling and queue implementation code among
 * other things.
 *
 * @deprecated use org.apache.commons.collections.Buffer instead
 *
 * @author  <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/03/11 13:29:16 $
 * @since 4.0
 */
public interface Buffer
{
    /**
     * Tests to see if the CircularBuffer is empty.
     */
    boolean isEmpty();

    /**
     * Returns the number of elements stored in the buffer.
     */
    int size();

    /**
     * Add an object into the buffer.
     *
     * @throws BufferOverflowException if adding this element exceeds the
     *         buffer's capacity.
     */
    void add( final Object o );

    /**
     * Removes the next object from the buffer.
     *
     * @throws BufferUnderflowException if the buffer is already empty
     */
    Object remove();
}
