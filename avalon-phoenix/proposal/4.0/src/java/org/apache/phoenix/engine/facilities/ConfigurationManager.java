/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities;

import org.apache.avalon.atlantis.Facility;
import org.apache.avalon.camelot.Entry;
import org.apache.avalon.camelot.pipeline.ConfigurationRepository;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.configuration.ConfigurationException;
import org.apache.phoenix.engine.blocks.BlockEntry;

/**
 * Repository from which all configuration data is retrieved.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultConfigurationRepository
    implements Facility
{
    public Configuration getConfiguration( String name, Entry entry )
        throws ConfigurationException
    {
        final BlockEntry blockEntry = (BlockEntry)entry;
        return blockEntry.getConfiguration();
    }
}
