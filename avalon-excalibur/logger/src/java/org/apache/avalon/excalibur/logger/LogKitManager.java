/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;

/**
 * LogKitManager Interface.
 *
 * @deprecated we should use the new LoggerManager interface that directly
 *             supports the new framework Logger interface.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 02:34:14 $
 */
public interface LogKitManager
{
    /**
     * Find a logger based on a category name.
     */
    Logger getLogger( String categoryName );

    /**
     * Retrieve Hierarchy for Loggers configured by the system.
     *
     * @return the Hierarchy
     */
    Hierarchy getHierarchy();
}
