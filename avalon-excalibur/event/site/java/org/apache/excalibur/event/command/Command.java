/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.command;

import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.excalibur.event.Signal;

/**
 * A Signal is a specific type of QueueElement that denotes a Control code for
 * the Queue system.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Command extends Signal, Executable
{
    /**
     * Test whether this command is repeatable.  If so, it will be placed back
     * on the CommandQueue to be issued again when it is time.
     */
    boolean isRepeatable();

    /**
     * Gets the repeat interval so that the CommandQueue keeps it for the specified
     * amount of time before enqueuing it.  If the Command is not repeatable,
     * then the repeat interval is -1.
     */
    long getRepeatInterval();
}