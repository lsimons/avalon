/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.testcase;

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
 * @version CVS $Revision: 1.1 $ $Date: 2003/03/07 19:34:31 $
 */
public final class FullLifecycleComponent
    implements LogEnabled, Contextualizable, Parameterizable, Configurable,
    Serviceable, Initializable, Startable, Suspendable, Disposable,
    ThreadSafe
{
    private ComponentStateValidator m_validator = new ComponentStateValidator( this );
    private Logger m_logger;
    private Context m_context;
    private Parameters m_parameters;
    private Configuration m_configuration;
    private ServiceManager m_manager;

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

    public void service( ServiceManager manager )
        throws ServiceException
    {
        m_validator.checkNotAssigned( m_manager );
        m_validator.checkComposed();
        m_manager = manager;
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
        m_manager = null;
    }
}
