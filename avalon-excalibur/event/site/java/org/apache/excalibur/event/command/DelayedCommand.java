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
 * A DelayedCommand is a specific type of Command that denotes a an execution
 * unit that will be delayed at least X number of milliseconds.  The mechanism
 * is not guaranteed to be deterministic.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface DelayedCommand extends Command
{
    /**
     * Sets the initial delay for the Command.  This defaults to 0 milliseconds.
     * The value must be positive.
     */
    long getDelayInterval();
}