/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.event;

/**
 * A <code>PreparedEnqueue</code> is an object returned from a
 * <code>prepareEnqueue</code> method that allows you to either
 * commit or abort the enqueue operation.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface PreparedEnqueue
{

    /**
     * Commit a previously prepared provisional enqueue operation (from
     * the <code>prepareEnqueue</code> method). Causes the provisionally
     * enqueued elements to appear on the queue for future dequeue operations.
     * Note that once a <code>prepareEnqueue</code> has returned an enqueue
     * key, the queue cannot reject the entries.
     */
    void commit();

    /**
     * Abort a previously prepared provisional enqueue operation (from
     * the <code>prepareEnqueue</code> method). Causes the queue to discard
     * the provisionally enqueued elements.
     */
    void abort();

}
