/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.excalibur.containerkit.lifecycle.ResourceProvider;

/**
 * PUnitBlock
 * @author Paul Hammant
 */
public class PUnitBlock
{
    private String m_blockName;
    private Object m_block;
    private ResourceProvider m_resourceProvider;

    /**
     * Construct a PUnitBlock
     * @param blockName The block name
     * @param block The block
     * @param resourceProvider The resource provider for the block
     */
    public PUnitBlock( String blockName, Object block, ResourceProvider resourceProvider )
    {
        this.m_blockName = blockName;
        this.m_block = block;
        this.m_resourceProvider = resourceProvider;
    }

    /**
     * Get the block name
     * @return The block name
     */
    public String getBlockName()
    {
        return m_blockName;
    }

    /**
     * Get The block
     * @return The block
     */
    public Object getBlock()
    {
        return m_block;
    }

    /**
     * Get the resource provider
     * @return The resource provider.
     */
    public ResourceProvider getResourceProvider()
    {
        return m_resourceProvider;
    }
}
