/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.framework.test;

import org.apache.avalon.framework.AbstractComponent;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This test class is used to test the AbstractComponent facilities for you.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/11/30 21:33:13 $
 */
public final class FullLifecycleComponent
    extends AbstractComponent
    implements LogEnabled, Contextualizable, Parameterizable, Configurable,
               Composable, Initializable, Startable, Suspendable, Disposable,
               ThreadSafe
{
    private Logger m_logger;
    private Context m_context;
    private Parameters m_parameters;
    private Configuration m_configuration;
    private ComponentManager m_componentManager;

    public void enableLogging( Logger logger )
    {
        checkAssigned( m_logger, "Logger already set!" );
        checkLogEnabled( "Logger: Initialization out of order." );

        m_logger = logger;
    }

    public void contextualize( Context context )
        throws ContextException
    {
        checkAssigned( m_context, "Context already set!" );
        checkContextualized( "Context: Initialization out of order." );

        m_context = context;
    }

    public void parameterize( Parameters params )
        throws ParameterException
    {
        checkAssigned( m_parameters, "Parameters already set!" );
        checkParameterized( "Parameters: Initialization out of order." );

        m_parameters = params;
    }

    public void configure( Configuration config )
        throws ConfigurationException
    {
        checkAssigned( m_configuration, "Configuration already set!" );
        checkConfigured( "Configuration: Initialization out of order."  );

        m_configuration = config;
    }

    public void compose( ComponentManager manager )
        throws ComponentException
    {
        checkAssigned( m_componentManager, "Component Manager already set!" );
        checkComposed( "ComponentManager: Initialization out of order." );
    }

    public void initialize()
        throws Exception
    {
        checkInitialized( "Initialize: Initialization out of order." );
    }

    public void start()
        throws Exception
    {
        checkStarted( "Start: Initialization out of order." );
    }

    public void suspend()
    {
        checkSuspended( "Suspend: Initialization out of order." );
    }

    public void resume()
    {
        checkResumed( "Resume: Initialization out of order." );
    }

    public void stop()
        throws Exception
    {
        checkStopped( "Stop: Initialization out of order." );
    }

    public void dispose()
    {
        checkDisposed( "Dispose: Initialization out of order." );

        m_logger = null;
        m_context = null;
        m_parameters = null;
        m_configuration = null;
        m_componentManager = null;
    }
}
