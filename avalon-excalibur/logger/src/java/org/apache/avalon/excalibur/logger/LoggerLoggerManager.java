/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger;

import org.apache.avalon.framework.logger.Logger;

/**
 * A LoggerManager that operates off of an existing Logger instance.
 *
 * @author <a href="proyal@apache.org">Peter Royal</a>
 */
public class LoggerLoggerManager implements LoggerManager
{
    private final Logger logger;

    public LoggerLoggerManager( Logger logger )
    {
        this.logger = logger;
    }

    public Logger getLoggerForCategory( String categoryName )
    {
        return logger.getChildLogger( categoryName );
    }

    public Logger getDefaultLogger()
    {
        return logger;
    }
}
