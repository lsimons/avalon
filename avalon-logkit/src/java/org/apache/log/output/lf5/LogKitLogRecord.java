/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.lf5;

import org.apache.log.*;
import org.apache.log.format.Formatter;
import org.apache.log.util.StackIntrospector;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.lf5.LogRecord;
import org.apache.log4j.lf5.LogLevel;

/**
 * An implementation of a LogFactor5 <code>LogRecord</code> based on a
 * LogKit {@link LogEvent}.
 *
 * @author <a href="sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/01/23 09:08:45 $
 */

public class LogKitLogRecord extends LogRecord
{
    /** Is this a severe event ? */
    private boolean m_severe;

    /**
     * Create a LogFactor record from a LogKit event
     */
    public LogKitLogRecord( final LogEvent event, final Formatter fmt )
    {
        final ContextMap contextMap = event.getContextMap();

        Object contextObject;

        // Category
        this.setCategory( event.getCategory() );

        // Level
        this.setLevel( toLogLevel( event.getPriority() ) );
        m_severe = event.getPriority().isGreater( Priority.INFO );

        // Location
        if ( null != contextMap && null != ( contextObject = contextMap.get( "method" ) ) )
        {
            this.setLocation( contextObject.toString() );
        }
        else
        {
            this.setLocation( StackIntrospector.getCallerMethod( Logger.class ) );
        }

        // Message
        this.setMessage( event.getMessage() );

        // Millis
        this.setMillis( event.getTime() );

        // NDC
        this.setNDC( fmt.format(event) );

        // SequenceNumber
        //this.setSequenceNumber( 0L );

        // ThreadDescription
        if( null != contextMap && null != ( contextObject = contextMap.get( "thread" ) ) )
        {
            this.setThreadDescription( contextObject.toString() );
        }
        else
        {
            this.setThreadDescription( Thread.currentThread().getName() );
        }

        // Thrown
        this.setThrown( event.getThrowable() );

        // ThrownStackTrace
        //this.setThrownStackTrace("");
    }

    public boolean isSevereLevel()
    {
        return m_severe;
    }

    /**
     * Convert a LogKit <code>Priority</code> to a LogFactor <code>LogLevel</code>.
     */
    public LogLevel toLogLevel( final Priority priority )
    {
        if ( Priority.DEBUG == priority )
            return LogLevel.DEBUG;
        else if ( Priority.INFO == priority )
            return LogLevel.INFO;
        else if ( Priority.WARN == priority )
            return LogLevel.WARN;
        else if ( Priority.ERROR == priority )
            return LogLevel.ERROR;
        else if ( Priority.FATAL_ERROR == priority )
            return LogLevel.FATAL;
        else
            return new LogLevel( priority.getName(), priority.getValue() );
    }

    /**
     * The <code>LogLevel</code>s corresponding to LogKit priorities.
     */
    public final static List LOGKIT_LOGLEVELS =
        Arrays.asList(new LogLevel[] {
            LogLevel.FATAL, LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO, LogLevel.DEBUG
        });
}