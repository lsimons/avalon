/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.configuration.validator;

import org.apache.avalon.phoenix.interfaces.ConfigurationValidator;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * A ConfigurationValidator that always says everything is okay
 *
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 */
public class NoopConfigurationValidator implements ConfigurationValidator
{
    public void addSchema( String application, String block, String schemaType, String url )
      throws ConfigurationException
    {
    }

    public void removeSchema( String application, String block )
    {
    }

    public boolean isValid( String application, String block, Configuration configuration )
      throws ConfigurationException
    {
        return true;
    }

    public boolean isFeasiblyValid( String application, String block, Configuration configuration )
      throws ConfigurationException
    {
        return true;
    }
}
