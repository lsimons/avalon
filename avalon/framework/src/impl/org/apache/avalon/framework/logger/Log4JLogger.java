/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.logger;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

/**
 * The default Log4J wrapper class for Logger.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class Log4JLogger implements Logger
{
    private final Category m_logger;

    public Log4JLogger( Category logImpl )
    {
        m_logger = logImpl;
    }

    public final void debug( final String message )
    {
        m_logger.debug(message);
    }

    public final void debug( final String message, final Throwable throwable )
    {
        m_logger.debug( message, throwable );
    }

    public final boolean isDebugEnabled()
    {
        return m_logger.isDebugEnabled();
    }

    public final void info( final String message )
    {
        m_logger.info( message );
    }

    public final void info( final String message, final Throwable throwable )
    {
        m_logger.info( message, throwable );
    }

    public final boolean isInfoEnabled()
    {
        return m_logger.isInfoEnabled();
    }

    public final void warn( final String message )
    {
        m_logger.warn( message );
    }

    public final void warn( final String message, final Throwable throwable )
    {
        m_logger.warn( message, throwable );
    }

    public final boolean isWarnEnabled()
    {
        return m_logger.isEnabledFor( Priority.WARN );
    }

    public final void error( final String message )
    {
        m_logger.error( message );
    }

    public final void error( final String message, final Throwable throwable )
    {
        m_logger.error( message, throwable );
    }

    public final boolean isErrorEnabled()
    {
        return m_logger.isEnabledFor( Priority.ERROR );
    }

    public final void fatalError( final String message )
    {
        m_logger.fatal( message );
    }

    public final void fatalError( final String message, final Throwable throwable )
    {
        m_logger.fatal( message, throwable );
    }

    public final boolean isFatalErrorEnabled()
    {
        return m_logger.isEnabledFor( Priority.FATAL );
    }

    public final Logger getChildLogger( final String name )
    {
        return new Log4JLogger( Category.getInstance( m_logger.getName() + "." + name ) );
    }
}