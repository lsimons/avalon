/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities.configuration;

import org.apache.avalon.camelot.Entry;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.phoenix.engine.blocks.BlockEntry;
import org.apache.phoenix.engine.facilities.ConfigurationRepository;

/**
 * Repository from which all configuration data is retrieved.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultConfigurationRepository
    implements ConfigurationRepository
{
    public Configuration getConfiguration( final String name, final Entry entry )
        throws ConfigurationException
    {
        final BlockEntry blockEntry = (BlockEntry)entry;
        return blockEntry.getConfiguration();
    }
}
