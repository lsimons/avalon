/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.classloader;

import org.apache.excalibur.loader.builder.DefaultLoaderResolver;
import org.apache.avalon.excalibur.packagemanager.PackageManager;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.container.ContainerUtil;
import java.net.URL;
import java.security.Policy;
import java.io.File;

/**
 * a LoaderResolver that knows about Phoenixs environment,
 * and the way it is split across multiple directories.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $ $Date: 2002/10/31 21:52:31 $
 */
class SarLoaderResolver
    extends DefaultLoaderResolver
    implements LogEnabled
{
    /**
     * Logger to use when reporting information
     */
    private Logger m_logger;

    /**
     * The policy object to use when creating ClassLoaders
     */
    private Policy m_policy;

    /**
     * Base work directory for application.
     */
    private File m_workDirectory;

    /**
     * Create a resolver for a jar.
     * The resolver merges both the work and base directory
     * hierarchies.
     *
     * @param manager the PackageManager
     * @param policy the policy to use when creating classloaders
     * @param baseDirectory the base directory
     * @param workDirectory the base work directory
     */
    SarLoaderResolver( final PackageManager manager,
                       final Policy policy,
                       final File baseDirectory,
                       final File workDirectory )
    {
        super( baseDirectory, manager );
        if( null == policy )
        {
            throw new NullPointerException( "policy" );
        }
        if( null == baseDirectory )
        {
            throw new NullPointerException( "baseDirectory" );
        }
        if( null == workDirectory )
        {
            throw new NullPointerException( "workDirectory" );
        }

        m_policy = policy;
        m_workDirectory = workDirectory;
    }

    /**
     * Aquire an Avalon Logger.
     *
     * @param logger the avalon logger
     */
    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    /**
     * Resolve a fileset. Make sure it is resolved against
     * both the work and the base directories of application.
     *
     * @param baseDirectory the base directory of fileset
     * @param includes the fileset includes
     * @param excludes the ant style excludes
     * @return the URLs that are in fileset
     * @throws Exception if unable to resolve fileset
     */
    public URL[] resolveFileSet( final String baseDirectory,
                                 final String[] includes,
                                 final String[] excludes )
        throws Exception
    {
        final URL[] baseURLs =
            resolveFileSet( getBaseDirectory(), baseDirectory, includes, excludes );
        final URL[] workURLs =
            resolveFileSet( m_workDirectory, baseDirectory, includes, excludes );
        final URL[] urls = new URL[ baseURLs.length + workURLs.length ];
        System.arraycopy( baseURLs, 0, urls, 0, baseURLs.length );
        System.arraycopy( workURLs, 0, urls, baseURLs.length, workURLs.length );
        return urls;
    }

    /**
     * Create a ClassLoader that obeys policy in environment.xml.
     *
     * @param parent the parent classloader
     * @param urls the set of URLs for classloader
     * @return the new classloader
     * @throws Exception if unable to create classloader
     */
    public ClassLoader createClassLoader( final ClassLoader parent,
                                          final URL[] urls )
        throws Exception
    {
        final PolicyClassLoader loader =
            new PolicyClassLoader( urls, parent, m_policy );
        ContainerUtil.enableLogging( loader, m_logger );
        return loader;
    }

    /**
     * Route Logging to Avalons Logger.
     *
     * @param message the debug message
     */
    protected void debug( final String message )
    {
        m_logger.debug( message );
    }

    /**
     * Defer to Avalons Logger to see if debug is enabled.
     *
     * @return true if debug is enabled
     */
    protected boolean isDebugEnabled()
    {
        return m_logger.isDebugEnabled();
    }

    /**
     * Route Logging to Avalons Logger.
     *
     * @param message the warn message
     */
    protected void warn( final String message )
    {
        m_logger.warn( message );
    }
}
