/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.metainfo;

import org.apache.avalon.framework.Version;

/**
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public class BlockDescriptor
{
    /**
     * The short name of the Block. Useful for displaying
     * human readable strings describing the type in
     * assembly tools or generators.
     */
    private final String m_name;

    private final String m_classname;

    private final Version m_version;

    private final String m_schemaType;

    public BlockDescriptor( final String name,
                            final String classname,
                            final String schemaType,
                            final Version version )
    {
        m_name = name;
        m_classname = classname;
        m_version = version;
        m_schemaType = schemaType;
    }

    /**
     * Retrieve the name of Block type.
     *
     * @return the name of Block type.
     */
    public String getName()
    {
        return m_name;
    }

    /**
     * Retrieve the Class Name of Block.
     *
     * @return the Class Name of block
     */
    public String getClassname()
    {
        return m_classname;
    }

    /**
     * Retrieve Version of current Block.
     *
     * @return the version of block
     */
    public Version getVersion()
    {
        return m_version;
    }

    /**
     * Retrieve the Schema Type of Block
     *
     * @return the Schema Type of block
     */
    public String getSchemaType()
    {
        return m_schemaType;
    }
}

