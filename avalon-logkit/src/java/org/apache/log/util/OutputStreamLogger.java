/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.util;

import org.apache.log.Logger;
import org.apache.log.Priority;

/**
 * Redirect an output stream to a logger.
 * This class is useful to redirect standard output or
 * standard error to a Logger. An example use is
 *
 * <pre>
 * final OutputStreamLogger outputStream =
 *     new OutputStreamLogger( logger, Priority.DEBUG );
 * final PrintStream output = new PrintStream( outputStream, true );
 *
 * System.setOut( output );
 * </pre>
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @deprecated Use LoggerOutputStream as this class was misnamed.
 */
public class OutputStreamLogger
    extends LoggerOutputStream
{
    /**
     * Construct OutputStreamLogger to write to a particular logger at a particular priority.
     *
     * @param logger the logger to write to
     * @param priority the priority at which to log
     * @deprecated Use LoggerOutputStream as this class was misnamed.
     */
    public OutputStreamLogger( final Logger logger,
                               final Priority priority )
    {
        super( logger, priority );
    }
}
