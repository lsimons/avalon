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

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.UnboundedFifoBuffer;
import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.excalibur.event.PreparedEnqueue;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.SinkFullException;

/**
 * The default queue implementation is a variable size queue.  This queue is
 * thread safe, however the overhead in synchronization costs a few extra
 * milliseconds.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class DefaultQueue extends AbstractQueue
{
    private final Buffer m_elements;
    private final Mutex m_mutex;
    private int m_reserve;
    private final int m_maxSize;

    /**
     * Construct a new DefaultQueue with the specified number of elements.
     * if the number of elements is greater than zero, then the
     * <code>Queue</code> is bounded by that number.  Otherwise, the
     * <code>Queue</code> is not bounded at all.
     *
     * @param  size  The maximum number of elements in the <code>Queue</code>.
     *               Any number less than 1 means there is no limit.
     */
    public DefaultQueue( int size )
    {
        int maxSize;

        if( size > 0 )
        {
            m_elements = new UnboundedFifoBuffer( size );
            maxSize = size;
        }
        else
        {
            m_elements = new UnboundedFifoBuffer();
            maxSize = -1;
        }

        m_mutex = new Mutex();
        m_reserve = 0;
        m_maxSize = maxSize;
    }

    /**
     * Create an unbounded DefaultQueue.
     */
    public DefaultQueue()
    {
        this( -1 );
    }

    /**
     * Return the number of elements currently in the <code>Queue</code>.
     *
     * @return <code>int</code> representing the number of elements.
     */
    public int size()
    {
        return m_elements.size();
    }

    /**
     * Return the maximum number of elements that will fit in the
     * <code>Queue</code>.  A number below 1 indecates an unbounded
     * <code>Queue</code>, which means there is no limit.
     *
     * @return <code>int</code> representing the maximum number of elements
     */
    public int maxSize()
    {
        return m_maxSize;
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

                if( maxSize() > 0 && elements.length + m_reserve + size() > maxSize() )
                {
                    throw new SinkFullException( "Not enough room to enqueue these elements." );
                }

                enqueue = new DefaultPreparedEnqueue( this, elements );
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

                if( maxSize() > 0 && 1 + m_reserve + size() > maxSize() )
                {
                    return false;
                }

                m_elements.add( element );
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
                if( maxSize() > 0 && elements.length + m_reserve + size() > maxSize() )
                {
                    throw new SinkFullException( "Not enough room to enqueue these elements." );
                }

                for( int i = 0; i < len; i++ )
                {
                    m_elements.add( elements[ i ] );
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
                if( maxSize() > 0 && 1 + m_reserve + size() > maxSize() )
                {
                    throw new SinkFullException( "Not enough room to enqueue these elements." );
                }

                m_elements.add( element );
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
                    elements = retrieveElements( m_elements,
                                                 Math.min( size(),
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

    public Object[] dequeueAll()
    {
        Object[] elements = EMPTY_ARRAY;

        try
        {
            if( m_mutex.attempt( m_timeout ) )
            {
                try
                {
                    elements = retrieveElements( m_elements, size() );
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

    /**
     * Removes the given number of elements from the given <code>buf</code>
     * and returns them in an array. Trusts the caller to pass in a buffer
     * full of <code>Object</code>s and with at least
     * <code>count</code> elements available.
     * <p>
     * @param buf to remove elements from, the caller is responsible
     *            for synchronizing access
     * @param count number of elements to remove/return
     * @return requested number of elements
     */
    private static Object[] retrieveElements( Buffer buf, int count )
    {
        Object[] elements = new Object[ count ];

        for( int i = 0; i < count; i++ )
        {
            elements[ i ] = (Object)buf.remove();
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
                        element = (Object)m_elements.remove();
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

    private static final class DefaultPreparedEnqueue implements PreparedEnqueue
    {
        private final DefaultQueue m_parent;
        private Object[] m_elements;

        private DefaultPreparedEnqueue( DefaultQueue parent, Object[] elements )
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
