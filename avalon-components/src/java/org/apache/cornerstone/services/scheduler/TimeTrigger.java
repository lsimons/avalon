/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.services.scheduler;

/**
 * This is the marker interface for Triggers.
 * Triggers can be time-based, event-based or other.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface TimeTrigger
    extends Trigger
{
    /**
     * Retrieve the next time at trigger activates relative to another time.
     *
     * @return the time at which the trigger activates
     */
    long getTimeAfter( long time );
}
