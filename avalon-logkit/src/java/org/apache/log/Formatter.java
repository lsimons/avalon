/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

/**
 * Formatter's format log events into strings.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Formatter
{
    /**
     * Format log event into string.
     *
     * @param event the event
     * @return the formatted string
     */
    String format( LogEvent event );
}
