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

import org.apache.avalon.excalibur.logger.LoggerManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * This is the base class to create LoggerManager decorators.
 * It passes all lifecycle and LoggerManagerc
 * calls onto the wrapped object.
 *
 * <p> Decorators are expected to be slim - be able to run
 * for instance both with and without having their 
 * enableLogging() method called.
 *
 * <p> This constraint is imposed to allow decorators to
 * be applicable to an object both at its burth, like
 *
 * <pre>
 * C c = new C();
 * DecoratorX d = new DecoratorX( c );
 * x.enableLogging( logger );
 * </pre>
 *
 * and after the object has been completely configured
 *
 * <pre>
 * C c = (C)manager.lookup( C.ROLE );
 * DecoratorX d = new DecoratorX( c );
 * </pre>
 * 
 * If this constrianed is not obeyed this should be clearly
 * stated in the javadocs. For instance, LogToSelfDecorator
 * _only_ makes sense if it passes the <code>enableLogging</code>
 * call through it.
 *
 * <p>
 * This implementation is incomplete, 
 * it passes only those calls that are needed in
 *
 * <code>org.apache.avalon.excalibur.logger.decorator.*</code> and
 * <code>org.apache.avalon.excalibur.logger.adapter.*</code>:
 * <pre>
 *    LogEnabled
 *    Contextualizable
 *    Configurable
 *    Startable
 *    Disposable
 * </pre>
 *
 * This object differes from LoggerManagerTee by being abstract,
 * by absence of addTee() public method and by implementation.
 * LoggerManagerTee might be used instead of this but maintaining
 * it as a separate class seemed cleaner.
 * 
 * @author <a href="http://cvs.apache.org/~atagunov">Anton Tagunov</a>
 * @version CVS $Revision: 1.1.1.1 $ $Date: 2003/10/02 19:18:44 $
 * @since 4.0
 */
public abstract class LoggerManagerDecorator implements
        LoggerManager, 
        LogEnabled, 
        Contextualizable, 
        Configurable, 
        Startable, 
        Disposable
{
    /**
     * The wrapped-in LoggerManager.
     */
    protected final LoggerManager m_loggerManager;

    public LoggerManagerDecorator( final LoggerManager loggerManager )
    {
        if ( loggerManager == null ) throw new NullPointerException( "loggerManager" );
        m_loggerManager = loggerManager;
    }

    public void enableLogging( final Logger logger )
    {
        ContainerUtil.enableLogging( m_loggerManager, logger );
    }

    public void contextualize( final Context context ) throws ContextException
    {
        ContainerUtil.contextualize( m_loggerManager, context );
    }
    
    public void configure( final Configuration configuration ) throws ConfigurationException
    {
        ContainerUtil.configure( m_loggerManager, configuration );
    }

    public void start() throws Exception
    {
        ContainerUtil.start( m_loggerManager );
    }

    public void stop() throws Exception
    {
        ContainerUtil.stop( m_loggerManager );
    }

    public void dispose()
    {
        ContainerUtil.dispose( m_loggerManager );
    }

    /**
     * Return the Logger for the specified category.
     */
    public Logger getLoggerForCategory( final String categoryName )
    {
        return m_loggerManager.getLoggerForCategory( categoryName );
    }

    /**
     * Return the default Logger.  This is basically the same
     * as getting the Logger for the "" category.
     */
    public Logger getDefaultLogger()
    {
        return m_loggerManager.getDefaultLogger();
    }
}
