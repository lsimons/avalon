/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.collections;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Unsynchronized stakc.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ArrayStack
    extends ArrayList
{
    public void setSize( final int size )
    {
        if( 0 == size ) clear();
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
     * @exception EmptyStackException if no elements left on stack
     */
    public Object pop()
        throws EmptyStackException
    {
        final int size = size();
        if( 0 == size ) throw new EmptyStackException();

        return remove( size - 1 );
    }
}

