/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.punit;

import org.apache.avalon.framework.configuration.Configuration;

public class PUnitBlock
{
    private String m_blockName;
    private Object m_block;
    private Configuration m_configuration;

    public PUnitBlock( String blockName, Object block, Configuration configuration )
    {
        this.m_blockName = blockName;
        this.m_block = block;
        this.m_configuration = configuration;
        // logger ?
        // context ?
    }

    public String getBlockName()
    {
        return m_blockName;
    }

    public Object getBlock()
    {
        return m_block;
    }

    public Configuration getConfiguration()
    {
        return m_configuration;
    }
}
