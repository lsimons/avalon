/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.event;

import java.util.ArrayList;

/**
 * The default queue implementation is a variabl size queue.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class DefaultQueue extends AbstractQueue
{
    private final ArrayList m_elements;

    public DefaultQueue()
    {
        m_elements = new ArrayList();
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
        boolean success = m_elements.add( element );
        m_elements.notifyAll();

        return success;
    }

    public void enqueue( final QueueElement[] elements )
        throws SourceException
    {
        final int len = elements.length;

        for ( int i = 0; i < len; i++ )
        {
            m_elements.add( elements[i] );
        }

        m_elements.notifyAll();
    }

    public void enqueue( final QueueElement element )
        throws SourceException
    {
        m_elements.add( element );
    }

    public QueueElement[] dequeue( final int numElements )
    {
        block( m_elements );

        int arraySize = numElements;

        if ( size() < numElements )
        {
            arraySize = size();
        }

        QueueElement[] elements = new QueueElement[ arraySize ];

        for ( int i = 0; i < arraySize; i++ )
        {
            elements[i] = (QueueElement) m_elements.remove( 0 );
        }

        return elements;
    }

    public QueueElement[] dequeueAll()
    {
        block( m_elements );

        QueueElement[] elements = (QueueElement[]) m_elements.toArray( new QueueElement [] {} );
        m_elements.clear();

        return elements;
    }

    public QueueElement dequeue()
    {
        block( m_elements );

        if ( size() <= 0 )
        {
            return null;
        }

        return (QueueElement) m_elements.remove( 0 );
    }

    private final static class DefaultPreparedEnqueue implements PreparedEnqueue
    {
        private final DefaultQueue m_parent;
        private final QueueElement[] m_elements;

        private DefaultPreparedEnqueue( DefaultQueue parent, QueueElement[] elements )
        {
            m_parent = parent;
            m_elements = elements;
        }

        public void commit()
        {
            try
            {
                m_parent.enqueue( m_elements );
            }
            catch (Exception e)
            {
                throw new IllegalStateException("Default enqueue did not happen--should be impossible");
                // will never happen
            }
        }

        public void abort()
        {
            // do nothing.  DefaultQueue is unbounded, so there is nothing to manage.
        }
    }
}