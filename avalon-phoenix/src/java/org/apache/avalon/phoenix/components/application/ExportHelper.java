/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.application;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.avalon.phoenix.metadata.BlockMetaData;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * Utility class to help with exporting Blocks to management subsystem.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/05/19 02:31:04 $
 */
class ExportHelper
    extends AbstractLogEnabled
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( ExportHelper.class );

    /**
     * Export the services of block, declared to be management
     * services, into management system.
     */
    void exportBlock( final ApplicationContext context,
                      final BlockMetaData metaData,
                      final Object block )
        throws CascadingException
    {
        final ServiceDescriptor[] services = metaData.getBlockInfo().getManagementAccessPoints();
        final String name = metaData.getName();
        final ClassLoader classLoader = block.getClass().getClassLoader();

        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            try
            {
                final Class clazz = classLoader.loadClass( service.getName() );
                context.exportObject( name, clazz, block );
            }
            catch( final Exception e )
            {
                final String reason = e.toString();
                final String message =
                    REZ.getString( "export.error", name, service.getName(), reason );
                getLogger().error( message );
                throw new CascadingException( message, e );
            }
        }
    }

    /**
     * Unxport the services of block, declared to be management
     * services, into management system.
     */
    void unexportBlock( final ApplicationContext context,
                        final BlockMetaData metaData,
                        final Object block )
    {
        final ServiceDescriptor[] services = metaData.getBlockInfo().getManagementAccessPoints();
        final String name = metaData.getName();
        final ClassLoader classLoader = block.getClass().getClassLoader();

        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            try
            {
                final Class clazz = classLoader.loadClass( service.getName() );
                context.unexportObject( name, clazz );
            }
            catch( final Exception e )
            {
                final String reason = e.toString();
                final String message =
                    REZ.getString( "unexport.error", name, service.getName(), reason );
                getLogger().error( message );
            }
        }
    }
}
