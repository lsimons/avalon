/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * This is an <code>AbstractBlock</code> that makes deployment a bit
 * easier.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.5 $ $Date: 2001/11/19 12:21:28 $
 */
public abstract class AbstractBlock
    extends AbstractLogEnabled
    implements Block, Contextualizable, Composable, Configurable
{
    private BlockContext           m_context;
    private Configuration          m_configuration;
    private ComponentManager       m_componentManager;

    public void contextualize( final Context context )
    {
        m_context = (BlockContext)context;
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_configuration = configuration;
    }

    public void compose( final ComponentManager componentManager )
        throws ComponentException
    {
        m_componentManager = componentManager;
    }

    protected final BlockContext getBlockContext()
    {
        return m_context;
    }

    protected final ComponentManager getComponentManager()
    {
        return m_componentManager;
    }

    /**
     * Retrieve cached configuration values.
     *
     * @return the configuration
     * @deprecated No Block should be relying on AbstractBlock to implement Configurable
     */
    protected final Configuration getConfiguration()
    {
        return m_configuration;
    }
}
