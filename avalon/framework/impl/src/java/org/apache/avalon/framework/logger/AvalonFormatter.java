/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.format;

import java.util.Date;
import org.apache.avalon.framework.ExceptionUtil;

/**
 * This formatter extends PatternFormatter so that 
 * CascadingExceptions are formatted with all nested exceptions.
 *
 * @deprecated Use <code>org.apache.avalon.framework.logger.AvalonFormatter</code>
               instead of this one.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class AvalonFormatter 
    extends PatternFormatter
{
    /**
     * Utility method to format stack trace.
     *
     * @param throwable the throwable instance
     * @param format ancilliary format parameter - allowed to be null
     * @return the formatted string
     */
    protected String getStackTrace( final Throwable throwable, final String format )
    {
        if( null == throwable ) return "";
        return ExceptionUtil.printStackTrace( throwable, 8, true );
    }

    /**
     * Utility method to format time.
     *
     * @param time the time
     * @param format ancilliary format parameter - allowed to be null
     * @return the formatted string
     */
    protected String getTime( final long time, final String format )
    {
        return new Date().toString();
    }
}
