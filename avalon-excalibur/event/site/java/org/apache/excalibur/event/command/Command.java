/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.event.command;

import org.apache.avalon.framework.activity.Executable;
import org.apache.excalibur.event.Signal;

/**
 * A Command is a specific type of Signal that denotes an asynchronous execution
 * unit that must be performed by the CommandManager.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface Command extends Signal, Executable
{
}
