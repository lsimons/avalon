/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.concurrent;

/**
 * Also called counting semaphores, Djikstra semaphores are used to control
 * access to a set of resources. A Djikstra semaphore has a count associated
 * with it and each acquire() call reduces the count. A thread that tries to
 * acquire() a Djikstra semaphore with a zero count blocks until someone else
 * calls release() thus increasing the count.
 *
 * @author <a href="mailto:kranga@sapient.com">Karthik Rangaraju</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/03/11 13:29:17 $
 * @since 4.0
 * @deprecated Replaced by {@link DijkstraSemaphore}.
 */
public class DjikstraSemaphore
    extends DijkstraSemaphore
{
    /**
     * Creates a Djikstra semaphore with the specified max count and initial
     * count set to the max count (all resources released)
     * @param maxCount is the max semaphores that can be acquired
     */
    public DjikstraSemaphore( int maxCount )
    {
        super( maxCount, maxCount );
    }

    /**
     * Creates a Djikstra semaphore with the specified max count and an initial
     * count of acquire() operations that are assumed to have already been
     * performed.
     * @param maxCount is the max semaphores that can be acquired
     * @param initialCount is the current count (setting it to zero means all
     * semaphores have already been acquired). 0 <= initialCount <= maxCount
     */
    public DjikstraSemaphore( int maxCount, int initialCount )
    {
        super( maxCount, initialCount );
    }
}

