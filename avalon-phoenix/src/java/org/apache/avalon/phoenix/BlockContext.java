/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix;

import java.io.File;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.log.Logger;

/**
 * Context via which Blocks communicate with container.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface BlockContext
    extends Context
{
    String    APP_NAME          = "app.name";
    String    APP_HOME_DIR      = "app.home";
    String    NAME              = "block.name";

    /**
     * Base directory of .sar application.
     *
     * @return the base directory
     */
    File getBaseDirectory();

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    String getName();

    /**
     * Retrieve thread pool by category.
     * ThreadPools are given names so that you can manage different thread
     * count to different components.
     *
     * @param category the category
     * @return the ThreadManager
     * @deprecated Use ThreadManager service rather than 
     *             accessing ThreadPool via this method.
     */
    ThreadPool getThreadPool( String category );

    /**
     * Retrieve default thread pool.
     * Equivelent to getThreadPool( "default" );
     *
     * @return the default ThreadPool
     * @deprecated Use ThreadManager service rather than 
     *             accessing ThreadPool via this method.
     */
    ThreadPool getDefaultThreadPool();

    /**
     * Retrieve logger coresponding to named category.
     *
     * @return the logger
     */
    Logger getLogger( String name );

    /**
     * Retrieve logger coresponding to root category of application.
     *
     * @return the base logger
     * @deprecated Use the getLogger(String) version
     */
    Logger getBaseLogger();
}
