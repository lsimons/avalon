/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.event;

/**
 * An EventHandler takes care of processing specific events in an Event Based
 * architecture.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface EventHandler
{
    /**
     * Handle one event at a time.
     */
    void handleEvent( QueueElement element );

    /**
     * Handle a whole array of events at a time.
     */
    void handleEvents( QueueElement[] elements );
}