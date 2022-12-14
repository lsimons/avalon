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
