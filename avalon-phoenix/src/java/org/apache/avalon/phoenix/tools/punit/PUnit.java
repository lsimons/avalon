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
import junit.framework.TestCase;
import java.util.ArrayList;


public abstract class PUnit extends TestCase
{

    private LifecycleHelper m_lifecycleHelper = new LifecycleHelper();
    private PUnitResourceProvider m_pUnitResourceProvider;

    private ArrayList m_blocks = new ArrayList();

    public PUnit(String name)
    {
        super(name);
        m_lifecycleHelper.enableLogging(new ConsoleLogger());
    }

    protected void addBlock(String blockName, Object block) {
        PUnitBlock pBlock= new PUnitBlock(blockName, block, null);
        m_blocks.add(pBlock);
    }


    protected final void startup() throws LifecycleException
    {

        m_pUnitResourceProvider = new PUnitResourceProvider();
        for (int i = 0; i < m_blocks.size(); i++)
        {
            PUnitBlock block = (PUnitBlock) m_blocks.get(i);
            m_lifecycleHelper.startup(block.getBlockName(),block.getBlock(),
                    m_pUnitResourceProvider);

        }

    }

    protected final void shutdown() throws LifecycleException
    {
        m_pUnitResourceProvider = new PUnitResourceProvider();
        for (int i = 0; i < m_blocks.size(); i++)
        {
            PUnitBlock block = (PUnitBlock) m_blocks.get(i);
            m_lifecycleHelper.shutdown(block.getBlockName(),block.getBlock());

        }

    }


}
