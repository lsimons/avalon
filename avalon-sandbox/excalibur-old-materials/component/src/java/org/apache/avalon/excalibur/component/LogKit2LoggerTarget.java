/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component;

import org.apache.avalon.framework.logger.Logger;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;
import org.apache.log.Priority;
import org.apache.log.Hierarchy;

/**
 * A basic LogKit target that routes from LogKit to
 * Avalon Logger.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2002/06/02 06:03:01 $
 */
class LogKit2LoggerTarget
    implements LogTarget
{
    private final Logger m_logger;

    static org.apache.log.Logger createLogger( final Logger logger )
    {
        final Hierarchy hierarchy = new Hierarchy();
        final org.apache.log.Logger logKitLogger = hierarchy.getLoggerFor( "" );
        final LogKit2LoggerTarget target =
            new LogKit2LoggerTarget( logger );
        logKitLogger.setLogTargets( new LogTarget[ ] { target } );
        return logKitLogger;
    }

    LogKit2LoggerTarget( final Logger logger )
    {
        if( null == logger )
        {
            throw new NullPointerException( "logger" );
        }
        m_logger = logger;
    }

    public void processEvent( LogEvent event )
    {
        final Logger logger = getLoggerForEvent( event );

        final String message = event.getMessage();
        final Throwable throwable = event.getThrowable();
        final Priority priority = event.getPriority();
        if( Priority.DEBUG == priority )
        {
            logger.debug( message, throwable );
        }
        else if( Priority.INFO == priority )
        {
            logger.info( message, throwable );
        }
        else if( Priority.WARN == priority )
        {
            logger.warn( message, throwable );
        }
        else if( Priority.ERROR == priority )
        {
            logger.error( message, throwable );
        }
        else
        {
            logger.fatalError( message, throwable );
        }
    }

    /**
     * Retrieve Logger for event. If event is from a child
     * Log
     *
     * @param event the LogEvent
     * @return the Logger
     */
    private Logger getLoggerForEvent( final LogEvent event )
    {
        final String category = event.getCategory();
        Logger logger = m_logger;
        if( !"".equals( category ) )
        {
            logger = m_logger.getChildLogger( category );
        }
        return logger;
    }
}
