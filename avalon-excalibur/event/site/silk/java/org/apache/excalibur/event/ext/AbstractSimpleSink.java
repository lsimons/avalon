/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.ext;

import org.apache.avalon.excalibur.concurrent.Mutex;
import org.apache.excalibur.event.PreparedEnqueue;
import org.apache.excalibur.event.SinkException;

/**
 * The AbstractSimpleSink class is an abstract class 
 * which implements 'null' functionality for most of 
 * the administrative methods of Sink. This class can 
 * be extended to implement simple Sink's which don't 
 * require most of the special behavior of the fully 
 * general case.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:mschier@earthlink.net">schierma</a>
 */
public abstract class AbstractSimpleSink extends AbstractSink
{
    /**
     * A mutex to synchronize operation on.
     */
    private final Mutex m_mutex = new Mutex();

    //------------------------ Sink implementation
    /**
     * @see Sink#tryEnqueue(Object)
     */
    public boolean tryEnqueue(Object enqueueMe)
    {
        try
        {
            m_mutex.acquire();

            enqueue(enqueueMe);
            return true;
        }
        catch (SinkException se)
        {
            return false;
        }
        catch (InterruptedException ie)
        {
            return false;
        }
        finally
        {
            m_mutex.release();
        }
    }

    /**
     * @see Sink#maxSize()
     */
    public int maxSize()
    {
        return -1;
    }

    /**
     * @see Sink#canAccept()
     */
    public int canAccept()
    {
        return 0;
    }

    /**
     * @see Sink#canAccept()
     */
    public boolean isFull()
    {
        return false;
    }

    /**
     * Simply calls enqueue() on each item in the array. 
     * Note that this behavior <b>breaks</b> the property 
     * that {@link Sink#enqueue(Object[])} should 
     * be an "all or nothing" operation, since enqueue() 
     * might reject some items but not others. Don't use 
     * AbstractSimpleSink if this is going to be a problem.
     * @since May 29, 2002
     * 
     * @see Sink#enqueue(Object[])
     */
    public void enqueue(Object[] enqueueMe) throws SinkException
    {
        for (int i = 0; i < enqueueMe.length; i++)
        {
            enqueue(enqueueMe[i]);
        }
    }

    /**
     * @see Sink#size()
     */
    public int size()
    {
        return -1;
    }

    /**
     * @see Sink#prepareEnqueue(Object[])
     */
    public PreparedEnqueue prepareEnqueue(Object[] elements)
        throws SinkException
    {
        throw new IllegalStateException();
    }

}