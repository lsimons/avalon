/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger;

import org.apache.log.LogTarget;

/**
 * LogTargetManager Interface.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 02:34:14 $
 * @since 4.0
 */
public interface LogTargetManager
{
    /**
     * Find a logger based on a category name.
     */
    LogTarget getLogTarget( String targetId );
}
