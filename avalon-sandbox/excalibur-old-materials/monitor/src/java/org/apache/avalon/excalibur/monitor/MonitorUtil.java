/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.monitor;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import java.util.ArrayList;
import java.lang.reflect.Constructor;

/**
 * A class that contains a few utility methods for working
 * creating resource sets from Avalons configuration objects.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/09/07 12:14:01 $
 */
class MonitorUtil
{
    private static final Class[] c_constructorParams =
        new Class[]{String.class};

    public static Resource[] configureResources( final Configuration[] resources,
                                                 final Logger logger )
    {
        final ArrayList results = new ArrayList();
        for( int i = 0; i < resources.length; i++ )
        {
            final Configuration initialResource = resources[ i ];
            final String key =
                initialResource.getAttribute( "key", "** Unspecified key **" );
            final String className =
                initialResource.getAttribute( "class", "** Unspecified class **" );

            try
            {
                final Resource resource = createResource( className, key );
                results.add( resource );

                if( logger.isDebugEnabled() )
                {
                    final String message =
                        "Initial Resource: \"" + key + "\" Initialized.";
                    logger.debug( message );
                }
            }
            catch( final Exception e )
            {
                if( logger.isWarnEnabled() )
                {
                    final String message =
                        "Initial Resource: \"" + key +
                        "\" Failed (" + className + ").";
                    logger.warn( message, e );
                }
            }
        }

        return (Resource[])results.toArray( new Resource[ results.size() ] );
    }

    private static Resource createResource( final String className,
                                            final String key )
        throws Exception
    {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class clazz = loader.loadClass( className );
        final Constructor initializer =
            clazz.getConstructor( c_constructorParams );
        return (Resource)initializer.newInstance( new Object[]{key} );
    }
}
