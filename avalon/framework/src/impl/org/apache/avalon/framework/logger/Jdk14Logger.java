/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.logger;

import java.util.logging.Level;

/**
 * The default JDK 1.4 wrapper class for Logger.  Please note that there is
 * not an exact match to the priority levels that JDK 1.4 logging has and
 * what LogKit or Log4J has.  For that reason, the following priority level
 * matching was used:
 *
 * <ul>
 *   <li>SEVERE  = error, fatalError</li>
 *   <li>WARNING = warn</li>
 *   <li>INFO    = info</li>
 *   <li>FINE    = debug</li>
 * </ul>
 *
 * <p>
 *   JDK 1.4 does allow you to have other levels like: CONFIG, FINER, and
 *   FINEST.  Most projects don't separate out configuration logging from
 *   debugging information.  Also, we wanted to maintain backwards
 *   compatibility as much as possible.  Unfortunately, with all the "fineness"
 *   details, there is no equivalent to the "error" log level.
 * </p>
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class Jdk14Logger implements Logger
{
    private final java.util.logging.Logger m_logger;

    public Jdk14Logger( java.util.logging.Logger logImpl )
    {
        m_logger = logImpl;
    }

    public final void debug( final String message )
    {
        m_logger.log( Level.FINE, message );
    }

    public final void debug( final String message, final Throwable throwable )
    {
        m_logger.log( Level.FINE, message, throwable );
    }

    public final boolean isDebugEnabled()
    {
        return m_logger.isLoggable( Level.FINE );
    }

    public final void info( final String message )
    {
        m_logger.log( Level.INFO, message );
    }

    public final void info( final String message, final Throwable throwable )
    {
        m_logger.log( Level.INFO, message, throwable );
    }

    public final boolean isInfoEnabled()
    {
        return m_logger.isLoggable( Level.INFO );
    }

    public final void warn( final String message )
    {
        m_logger.log( Level.WARNING, message );
    }

    public final void warn( final String message, final Throwable throwable )
    {
        m_logger.log( Level.WARNING, message, throwable );
    }

    public final boolean isWarnEnabled()
    {
        return m_logger.isLoggable( Level.WARNING );
    }

    public final void error( final String message )
    {
        m_logger.log( Level.SEVERE, message );
    }

    public final void error( final String message, final Throwable throwable )
    {
        m_logger.log( Level.SEVERE, message, throwable );
    }

    public final boolean isErrorEnabled()
    {
        return m_logger.isLoggable( Level.SEVERE );
    }

    public final void fatalError( final String message )
    {
        m_logger.log( Level.SEVERE, message );
    }

    public final void fatalError( final String message, final Throwable throwable )
    {
        m_logger.log( Level.SEVERE, message, throwable );
    }

    public final boolean isFatalErrorEnabled()
    {
        return m_logger.isLoggable( Level.SEVERE );
    }

    public final Logger getChildLogger( final String name )
    {
        return new Jdk14Logger( java.util.logging.Logger
                                .getLogger( m_logger.getName() + "." + name ) );
    }
}