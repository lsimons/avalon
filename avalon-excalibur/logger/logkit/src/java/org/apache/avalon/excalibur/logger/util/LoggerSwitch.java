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
package org.apache.avalon.excalibur.logger.util;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;

/**
 * A proxy logger that switches between two underlying loggers
 * with recursive invocation detection.
 * <p>
 * This class is intended to be used by o.a.a.e.logger.AbstractLoggerManager.
 * all the logger switching is done in it during the "warm-up" phase
 * (constructor, enableLogging, contextualize, configure, start).
 * All these operations are held our on a single thread and the
 * object is not exposed to other threads untill (in strict synchronization
 * sense) it has been fully configured. That's why there is no synchronization
 * in this class. If the switching was to occur in a mulitythreaded
 * fasion we would have to synchronize access to m_fallback and m_preferred.
 *
 * @author <a href="mailto:tagunov@motor.ru">Anton Tagunov</a>
 */

public class LoggerSwitch
{
    private final static Logger SHARED_NULL = new NullLogger();

    private final static class BooleanThreadLocal extends ThreadLocal
    {
        public Object initialValue() { return Boolean.FALSE; }
        public boolean value() { return ((Boolean)this.get()).booleanValue(); }
    }

    private static class SwitchingLogger implements Logger
    {
        Logger m_fallback;
        Logger m_preferred;

        BooleanThreadLocal m_recursionOnPreferred = new BooleanThreadLocal();
        BooleanThreadLocal m_recursionOnFallback = new BooleanThreadLocal();

        SwitchingLogger( final Logger fallback, final Logger preferred )
        {
            m_fallback = fallback != null ? fallback : SHARED_NULL;
            m_preferred = preferred;
        }

        void setFallback( final Logger fallback )
        {
            m_fallback = fallback != null ? fallback : SHARED_NULL;
        }

        void setPreferred( final Logger preferred )
        {
            m_preferred = preferred;
        }

        /**
         * Retrieve m_preferred or if that is null m_fallback.
         * Safeguard against recursion. That is possible if
         * try to log something via a Logger that is failing
         * and trying to log its own error via itself.
         */
        private Logger getLogger()
        {
            final Logger fallback;
            final Logger preferred;

            if ( m_recursionOnFallback.value() )
            {
                throw new IllegalStateException( "infinite recursion" );
            }
            else if ( m_recursionOnPreferred.value() || m_preferred == null )
            {
                m_recursionOnFallback.set( Boolean.TRUE );
                return m_fallback;
            }
            else
            {
                m_recursionOnPreferred.set( Boolean.TRUE );
                return m_preferred;
            }
        }

        private Logger getLoggerLight()
        {
            return m_preferred != null ? m_preferred : m_fallback;
        }

        private void releaseLogger()
        {
            if ( m_recursionOnFallback.value() )
            {
                m_recursionOnFallback.set( Boolean.FALSE );
            }
            else if ( m_recursionOnPreferred.value() )
            {
                m_recursionOnPreferred.set( Boolean.FALSE );
            }
            else
            {
                throw new IllegalStateException( "no recursion" );
            }
        }

        public void debug( final String message )
        {
            final Logger logger = getLogger();
            try
            {
                logger.debug( message );
            }
            finally
            {
                releaseLogger();
            }
        }
    
        public void debug( final String message, final Throwable throwable )
        {
            final Logger logger = getLogger();
            try
            {
                logger.debug( message, throwable );
            }
            finally
            {
                releaseLogger();
            }
        }
    
        /** 
         * This and similar method may probably be optimized in the
         * future by caching the boolean in our instance variables.
         * Each time setPreferred() or setFallback() is called they
         * will be cached. Maybe in the future. :-)
         */
        public boolean isDebugEnabled()
        {
            final Logger logger = getLoggerLight();
            return logger.isDebugEnabled();
        }

    
        public void info( final String message )
        {
            final Logger logger = getLogger();
            try
            {
                logger.info( message );
            }
            finally
            {
                releaseLogger();
            }
        }
    
        public void info( final String message, final Throwable throwable )
        {
            final Logger logger = getLogger();
            try
            {
                logger.info( message, throwable );
            }
            finally
            {
                releaseLogger();
            }
        }
    
        public boolean isInfoEnabled()
        {
            final Logger logger = getLoggerLight();
            return logger.isInfoEnabled();
        }
    
        public void warn( final String message )
        {
            final Logger logger = getLogger();
            try
            {
                logger.warn( message );
            }
            finally
            {
                releaseLogger();
            }
        }
    
        public void warn( final String message, final Throwable throwable )
        {
            final Logger logger = getLogger();
            try
            {
                logger.warn( message, throwable );
            }
            finally
            {
                releaseLogger();
            }
        }
    
        public boolean isWarnEnabled()
        {
            final Logger logger = getLoggerLight();
            return logger.isWarnEnabled();
        }
    
        public void error( final String message )
        {
            final Logger logger = getLogger();
            try
            {
                logger.error( message );
            }
            finally
            {
                releaseLogger();
            }
        }
    
        public void error( final String message, final Throwable throwable )
        {
            final Logger logger = getLogger();
            try
            {
                logger.error( message, throwable );
            }
            finally
            {
                releaseLogger();
            }
        }
    
        public boolean isErrorEnabled()
        {
            final Logger logger = getLoggerLight();
            return logger.isErrorEnabled();
        }
    
        public void fatalError( final String message )
        {
            final Logger logger = getLogger();
            try
            {
                logger.fatalError( message );
            }
            finally
            {
                releaseLogger();
            }
        }
    
        public void fatalError( final String message, final Throwable throwable )
        {
            final Logger logger = getLogger();
            try
            {
                logger.fatalError( message, throwable );
            }
            finally
            {
                releaseLogger();
            }
        }
    
        public boolean isFatalErrorEnabled()
        {
            final Logger logger = getLoggerLight();
            return logger.isFatalErrorEnabled();
        }
    
        public Logger getChildLogger( final String name ) { return this; }

    }

    private SwitchingLogger m_switch;

    /**
     * We create a logger with no methods for changing
     * m_fallback and m_preferred for security reasons.
     * All the control is done by the parent class
     * that does not implement Logger itself.
     */
    public Logger get()
    {
        return m_switch;
    }

    public LoggerSwitch( final Logger fallback )
    {
        this( fallback, null );
    }

    public LoggerSwitch( final Logger fallback, final Logger preferred )
    {
        m_switch = new SwitchingLogger( fallback, preferred );
    }

    public void setFallback( final Logger fallback )
    {
        m_switch.setFallback( fallback );
    }

    public void setPreferred( final Logger preferred )
    {
        m_switch.setPreferred( preferred );
    }
}
