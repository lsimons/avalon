/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.Manifest;
import org.apache.avalon.excalibur.extension.Extension;
import org.apache.avalon.excalibur.extension.OptionalPackage;
import org.apache.avalon.excalibur.extension.PackageManager;
import org.apache.avalon.excalibur.extension.PackageRepository;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
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
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @see ClassLoaderManager
 */
public class DefaultClassLoaderManager
    extends AbstractLogEnabled
    implements ClassLoaderManager, Contextualizable, Serviceable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( DefaultClassLoaderManager.class );

    /**
     * Component to manage "Optional Packages" aka
     * Extensions to allow programs to declare dependencies
     * on such extensions.
     */
    private PackageManager m_packageManager;

    /**
     * Parent ClassLoader for all applications
     * aka as the "common" classloader.
     */
    private ClassLoader m_commonClassLoader;

    /**
     * Pass the Context to the Manager.
     * It is expected that the there will be an entry
     * <ul>
     *   <b>common.classloader</b> : ClassLoader shared betweeen
     *      container and applications</li>
     * </ul>
     *
     * @param context the context
     * @throws ContextException if context does not contain common classloader
     */
    public void contextualize( Context context )
        throws ContextException
    {
        m_commonClassLoader = (ClassLoader)context.get( "common.classloader" );
    }

    public void service( final ServiceManager serviceManager )
        throws ServiceException
    {
        final PackageRepository packageRepository =
            (PackageRepository)serviceManager.lookup( PackageRepository.ROLE );
        m_packageManager = new PackageManager( packageRepository );
    }

    /**
     * Create a <code>ClassLoader</code> for a specific application.
     * See Class Javadoc for description of technique for creating
     * <code>ClassLoader</code>.
     *
     * @param environment the configuration "environment.xml" for the application
     * @param source the source of application. (usually the name of the .sar file
     *               or else the same as baseDirectory)
     * @param homeDirectory the base directory of application
     * @param classPath the list of URLs in applications deployment
     * @return the ClassLoader created
     * @throws Exception if an error occurs
     */
    public ClassLoader createClassLoader( final Configuration environment,
                                          final File source,
                                          final File homeDirectory,
                                          final String[] classPath )
        throws Exception
    {
        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.getString( "classpath-entries", Arrays.asList( classPath ) );
            getLogger().debug( message );
        }

        //Configure policy
        final Configuration policyConfig = environment.getChild( "policy" );
        final Policy policy = configurePolicy( policyConfig, homeDirectory );

        final File[] extensions = getOptionalPackagesFor( classPath );
        if( getLogger().isDebugEnabled() )
        {
            final String message =
                REZ.getString( "optional-packages-added", Arrays.asList( extensions ) );
            getLogger().debug( message );
        }

        //If source is not a file then there will be no need to pass in
        //a URLStreamHandler factory anyway so we can just pass in null
        //SarURLStreamHandlerFactory factory = null;
        //if( source.isFile() )
        //{
        //final JarFile archive = new JarFile( source, true, JarFile.OPEN_READ );
        //factory = new SarURLStreamHandlerFactory( archive );
        //URL.setURLStreamHandlerFactory( factory );
        //}
        final PolicyClassLoader classLoader =
            new PolicyClassLoader( classPath, m_commonClassLoader, null, policy );
        setupLogger( classLoader, "classloader" );

        for( int i = 0; i < extensions.length; i++ )
        {
            final URL url = extensions[ i ].toURL();
            classLoader.addURL( url );
        }

        return classLoader;
    }

    /**
     * Retrieve the files for the optional packages required by
     * the jars in ClassPath.
     *
     * @param classPath the Classpath array
     * @return the files that need to be added to ClassLoader
     */
    private File[] getOptionalPackagesFor( final String[] classPath )
        throws Exception
    {
        final Manifest[] manifests = getManifests( classPath );
        final Extension[] available = Extension.getAvailable( manifests );
        final Extension[] required = Extension.getRequired( manifests );

        if( getLogger().isDebugEnabled() )
        {
            final String message1 =
                REZ.getString( "available-extensions", Arrays.asList( available ) );
            getLogger().debug( message1 );
            final String message2 =
                REZ.getString( "required-extensions", Arrays.asList( required ) );
            getLogger().debug( message2 );
        }

        final ArrayList dependencies = new ArrayList();
        final ArrayList unsatisfied = new ArrayList();

        m_packageManager.scanDependencies( required,
                                           available,
                                           dependencies,
                                           unsatisfied );

        if( 0 != unsatisfied.size() )
        {
            final int size = unsatisfied.size();
            for( int i = 0; i < size; i++ )
            {
                final Extension extension = (Extension)unsatisfied.get( i );
                final Object[] params = new Object[]
                {
                    extension.getExtensionName(),
                    extension.getSpecificationVendor(),
                    extension.getSpecificationVersion(),
                    extension.getImplementationVendor(),
                    extension.getImplementationVendorID(),
                    extension.getImplementationVersion(),
                    extension.getImplementationURL()
                };
                final String message = REZ.format( "missing.extension", params );
                getLogger().warn( message );
            }

            final String message =
                REZ.getString( "unsatisfied.extensions", new Integer( size ) );
            throw new Exception( message );
        }

        final OptionalPackage[] packages =
            (OptionalPackage[])dependencies.toArray( new OptionalPackage[ 0 ] );
        return OptionalPackage.toFiles( packages );
    }

    private Manifest[] getManifests( final String[] classPath )
        throws Exception
    {
        final ArrayList manifests = new ArrayList();

        for( int i = 0; i < classPath.length; i++ )
        {
            final String element = classPath[ i ];

            if( element.endsWith( ".jar" ) )
            {
                try
                {
                    final URL url = new URL( "jar:" + element + "!/" );
                    final JarURLConnection connection = (JarURLConnection)url.openConnection();
                    final Manifest manifest = connection.getManifest();
                    manifests.add( manifest );
                }
                catch( final IOException ioe )
                {
                    final String message = REZ.getString( "bad-classpath-entry", element );
                    getLogger().warn( message );
                    throw new Exception( message );
                }
            }
        }

        return (Manifest[])manifests.toArray( new Manifest[ 0 ] );
    }

    /**
     * Setup policy based on configuration data.
     *
     * @param configuration the configuration data
     * @param baseDirectory the applications base directory
     * @throws ConfigurationException if an error occurs
     */
    private Policy configurePolicy( final Configuration configuration,
                                    final File baseDirectory )
        throws ConfigurationException
    {
        final DefaultPolicy policy = new DefaultPolicy( baseDirectory );
        policy.enableLogging( getLogger() );
        policy.configure( configuration );
        return policy;
    }
}
