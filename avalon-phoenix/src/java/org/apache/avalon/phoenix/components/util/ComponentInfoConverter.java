/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.util;

import java.util.ArrayList;
import org.apache.avalon.framework.Version;
import org.apache.avalon.framework.info.Attribute;
import org.apache.avalon.framework.info.ComponentDescriptor;
import org.apache.avalon.framework.info.ComponentInfo;
import org.apache.avalon.framework.info.FeatureDescriptor;
import org.apache.avalon.phoenix.metainfo.BlockDescriptor;
import org.apache.avalon.phoenix.metainfo.BlockInfo;
import org.apache.avalon.phoenix.metainfo.DependencyDescriptor;
import org.apache.avalon.phoenix.metainfo.ServiceDescriptor;

/**
 * Convert a {@link ComponentInfo} into a {@link BlockInfo}.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.8 $ $Date: 2002/12/09 17:09:59 $
 */
public class ComponentInfoConverter
{
    private ComponentInfoConverter()
    {
    }

    /**
     * Convert a ComponentInfo object into a BlockInfo object.
     *
     * @param component the ComponentInfo object
     * @return the BlockInfo object
     */
    public static BlockInfo toBlockInfo( final ComponentInfo component )
    {
        final BlockDescriptor descriptor =
            toBlockDescriptor( component );
        final ServiceDescriptor[] services =
            toPhoenixServices( component.getServices() );
        final ServiceDescriptor[] mxServices =
            getMXServices( component.getServices() );
        final DependencyDescriptor[] dependencys =
            toPhoenixDependencys( component.getDependencies() );

        return new BlockInfo( descriptor,
                              services,
                              mxServices,
                              dependencys );
    }

    /**
     * Return Phoenix Management services from Info Service array.
     *
     * @param services the services
     * @return the management services
     */
    private static ServiceDescriptor[] getMXServices(
        final org.apache.avalon.framework.info.ServiceDescriptor[] services )
    {
        final ArrayList serviceSet = new ArrayList();
        for( int i = 0; i < services.length; i++ )
        {
            final Attribute tag = services[ i ].getAttribute( "mx" );
            if( null != tag )
            {
                serviceSet.add( toPhoenixService( services[ i ] ) );
            }
        }
        return (ServiceDescriptor[])serviceSet.toArray( new ServiceDescriptor[ serviceSet.size() ] );
    }

    /**
     * Return Phoenix services from Info Service array.
     *
     * @param services the services
     * @return the Phoenix services
     */
    private static ServiceDescriptor[] toPhoenixServices(
        final org.apache.avalon.framework.info.ServiceDescriptor[] services )
    {
        final ArrayList serviceSet = new ArrayList();
        for( int i = 0; i < services.length; i++ )
        {
            serviceSet.add( toPhoenixService( services[ i ] ) );
        }
        return (ServiceDescriptor[])serviceSet.toArray( new ServiceDescriptor[ serviceSet.size() ] );
    }

    /**
     * Convert Info service to Phoenix Service descriptor.
     *
     * @param service the Info Service
     * @return the Phoenix service
     */
    private static ServiceDescriptor toPhoenixService(
        final org.apache.avalon.framework.info.ServiceDescriptor service )
    {
        final Version version = toVersion( service );
        final String classname = service.getType();
        return new ServiceDescriptor( classname, version );
    }

    /**
     * Convert Info dependencys to Phoenix dependencys.
     *
     * @param dependencies the Info dependencys
     * @return the Phoenix dependencys
     */
    private static DependencyDescriptor[] toPhoenixDependencys(
        final org.apache.avalon.framework.info.DependencyDescriptor[] dependencies )
    {
        final ArrayList depends = new ArrayList();
        for( int i = 0; i < dependencies.length; i++ )
        {
            depends.add( toPhoenixDependency( dependencies[ i ] ) );
        }
        return (DependencyDescriptor[])depends.toArray( new DependencyDescriptor[ depends.size() ] );
    }

    /**
     * Convert Info dependency to Phoenix dependency descriptor.
     *
     * @param dependency the Info dependency
     * @return the Phoenix dependency
     */
    private static DependencyDescriptor toPhoenixDependency(
        final org.apache.avalon.framework.info.DependencyDescriptor dependency )
    {
        final Version version = toVersion( dependency );
        final ServiceDescriptor service =
            new ServiceDescriptor( dependency.getType(), version );
        return new DependencyDescriptor( dependency.getKey(), service );
    }

    /**
     * Create a BlockDescriptor object from ComponentInfo.
     *
     * @param component the info
     * @return the BlockDescriptor
     */
    private static BlockDescriptor toBlockDescriptor( final ComponentInfo component )
    {
        final ComponentDescriptor descriptor = component.getDescriptor();
        final Version version = toVersion( descriptor );

        //FIXME: Assuming that getSchema is replaced with getConfigurationSchema. /LS
        String schemaType = component.getConfigurationSchema().getType();
        if( "".equals( schemaType ) )
        {
            schemaType = null;
        }

        return new BlockDescriptor( null,
                                    descriptor.getImplementationKey(),
                                    schemaType,
                                    version );
    }

    /**
     * Create a version for a feature. Defaults to 1.0 if not specified.
     *
     * @param feature the feature
     * @return the Version object
     */
    private static Version toVersion( final FeatureDescriptor feature )
    {
        final Attribute tag = feature.getAttribute( "avalon" );
        Version version = new Version( 1, 0, 0 );
        if( null != tag )
        {
            final String versionString = tag.getParameter( "version" );
            if( null != versionString )
            {
                version = Version.getVersion( versionString );
            }
        }
        return version;
    }
}
