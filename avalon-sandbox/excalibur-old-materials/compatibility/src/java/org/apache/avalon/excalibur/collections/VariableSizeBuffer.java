/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:
 
 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
 
 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.
 
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the 
 Apache Software Foundation, please see <http://www.apache.org/>.
 
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
 * @version CVS $Revision: 1.4 $ $Date: 2003/03/22 12:46:22 $
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

