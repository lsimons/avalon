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
 * Repository from which all configuration data is retrieved.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface ConfigurationRepository
{
    String ROLE = ConfigurationRepository.class.getName();

    /**
     * Retrieve configuration information from the repository
     *
     * @param application Application name
     * @param block Block name to get configuration for
     *
     * @return Configuration information
     *
     * @throws org.apache.avalon.framework.configuration.Configuration if no configuration could be found
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
     * Question: Is the storing of a null value to remove configuration information part of the contract?
     *
     * @throws org.apache.avalon.framework.configuration.Configuration if configuration could not be stored
     */
    void storeConfiguration( String application, String block, Configuration configuration )
        throws ConfigurationException;
}
