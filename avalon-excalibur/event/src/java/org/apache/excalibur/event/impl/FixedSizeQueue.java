/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.excalibur.event.impl;

import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.excalibur.event.PreparedEnqueue;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.SinkFullException;

/**
 * An implementation of the <code>Queue</code> that has a fixed size.  Once
 * the maximum number of elements are set, this <code>Queue</code> cannot be
 * changed.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class FixedSizeQueue
    extends AbstractQueue
{
    private final Object[] m_elements;
    private final Mutex m_mutex;
    private int m_start = 0;
    private int m_end = 0;
    private int m_reserve = 0;

    /**
     * Create a <code>FixedSizedQueue</code> with the specified maximum size.
     * The maximum size must be 1 or more.
     *
     * @param size  The maximum number of events the Queue can handle
     */
    public FixedSizeQueue( int size )
    {
        if ( size < 1 )
            throw new IllegalArgumentException("Cannot specify an unbounded Queue");

        m_elements = new Object[ size + 1 ];
        m_mutex = new Mutex();
    }

    public int size()
    {
        int size = 0;

        if( m_end < m_start )
        {
            size = maxSize() - m_start + m_end;
        }
        else
        {
            size = m_end - m_start;
        }

        return size;
    }

    public int maxSize()
    {
        return m_elements.length;
    }

    public PreparedEnqueue prepareEnqueue( final Object[] elements )
        throws SinkException
    {
        PreparedEnqueue enqueue = null;

        try
        {
            m_mutex.acquire();
            try
            {
                if( elements.length + m_reserve + size() > maxSize() )
                {
                    throw new SinkFullException( "Not enough room to enqueue these elements." );
                }

                enqueue = new FixedSizePreparedEnqueue( this, elements );
            }
            finally
            {
                m_mutex.release();
            }
        }
        catch( InterruptedException ie )
        {
        }

        return enqueue;
    }

    public boolean tryEnqueue( final Object element )
    {
        boolean success = false;

        try
        {
            m_mutex.acquire();
            try
            {
                if( 1 + m_reserve + size() > maxSize() )
                {
                    return false;
                }

                addElement( element );
                success = true;
            }
            finally
            {
                m_mutex.release();
            }
        }
        catch( InterruptedException ie )
        {
        }

        return success;
    }

    public void enqueue( final Object[] elements )
        throws SinkException
    {
        final int len = elements.length;

        try
        {
            m_mutex.acquire();
            try
            {
                if( elements.length + m_reserve + size() > maxSize() )
                {
                    throw new SinkFullException( "Not enough room to enqueue these elements." );
                }

                for( int i = 0; i < len; i++ )
                {
                    addElement( elements[ i ] );
                }
            }
            finally
            {
                m_mutex.release();
            }
        }
        catch( InterruptedException ie )
        {
        }
    }

    public void enqueue( final Object element )
        throws SinkException
    {
        try
        {
            m_mutex.acquire();
            try
            {
                if( 1 + m_reserve + size() > maxSize() )
                {
                    throw new SinkFullException( "Not enough room to enqueue these elements." );
                }

                addElement( element );
            }
            finally
            {
                m_mutex.release();
            }
        }
        catch( InterruptedException ie )
        {
        }
    }

    public Object[] dequeue( final int numElements )
    {
        Object[] elements = EMPTY_ARRAY;

        try
        {
            if( m_mutex.attempt( m_timeout ) )
            {
                try
                {
                    elements = retrieveElements( Math.min( size(),
                                                           numElements ) );
                }
                finally
                {
                    m_mutex.release();
                }
            }
        }
        catch( InterruptedException ie )
        {
        }

        return elements;
    }

    private final void addElement( Object element )
    {
        m_elements[ m_end ] = element;

        m_end++;
        if( m_end >= maxSize() )
        {
            m_end = 0;
        }
    }

    private final Object removeElement()
    {
        Object element = m_elements[ m_start ];

        if( null != element )
        {
            m_elements[ m_start ] = null;

            m_start++;
            if( m_start >= maxSize() )
            {
                m_start = 0;
            }
        }

        return element;
    }

    /**
     * Removes exactly <code>count</code> elements from the underlying
     * element store and returns them as an array of Objects.
     * The caller is responsible for synchronizing access to the
     * element store and passing the correct value for
     * <code>count</code>.
     * <p>
     * The method can be further optimized by using System.arraycopy
     * if it is found to underperform.
     *
     * @param count number of elements to return
     * @return requested number of elements
     */
    private final Object[] retrieveElements( int count )
    {
        Object[] elements = new Object[ count ];

        for( int i = 0; i < count; i++ )
        {
            elements[ i ] = removeElement();
        }

        return elements;
    }

    public Object[] dequeueAll()
    {
        Object[] elements = EMPTY_ARRAY;

        try
        {
            if( m_mutex.attempt( m_timeout ) )
            {
                try
                {
                    elements = retrieveElements( size() );
                }
                finally
                {
                    m_mutex.release();
                }
            }
        }
        catch( InterruptedException ie )
        {
        }

        return elements;
    }

    public Object dequeue()
    {
        Object element = null;

        try
        {
            if( m_mutex.attempt( m_timeout ) )
            {
                try
                {
                    if( size() > 0 )
                    {
                        element = removeElement();
                    }
                }
                finally
                {
                    m_mutex.release();
                }
            }
        }
        catch( InterruptedException ie )
        {
        }

        return element;
    }

    private static final class FixedSizePreparedEnqueue implements PreparedEnqueue
    {
        private final FixedSizeQueue m_parent;
        private Object[] m_elements;

        private FixedSizePreparedEnqueue( FixedSizeQueue parent, Object[] elements )
        {
            m_parent = parent;
            m_elements = elements;
        }

        public void commit()
        {
            if( null == m_elements )
            {
                throw new IllegalStateException( "This PreparedEnqueue has already been processed!" );
            }

            try
            {
                m_parent.enqueue( m_elements );
                m_parent.m_reserve -= m_elements.length;
                m_elements = null;
            }
            catch( Exception e )
            {
                throw new IllegalStateException( "Default enqueue did not happen--should be impossible" );
                // will never happen
            }
        }

        public void abort()
        {
            if( null == m_elements )
            {
                throw new IllegalStateException( "This PreparedEnqueue has already been processed!" );
            }

            m_parent.m_reserve -= m_elements.length;
            m_elements = null;
        }
    }
}
