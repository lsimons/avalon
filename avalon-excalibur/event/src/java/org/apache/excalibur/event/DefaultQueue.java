/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event;

import java.util.ArrayList;
import org.apache.avalon.excalibur.concurrent.Mutex;

/**
 * The default queue implementation is a variabl size queue.  This queue is
 * ThreadSafe, however the overhead in synchronization costs a few extra millis.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class DefaultQueue extends AbstractQueue
{
    private final ArrayList m_elements;
    private final Mutex     m_mutex;

    public DefaultQueue()
    {
        m_elements = new ArrayList();
        m_mutex = new Mutex();
    }

    public int size()
    {
        return m_elements.size();
    }

    public PreparedEnqueue prepareEnqueue( final QueueElement[] elements )
        throws SourceException
    {
        return new DefaultPreparedEnqueue( this, elements );
    }

    public boolean tryEnqueue( final QueueElement element )
    {
        boolean success = false;
        try
        {
            m_mutex.acquire();
            success = m_elements.add( element );
        }
        catch ( InterruptedException ie )
        {
        }
        finally
        {
            m_mutex.release();
        }

        return success;
    }

    public void enqueue( final QueueElement[] elements )
        throws SourceException
    {
        final int len = elements.length;

        try
        {
            m_mutex.acquire();

            for ( int i = 0; i < len; i++ )
            {
                m_elements.add( elements[i] );
            }
        }
        catch ( InterruptedException ie )
        {
        }
        finally
        {
            m_mutex.release();
        }
    }

    public void enqueue( final QueueElement element )
        throws SourceException
    {
        try
        {
            m_mutex.acquire();
            m_elements.add( element );
        }
        catch ( InterruptedException ie )
        {
        }
        finally
        {
            m_mutex.release();
        }
    }

    public QueueElement[] dequeue( final int numElements )
    {
        int arraySize = numElements;

        if ( size() < numElements )
        {
            arraySize = size();
        }

        QueueElement[] elements = null;

        try
        {
            m_mutex.attempt( m_timeout );

            elements = new QueueElement[ arraySize ];

            for ( int i = 0; i < arraySize; i++ )
            {
                elements[i] = (QueueElement) m_elements.remove( 0 );
            }
        }
        catch ( InterruptedException ie )
        {
        }
        finally
        {
            m_mutex.release();
        }

        return elements;
    }

    public QueueElement[] dequeueAll()
    {
        QueueElement[] elements = null;

        try
        {
            m_mutex.attempt( m_timeout );

            elements = (QueueElement[]) m_elements.toArray( new QueueElement [] {} );
            m_elements.clear();
        }
        catch ( InterruptedException ie )
        {
        }
        finally
        {
            m_mutex.release();
        }

        return elements;
    }

    public QueueElement dequeue()
    {
        QueueElement element = null;

        try
        {
            m_mutex.attempt( m_timeout );

            if ( size() > 0 )
            {
                element = (QueueElement) m_elements.remove( 0 );
            }
        }
        catch ( InterruptedException ie )
        {
        }
        finally
        {
            m_mutex.release();
        }

        return element;
    }

    private final static class DefaultPreparedEnqueue implements PreparedEnqueue
    {
        private final DefaultQueue m_parent;
        private       QueueElement[] m_elements;

        private DefaultPreparedEnqueue( DefaultQueue parent, QueueElement[] elements )
        {
            m_parent = parent;
            m_elements = elements;
        }

        public void commit()
        {
            if ( null == m_elements )
            {
                throw new IllegalStateException("This PreparedEnqueue has already been processed!");
            }

            try
            {
                m_parent.enqueue( m_elements );
                m_elements = null;
            }
            catch (Exception e)
            {
                throw new IllegalStateException("Default enqueue did not happen--should be impossible");
                // will never happen
            }
        }

        public void abort()
        {
            if ( null == m_elements )
            {
                throw new IllegalStateException("This PreparedEnqueue has already been processed!");
            }

            m_elements = null;
        }
    }
}