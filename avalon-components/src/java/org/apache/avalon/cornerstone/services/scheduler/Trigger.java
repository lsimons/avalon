/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.services.scheduler;

/**
 * This is the marker interface for Triggers.
 * Triggers can be time-based, event-based or other.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:ram.chidambaram@telus.com">Ram Chidambaram</a>
 */
public interface Trigger
{
    /**
     * Reset the Trigger. The Triggers can be time-based,
     * event-based or other.
     */
    void reset();
}
