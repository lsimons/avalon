/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.util;

import org.apache.log.Logger;

/**
 * The LoggerListener class is used to notify listeners
 * when a new Logger object is created. Loggers are created
 * when a client requests a new Logger via {@link Logger#getChildLogger}.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public abstract class LoggerListener
{
    /**
     * Notify listener that Logger was created.
     *
     * @param category the error message
     * @param logger the logger that was created
     */
    public abstract void loggerCreated( String category, Logger logger );
}
