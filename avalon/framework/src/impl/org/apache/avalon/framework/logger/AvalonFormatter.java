/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.logger;

import org.apache.avalon.framework.ExceptionUtil;
import org.apache.log.format.ExtendedPatternFormatter;
import org.apache.log.format.PatternFormatter;
import org.apache.log.LogEvent;
import org.apache.log.ContextMap;
import org.apache.log.util.StackIntrospector;

/**
 * This formatter extends ExtendedPatternFormatter so that
 * CascadingExceptions are formatted with all nested exceptions.
 *
 * <ul>
 * <li><code>class</code> : outputs the name of the class that has logged the
 *     message. The optional <code>short</code> subformat removes the
 *     package name. Warning : this pattern works only if formatting occurs in
 *     the same thread as the call to Logger, i.e. it won't work with
 *     <code>AsyncLogTarget</code>.</li>
 * </ul>
 *
 * @author <a href="mailto:sylvain@apache.org">Sylvain Wallez</a>
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public class AvalonFormatter
    extends ExtendedPatternFormatter
{
    private static final int TYPE_CLASS = MAX_TYPE + 1;

    private static final String TYPE_CLASS_STR = "class";
    private final static String TYPE_CLASS_SHORT_STR = "short";

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
    public AvalonFormatter( final String pattern, final int stackDepth,
                            final boolean printCascading )
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
        if( null == throwable )
        {
            return "";
        }
        return ExceptionUtil.printStackTrace( throwable, m_stackDepth, m_printCascading );
    }

    /**
     * Retrieve the type-id for a particular string.
     *
     * @param type the string
     * @return the type-id
     */
    protected int getTypeIdFor( final String type )
    {
        if( type.equalsIgnoreCase( TYPE_CLASS_STR ) )
        {
            return TYPE_CLASS;
        }
        else
        {
            return super.getTypeIdFor( type );
        }
    }

    protected String formatPatternRun( LogEvent event, PatternFormatter.PatternRun run )
    {
        switch( run.m_type )
        {
            case TYPE_CLASS:
                return getClass( run.m_format );
            default:
                return super.formatPatternRun( event, run );
        }
    }

    /**
     * Finds the class that has called Logger.
     */
    private String getClass( String format )
    {
        final Class clazz = StackIntrospector.getCallerClass( Logger.class );

        if( null == clazz )
        {
            return "Unknown-class";
        }
        else
        {
            // Found : the caller is the previous stack element
            String className = clazz.getName();

            // Handle optional format
            if( TYPE_CLASS_SHORT_STR.equalsIgnoreCase( format ) )
            {
                int pos = className.lastIndexOf( '.' );

                if( pos >= 0 )
                {
                    className = className.substring( pos + 1 );
                }
            }

            return className;
        }
    }
}
