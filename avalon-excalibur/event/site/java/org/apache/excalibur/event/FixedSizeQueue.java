/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.event;

import org.apache.avalon.excalibur.concurrent.Mutex;

/**
 * The default queue implementation is a variable size queue.  This queue is
 * ThreadSafe, however the overhead in synchronization costs a few extra millis.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class FixedSizeQueue
    extends AbstractQueue
{
    private final QueueElement[] m_elements;
    private final Mutex m_mutex;
    private int m_start = 0;
    private int m_end = 0;
    private int m_reserve = 0;

    public FixedSizeQueue( int size )
    {
        m_elements = new QueueElement[ size + 1 ];
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

    public PreparedEnqueue prepareEnqueue( final QueueElement[] elements )
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

    public boolean tryEnqueue( final QueueElement element )
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

    public void enqueue( final QueueElement[] elements )
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

    public void enqueue( final QueueElement element )
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

    public QueueElement[] dequeue( final int numElements )
    {
        QueueElement[] elements = EMPTY_ARRAY;

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

    private final void addElement( QueueElement element )
    {
        m_elements[ m_end ] = element;

        m_end++;
        if( m_end >= maxSize() )
        {
            m_end = 0;
        }
    }

    private final QueueElement removeElement()
    {
        QueueElement element = m_elements[ m_start ];

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
     * element store and returns them as an array of QueueElements.
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
    private final QueueElement[] retrieveElements( int count )
    {
        QueueElement[] elements = new QueueElement[ count ];

        for( int i = 0; i < count; i++ )
        {
            elements[ i ] = removeElement();
        }

        return elements;
    }

    public QueueElement[] dequeueAll()
    {
        QueueElement[] elements = EMPTY_ARRAY;

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

    public QueueElement dequeue()
    {
        QueueElement element = null;

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
        private QueueElement[] m_elements;

        private FixedSizePreparedEnqueue( FixedSizeQueue parent, QueueElement[] elements )
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
