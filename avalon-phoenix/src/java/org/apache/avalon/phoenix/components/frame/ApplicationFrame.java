/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.frame;

import org.apache.avalon.excalibur.lang.ThreadContext;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.BlockListener;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.log.Logger;

/**
 * Manage the "frame" in which Applications operate.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ApplicationFrame
    extends Component, BlockListener
{
    String ROLE = "org.apache.avalon.phoenix.components.frame.ApplicationFrame";

    /**
     * Add a BlockListener to those requiring notification of
     * <code>BlockEvent</code>s.
     *
     * @param listener the BlockListener
     */
    void addBlockListener( BlockListener listener );

    /**
     * Remove a BlockListener from those requiring notification of
     * <code>BlockEvent</code>s.
     *
     * @param listener the BlockListener
     */
    void removeBlockListener( BlockListener listener );

    /**
     * Get ThreadContext for the current application.
     *
     * @return the ThreadContext
     */
    ThreadContext getThreadContext();

    SarMetaData getMetaData();

    /**
     * Get ClassLoader for the current application.
     *
     * @return the ClassLoader
     */
    ClassLoader getClassLoader();

    /**
     * Get the Configuration for specified component.
     *
     * @param component the component
     * @return the Configuration
     */
    Configuration getConfiguration( String component )
        throws ConfigurationException;

    /**
     * Get logger with category for application.
     * Note that this name may not be the absolute category.
     *
     * @param category the logger category
     * @return the Logger
     */
    Logger getLogger( String category );

    /**
     * Create a BlockContext for a particular Block.
     *
     * @param name the name of the Block
     * @return the created BlockContext
     */
    BlockContext createBlockContext( String name );

    /**
     * Retrieve thread pool by name.
     *
     * @param name the name of thread pool
     * @return the threadpool
     * @exception IllegalArgumentException if the name of thread pool is
     *            invalid or named pool does not exist
     */
    ThreadPool getThreadPool( String name )
        throws IllegalArgumentException;

    /**
     * Retrieve the default thread pool.
     *
     * @return the thread pool
     */
    ThreadPool getDefaultThreadPool();
}
