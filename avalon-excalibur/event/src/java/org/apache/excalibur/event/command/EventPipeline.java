/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.command;

import org.apache.avalon.excalibur.event.Sink;
import org.apache.avalon.excalibur.event.EventHandler;

/**
 * An EventPipeline is used by the ThreadManager to manage the event Queue and
 * EventHandler relationship.  The ThreadManager manages the automatic forwarding
 * of the Events from the queue to the Event Handler.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface EventPipeline
{
    /**
     * There can be many different sinks to merge into a pipeline.  For the
     * CommandManager, there is only one sink.
     */
    Sink[] getSinks();

    /**
     * Returns the reference to the EventHandler that the events from all the
     * Sinks get merged into.
     */
    EventHandler getEventHandler();
}