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
 * This formatter extends PatternFormatter so that 
 * CascadingExceptions are formatted with all nested exceptions.
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
        final StringBuffer sb = new StringBuffer();
        sb.append( super.getStackTrace( throwable, format ) );

        if( throwable instanceof CascadingThrowable )
        {
            final Throwable t = ((CascadingThrowable)throwable).getCause();

            sb.append( getStackTrace( t, format ) );
        }

        return sb.toString();
    }
}
