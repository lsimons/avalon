/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.Policy;
import java.util.ArrayList;
import java.util.jar.JarFile;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.interfaces.ClassLoaderManager;

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
     * @param source the source of application. (usually the name of the .sar file
     *               or else the same as baseDirectory)
     * @param baseDirectory the base directory of application
     * @param classPath the list of URLs in applications deployment
     * @return the ClassLoader created
     * @exception Exception if an error occurs
     */
    public ClassLoader createClassLoader( final Configuration server,
                                          final File source,
                                          final File homeDirectory,
                                          final String[] classPath )
        throws Exception
    {
        //Configure policy
        final Configuration policyConfig = server.getChild( "policy" );
        final Policy policy = configurePolicy( policyConfig, homeDirectory );

        //TODO: Load Extensions from Package Repository as required

        //TODO: Determine parentClassLoader in a safer fashion
        final ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();

        //If source is not a file then there will be no need to pass in
        //a URLStreamHandler factory anyway so we can just pass in null
        SarURLStreamHandlerFactory factory = null;
        if( source.isFile() )
        {
            final JarFile archive = new JarFile( source, true, JarFile.OPEN_READ );
            factory = new SarURLStreamHandlerFactory( archive );
            URL.setURLStreamHandlerFactory( factory );
        }

        final URL[] urls = createURLs( classPath, factory );

        final PolicyClassLoader classLoader = 
            new PolicyClassLoader( urls, parentClassLoader, factory, policy );
        setupLogger( classLoader, "classloader" );
        return classLoader;
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

    /**
     * Create an array of URL objects from strings, using specified URLHandlerFactory.
     *
     * @param classPath the string representation of urls
     * @return the URL array
     * @exception MalformedURLException if an error occurs
     */
    private URL[] createURLs( final String[] classPath,
                              final URLStreamHandlerFactory factory )
        throws MalformedURLException
    {
        final ArrayList urls = new ArrayList();

        for( int i = 0; i < classPath.length; i++ )
        {
            final URL url = createURL( classPath[ i ], factory );
            urls.add( url );
        }

        return (URL[])urls.toArray( new URL[ 0 ] );
    }

    /**
     * Utility method to create a URL from string representation
     * using our <code>URLStreamHandlerFactory</code> object.
     *
     * @param urlString the string representation of URL
     * @exception MalformedURLException if URL is badly formed or
     *            protocol can not be found
     */
    private URL createURL( final String urlString,
                           final URLStreamHandlerFactory factory )
        throws MalformedURLException
    {
        if( null == urlString )
        {
            throw new NullPointerException( "url" );
        }

        final int index = urlString.indexOf( ':' );
        if( -1 == index )
        {
            throw new MalformedURLException( "No scheme specified for url " + urlString );
        }

        final String scheme = urlString.substring( 0, index );

        URLStreamHandler handler = null;
        if( null != factory )
        {
            handler = factory.createURLStreamHandler( scheme );
        }

        return new URL( null, urlString, handler );
    }
}
