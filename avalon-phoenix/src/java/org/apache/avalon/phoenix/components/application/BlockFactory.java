/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metainfo.BlockInfo;

/**
 * @author ifedorenko
 */
public interface BlockFactory
{
    /**
     * Load a BlockInfo for Block with specified name and classname.
     *
     * @param metaData the metaData representing Block
     * @return the BlockInfo for specified block
     */
    BlockInfo getBlockInfo( BlockMetaData metaData,
                            ClassLoader classLoader,
                            Configuration block )
        throws Exception;

    String getConfigurationSchemaURL( String name,
                                      String implementationKey,
                                      ClassLoader classLoader )
        throws Exception;

    Object createBlock( String implementationKey,
                        ClassLoader classLoader )
        throws Exception;
}
