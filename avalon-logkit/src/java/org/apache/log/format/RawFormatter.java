/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.format;

import org.apache.log.LogEvent;

/**
 * Basic formatter that just returns raw 
 * message string and ignores remainder of LogEvent.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class RawFormatter
    implements Formatter, org.apache.log.Formatter
{
    /**
     * Format log event into string.
     *
     * @param event the event
     * @return the formatted string
     */
    public String format( final LogEvent event )
    {
        final String message = event.getMessage();
        if( null == message )
        {
            return "";
        }
        else
        {
            return message;
        }
    }
}
