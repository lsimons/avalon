/* 
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.collections;

/**
 * A Buffer is an ordered list of objects, that does not support querying or
 * direct access to the elements.  It is basically a First In/First Out (FIFO)
 * buffer.  It is useful in both pooling and queue implementation code among
 * other things.
 *
 * @deprecated use org.apache.commons.collections.Buffer instead
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:15 $
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
