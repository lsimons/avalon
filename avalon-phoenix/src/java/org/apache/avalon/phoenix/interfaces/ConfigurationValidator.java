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
 * @version CVS $Revision: 1.4 $ $Date: 2002/07/26 09:49:22 $
 */
public interface ConfigurationValidator
{
    String ROLE = ConfigurationValidator.class.getName();

    /**
     * Add configuration schema to validator
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param url url that the schema may be located at
     *
     * @throws ConfigurationException if schema is invalid
     */
    void addSchema( String application, String block, String schemaType, String url )
        throws ConfigurationException;

    /**
     * Add configuration schema to validator
     *
     * @param application Application name
     * @param block Block name to store configuration for
     * @param url url that the schema may be located at
     *
     * @throws ConfigurationException if schema is invalid
     */
    void removeSchema( String application, String block );

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
     * @throws ConfigurationException if no schema is found
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
     * @throws ConfigurationException if no schema is found
     */
    boolean isValid( String application, String block, Configuration configuration )
        throws ConfigurationException;
}
