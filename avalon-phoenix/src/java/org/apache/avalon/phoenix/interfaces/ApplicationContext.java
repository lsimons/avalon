/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import org.apache.excalibur.threadcontext.ThreadContext;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.log.Logger;

/**
 * Manage the "context" in which Applications operate.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface ApplicationContext
    extends Component
{
    String ROLE = ApplicationContext.class.getName();

    SarMetaData getMetaData();

    ThreadContext getThreadContext();

    /**
     * Export specified object into management system.
     * The object is exported using specifed interface
     * and using the specified name.
     *
     * @param name the name of object to export
     * @param interfaceClass the interface of object with which to export
     * @param object the actual object to export
     */
    void exportObject( String name, Class interfaceClass, Object object )
        throws Exception;

    /**
     * Unexport specified object from management system.
     *
     * @param name the name of object to unexport
     * @param interfaceClass the interface of object with which to unexport
     */
    void unexportObject( String name, Class interfaceClass )
        throws Exception;

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
     * @param name the name of logger
     * @return the Logger
     */
    Logger getLogger( String name );

    /**
     * Retrieve thread pool by name.
     *
     * @param name the name of thread pool
     * @return the threadpool
     * @throws IllegalArgumentException if the name of thread pool is
     *            invalid or named pool does not exist
     */
    ThreadPool getThreadPool( String name )
        throws IllegalArgumentException;
}
