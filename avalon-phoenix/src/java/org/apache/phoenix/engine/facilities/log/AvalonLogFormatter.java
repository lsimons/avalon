/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.facilities.log;

import java.util.Date;
import org.apache.avalon.util.StringUtil;
import org.apache.log.format.PatternFormatter;

/**
 * Specialized formatter that knows about CascadingThrowables.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class AvalonLogFormatter
    extends PatternFormatter
{
    protected String getStackTrace( final Throwable throwable, final String format )
    {
        if( null == throwable ) return "";
        return StringUtil.printStackTrace( throwable, 8, true );
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
