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
package org.apache.avalon.fortress.test.util;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This test class is used to test the AbstractComponent facilities for you.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.5 $ $Date: 2003/04/11 07:37:58 $
 */
public final class FullLifecycleComponent
    implements LogEnabled, Contextualizable, Parameterizable, Configurable,
    Serviceable, Initializable, Startable, Suspendable, Disposable,
    ThreadSafe
{
    private org.apache.avalon.fortress.test.util.ComponentStateValidator m_validator = new org.apache.avalon.fortress.test.util.ComponentStateValidator( this );
    private Logger m_logger;
    private Context m_context;
    private Parameters m_parameters;
    private Configuration m_configuration;
    private ServiceManager m_componentManager;

    public void enableLogging( Logger logger )
    {
        m_validator.checkNotAssigned( m_logger );
        m_validator.checkLogEnabled();

        m_logger = logger;
    }

    public void contextualize( Context context )
        throws ContextException
    {
        m_validator.checkNotAssigned( m_context );
        m_validator.checkContextualized();

        m_context = context;
    }

    public void parameterize( Parameters params )
        throws ParameterException
    {
        m_validator.checkNotAssigned( m_parameters );
        m_validator.checkParameterized();

        m_parameters = params;
    }

    public void configure( Configuration config )
        throws ConfigurationException
    {
        m_validator.checkNotAssigned( m_configuration );
        m_validator.checkConfigured();

        m_configuration = config;
    }

    public void service( final ServiceManager manager )
        throws ServiceException
    {
        m_validator.checkNotAssigned( m_componentManager );
        m_validator.checkServiced();
    }

    public void initialize()
        throws Exception
    {
        m_validator.checkInitialized();
    }

    public void start()
        throws Exception
    {
        m_validator.checkStarted();
    }

    public void suspend()
    {
        m_validator.checkSuspended();
    }

    public void resume()
    {
        m_validator.checkResumed();
    }

    public void stop()
        throws Exception
    {
        m_validator.checkStopped();
    }

    public void dispose()
    {
        m_validator.checkDisposed();

        m_logger = null;
        m_context = null;
        m_parameters = null;
        m_configuration = null;
        m_componentManager = null;
    }
}
