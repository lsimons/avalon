/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.collections;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Unsynchronized stack.
 *
 * @deprecated use org.apache.commons.collections.ArrayStack instead;
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/03/22 12:31:43 $
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

