/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.metadata.SarMetaData;
import org.apache.excalibur.threadcontext.ThreadContext;
import java.io.InputStream;

/**
 * Manage the "context" in which Applications operate.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface ApplicationContext
{
    String ROLE = ApplicationContext.class.getName();

    SarMetaData getMetaData();

    ThreadContext getThreadContext();

    /**
     * A application can request that it be be shutdown. In most cases
     * the kernel will schedule the shutdown to occur in another thread.
     */
    void requestShutdown();

    /**
     * Export specified object into management system.
     * The object is exported using specifed interface
     * and using the specified name.
     *
     * @param name the name of object to export
     * @param interfaceClasses the interface of object with which to export
     * @param object the actual object to export
     */
    void exportObject( String name, Class[] interfaceClasses, Object object )
        throws Exception;

    /**
     * Unexport specified object from management system.
     *
     * @param name the name of object to unexport
     */
    void unexportObject( String name )
        throws Exception;

    /**
     * Get ClassLoader for the current application.
     *
     * @return the ClassLoader
     */
    ClassLoader getClassLoader();

    /**
     * Retrieve a resource from the SAR file. The specified
     * name is relative the root of the archive. So you could
     * use it to retrieve a html page from within sar by loading
     * the resource named "data/main.html" or similar.
     */
    InputStream getResourceAsStream( String name );

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
}
