/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.infobuilder;

import java.util.ArrayList;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.phoenix.metadata.InterceptorMetaData;

/**
 * A InterceptorInfoBuilder is responsible for building {@link org.apache.avalon.phoenix.metadata.InterceptorMetaData}.
 * 
 * @author <a href="mailto:igorfie at yahoo.com">Igor Fedorenko</a>
 * @version $Revision: 1.1.2.2 $ $Date: 2002/10/20 01:00:15 $
 */
public class InterceptorInfoBuilder
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( InterceptorInfoBuilder.class );

    /**
     * A utility method to build an array of {@link org.apache.avalon.phoenix.metadata.InterceptorMetaData}
     * objects from specified configuraiton.
     * 
     * @param configuration the interceptors configuration
     * @return the created InterceptorMetaData array
     * @throws ConfigurationException if an error occurs
     */
    public InterceptorMetaData[] build( Configuration configuration )
        throws ConfigurationException
    {
        final boolean proxyDisabled = configuration.getAttributeAsBoolean("disable", false);
        final Configuration[] elements = configuration.getChildren( "interceptor" );
        if( proxyDisabled && elements.length > 0 )
        {
            final String message =
                REZ.getString( "interceptors-without-proxy", configuration.getLocation() );
            throw new ConfigurationException( message );
        }
        final ArrayList interceptors = new ArrayList();

        for( int i = 0; i < elements.length; i++ )
        {
            final InterceptorMetaData interceptor = buildInterceptor( elements[ i ] );
            interceptors.add( interceptor );
        }

        return (InterceptorMetaData[])interceptors.toArray( new InterceptorMetaData[ 0 ] );
    }

    /**
     * A utility method to build a {@link org.apache.avalon.phoenix.metadata.InterceptorMetaData}
     * object from specified configuraiton.
     *
     * @param interceptor the interceptor configuration
     * @return the created InterceptorMetaData
     * @throws ConfigurationException if an error occurs
     */
    private InterceptorMetaData buildInterceptor( final Configuration interceptor )
        throws ConfigurationException
    {
        String classname = interceptor.getAttribute( "class" );

        return new InterceptorMetaData( classname );
    }

}
