/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.facilities;

import org.apache.avalon.framework.atlantis.Facility;
import org.apache.log.Logger;

/**
 * This facility manages the logs for an application instance.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface LogManager
    extends Facility
{
    /**
     * Get logger with category for application.
     * Note that this name may not be the absolute category.
     *
     * @param category the logger category
     * @return the Logger
     */
    Logger getLogger( String category );
}
