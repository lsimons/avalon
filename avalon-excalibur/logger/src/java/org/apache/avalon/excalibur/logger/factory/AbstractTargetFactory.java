/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger.factory;

import org.apache.avalon.excalibur.logger.LogTargetFactory;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.log.LogTarget;

/**
 * AbstractTargetFactory class.
 *
 * This factory implements basic functionality for LogTargetFactories
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 02:34:15 $
 * @since 4.0
 */
public abstract class AbstractTargetFactory
    extends AbstractLogEnabled
    implements LogTargetFactory,
    Configurable,
    Contextualizable
{
    public abstract LogTarget createTarget( Configuration configuration )
        throws ConfigurationException;

    /** The Configuration object */
    protected Configuration m_configuration;

    /** The Context object */
    protected Context m_context;

    /**
     * Get the Configuration object
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        this.m_configuration = configuration;
    }

    /**
     * Get the Context object
     */
    public void contextualize( Context context )
        throws ContextException
    {
        this.m_context = context;
    }
}
