/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger.factory;

import org.apache.avalon.excalibur.logger.LogTargetFactory;
import org.apache.avalon.excalibur.logger.LogTargetFactoryManageable;
import org.apache.avalon.excalibur.logger.LogTargetFactoryManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.log.LogTarget;
import org.apache.log.Priority;
import org.apache.log.filter.PriorityFilter;

/**
 * PriorityFilterTargetFactory class.
 *
 * This factory creates LogTargets with a wrapped PriorityFilter around it:
 *
 * <pre>
 *
 * &lt;priority-filter id="target-id" log-level="ERROR"&gt;
 *  &lt;any-target-definition/&gt;
 *  ...
 *  &lt;any-target-definition/&gt;
 * &lt;/priority-filter&gt;
 *
 * </pre>
 * <p>
 *  This factory creates a PriorityFilter object with a logging Priority set
 *  to the value of the log-level attribute (which defaults to INFO if absent).
 *  The LogTarget to filter is described in child elements of the configuration (in
 *  the sample above named as &lt;any-target-definition/&gt;).
 * </p>
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/04/04 02:34:15 $
 * @since 4.0
 */
public final class PriorityFilterTargetFactory
    extends AbstractTargetFactory
    implements LogTargetFactoryManageable
{
    /** The LogTargetFactoryManager */
    protected LogTargetFactoryManager m_logTargetFactoryManager;

    /**
     * create a LogTarget based on a Configuration
     */
    public final LogTarget createTarget( final Configuration configuration )
        throws ConfigurationException
    {
        final String loglevel = configuration.getAttribute( "log-level", "INFO" );
        getLogger().debug( "loglevel is " + loglevel );
        final PriorityFilter filter = new PriorityFilter( Priority.getPriorityForName( loglevel ) );

        final Configuration[] configs = configuration.getChildren();
        for( int i = 0; i < configs.length; i++ )
        {
            final LogTargetFactory factory = m_logTargetFactoryManager.getLogTargetFactory( configs[ i ].getName() );
            getLogger().debug( "creating target " + configs[ i ].getName() + ": " + configs[ i ].toString() );
            final LogTarget logtarget = factory.createTarget( configs[ i ] );
            filter.addTarget( logtarget );
        }
        return filter;
    }

    /**
     * get the LogTargetFactoryManager
     */
    public final void setLogTargetFactoryManager( LogTargetFactoryManager logTargetFactoryManager )
    {
        m_logTargetFactoryManager = logTargetFactoryManager;
    }

}

