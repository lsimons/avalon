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

import org.apache.log.Hierarchy;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;
import org.apache.log.Priority;

/**
 * A basic adapter that adapts an Avalon Logger to a Logkit Logger.
 * Useful when providing backwards compatability support for Loggable
 * components.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.7 $ $Date: 2003/02/11 15:58:41 $
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
