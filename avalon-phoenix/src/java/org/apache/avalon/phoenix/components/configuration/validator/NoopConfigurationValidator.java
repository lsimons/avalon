/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.configuration.validator;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidator;
import org.apache.avalon.phoenix.interfaces.ConfigurationValidatorMBean;

/**
 * A ConfigurationValidator that always says everything is okay
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class NoopConfigurationValidator
    implements ConfigurationValidator, ConfigurationValidatorMBean
{
    public void addSchema( final String application, final String block, final String schemaType, final String url )
        throws ConfigurationException
    {
    }

    public void removeSchema( final String application, final String block )
    {
    }

    public boolean isValid( final String application, final String block, final Configuration configuration )
        throws ConfigurationException
    {
        return true;
    }

    public boolean isFeasiblyValid( final String application, final String block, final Configuration configuration )
        throws ConfigurationException
    {
        return true;
    }

    public String getSchema( final String application, final String block )
    {
        return null;
    }

    public String getSchemaType( final String application, final String block )
    {
        return null;
    }

    public boolean isValid( final String application, final String block, final String configurationXml )

    {
        return true;
    }
}
