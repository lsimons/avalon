/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.event.command;

import org.apache.avalon.excalibur.thread.ThreadPool;

/**
 * A ThreadManager that will use an external ThreadPool. This will be useful if you
 * want to have several ThreadManagers talking to a commonly defined set of ThreadPools,
 * such as <a href="http://jakarta.apache.org/avalon/cornerstone">Cornerstone's</a>
 * (similarly named) ThreadManager (which manages ThreadPools).
 *
 * @see org.apache.avalon.cornerstone.services.thread.ThreadManager
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class DefaultThreadManager extends AbstractThreadManager
{
    public DefaultThreadManager( final ThreadPool pool )
    {
        setThreadPool( pool );
    }
}
