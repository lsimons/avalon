/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.event;

import org.apache.avalon.excalibur.collections.Buffer;
import org.apache.avalon.excalibur.collections.VariableSizeBuffer;
import org.apache.avalon.excalibur.concurrent.Mutex;

/**
 * The default queue implementation is a variable size queue.  This queue is
 * ThreadSafe, however the overhead in synchronization costs a few extra millis.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class DefaultQueue extends AbstractQueue
{
    private final Buffer m_elements;
    private final Mutex m_mutex;
    private int m_reserve;
    private final int m_maxSize;

    public DefaultQueue( int size )
    {
        int maxSize;

        if( size > 0 )
        {
            m_elements = new VariableSizeBuffer( size );
            maxSize = size;
        }
        else
        {
            m_elements = new VariableSizeBuffer();
            maxSize = -1;
        }

        m_mutex = new Mutex();
        m_reserve = 0;
        m_maxSize = maxSize;
    }

    public DefaultQueue()
    {
        this( -1 );
    }

    public int size()
    {
        return m_elements.size();
    }

    public int maxSize()
    {
        return m_maxSize;
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

    public boolean tryEnqueue( final QueueElement element )
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

    public void enqueue( final QueueElement[] elements )
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

    public void enqueue( final QueueElement element )
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

    public QueueElement[] dequeue( final int numElements )
    {
        QueueElement[] elements = EMPTY_ARRAY;

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

    public QueueElement[] dequeueAll()
    {
        QueueElement[] elements = EMPTY_ARRAY;

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
     * full of <code>QueueElement</code>s and with at least <code>count</code>
     * elements available.
     * <p>
     * @param buf to remove elements from, the caller is responsible
     *            for synchronizing access
     * @param count number of elements to remove/return
     * @return requested number of elements
     */
    private static QueueElement[] retrieveElements( Buffer buf, int count )
    {
        QueueElement[] elements = new QueueElement[ count ];
 
        for( int i = 0; i < count; i++ )
        {
            elements[ i ] = (QueueElement) buf.remove();
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
                        element = (QueueElement)m_elements.remove();
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
        private QueueElement[] m_elements;

        private DefaultPreparedEnqueue( DefaultQueue parent, QueueElement[] elements )
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
