/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.collections;

/**
 * The FixedSizeBuffer is a <strong>very</strong> efficient implementation of
 * Buffer that does not alter the size of the buffer at runtime.
 *
 * @deprecated use org.apache.commons.collections.BoundedFifoBuffer instead
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class FixedSizeBuffer implements Buffer
{
    private final Object[] m_elements;
    private int m_start = 0;
    private int m_end = 0;
    private boolean m_full = false;

    public FixedSizeBuffer( int size )
    {
        m_elements = new Object[ size ];
    }

    public FixedSizeBuffer()
    {
        this( 32 );
    }

    public final int size()
    {
        int size = 0;

        if( m_end < m_start )
        {
            size = m_elements.length - m_start + m_end;
        }
        else if( m_end == m_start )
        {
            size = ( m_full ? m_elements.length : 0 );
        }
        else
        {
            size = m_end - m_start;
        }

        return size;
    }

    public final boolean isEmpty()
    {
        return size() == 0;
    }

    public final void add( Object element )
    {
        if( null == element )
        {
            throw new NullPointerException( "Attempted to add null object to buffer" );
        }

        if( m_full )
        {
            throw new BufferOverflowException( "The buffer cannot hold more than "
                                               + m_elements.length + " objects." );
        }

        m_elements[ m_end++ ] = element;

        if( m_end >= m_elements.length )
        {
            m_end = 0;
        }

        if( m_end == m_start )
        {
            m_full = true;
        }
    }

    public final Object remove()
    {
        if( isEmpty() )
        {
            throw new BufferUnderflowException( "The buffer is already empty" );
        }

        Object element = m_elements[ m_start ];

        if( null != element )
        {
            m_elements[ m_start++ ] = null;

            if( m_start >= m_elements.length )
            {
                m_start = 0;
            }

            m_full = false;
        }

        return element;
    }
}