/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.logger;

import org.apache.log.Hierarchy;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;
import org.apache.log.Priority;

/**
 * A basic adapter that adapts an Avalon Logger to a Logkit Logger.
 * Useful when providing backwards compatability support for Loggable
 * components.
 *
 * @author <a href="mailto:avalon-dev@jakarta.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2002/11/23 08:58:59 $
 */
public final class LogKit2AvalonLoggerAdapter
    implements LogTarget
{
    /**
     * The Avalon Logger that we re-route to.
     */
    private final Logger m_logger;

    /**
     * Create a Logkit {@link org.apache.log.Logger} instance that
     * redirects to an Avalon {@link org.apache.avalon.framework.logger.Logger} instance.
     *
     * @param logger the Avalon Logger
     * @return the LogKit Logger
     */
    public static org.apache.log.Logger createLogger( final Logger logger )
    {
        final Hierarchy hierarchy = new Hierarchy();
        final org.apache.log.Logger logKitLogger = hierarchy.getLoggerFor( "" );
        final LogKit2AvalonLoggerAdapter target =
            new LogKit2AvalonLoggerAdapter( logger );
        logKitLogger.setLogTargets( new LogTarget[ ] { target } );
        return logKitLogger;
    }

    /**
     * Constructor for an Adaptor. Adapts to
     * specified Avalon Logger.
     *
     * @param logger the avalon logger.
     */
    public LogKit2AvalonLoggerAdapter( final Logger logger )
    {
        if( null == logger )
        {
            throw new NullPointerException( "logger" );
        }
        m_logger = logger;
    }

    /**
     * Route a LogKit message to an avalon Logger.
     *
     * @param event the log message
     */
    public void processEvent( LogEvent event )
    {
        final String message = event.getMessage();
        final Throwable throwable = event.getThrowable();
        final Priority priority = event.getPriority();
        if( Priority.DEBUG == priority )
        {
            m_logger.debug( message, throwable );
        }
        else if( Priority.INFO == priority )
        {
            m_logger.info( message, throwable );
        }
        else if( Priority.WARN == priority )
        {
            m_logger.warn( message, throwable );
        }
        else if( Priority.ERROR == priority )
        {
            m_logger.error( message, throwable );
        }
        else
        {
            m_logger.fatalError( message, throwable );
        }
    }
}
