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
package org.apache.avalon.excalibur.logger.decorator;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.excalibur.logger.util.LoggerSwitch;

/**
 *
 * This class intercepts the class passed to us via
 * <code>enableLogging()</code> and substibutes it
 * by <code>LoggerSwitch.get()</code> logger.
 * <p>
 * Later on at the <code>start()</code> stage
 * we assume that our wrapped LoggerManager has already
 * completely initialized itself and extract
 * a <code>Logger</code> from it.
 * <p>
 * <code>LoggerSwitch</code> allowes us to supply this
 * logger to it via <code>LoggerSwitch.setPreferred()</code>.
 * This has the effect of all the log messages directed
 * to <code>LoggerSwitch.get()</code> obtained logger
 * being directed to the new <code>Logger</code> unless
 * a recursion error happens.
 *
 * @author <a href="http://cvs.apache.org/~atagunov">Anton Tagunov</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/06/11 10:52:10 $
 * @since 4.0
 */
public class LogToSelfDecorator extends LoggerManagerDecorator
{
    /* The category to switch our logging to. */
    private final String m_switchTo;
    /* The LoggerSwitch object controlling our substitute Logger. */
    private LoggerSwitch m_switch;
    /** 
     * Our substitute logger obtained from m_switch. 
     * Used for our own logging.
     */
    private Logger m_logger;

    /**
     * Creates a LogToSelfDecorator instance.
     * @param switchTo the name of the category we should extract
     *         a Logger and switch our logging to at the 
     *         <code>start()</code> stage; can not be null;
     *         empty value causes logging to be switched to the
     *         "" category.
     */
    public LogToSelfDecorator( final LoggerManager loggerManager, final String switchTo )
    {
        super( loggerManager );
        if ( switchTo == null ) throw new NullPointerException( "switchTo" );
        m_switchTo = switchTo;
    }

    /**
     * Substitutes the supplied logger by <code>m_switch.get()</code>.
     * The substiting logger is used both for our own logging and
     * passed onto our decorated <code>LoggerManager</code>.
     * @param logger the logger supplied for us and our wrapped
     *        LoggerManager; we chould survive with a null logger
     *        (LoggerSwitch create a NullLogger in this case), but 
     *        for better error detection we shall rather blow up.
     */
    public void enableLogging( final Logger logger )
    {
        if ( m_switch != null )
        {
            throw new IllegalStateException( "enableLogging() already called" );
        }

        if ( logger == null )
        {
            throw new NullPointerException( "logger" );
        }

        m_switch = new LoggerSwitch( logger );
        m_logger = m_switch.get();
        ContainerUtil.enableLogging( m_loggerManager, m_logger );
    }

    /**
     * Invokes <code>start()</code> on our wrapped
     * <code>LoggerManager</code> and swithces the
     * logger used by us and all objects that we
     * decorate for a logger extracted from our
     * wrapped <code>LoggerManager</code>.
     */
    public void start() throws Exception
    {
        /** 
         * If our LoggerManager is <code>Startable</code>
         * its <code>start()</code> will be invoked now.
         */    
        super.start();

        final Logger preferred = m_loggerManager.getLoggerForCategory( m_switchTo );
        if ( m_logger.isDebugEnabled() )
        {
            final String message = "LoggerManager: switching logging to '" + 
                    m_switchTo + "'";
            m_logger.debug( message );
        }
        
        m_switch.setPreferred( preferred );

        if ( m_logger.isDebugEnabled() )
        {
            final String message = "LoggerManager: have switched logging to '" + 
                    m_switchTo + "'";
            m_logger.debug( message );
        }
    }
}
