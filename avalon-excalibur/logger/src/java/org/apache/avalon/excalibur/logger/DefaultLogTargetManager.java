/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger;

import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.log.LogTarget;

/**
 * Default LogTargetManager implementation.  It populates the LogTargetManager
 * from a configuration file.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/08/06 02:01:49 $
 * @since 4.0
 */
public class DefaultLogTargetManager
    extends AbstractLogEnabled
    implements LogTargetManager, LogTargetFactoryManageable, Configurable
{
    /** Map for ID to LogTarget mapping */
    final private Map m_targets = new HashMap();

    /** The LogTargetFactoryManager object */
    private LogTargetFactoryManager m_factoryManager;

    /**
     * Retrieves a LogTarget for an ID. If this LogTargetManager
     * does not have the match a null will be returned.
     *
     * @param id The LogTarget ID
     * @return the LogTarget or null if none is found.
     */
    public final LogTarget getLogTarget( final String id )
    {
        return (LogTarget)m_targets.get( id );
    }

    /**
     * Gets the LogTargetFactoryManager.
     */
    public final void setLogTargetFactoryManager( final LogTargetFactoryManager logTargetFactoryManager )
    {
        m_factoryManager = logTargetFactoryManager;
    }

    /**
     * Reads a configuration object and creates the log targets.
     *
     * @param configuration  The configuration object.
     * @throws ConfigurationException if the configuration is malformed
     */
    public final void configure( final Configuration configuration )
        throws ConfigurationException
    {
        if( null == m_factoryManager )
        {
            throw new ConfigurationException( "LogTargetFactory not received" );
        }

        final Configuration[] confs = configuration.getChildren();
        for( int i = 0; i < confs.length; i++ )
        {
            final String targetName = confs[ i ].getName();
            final LogTargetFactory logTargetFactory = m_factoryManager.getLogTargetFactory( targetName );
            if ( logTargetFactory == null )
            {
                throw new ConfigurationException( "Factory definition for '" + targetName
                    + "' missing from logger configuration." );
            }
            final LogTarget logTarget = logTargetFactory.createTarget( confs[ i ] );
            final String targetId = confs[ i ].getAttribute( "id" );
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "added new LogTarget of id " + targetId );
            }
            m_targets.put( targetId, logTarget );
        }
    }
}
