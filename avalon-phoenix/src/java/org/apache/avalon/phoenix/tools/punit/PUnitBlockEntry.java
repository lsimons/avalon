/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.avalon.phoenix.containerkit.lifecycle.ResourceProvider;

/**
 * PUnitBlockEntry contains the runtime state of a block.
 *
 * @author Paul Hammant
 */
public class PUnitBlockEntry
{
    private final String m_blockName;
    private final Object m_block;
    private final ResourceProvider m_resourceProvider;

    /**
     * Construct a PUnitBlockEntry
     *
     * @param blockName The block name
     * @param block The block
     * @param resourceProvider The resource provider for the block
     */
    public PUnitBlockEntry( final String blockName,
                       final Object block,
                       final ResourceProvider resourceProvider )
    {
        m_blockName = blockName;
        m_block = block;
        m_resourceProvider = resourceProvider;
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
