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
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.service.DefaultServiceManager;
import junit.framework.TestCase;
import java.util.ArrayList;

/**
 * PUnitTestCase
 * @author Paul Hammant
 */
public abstract class PUnitTestCase extends TestCase implements PUnit
{
    private PUnitHelper m_pUnitHelper = new PUnitHelper();


    /**
     * PUnitTestCase
     */
    public PUnitTestCase()
    {
        super( PUnitTestCase.class.getName() );
    }

    /**
     * PUnitTestCase
     * @param name The method name for JUnit
     */
    public PUnitTestCase( String name )
    {
        super( name );
    }

    /**
     * Query the log
     * @param startsWith For an expression that starts with this
     * @return The logged entry.
     */
    public final String lookupInLog( String startsWith )
    {
        return m_pUnitHelper.lookupInLog( startsWith );
    }

    /**
     * Query the log
     * @param startsWith For an expression that starts with this
     * @return true or not
     */
    public boolean logHasEntry( String startsWith )
    {
        return m_pUnitHelper.logHasEntry( startsWith );
    }

    /**
     * Setup as per Junit
     * @throws Exception If a problem
     */
    protected void setUp() throws Exception
    {
        m_pUnitHelper.initialize();
        super.setUp();
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
        m_pUnitHelper.addBlock(blockName, serviceName, block, configuration);
    }

    /**
     * Run blocks thru startup.
     * @throws LifecycleException If a problem
     */
    public void startup() throws LifecycleException
    {

        m_pUnitHelper.startup();
    }

    /**
     * Run blocks thru shutdown
     * @throws LifecycleException If a problem
     */
    public void shutdown() throws LifecycleException
    {
        m_pUnitHelper.shutdown();
    }
}
