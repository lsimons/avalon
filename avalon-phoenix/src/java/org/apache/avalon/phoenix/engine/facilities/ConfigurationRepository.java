/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.facilities;

import org.apache.avalon.framework.camelot.Entry;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Repository from which all configuration data is retrieved.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ConfigurationRepository
    extends Component
{
    Configuration getConfiguration( String name, Entry entry )
        throws ConfigurationException;
}
