/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.interfaces;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.context.Context;

/**
 * Interface that is used to manage Log objects for a Sar.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 */
public interface LogManager
{
    String ROLE = LogManager.class.getName();

    /**
     * Create a Logger hierarchy for an applicaiton.
     *
     * @param logs the configuration data for logs
     * @param context the context in which to build hierarchy
     * @return the configured Logger hierarchy
     * @throws Exception if an error occurs
     */
    Logger createHierarchy( Configuration logs, Context context )
        throws Exception;
}
