/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.16 $ $Date: 2003/02/11 15:58:41 $
 */
public final class Jdk14Logger
    implements Logger
{
    //The actual JDK1.4 logger implementation
    private final java.util.logging.Logger m_logger;

    /**
     * Construct a Logger with specified jdk1.4 logger instance as implementation.
     *
     * @param logImpl the jdk1.4 logger instance to delegate to
     */
    public Jdk14Logger( java.util.logging.Logger logImpl )
    {
        m_logger = logImpl;
    }

    /**
     * Log a debug message.
     *
     * @param message the message
     */
    public final void debug( final String message )
    {
        m_logger.log( Level.FINE, message );
    }

    /**
     * Log a debug message.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public final void debug( final String message, final Throwable throwable )
    {
        m_logger.log( Level.FINE, message, throwable );
    }

    /**
     * Determine if messages of priority "debug" will be logged.
     *
     * @return true if "debug" messages will be logged
     */
    public final boolean isDebugEnabled()
    {
        return m_logger.isLoggable( Level.FINE );
    }

    /**
     * Log a info message.
     *
     * @param message the message
     */
    public final void info( final String message )
    {
        m_logger.log( Level.INFO, message );
    }

    /**
     * Log a info message.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public final void info( final String message, final Throwable throwable )
    {
        m_logger.log( Level.INFO, message, throwable );
    }

    /**
     * Determine if messages of priority "info" will be logged.
     *
     * @return true if "info" messages will be logged
     */
    public final boolean isInfoEnabled()
    {
        return m_logger.isLoggable( Level.INFO );
    }

    /**
     * Log a warn message.
     *
     * @param message the message
     */
    public final void warn( final String message )
    {
        m_logger.log( Level.WARNING, message );
    }

    /**
     * Log a warn message.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public final void warn( final String message, final Throwable throwable )
    {
        m_logger.log( Level.WARNING, message, throwable );
    }

    /**
     * Determine if messages of priority "warn" will be logged.
     *
     * @return true if "warn" messages will be logged
     */
    public final boolean isWarnEnabled()
    {
        return m_logger.isLoggable( Level.WARNING );
    }

    /**
     * Log a error message.
     *
     * @param message the message
     */
    public final void error( final String message )
    {
        m_logger.log( Level.SEVERE, message );
    }

    /**
     * Log a error message.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public final void error( final String message, final Throwable throwable )
    {
        m_logger.log( Level.SEVERE, message, throwable );
    }

    /**
     * Determine if messages of priority "error" will be logged.
     *
     * @return true if "error" messages will be logged
     */
    public final boolean isErrorEnabled()
    {
        return m_logger.isLoggable( Level.SEVERE );
    }

    /**
     * Log a fatalError message.
     *
     * @param message the message
     */
    public final void fatalError( final String message )
    {
        m_logger.log( Level.SEVERE, message );
    }

    /**
     * Log a fatalError message.
     *
     * @param message the message
     * @param throwable the throwable
     */
    public final void fatalError( final String message, final Throwable throwable )
    {
        m_logger.log( Level.SEVERE, message, throwable );
    }

    /**
     * Determine if messages of priority "fatalError" will be logged.
     *
     * @return true if "fatalError" messages will be logged
     */
    public final boolean isFatalErrorEnabled()
    {
        return m_logger.isLoggable( Level.SEVERE );
    }

    /**
     * Create a new child logger.
     * The name of the child logger is [current-loggers-name].[passed-in-name]
     * Throws <code>IllegalArgumentException</code> if name has an empty element name
     *
     * @param name the subname of this logger
     * @return the new logger
     */
    public final Logger getChildLogger( final String name )
    {
        return new Jdk14Logger( java.util.logging.Logger
                                .getLogger( m_logger.getName() + "." + name ) );
    }
}
