/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.blocks;

import org.apache.framework.logger.AbstractLoggable;
import org.apache.framework.component.ComponentManager;
import org.apache.framework.component.ComponentException;
import org.apache.framework.component.Composer;
import org.apache.framework.configuration.Configurable;
import org.apache.framework.configuration.Configuration;
import org.apache.framework.configuration.ConfigurationException;
import org.apache.framework.context.Context;
import org.apache.framework.context.Contextualizable;
import org.apache.framework.lifecycle.Disposable;
import org.apache.framework.lifecycle.Initializable;

/**
 * This is an <code>AbstractBlock</code> that makes deployment a bit
 * easier.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/04/22 10:07:17 $
 */
public abstract class AbstractBlock
    extends AbstractLoggable
    implements Block, Contextualizable, Composer, Configurable
{
    protected BlockContext           m_context;
    protected Configuration          m_configuration;
    protected ComponentManager       m_componentManager;

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

    protected final Configuration getConfiguration()
    {
        return m_configuration;
    }
}
