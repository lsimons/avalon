/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.collections;

/**
 *
 * @author  Federico Barbieri <fede@apache.org>
 */
public class CircularBuffer
{
    protected Object[]   m_buffer;
    protected int        m_bufferSize;
    protected int        m_contentSize;
    protected int        m_head;
    protected int        m_tail;

    public CircularBuffer( int size )
    {
        m_buffer = new Object[size];
        m_bufferSize = size;
        m_contentSize = 0;
        m_head = 0;
        m_tail = 0;
    }

    public CircularBuffer()
    {
        this( 32 );
    }

    public boolean isEmpty()
    {
        return (m_contentSize == 0);
    }

    public int getContentSize()
    {
        return m_contentSize;
    }

    public int getBufferSize()
    {
        return m_bufferSize;
    }

    public void append( final Object o )
    {
        if( m_contentSize >= m_bufferSize )
        {
            int j = 0;
            int i = m_tail;
            Object[] tmp = new Object[ m_bufferSize * 2 ];

            while( m_contentSize > 0 )
            {
                i++;
                i %= m_bufferSize;
                j++;
                m_contentSize--;
                tmp[ j ] = m_buffer[ i ];
            }
            m_buffer = tmp;
            m_tail = 0;
            m_head = j;
            m_contentSize = j;
            m_bufferSize *= 2;
        }

        m_buffer[ m_head ] = o;
        m_head++;
        m_head %= m_bufferSize;
        m_contentSize++;
    }

    public Object get()
    {
        if( m_contentSize <= 0 )
        {
            return null;
        }

        Object o = m_buffer[ m_tail ];
        m_tail++;
        m_tail %= m_bufferSize;
        m_contentSize--;
        return o;
    }
}

