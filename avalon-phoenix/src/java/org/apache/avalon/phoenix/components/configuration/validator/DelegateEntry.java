/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.configuration.validator;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidator;

/**
 * Configuration Validator entry for the DelegatingConfigurationValidator.
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
class DelegateEntry
{
    private final String m_schemaType;

    private final String m_className;

    private final Configuration m_configuration;

    private ConfigurationValidator m_validator;

    public DelegateEntry( String schemaType, String className, Configuration configuration )
    {
        this.m_className = className;
        this.m_configuration = configuration;
        this.m_schemaType = schemaType;
    }

    public String getSchemaType()
    {
        return this.m_schemaType;
    }

    public Configuration getConfiguration()
    {
        return this.m_configuration;
    }

    public String getClassName()
    {
        return this.m_className;
    }

    public ConfigurationValidator getValidator()
    {
        return this.m_validator;
    }

    public void setValidator( ConfigurationValidator validator )
    {
        this.m_validator = validator;
    }
}
