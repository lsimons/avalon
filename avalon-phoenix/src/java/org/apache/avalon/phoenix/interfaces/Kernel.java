/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface Kernel
{
    String ROLE = Kernel.class.getName();

    /**
     * Adds an application to the container
     */
    void addApplication( SarMetaData metaData,
                         ClassLoader classLoader,
                         Logger hierarchy,
                         Configuration server )
        throws Exception;

    /**
     * Removes the application from the container
     *
     * @param name the name of application to remove
     */
    void removeApplication( String name )
        throws Exception;


    /**
     * Gets the named application
     *
     * @param name the name of application
     */
    Application getApplication( String name );

    /**
     * Gets the list of applications running in the container
     *
     * @return applicationNames The array of application names
     */
    String[] getApplicationNames();
}
