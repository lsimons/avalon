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
 * The FixedSizeBuffer is a <strong>very</strong> efficient implementation of
 * Buffer that does not alter the size of the buffer at runtime.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @deprecated use org.apache.commons.collections.BoundedFifoBuffer instead
 */
public final class FixedSizeBuffer implements Buffer
{
    private final Object[] m_elements;
    private int m_start = 0;
    private int m_end = 0;
    private boolean m_full = false;

    public FixedSizeBuffer( int size )
    {
        m_elements = new Object[size];
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
            size = (m_full ? m_elements.length : 0);
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

        m_elements[m_end++] = element;

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

        Object element = m_elements[m_start];

        if( null != element )
        {
            m_elements[m_start++] = null;

            if( m_start >= m_elements.length )
            {
                m_start = 0;
            }

            m_full = false;
        }

        return element;
    }
}
