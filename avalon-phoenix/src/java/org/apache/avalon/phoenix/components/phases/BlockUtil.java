/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.phases;

import org.apache.avalon.phoenix.Block;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * Class containing utility methods for blocks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
*/
final class BlockUtil 
{
    /**
     * Private constructor to block instantiation.
     */
    private BlockUtil()
    {
    }

    public static Class[] getServiceClasses( final Block block, final ServiceDescriptor[] services )
    {
        final Class[] classes = new Class[ services.length + 1 ];
        final ClassLoader classLoader = block.getClass().getClassLoader();

        for( int i = 0; i < services.length; i++ )
        {
            try
            {
                classes[ i ] = classLoader.loadClass( services[ i ].getName() );
            }
            catch( final Throwable throwable ) {}
        }

        classes[ services.length ] = Block.class;
        return classes;
    }

    public static boolean implementsService( final Block block, final ServiceDescriptor service )
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


