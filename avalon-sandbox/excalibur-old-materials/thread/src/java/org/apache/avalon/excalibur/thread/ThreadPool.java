/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.thread;

import org.apache.avalon.framework.activity.Executable;
import org.apache.excalibur.thread.ThreadControl;

/**
 * This class is the public frontend for the thread pool code.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @deprecated Replaced with org.apache.excalibur.thread.ThreadPool
 */
public interface ThreadPool
    extends org.apache.excalibur.thread.ThreadPool
{
    /**
     * Run work in separate thread.
     * Return a valid ThreadControl to control work thread.
     *
     * @param work the work to be executed.
     * @return the ThreadControl
     */
    ThreadControl execute( Executable work );
}
