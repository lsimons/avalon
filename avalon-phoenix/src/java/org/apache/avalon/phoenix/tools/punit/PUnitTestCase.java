/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.excalibur.containerkit.lifecycle.LifecycleHelper;
import org.apache.excalibur.containerkit.lifecycle.LifecycleException;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.configuration.Configuration;
import junit.framework.TestCase;
import java.util.ArrayList;

public abstract class PUnitTestCase extends TestCase
{
    private LifecycleHelper m_lifecycleHelper;
    private ArrayList m_blocks;
    private PUnitServiceManager m_pUnitServiceManager;

    protected void setUp() throws Exception
    {
        m_lifecycleHelper = new LifecycleHelper();
        m_lifecycleHelper.enableLogging( new ConsoleLogger() );
        m_pUnitServiceManager = new PUnitServiceManager();
        m_blocks = new ArrayList();
    }

    public PUnitTestCase( String name )
    {
        super( name );
    }

    protected void addBlock( String blockName, String serviceName,
                             Object block , Configuration configuration )
    {
        PUnitBlock pBlock = new PUnitBlock( blockName, block,
                new PUnitResourceProvider(m_pUnitServiceManager, configuration) );
        m_blocks.add( pBlock );
        if (serviceName != null)
        {
            m_pUnitServiceManager.addService(serviceName, block);
        }
    }

    protected final void startup() throws LifecycleException
    {

        System.out.println("--> a? ");
        for( int i = 0; i < m_blocks.size(); i++ )
        {
            System.out.println("--> a " + i);
            final PUnitBlock block = (PUnitBlock) m_blocks.get( i );
            m_lifecycleHelper.startup( block.getBlockName(),
                                       block.getBlock(),
                                       block.getResourceProvider() );
        }
    }

    protected final void shutdown() throws LifecycleException
    {
        for( int i = 0; i < m_blocks.size(); i++ )
        {
            PUnitBlock block = (PUnitBlock) m_blocks.get( i );
            m_lifecycleHelper.shutdown( block.getBlockName(), block.getBlock() );
        }
    }
}
