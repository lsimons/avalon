/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Repository from which all configuration data is retrieved.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public interface ConfigurationRepository
    extends Component
{
    String ROLE = "org.apache.avalon.phoenix.interfaces.ConfigurationRepository";

    Configuration getConfiguration( String application, String block )
        throws ConfigurationException;

    void storeConfiguration( String application, String block, Configuration configuration )
        throws ConfigurationException;
}
