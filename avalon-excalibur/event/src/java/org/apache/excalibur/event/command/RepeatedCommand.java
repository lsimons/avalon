/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.event.command;

/**
 * A Signal is a specific type of QueueElement that denotes a Control code for
 * the Queue system.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface RepeatedCommand extends DelayedCommand
{
    /**
     * If the value is less than 1 (0 or negative), the command repeats for as
     * long as the CommandManager is running.  If the value is above 0, the Command
     * repeats only for that specific amount of times before it is removed from
     * the system.
     */
    int getNumberOfRepeats();

    /**
     * Gets the repeat interval so that the CommandQueue keeps it for the specified
     * amount of time before enqueuing it again.  This value must not be negative.
     */
    long getRepeatInterval();
}
