/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger;

import org.apache.avalon.framework.logger.Logger;

/**
 * LoggerManager Interface.  This is the interface used to get instances of
 * a Logger for your system.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 02:34:15 $
 */
public interface LoggerManager
{
    /**
     * Return the Logger for the specified category.
     */
    Logger getLoggerForCategory( String categoryName );

    /**
     * Return the default Logger.  This is basically the same
     * as getting the Logger for the "" category.
     */
    Logger getDefaultLogger();
}