/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.logger;

import java.util.Date;
import org.apache.avalon.framework.ExceptionUtil;
import org.apache.log.format.PatternFormatter;

/**
 * This formatter extends PatternFormatter so that
 * CascadingExceptions are formatted with all nested exceptions.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class AvalonFormatter
    extends PatternFormatter
{
    /**
     * The constant defining the default stack depth when 
     * none other is specified.
     */
    private static final int DEFAULT_STACK_DEPTH = 8;

    //The depth to which stacktraces are printed out
    private final int m_stackDepth;

    /**
     * Construct the formatter with the specified pattern 
     * and which which prints out exceptions to stackDepth of 8.
     */
    public AvalonFormatter( final String pattern )
    {
        this( pattern, DEFAULT_STACK_DEPTH );
    }

    /**
     * Construct the formatter with the specified pattern 
     * and which which prints out exceptions to stackDepth specified.
     */
    public AvalonFormatter( final String pattern, final int stackDepth )
    {
        super( pattern );
        m_stackDepth = stackDepth;
    }

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
        return ExceptionUtil.printStackTrace( throwable, m_stackDepth, true );
    }
}
