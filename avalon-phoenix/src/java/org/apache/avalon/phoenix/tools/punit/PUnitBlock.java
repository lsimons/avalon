/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.excalibur.containerkit.lifecycle.ResourceProvider;

public class PUnitBlock
{
    private String m_blockName;
    private Object m_block;
    private ResourceProvider m_resourceProvider;

    public PUnitBlock( String blockName, Object block, ResourceProvider resourceProvider )
    {
        this.m_blockName = blockName;
        this.m_block = block;
        this.m_resourceProvider = resourceProvider;
    }

    public String getBlockName()
    {
        return m_blockName;
    }

    public Object getBlock()
    {
        return m_block;
    }

    public ResourceProvider getResourceProvider()
    {
        return m_resourceProvider;
    }
}
