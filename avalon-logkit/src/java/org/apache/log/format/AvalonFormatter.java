/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.format;

import org.apache.avalon.framework.CascadingThrowable;

/**
 * This formater formats the LogEntries according to a input pattern
 * string, and prints all exceptions in CascadingThrowable exceptions.
 *
 * The format of each pattern element can be %[+|-]#.#{field:subformat}
 *
 * The +|- indicates left or right justify.
 * The #.# indicates the minimum and maximum size of output.
 * 'field' indicates which field is to be output and must be one of
 *  proeprties of LogEvent
 * 'subformat' indicates a particular subformat and is currently unused.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class AvalonFormatter extends PatternFormatter
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
        StringBuffer buf = new StringBuffer(super.getStackTrace(throwable, format));

        if (throwable instanceof CascadingThrowable) {
            buf.append(getStackTrace(((CascadingThrowable) throwable).getCause(), format));
        }

        return buf.toString();
    }
}