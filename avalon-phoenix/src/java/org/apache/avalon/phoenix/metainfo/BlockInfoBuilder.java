/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.metainfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.Version;

/**
 * A BlockInfoBuilder builds configurations via SAX2 compliant parser.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class BlockInfoBuilder
{
    private DefaultConfigurationBuilder     m_builder = new DefaultConfigurationBuilder();

    public BlockInfo build( final String resource )
        throws Exception
    {
        final Configuration info = m_builder.build( resource );

        Configuration configuration  = null;

        configuration = info.getChild( "services" );
        final ServiceDescriptor services[] = buildServices( configuration );

        configuration = info.getChild( "dependencies" );
        final DependencyDescriptor dependencies[] = buildDependencies( configuration );

        configuration = info.getChild( "meta" );
        final BlockDescriptor descriptor = buildBlockDescriptor( configuration );

        return new BlockInfo( descriptor, services, dependencies );
    }

    private DependencyDescriptor[] buildDependencies( final Configuration configuration )
        throws ConfigurationException
    {
        if( null == configuration ) return new DependencyDescriptor[0];

        final Configuration[] elements = configuration.getChildren( "dependency" );
        final ArrayList descriptors = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final DependencyDescriptor descriptor = buildDependency( elements[ i ] );
            descriptors.add( descriptor );
        }

        return (DependencyDescriptor[]) descriptors.toArray( new DependencyDescriptor[0] );
    }

    private DependencyDescriptor buildDependency( final Configuration dependency )
        throws ConfigurationException
    {
        final String role = dependency.getChild( "role" ).getValue();
        final ServiceDescriptor serviceDescriptor = buildService( dependency.getChild( "service" ) );

        return new DependencyDescriptor( role, serviceDescriptor );
    }

    private ServiceDescriptor[] buildServices( final Configuration servicesSet )
        throws ConfigurationException
    {
        if( null == servicesSet ) return new ServiceDescriptor[0];

        final Configuration[] elements = servicesSet.getChildren( "service" );
        final ArrayList descriptors = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final ServiceDescriptor descriptor = buildService( elements[ i ] );
            descriptors.add( descriptor );
        }

        return (ServiceDescriptor[])descriptors.toArray( new ServiceDescriptor[0] );
    }

    private ServiceDescriptor buildService( final Configuration service )
        throws ConfigurationException
    {
        final String name = service.getAttribute( "name" );
        //TODO: replace this with Version.getVersion( version );
        final Version version =  buildVersion( service.getAttribute( "version" ) );
        return new ServiceDescriptor( name, version );
    }

    private Version buildVersion( final String version )
    {
        final int length = version.length();
        int start = 0;
        int end = version.indexOf( '.' );
        int major = 1;
        int minor = 0;
        int revision = 0;

        try { major = Integer.parseInt( version.substring( start, end ) ); }
        catch( final NumberFormatException nfe ) { }

        start = end + 1;
        end = version.indexOf( '.', start );

        if( -1 == end ) end = version.length();

        try { minor = Integer.parseInt( version.substring( start, end ) ); }
        catch( final NumberFormatException nfe ) { }

        if( end != length )
        {
            start = end + 1;
            end = length;

            try { revision = Integer.parseInt( version.substring( start, end ) ); }
            catch( final NumberFormatException nfe ) { }
        }

        return new Version( major, minor, revision );
    }

    private BlockDescriptor buildBlockDescriptor( final Configuration meta )
    {
        if( null == meta ) return null;

        return null;
    }
}
