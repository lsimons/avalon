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
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;
import org.apache.avalon.phoenix.metadata.SarMetaData;

/**
 * Component that creates and manages the <code>ClassLoader</code>
 * for an application loaded out of a <code>.sar</code> deployment.
 *
 * <p>Currently it creates a policy based on the policy declaration 
 * in the configuration. It then just creates a URLClassLoader and 
 * populates it with the specified codebase <code>URL</code>s.</p>
 *
 * <p>In the future this class will scan the manifests for "Optional
 * Packages" formely called "Extensions" which it will add to the 
 * <code>ClassLoader</code></p>
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultClassLoaderManager
    extends AbstractLoggable
    implements ClassLoaderManager
{
    /**
     * Create a <code>ClassLoader</code> for a specific application.
     * See Class Javadoc for description of technique for creating 
     * <code>ClassLoader</code>.
     * 
     * @param server the configuration "server.xml" for the application
     * @param baseDirectory the base directory of application
     * @param classPath the list of URLs in applications deployment
     * @return the ClassLoader created
     * @exception Exception if an error occurs
     */
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
        final SarURLStreamHandlerFactory factory = new SarURLStreamHandlerFactory();
        return new PolicyClassLoader( classPath, parentClassLoader, factory, policy );
    }

    /**
     * Setup policy based on configuration data.
     *
     * @param configuration the configuration data
     * @param baseDirectory the applications base directory
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
