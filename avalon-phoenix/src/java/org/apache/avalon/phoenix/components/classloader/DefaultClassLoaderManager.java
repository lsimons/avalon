/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.net.URL;
import java.security.Policy;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultClassLoaderManager
    extends AbstractLoggable
    implements ClassLoaderManager
{
    public ClassLoader createClassLoader( final Configuration server,
                                          final File homeDirectory,
                                          final URL[] classPath )
        throws Exception
    {
        //Configure policy
        final Configuration policyConfig = server.getChild( "policy" );
        final Policy policy = configurePolicy( policyConfig, homeDirectory );

        //TODO: Load Extensions from Package Repository as required
        //TODO: Determine parentClassLoader in a safer fashion
        final ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        return new PolicyClassLoader( classPath, parentClassLoader, policy );
    }

    /**
     * Setup policy based on configuration data.
     *
     * @param configuration the configuration data
     * @exception ConfigurationException if an error occurs
     */
    private Policy configurePolicy( final Configuration configuration,
                                    final File baseDirectory )
        throws ConfigurationException
    {
        final DefaultPolicy policy = new DefaultPolicy( baseDirectory );
        policy.setLogger( getLogger() );
        policy.configure( configuration );
        return policy;
    }
}
