/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.engine.facilities.policy;

import java.security.Policy;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.log.Logger;
import org.apache.avalon.phoenix.engine.facilities.PolicyManager;

/**
 * This facility manages the policy for an application instance.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultPolicyManager
    implements PolicyManager, Loggable, Contextualizable, Configurable, Initializable
{
    private DefaultPolicy      m_policy = new DefaultPolicy();

    public void setLogger( final Logger logger )
    {
        m_policy.setLogger( logger );
    }

    public void contextualize( final Context context )
    {
        m_policy.contextualize( context );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_policy.configure( configuration );
    }

    public void initialize()
        throws Exception
    {
        m_policy.initialize();
    }

    /**
     * Get policy for the current application.
     *
     * @return the Policy
     */
    public Policy getPolicy()
    {
        return m_policy;
    }
}
