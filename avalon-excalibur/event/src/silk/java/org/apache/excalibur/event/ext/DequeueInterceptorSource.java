/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.ext;



/**
 * A dequeue executable source executes the operations
 * defined in the {@link DequeueInterceptor} interface 
 * when elements are pulled from the source to be handled.
 * 
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public interface DequeueInterceptorSource
{
    /**
     * Set the dequeue executable for this sink. This mechanism 
     * allows users to define a methods that will be executed
     * before or after dequeuing elements from a source
     * @since Sep 23, 2002
     * 
     * @param executable
     *  The dequeue executable for this sink.
     */
    public void setDequeueInterceptor(DequeueInterceptor executable);

    /**
     * Return the dequeue executable for this sink.
     * @since Sep 23, 2002
     * 
     * @return {@link DequeueInterceptor}
     *  The dequeue executable for this sink.
     */
    public DequeueInterceptor getDequeueInterceptor();
}