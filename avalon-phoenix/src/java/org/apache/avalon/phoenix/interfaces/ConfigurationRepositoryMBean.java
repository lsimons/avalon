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

/**
 * Management interface for a Configuration Repository
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @see ConfigurationRepository
 */
public interface ConfigurationRepositoryMBean
{
    /**
     * Retrieve configuration information from the repository
     *
     * @param application Application name
     * @param block Block name to get configuration for
     *
     * @return Configuration information
     *
     * @throws ConfigurationException if no configuration could be found
     */
    Configuration getConfiguration( String application, String block )
        throws ConfigurationException;

    /**
     * Store configuration information in the repository
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param configuration information to store.
     *
     * @throws ConfigurationException if configuration could not be stored
     */
    void storeConfiguration( String application,
                             String block,
                             Configuration configuration )
        throws ConfigurationException;

    /**
     * Check to see if the repository has configuration information for the specified
     * application and block
     *
     * @param application Application name
     * @param block Block name to check configuration for
     *
     * @return true if repository has configuration
     */
    boolean hasConfiguration( String application, String block );
}
