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
 * @deprecated use one of the Buffer implementations instead.
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/04/26 10:23:05 $
 * @since 4.0
 */
public class CircularBuffer
{
    protected Object[] m_buffer;
    protected int m_bufferSize;
    protected int m_contentSize;
    protected int m_head;
    protected int m_tail;

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
            Object[] tmp = new Object[m_bufferSize * 2];

            while( m_contentSize > 0 )
            {
                i++;
                i %= m_bufferSize;
                j++;
                m_contentSize--;
                tmp[j] = m_buffer[i];
            }
            m_buffer = tmp;
            m_tail = 0;
            m_head = j;
            m_contentSize = j;
            m_bufferSize *= 2;
        }

        m_buffer[m_head] = o;
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

        Object o = m_buffer[m_tail];
        m_tail++;
        m_tail %= m_bufferSize;
        m_contentSize--;
        return o;
    }
}

