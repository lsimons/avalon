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

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.excalibur.logger.util.LoggerSwitch;
import org.apache.avalon.excalibur.logger.util.LoggerUtil;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 *
 * This abstract class implements LogEnabled.
 * A derived class is expected to obtain a logger via
 * <code>getLogger()</code> and live with it.
 * The <code>Logger</code> supplied via <code>enableLogging</code>
 * will be used both as the "initial" and as the "fallback" logger.
 * <ul><li>
 * "initial" means that until a call to 
 * <code>start()</code> the messages logger via
 * <code>getLogger().xxx()</code> will go to this logger</li><li>
 * "fallback" means that if after a successfull <code>start</code>
 * a recursive invocation of <code>getLogger().xxx()</code> will be detected
 * the message will be logged via the initial logger as a fallback.</li></ul>
 * See {@link org.apache.avalon.excalibur.logger.util.LoggerSwitch} for
 * more details.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @author <a href="http://cvs.apache.org/~atagunov">Anton Tagunov</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/06/12 18:57:45 $
 * @since 4.0
 */
public abstract class AbstractLoggerManager
    implements LogEnabled, LoggerManager
{
    /** 
     * Map for name to logger mapping.
     * This instance variable is protected (not privated)
     * so that it may be pre-filled at configuration stage.
     */
    final protected Map m_loggers = new HashMap();

    /** The root logger to configure */
    protected String m_prefix;

    /** 
     * The object that wraps a swithing logger.
     * The switching logger itself for security reasons
     * has no methods of controlling it, but its wrapping
     * object has.
     */
    private LoggerSwitch m_switch;

    /** Always equals to m_switch.get() */
    private Logger m_logger;

    /** 
     * category we should switch our own loggin to 
     * on <code>start()</code>.
     */
    private String m_switchTo;

    /** safeguards against double <code>enableLogging()</code> invocation. */
    private boolean m_enableLoggingInvoked = false;

    /** safeguards against double <code>start()</code> invocation. */
    private boolean m_startInvoked = false;

    /** 
     * The logger used to be returned from <code>getDefaultLogger()</code>
     * and <code>getLoggerForCategory("")</code>,
     * if one has been forcibly set via a constructor. 
     */
    final private Logger m_defaultLoggerOverride;

    /**
     * Derived LoggerManager implementations should obtain
     * a logger to log their own messages via this call.
     * It is also safe to log messages about logging failures
     * via this logger as it safeguards internally gainst
     * recursion.
     */
    protected Logger getLogger()
    {
        return m_logger;
    }

    /**
     * Initializes AbstractLoggerManager.
     * @param prefix the prefix to prepended to the category name
     *         on each invocation of getLoggerForCategory before
     *         passing the category name on to the underlying logging
     *         system (currently LogKit or Log4J).
     * @param switchTo fuel for the <code>start()</code> method; 
     *         if null <code>start()</code> will do nothing; 
     *         if empty <code>start()</code> will switch to
     *         <code>getLoggerForCategory("")</code>.
     */
    public AbstractLoggerManager( final String prefix, final String switchTo, 
            Logger defaultLoggerOverride )
    {
        m_prefix = prefix;
        m_switchTo = switchTo;

        m_switch = new LoggerSwitch( null, null );
        m_logger = m_switch.get();

        m_defaultLoggerOverride = defaultLoggerOverride;
    }

    /**
     * Accept the logger we shall use as the initial and the fallback logger.
     */
    public void enableLogging( final Logger fallbackLogger )
    {
        if ( m_enableLoggingInvoked )
        {
            throw new IllegalStateException( "enableLogging() already called" );
        }
        m_switch.setFallback( fallbackLogger );
        m_enableLoggingInvoked = true;
    }

    /**
     * Get a logger from ourselves and pass it to <code>m_switch</code>.
     */
    public void start()
    {
        if ( m_startInvoked )
        {
            throw new IllegalStateException( "start() already invoked" );
        }

        if ( m_switchTo != null )
        {
            if ( m_logger.isDebugEnabled() )
            {
                final String message = "LoggerManager: switching logging to " + 
                        "this.getLoggerForCategory('" +
                        LoggerUtil.getFullCategoryName( m_prefix, m_switchTo) + "').";
                m_logger.debug( message );
            }

            final Logger ourOwn = this.getLoggerForCategory( m_switchTo );
        
            if ( ourOwn == null )
            {
                throw new NullPointerException( "ourOwn" );
            }
            
            m_switch.setPreferred( ourOwn );

            if ( m_logger.isDebugEnabled() )
            {
                final String message = "LoggerManager: have switched logging to " + 
                        "this.getLoggerForCategory('" +
                        LoggerUtil.getFullCategoryName( m_prefix, m_switchTo) + "').";
                m_logger.debug( message );
            }
        }
        else
        {
            if ( m_logger.isDebugEnabled() )
            {
                final String message = "LoggerManager: switchTo is null, " +
                        "no switch of our own logging.";
                m_logger.debug( message );
            }
        }
        m_startInvoked = true;
    }

    /** Startable.stop() empty implementation. */
    public void stop(){}

    /**
     * Retruns the logger for the <code>""</code> category.
     */
    public final Logger getDefaultLogger()
    {
        return getLoggerForCategory( null );
    }

    /**
     * Actually create a logger wrapping underlying logger
     * backed implementation for a give category. Bypasses the caching.
     * Derived LoggerManager implementations should provide an implementation
     * of this method.
     */
    protected abstract Logger doGetLoggerForCategory( final String fullCategoryName );

    /**
     * Retrieves a Logger from a category name. Usually
     * the category name refers to a configuration attribute name.  If
     * this LogKitManager does not have the match the default Logger will
     * be returned and a warning is issued.
     */
    public final Logger getLoggerForCategory( final String categoryName )
    {
        if ( m_defaultLoggerOverride != null &&
                ( categoryName == null || categoryName.length() == 0 ) )
        {
            return m_defaultLoggerOverride;
        }

        final String fullCategoryName = 
                LoggerUtil.getFullCategoryName( m_prefix, categoryName );

        final Logger logger;
        final Logger newLogger;

        synchronized( m_loggers )
        {
            logger = (Logger)m_loggers.get( fullCategoryName );
            
            if ( logger == null )
            {
                newLogger = doGetLoggerForCategory( fullCategoryName );
                m_loggers.put( fullCategoryName, newLogger );
            }
            else
            {
                /* Let's have no "variable might not have been initialized". */
                newLogger = null;
            }
        }

        if( null != logger )
        {
            if( m_logger.isDebugEnabled() )
            {
                m_logger.debug( "Logger for category " + fullCategoryName + " returned" );
            }
            return logger;
        }

        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Logger for category " + fullCategoryName + " not defined in "
                            + "configuration. New Logger created and returned" );
        }

        return newLogger;
    }
}
