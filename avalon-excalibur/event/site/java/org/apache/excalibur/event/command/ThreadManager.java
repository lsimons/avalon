/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.command;

/**
 * A ThreadManager handles the thread policies for EventPipelines.  It works
 * hand in hand with the CommandManager, and can be expanded to work with a
 * SEDA like architecture.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface ThreadManager
{
    /**
     * Register an EventPipeline with the ThreadManager.
     */
    void register( EventPipeline pipeline );

    /**
     * Deregister an EventPipeline with the ThreadManager
     */
    void deregister( EventPipeline pipeline );

    /**
     * Deregisters all EventPipelines from this ThreadManager
     */
    void deregisterAll();
}