/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.excalibur.containerkit.lifecycle.LifecycleException;
import org.apache.avalon.framework.configuration.Configuration;

/**
 * PUnit helper
 * @author Paul Hammant
 */
public interface PUnit
{

    /**
     * Query the log
     * @param startsWith For an expression that starts with this
     * @return The logged entry.
     */
    String lookupInLog( String startsWith );

    /**
     * Query the log
     * @param startsWith For an expression that starts with this
     * @return true or not
     */
    boolean logHasEntry( String startsWith );

    /**
     * Add a block
     * @param blockName The block name
     * @param block The block
     * @param serviceName The service name (for lookup)
     * @param configuration The configuration
     */
    void addBlock( final String blockName,
                             final String serviceName,
                             final Object block,
                             final Configuration configuration );

    /**
     * Run blocks thru startup.
     * @throws LifecycleException If a problem
     */
    void startup() throws LifecycleException;


    /**
     * Run blocks thru shutdown
     * @throws LifecycleException If a problem
     */
    void shutdown() throws LifecycleException;

}
