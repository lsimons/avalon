/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.metainfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.avalon.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.util.Version;

/**
 * A BlockInfoBuilder builds configurations via SAX2 compliant parser.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class BlockInfoBuilder
{
    protected DefaultConfigurationBuilder     m_builder = new DefaultConfigurationBuilder();

    public BlockInfo build( final InputStream inputStream )
        throws Exception
    {
        final Configuration info = m_builder.build( inputStream );

        Configuration configuration  = null;

        configuration = info.getChild( "services" );
        final ServiceInfo services[] = buildServices( configuration );

        configuration = info.getChild( "dependencies" );
        final DependencyInfo dependencies[] = buildDependencies( configuration );

        configuration = info.getChild( "meta" );
        final MetaInfo metaInfo = buildMetaInfo( configuration );

        return new DefaultBlockInfo( metaInfo, services, dependencies );
    }

    protected DependencyInfo[] buildDependencies( final Configuration configuration )
        throws ConfigurationException
    {
        if( null == configuration ) return new DependencyInfo[0];

        final Configuration[] elements = configuration.getChildren( "dependency" );
        final ArrayList infos = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final DependencyInfo info = buildDependency( elements[ i ] );
            infos.add( info );
        }

        return (DependencyInfo[]) infos.toArray( new DependencyInfo[0] );
    }

    protected DependencyInfo buildDependency( final Configuration dependency )
        throws ConfigurationException
    {
        final String role = dependency.getChild( "role" ).getValue();
        final ServiceInfo serviceInfo = buildService( dependency.getChild( "service" ) );

        return new DefaultDependencyInfo( role, serviceInfo );
    }

    protected ServiceInfo[] buildServices( final Configuration servicesSet )
        throws ConfigurationException
    {
        if( null == servicesSet ) return new ServiceInfo[0];

        final Configuration[] elements = servicesSet.getChildren( "service" );
        final ArrayList infos = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final ServiceInfo info = buildService( elements[ i ] );
            infos.add( info );
        }

        return (ServiceInfo[])infos.toArray( new ServiceInfo[0] );
    }

    protected ServiceInfo buildService( final Configuration service )
        throws ConfigurationException
    {
        final String name = service.getAttribute( "name" );
        final Version version =  buildVersion( service.getAttribute( "version" ) );
        return new DefaultServiceInfo( name, version );
    }

    protected Version buildVersion( final String version )
    {
        final int length = version.length();
        int start = 0;
        int end = version.indexOf('.');
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

    protected MetaInfo buildMetaInfo( final Configuration meta )
    {
        if( null == meta ) return null;

        return null;
    }
}
