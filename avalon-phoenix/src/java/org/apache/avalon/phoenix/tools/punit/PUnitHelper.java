/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.avalon.phoenix.containerkit.lifecycle.LifecycleHelper;
import org.apache.avalon.phoenix.containerkit.lifecycle.LifecycleException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.service.DefaultServiceManager;
import java.util.ArrayList;

/**
 * PUnit helper
 * @author Paul Hammant
 */
public final class PUnitHelper implements PUnit, Initializable
{
    private LifecycleHelper m_lifecycleHelper;
    private ArrayList m_blocks;
    private DefaultServiceManager m_serviceManager;
    private PUnitLogger m_logger;

    /**
     * Query the log
     * @param startsWith For an expression that starts with this
     * @return The logged entry.
     */
    public final String lookupInLog( String startsWith )
    {
        return m_logger.get( startsWith );
    }

    /**
     * Query the log
     * @param startsWith For an expression that starts with this
     * @return true or not
     */
    public boolean logHasEntry( String startsWith )
    {
        return m_logger.contains( startsWith );
    }

    /**
     * Initialize
     * @throws Exception If a problem
     */
    public void initialize() throws Exception
    {
        m_logger = new PUnitLogger();
        m_lifecycleHelper = new LifecycleHelper();
        m_lifecycleHelper.enableLogging( new ConsoleLogger() );
        m_serviceManager = new DefaultServiceManager();
        m_blocks = new ArrayList();
    }

    /**
     * Add a block
     * @param blockName The block name
     * @param block The block
     * @param serviceName The service name (for lookup)
     * @param configuration The configuration
     */
    public void addBlock( final String blockName,
                             final String serviceName,
                             final Object block,
                             final Configuration configuration )
    {
        final PUnitResourceProvider resourceProvider =
            new PUnitResourceProvider( m_serviceManager, configuration, m_logger );
        final PUnitBlockEntry pBlock = new PUnitBlockEntry( blockName, block, resourceProvider );
        m_blocks.add( pBlock );
        if( serviceName != null )
        {
            m_serviceManager.put( serviceName, block );
        }
    }

    /**
     * Run blocks thru startup.
     * @throws LifecycleException If a problem
     */
    public void startup() throws LifecycleException
    {

        for( int i = 0; i < m_blocks.size(); i++ )
        {
            final PUnitBlockEntry block = (PUnitBlockEntry)m_blocks.get( i );
            m_lifecycleHelper.startup( block.getBlockName(),
                                       block.getBlock(),
                                       block.getResourceProvider() );
        }
    }

    /**
     * Run blocks thru shutdown
     * @throws LifecycleException If a problem
     */
    public void shutdown() throws LifecycleException
    {
        final int size = m_blocks.size();
        for( int i = 0; i < size; i++ )
        {
            final PUnitBlockEntry block = (PUnitBlockEntry)m_blocks.get( i );
            m_lifecycleHelper.shutdown( block.getBlockName(), block.getBlock() );
        }
    }
}
