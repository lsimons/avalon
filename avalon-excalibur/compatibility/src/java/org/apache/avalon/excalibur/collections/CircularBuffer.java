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
 * @deprecated use one of the Buffer implementations instead.
 *
 * @author  Federico Barbieri <fede@apache.org>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 15:31:39 $
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
        m_buffer = new Object[ size ];
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
        return ( m_contentSize == 0 );
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

