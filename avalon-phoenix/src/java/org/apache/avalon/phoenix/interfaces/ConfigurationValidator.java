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
 * Handles parsing of configuration schema and validation against schema
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @version VSS $Revision: 1.1 $ $Date: 2002/06/28 05:09:27 $
 */
public interface ConfigurationValidator
{
    String ROLE = ConfigurationValidator.class.getName();

    /**
     * Store configuration schema
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param configuration schema represented as a configuration object
     *
     * @throws org.apache.avalon.framework.configuration.Configuration if schema is invalid
     */
    void storeSchema( String application, String block, Configuration schema )
      throws ConfigurationException;

    /**
     * Check to see if configuration is feasibly valid. That is, does this configuration match
     * the schema in its current state, but not neccessarily fullfill the requirements of the
     * schema.
     *
     * Implementations are not required to support checking feasibility. If feasibility cannot
     * be checked, the implementation should always return true
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param configuration Configuration to check
     *
     * @return true if configuration is feasibly valid
     *
     * @throws org.apache.avalon.framework.configuration.Configuration if no schema is found
     */
    boolean isFeasiblyValid( String application, String block, Configuration configuration )
      throws ConfigurationException;

    /**
     * Check to see if configuration is valid.
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param configuration Configuration to check
     *
     * @return true if configuration is valid
     *
     * @throws org.apache.avalon.framework.configuration.Configuration if no schema is found
     */
    boolean isValid( String application, String block, Configuration configuration )
      throws ConfigurationException;
}
