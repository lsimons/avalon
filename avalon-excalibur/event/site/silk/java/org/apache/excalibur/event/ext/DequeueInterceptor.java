/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.ext;

import org.apache.excalibur.event.Queue;

/**
 * The dequeue executable interface describes operations that 
 * are executed before and after elements are pulled from a 
 * queue.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface DequeueInterceptor
{

    /**
     * An operation executed before dequeing events from
     * the queue. The size of the queue is passed in so the
     * implementation can determine to execute based on the
     * size of the queue.
     * @since Sep 23, 2002
     * 
     * @param context
     *  The source from which the dequeue is performed.
     */
    public void before(Queue context);

    /**
     * An operation executed after dequeing events from
     * the queue. The size of the queue is passed in so the
     * implementation can determine to execute based on the
     * size of the queue.
     * @since Sep 23, 2002
     * 
     * @param context
     *  The source from which the dequeue is performed.
     */
    public void after(Queue context);

}