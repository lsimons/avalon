/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread;

import org.apache.avalon.framework.activity.Executable;

/**
 * This class is the public frontend for the thread pool code.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface ThreadPool
{
    /**
     * Run work in separate thread.
     * Return a valid ThreadControl to control work thread.
     *
     * @param work the work to be executed.
     * @return the ThreadControl
     */
    ThreadControl execute( Runnable work );

    /**
     * Run work in separate thread.
     * Return a valid ThreadControl to control work thread.
     *
     * @param work the work to be executed.
     * @return the ThreadControl
     */
    ThreadControl execute( Executable work );
}
