/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.logger;

/**
 * Components that need to log can implement this interface to
 * be provided Loggers.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public interface LogEnabled
{
    /**
     * Provide component with a logger.
     *
     * @param logger the logger
     */
    void enableLogging( Logger logger );
}
