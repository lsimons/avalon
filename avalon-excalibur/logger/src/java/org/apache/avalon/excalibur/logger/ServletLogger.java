/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.excalibur.logger;

import org.apache.avalon.framework.logger.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Logger to bootstrap avalon application iside a servlet.
 * Intended to be used as a logger for Fortress
 * ContextManager/ContainerManager.
 * 
 * Adapted from ConsoleLogger.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/06/09 08:14:07 $
 */

public class ServletLogger implements Logger
{
    /** Typecode for debugging messages. */
    public static final int LEVEL_DEBUG = 0;

    /** Typecode for informational messages. */
    public static final int LEVEL_INFO = 1;

    /** Typecode for warning messages. */
    public static final int LEVEL_WARN = 2;

    /** Typecode for error messages. */
    public static final int LEVEL_ERROR = 3;

    /** Typecode for fatal error messages. */
    public static final int LEVEL_FATAL = 4;

    /** Typecode for disabled log levels. */
    public static final int LEVEL_DISABLED = 5;

    private final ServletContext m_servletContext;
    private final int m_logLevel;
    private final String m_prompt;

    /**
     * Creates a new ServletLogger with the priority set to DEBUG.
     */
    public ServletLogger( final ServletConfig servletConfig )
    {
        this( servletConfig, LEVEL_DEBUG );
    }

    /** Helper method to write the constructors. */
    private void checkState()
    {
        if ( m_servletContext == null )
        {
            throw new NullPointerException( "servletContext" );
        }
        if ( m_logLevel < LEVEL_DEBUG || m_logLevel > LEVEL_DISABLED )
        {
            throw new IllegalArgumentException( "Bad logLevel: " + m_logLevel );
        }
    }

    /**
     * Creates a new ServletLogger.
     * @param servletContext ServletContext to log messages to
     * @param prompt text to prepend to every message
     * @param logLevel log level typecode
     */
    public ServletLogger( final ServletContext servletContext, final String prompt,
            final int logLevel )
    {
        m_servletContext = servletContext;
        m_logLevel = logLevel;
        checkState();
        m_prompt = prompt;
    }

    /**
     * Creates a new ServletLogger.
     * @param servletConfig the servletConfig to extract ServletContext from;
     *        also the servlet name is extracted to be prepended to every message.
     * @param logLevel log level typecode
     */
    public ServletLogger( final ServletConfig servletConfig, final int logLevel )
    {
        m_servletContext = servletConfig.getServletContext();
        m_logLevel = logLevel;
        checkState();

        final String servletName = servletConfig.getServletName();

        if ( servletName == null || "".equals( servletName ) )
        {
            m_prompt = "unknown: ";
        }
        else
        {
            m_prompt = servletName + ": ";
        }
    }

    /**
     * Logs a debugging message.
     *
     * @param message a <code>String</code> value
     */
    public void debug( final String message )
    {
        debug( message, null );
    }

    /**
     * Logs a debugging message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void debug( final String message, final Throwable throwable )
    {
        if( m_logLevel <= LEVEL_DEBUG )
        {
            m_servletContext.log( m_prompt + "[DEBUG] " + message, throwable );
        }
    }

    /**
     * Returns <code>true</code> if debug-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if debug-level logging
     */
    public boolean isDebugEnabled()
    {
        return m_logLevel <= LEVEL_DEBUG;
    }

    /**
     * Logs an informational message.
     *
     * @param message a <code>String</code> value
     */
    public void info( final String message )
    {
        info( message, null );
    }

    /**
     * Logs an informational message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void info( final String message, final Throwable throwable )
    {
        if( m_logLevel <= LEVEL_INFO )
        {
            m_servletContext.log( m_prompt + "[INFO] " + message, throwable );
        }
    }

    /**
     * Returns <code>true</code> if info-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if info-level logging is enabled
     */
    public boolean isInfoEnabled()
    {
        return m_logLevel <= LEVEL_INFO;
    }

    /**
     * Logs a warning message.
     *
     * @param message a <code>String</code> value
     */
    public void warn( final String message )
    {
        warn( message, null );
    }

    /**
     * Logs a warning message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void warn( final String message, final Throwable throwable )
    {
        if( m_logLevel <= LEVEL_WARN )
        {
            m_servletContext.log( m_prompt + "[WARNING] " + message, throwable );
        }
    }

    /**
     * Returns <code>true</code> if warn-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if warn-level logging is enabled
     */
    public boolean isWarnEnabled()
    {
        return m_logLevel <= LEVEL_WARN;
    }

    /**
     * Logs an error message.
     *
     * @param message a <code>String</code> value
     */
    public void error( final String message )
    {
        error( message, null );
    }

    /**
     * Logs an error message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void error( final String message, final Throwable throwable )
    {
        if( m_logLevel <= LEVEL_ERROR )
        {
            m_servletContext.log( m_prompt + "[ERROR] " + message, throwable );
        }
    }

    /**
     * Returns <code>true</code> if error-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if error-level logging is enabled
     */
    public boolean isErrorEnabled()
    {
        return m_logLevel <= LEVEL_ERROR;
    }

    /**
     * Logs a fatal error message.
     *
     * @param message a <code>String</code> value
     */
    public void fatalError( final String message )
    {
        fatalError( message, null );
    }

    /**
     * Logs a fatal error message and an exception.
     *
     * @param message a <code>String</code> value
     * @param throwable a <code>Throwable</code> value
     */
    public void fatalError( final String message, final Throwable throwable )
    {
        if( m_logLevel <= LEVEL_FATAL )
        {
            m_servletContext.log( m_prompt + "[FATAL ERROR] " + message, throwable );
        }
    }

    /**
     * Returns <code>true</code> if fatal-level logging is enabled, false otherwise.
     *
     * @return <code>true</code> if fatal-level logging is enabled
     */
    public boolean isFatalErrorEnabled()
    {
        return m_logLevel <= LEVEL_FATAL;
    }

    /**
     * Just returns this logger (<code>ServletLogger</code> is not hierarchical).
     *
     * @param name ignored
     * @return this logger
     */
    public Logger getChildLogger( final String name )
    {
        return this;
    }
}
