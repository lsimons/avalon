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
import org.apache.avalon.framework.tools.infobuilder.LegacyUtil;
import org.apache.avalon.framework.info.ServiceDescriptor;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.excalibur.containerkit.registry.ComponentProfile;
import java.util.ArrayList;

/**
 * Utility class to help with exporting Blocks to management subsystem.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.3.8.1 $ $Date: 2002/12/03 08:14:22 $
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
                      final ComponentProfile profile,
                      final Object block )
        throws CascadingException
    {
        final ServiceDescriptor[] services = getMxServices( profile );
        final String name = profile.getMetaData().getName();
        final ClassLoader classLoader = block.getClass().getClassLoader();

        final Class[] serviceClasses = new Class[ services.length ];

        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            try
            {
                serviceClasses[ i ] = classLoader.loadClass( service.getType() );
            }
            catch( final Exception e )
            {
                final String reason = e.toString();
                final String message =
                    REZ.getString( "bad-mx-service.error", name, service.getType(), reason );
                getLogger().error( message );
                throw new CascadingException( message, e );
            }
        }

        try
        {
            context.exportObject( name, serviceClasses, block );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "export.error", name, e );
            getLogger().error( message );
            throw new CascadingException( message, e );
        }

    }

    /**
     * Return an array of all Management services for profile.
     *
     * @param profile the component profile
     * @return the management services.
     */
    private ServiceDescriptor[] getMxServices( final ComponentProfile profile )
    {
        final ArrayList mxServices = new ArrayList();
        final ServiceDescriptor[] services = profile.getInfo().getServices();
        for( int i = 0; i < services.length; i++ )
        {
            final ServiceDescriptor service = services[ i ];
            if( LegacyUtil.isMxService( service ) )
            {
                mxServices.add( service );
            }
        }

        return (ServiceDescriptor[])mxServices.toArray( new ServiceDescriptor[ mxServices.size() ] );
    }

    /**
     * Unxport the services of block, declared to be management
     * services, into management system.
     */
    void unexportBlock( final ApplicationContext context,
                        final ComponentProfile profile,
                        final Object block )
    {
        final String name = profile.getMetaData().getName();
        try
        {
            context.unexportObject( name );
        }
        catch( final Exception e )
        {
            final String message =
                REZ.getString( "unexport.error", name, e );
            getLogger().error( message );
        }
    }
}
