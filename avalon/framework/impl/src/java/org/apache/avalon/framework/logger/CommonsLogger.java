/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation. All rights
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

/**
 * The default Commons-Logging wrapper class for Logger.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @since 4.1.4
 * @see http://jakarta.apache.org/commons/logging/
 */
public final class CommonsLogger
    implements Logger, Log
{
    //underlying implementation to delegate to
    private final Log m_logger;

    /**
     * Create a logger that delegates to specified Log.
     *
     * @param logImpl the Commons-Logging Log to delegate to
     */
    public CommonsLogger( Log logImpl )
    {
        m_logger = logImpl;
    }

    /**
     * Log a debug message.
     *
     * @param o the message
     */
    public final void debug( final String message )
    {
        m_logger.debug( message );
    }

    /**
     * Log a debug message.
     *
     * @param o the message
     * @param throwable the throwable
     */
    public final void debug( final String message, final Throwable throwable )
    {
        m_logger.debug( message, throwable );
    }

    /**
     * Determine if messages of priority "debug" will be logged.
     *
     * @return true if "debug" messages will be logged
     */
    public final boolean isDebugEnabled()
    {
        return m_logger.isDebugEnabled();
    }

    /**
     * Log a info message.
     *
     * @param o the message
     */
    public final void info( final String message )
    {
        m_logger.info( message );
    }

    /**
     * Log a info message.
     *
     * @param o the message
     * @param throwable the throwable
     */
    public final void info( final String message, final Throwable throwable )
    {
        m_logger.info( message, throwable );
    }

    /**
     * Determine if messages of priority "info" will be logged.
     *
     * @return true if "info" messages will be logged
     */
    public final boolean isInfoEnabled()
    {
        return m_logger.isInfoEnabled();
    }

    /**
     * Log a warn message.
     *
     * @param o the message
     */
    public final void warn( final String message )
    {
        m_logger.warn( message );
    }

    /**
     * Log a warn message.
     *
     * @param o the message
     * @param throwable the throwable
     */
    public final void warn( final String message, final Throwable throwable )
    {
        m_logger.warn( message, throwable );
    }

    /**
     * Determine if messages of priority "warn" will be logged.
     *
     * @return true if "warn" messages will be logged
     */
    public final boolean isWarnEnabled()
    {
        return m_logger.isWarnEnabled();
    }

    /**
     * Log a error message.
     *
     * @param o the message
     */
    public final void error( final String message )
    {
        m_logger.error( message );
    }

    /**
     * Log a error message.
     *
     * @param o the message
     * @param throwable the throwable
     */
    public final void error( final String message, final Throwable throwable )
    {
        m_logger.error( message, throwable );
    }

    /**
     * Determine if messages of priority "error" will be logged.
     *
     * @return true if "error" messages will be logged
     */
    public final boolean isErrorEnabled()
    {
        return m_logger.isErrorEnabled();
    }

    /**
     * Log a fatalError message.
     *
     * @param o the message
     */
    public final void fatalError( final String message )
    {
        m_logger.fatal( message );
    }

    /**
     * Log a fatalError message.
     *
     * @param o the message
     * @param throwable the throwable
     */
    public final void fatalError( final String message, final Throwable throwable )
    {
        m_logger.fatal( message, throwable );
    }

    /**
     * Determine if messages of priority "fatalError" will be logged.
     *
     * @return true if "fatalError" messages will be logged
     */
    public final boolean isFatalErrorEnabled()
    {
        return m_logger.isFatalEnabled();
    }

    /**
     * Create a new child logger.
     * The name of the child logger is set to be equal to the passed-in name.
     *
     * Note that this method does <b>not</b> behave completely correctly as
     * specified by the Logger contract, as the commons-logging Log interface
     * does not allow access to the name of the current logger. Hence use of
     * this method is <b>not</b> recommended.
     *
     * Throws <code>IllegalArgumentException</code> if name has an empty element name
     *
     * @param name the subname of this logger
     * @return the new logger
     */
    public final Logger getChildLogger( final String name )
    {
        return new CommonsLogger( LogFactoryImpl.getLog( name ) );
    }

    ///
    /// IMPLEMENTATION OF the commons-logging Log interface
    ///

    /**
     * Determine if messages of priority "fatalError" will be logged.
     *
     * @return true if "fatalError" messages will be logged
     */
    public boolean isFatalEnabled()
    {
        return m_logger.isFatalEnabled();
    }

    /**
     * Determine if messages of priority "trace" will be logged.
     *
     * @return true if "trace" messages will be logged
     */
    public boolean isTraceEnabled()
    {
        return m_logger.isTraceEnabled();
    }

    /**
     * Log a trace message.
     *
     * @param o the message
     */
    public final void trace( Object o )
    {
        m_logger.trace( o );
    }

    /**
     * Log a trace message.
     *
     * @param o the message
     * @param throwable the throwable
     */
    public final void trace( Object o, Throwable throwable )
    {
        m_logger.trace( o, throwable );
    }

    /**
     * Log a debug message.
     *
     * @param o the message
     */
    public final void debug( Object o )
    {
        m_logger.debug( o );
    }

    /**
     * Log a debug message.
     *
     * @param o the message
     * @param throwable the throwable
     */
    public final void debug( Object o, Throwable throwable )
    {
        m_logger.debug( o, throwable );
    }

    /**
     * Log an info message.
     *
     * @param o the message
     */
    public final void info( Object o )
    {
        m_logger.info( o );
    }

    /**
     * Log an info message.
     *
     * @param o the message
     * @param throwable the throwable
     */
    public final void info( Object o, Throwable throwable )
    {
        m_logger.info( o, throwable );
    }

    /**
     * Log a warning message.
     *
     * @param o the message
     */
    public final void warn( Object o )
    {
        m_logger.warn( o );
    }

    /**
     * Log a warning message.
     *
     * @param o the message
     * @param throwable the throwable
     */
    public final void warn( Object o, Throwable throwable )
    {
        m_logger.warn( o, throwable );
    }

    /**
     * Log an error message.
     *
     * @param o the message
     */
    public final void error( Object o )
    {
        m_logger.error( o );
    }

    /**
     * Log an error message.
     *
     * @param o the message
     * @param throwable the throwable
     */
    public final void error( Object o, Throwable throwable )
    {
        m_logger.trace( o, throwable );
    }

    /**
     * Log a fatal error message.
     *
     * @param o the message
     */
    public final void fatal( Object o )
    {
        m_logger.trace( o );
    }

    /**
     * Log a fatal error message.
     *
     * @param o the message
     * @param throwable the throwable
     */
    public final void fatal( Object o, Throwable throwable )
    {
        m_logger.trace( o, throwable );
    }

}
