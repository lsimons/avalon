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
 * Management interface to the Configuration Validator
 *
 * @phoenix:mx-topic name="ConfigurationValidator"
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @see ConfigurationValidator
 */
public interface ConfigurationValidatorMBean
{
    /**
     * Get the schema type for the specified application and block. Returns
     * null if no schema
     *
     * @param application to get schema for
     * @param block to get schema for
     * @return schema type, or null if none exists
     * @phoenix:mx-operation
     */
    String getSchemaType( final String application, final String block );

    /**
     * Get the XML that represents the schema for the specified application and block.
     * Returns null if no schema.
     *
     * @param application to get schema for
     * @param block to get schema for
     * @return schema as string, or null if none exists
     * @phoenix:mx-operation
     */
    String getSchema( final String application, final String block );

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
     *
     * @see ConfigurationValidator#isValid
     *
     * @phoenix:mx-operation
     */
    boolean isValid( String application, String block, Configuration configuration )
        throws ConfigurationException;
}
