/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.collections;

/**
 * VariableSizeBuffer is a <strong>very</strong> efficient buffer implementation.
 * According to performance testing, it exhibits a constant access time, but it
 * also outperforms ArrayList when used for the same purpose.
 *
 * @deprecated use org.apache.commons.collections.UnboundedFifoBuffer instead
 *
 * @author  <a href="fede@apache.org">Federico Barbieri</a>
 * @author  <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/03/11 13:29:16 $
 * @since 4.0
 */
public final class VariableSizeBuffer implements Buffer
{
    protected Object[] m_buffer;
    protected int m_head;
    protected int m_tail;

    /**
     * Initialize the VariableSizeBuffer with the specified number of elements.  The
     * integer must be a positive integer.
     */
    public VariableSizeBuffer( int size )
    {
        m_buffer = new Object[ size + 1 ];
        m_head = 0;
        m_tail = 0;
    }

    /**
     * Initialize the VariableSizeBuffer with the default number of elements.  It is
     * exactly the same as performing the following:
     *
     * <pre>
     *   new VariableSizeBuffer( 32 );
     * </pre>
     */
    public VariableSizeBuffer()
    {
        this( 32 );
    }

    /**
     * Tests to see if the CircularBuffer is empty.
     */
    public final boolean isEmpty()
    {
        return ( size() == 0 );
    }

    /**
     * Returns the number of elements stored in the buffer.
     */
    public final int size()
    {
        int size = 0;

        if( m_tail < m_head )
        {
            size = m_buffer.length - m_head + m_tail;
        }
        else
        {
            size = m_tail - m_head;
        }

        return size;
    }

    /**
     * Add an object into the buffer
     */
    public final void add( final Object o )
    {
        if( null == o )
        {
            throw new NullPointerException( "Attempted to add null object to buffer" );
        }

        if( size() + 1 >= m_buffer.length )
        {
            Object[] tmp = new Object[ ( ( m_buffer.length - 1 ) * 2 ) + 1 ];

            int j = 0;
            for( int i = m_head; i != m_tail; )
            {
                tmp[ j ] = m_buffer[ i ];
                m_buffer[ i ] = null;

                j++;
                i++;
                if( i == m_buffer.length )
                {
                    i = 0;
                }
            }

            m_buffer = tmp;
            m_head = 0;
            m_tail = j;
        }

        m_buffer[ m_tail ] = o;
        m_tail++;
        if( m_tail >= m_buffer.length )
        {
            m_tail = 0;
        }
    }

    /**
     * Removes the next object from the buffer
     */
    public Object remove()
    {
        if( isEmpty() )
        {
            throw new BufferUnderflowException( "The buffer is already empty" );
        }

        Object element = m_buffer[ m_head ];

        if( null != element )
        {
            m_buffer[ m_head ] = null;

            m_head++;
            if( m_head >= m_buffer.length )
            {
                m_head = 0;
            }
        }

        return element;
    }
}

