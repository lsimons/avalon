/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix;

import org.apache.avalon.AbstractLoggable;
import org.apache.avalon.ComponentManager;
import org.apache.avalon.ComponentManagerException;
import org.apache.avalon.Composer;
import org.apache.avalon.configuration.Configurable;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.Context;
import org.apache.avalon.Contextualizable;
import org.apache.avalon.Disposable;
import org.apache.avalon.Initializable;

/**
 * This is an <code>AbstractBlock</code> that makes deployment a bit
 * easier.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/02/24 04:30:59 $
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
        throws ComponentManagerException
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
