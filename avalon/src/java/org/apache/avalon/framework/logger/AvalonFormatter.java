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
    public static final int DEFAULT_STACK_DEPTH = 8;

    /**
     * The constant defining the default behaviour for printing
     * nested exceptions.
     */
    public static final boolean DEFAULT_PRINT_CASCADING = true;

    //The depth to which stacktraces are printed out
    private final int m_stackDepth;

    //Determines if nested exceptions should be logged
    private final boolean m_printCascading;

    /**
     * Construct the formatter with the specified pattern
     * and which which prints out exceptions to stackDepth of 8.
     *
     * @param pattern The pattern to use to format the log entries
     */
    public AvalonFormatter( final String pattern )
    {
        this( pattern, DEFAULT_STACK_DEPTH, DEFAULT_PRINT_CASCADING );
    }

    /**
     * Construct the formatter with the specified pattern
     * and which which prints out exceptions to stackDepth specified.
     *
     * @param pattern The pattern to use to format the log entries
     * @param stackDepth The depth to which stacktraces are printed out
     * @param printCascading true enables printing of nested exceptions,
     *   false only prints out the outermost exception
     */
    public AvalonFormatter( final String pattern, final int stackDepth, final boolean printCascading )
    {
        super( pattern );
        m_stackDepth = stackDepth;
        m_printCascading = printCascading;
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
        return ExceptionUtil.printStackTrace( throwable, m_stackDepth, m_printCascading );
    }
}
