/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.thread;

/**
 * The Executable can be implemented by components that need to perform
 * some work. In many respects it is similar to Runnable except that it
 * also allows an application to throw a non-Runtime Exception.
 *
 * <p>The work done may be short lived (ie a simple task) or it could
 * be a long running.</p>
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface Executable
{
    /**
     * Execute the action associated with this component.
     *
     * @throws Exception if an error occurs
     */
    void execute()
        throws Exception;
}
