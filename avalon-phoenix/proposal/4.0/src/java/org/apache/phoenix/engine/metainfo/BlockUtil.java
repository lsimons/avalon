/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.metainfo;

import org.apache.phoenix.engine.blocks.Block;

/**
 * Class containing utility methods for blocks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class BlockUtil
{
    /**
     * Private constructor to block instantiation.
     */
    private BlockUtil()
    {
    }

    public static boolean implementsService( final Block block,  final ServiceDescriptor service )
    {
        try
        {
            final Class clazz =
                block.getClass().getClassLoader().loadClass( service.getName() );

            return clazz.isAssignableFrom( block.getClass() );
        }
        catch( final Throwable throwable ) {}

        return false;
    }

    public static boolean hasMatchingService( final ServiceDescriptor[] candidates,
                                              final ServiceDescriptor service )
    {
        for( int i = 0; i < candidates.length; i++ )
        {
            if( service.matches( candidates[ i ] ) )
            {
                return true;
            }
        }

        return false;
    }
}

