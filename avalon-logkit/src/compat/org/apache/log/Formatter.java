/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

/**
 * This defines the interface for components that wish to serialize
 * LogEvents into Strings.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @deprecated Use org.apache.log.format.Formatter instead.
 */
public interface Formatter
    extends org.apache.log.format.Formatter
{
    /**
     * Serialize log event into string.
     *
     * @param event the event
     * @return the formatted string
     */
    String format( LogEvent event );
}
