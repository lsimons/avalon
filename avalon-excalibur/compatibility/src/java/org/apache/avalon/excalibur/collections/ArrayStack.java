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

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Unsynchronized stack.
 *
 * @deprecated use org.apache.commons.collections.ArrayStack instead;
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2004/04/26 10:23:05 $
 * @since 4.0
 */
public class ArrayStack
        extends ArrayList
{
    public void setSize( final int size )
    {
        if( 0 == size )
        {
            clear();
        }
        else
        {
            removeRange( size, size() - 1 );
        }
    }

    /**
     * Adds the object to the top of the stack.
     *
     * @param element object to add to stack
     * @return the object
     */
    public Object push( final Object element )
    {
        add( element );
        return element;
    }

    /**
     * Remove element from top of stack and return it
     *
     * @return the element from stack
     * @throws EmptyStackException if no elements left on stack
     */
    public Object pop()
            throws EmptyStackException
    {
        final int size = size();
        if( 0 == size )
        {
            throw new EmptyStackException();
        }

        return remove( size - 1 );
    }
}

