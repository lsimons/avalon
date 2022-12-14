/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.Manifest;
import org.apache.avalon.excalibur.extension.Extension;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.phoenix.components.extensions.pkgmgr.OptionalPackage;
import org.apache.avalon.phoenix.components.extensions.pkgmgr.PackageManager;
import org.realityforge.classman.builder.LoaderResolver;
import org.realityforge.classman.runtime.JoinClassLoader;

/**
 * This is a class that performs resolver that;
 * <ul>
 *   <li>creates "Join" ClassLoaders using the  {@link JoinClassLoader}
 *       class</li>
 *   <li>creates normal ClassLoaders using a {@link URLClassLoader}. It
 *       also makes sure that all dependencies of jars (as declared using
 *       the JDK1.3 "Optional Pakcages" Spec) are present in classloader.</li>
 *   <li>The locations are resolved to a single base directiory.</li>
 *   <li>The Extensions are resolved accoriding to a specified
 *       {@link PackageManager}.</li>
 *   <li>FileSets are currently unsupported and throw a
 *       {@link UnsupportedOperationException} if attempt to be constructed.</li>
 * </ul>
 *
 * @author Peter Donald
 * @version $Revision: 1.4 $ $Date: 2003/12/05 15:14:35 $
 * @deprecated Convert to ClassMan SimpleLoaderResolver when it updates
 *             dependecy to latest Excalibur-Extension
 */
class SimpleLoaderResolver
    implements LoaderResolver
{
    private final static Resources REZ =
        ResourceManager.getPackageResources( SimpleLoaderResolver.class );

    /**
     * The base directory relative to which to aquire files.
     */
    private File m_baseDirectory;

    /**
     * The PackageManager to use to resolve Extensions.
     */
    private PackageManager m_manager;

    /**
     * Create a resolver that resolves all files according to specied
     * baseDirectory and using specified {@link PackageManager} to aquire
     * {@link Extension} objects.
     *
     * @param baseDirectory the base directory
     * @param manager the {@link PackageManager}
     */
    public SimpleLoaderResolver( final File baseDirectory,
                                 final PackageManager manager )
    {
        setBaseDirectory( baseDirectory );
        setManager( manager );
    }

    /**
     * Retrieve a URL for specified extension.
     *
     * @param extension the extension
     * @return the URL
     * @throws Exception if unable to locate URL for extension
     */
    public URL resolveExtension( final Extension extension )
        throws Exception
    {
        if( null == getManager() )
        {
            final String message =
                REZ.getString( "missing-packagemanager" );
            throw new IllegalStateException( message );
        }
        final OptionalPackage optionalPackage =
            getManager().getOptionalPackage( extension );
        return optionalPackage.getFile().toURL();
    }

    /**
     * Resolve a location to a particular URL.
     *
     * @param location the location
     * @return the URL
     * @throws Exception if unable to resolve location
     */
    public URL resolveURL( final String location )
        throws Exception
    {
        final File file = getFileFor( location );
        String url = file.toURL().toString();
        if( file.isDirectory() )
        {
            url += "/";
        }
        return new URL( url );
    }

    /**
     * Resolve all URLs in a particular fileset.
     *
     * @param baseDirectory the basedirectory
     * @param includes the pattern for includes
     * @param excludes the pattern for excludes
     * @return an array of URLs in fileset
     * @throws Exception if unable to aquire URLs.
     */
    public URL[] resolveFileSet( final String baseDirectory,
                                 final String[] includes,
                                 final String[] excludes )
        throws Exception
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Create a Join ClassLoader for specified ClassLoaders.
     * Use {@link JoinClassLoader} to implement functionality.
     *
     * @param classLoaders the ClassLoaders to "join"
     * @return the joined ClassLoader
     * @throws Exception if unable to create classloader
     */
    public ClassLoader createJoinClassLoader( final ClassLoader[] classLoaders )
        throws Exception
    {
        return new JoinClassLoader( classLoaders,
                                    ClassLoader.getSystemClassLoader() );
    }

    /**
     * Create a ClassLoader with specified parent and
     * containing specified URLs. This implementation just creates
     * it using the default URLClassLoader.
     *
     * @param parent the parent classloader
     * @param urls the URLs that the ClassLoader should contain
     * @return the newly created ClassLoader
     * @throws Exception if unable to create classloader
     */
    public ClassLoader createClassLoader( final ClassLoader parent,
                                          final URL[] urls )
        throws Exception
    {
        final URL[] classpath = determineCompleteClasspath( urls );
        return new URLClassLoader( classpath, parent );
    }

    /**
     * Retrieve the complete classpath given an input set of URLs.
     * The complete classpath includes all URLs for extensions
     * required by the jars (according to the "Optional Package"
     * Spec).
     *
     * @param urls the urls
     * @return the complete set of URLs for classpath
     * @throws Exception if unable to determine complete classpath set
     */
    protected final URL[] determineCompleteClasspath( final URL[] urls )
        throws Exception
    {
        final ArrayList classpathSet = new ArrayList();

        //Add all supplied URLS to classpath
        for( int i = 0; i < urls.length; i++ )
        {
            final URL url = urls[ i ];
            classpathSet.add( url );
        }

        //Add all the optional packages that are declared as
        // dependencies of class path elements
        final File[] files = getOptionalPackagesFor( urls );
        for( int i = 0; i < files.length; i++ )
        {
            final File file = files[ i ];
            classpathSet.add( file.toURL() );
        }

        //Define final classpath with all dependencies added
        return (URL[])classpathSet.toArray( new URL[ classpathSet.size() ] );
    }

    /**
     * Utility class to retrieve a file object for specified location.
     *
     * @param location which to get file for.
     * @return the file for specified location
     */
    protected File getFileFor( final String location )
        throws IOException
    {
        File base = getBaseDirectory();
        if( null == base )
        {
            base = new File( "." );
        }

        return new File( base, location ).getCanonicalFile();
    }

    /**
     * Return the base directory against which to resolve relative files.
     *
     * @return the base directory against which to resolve relative files.
     */
    protected File getBaseDirectory()
    {
        return m_baseDirectory;
    }

    /**
     * Set the base directory.
     *
     * @param baseDirectory the base directory.
     */
    protected void setBaseDirectory( File baseDirectory )
    {
        m_baseDirectory = baseDirectory;
    }

    /**
     * Return the PackageManager for resolver.
     *
     * @return the PackageManager for resolver.
     */
    protected PackageManager getManager()
    {
        return m_manager;
    }

    /**
     * Set the PackageManager for resolver.
     *
     * @param manager the PackageManager for resolver.
     */
    protected void setManager( final PackageManager manager )
    {
        m_manager = manager;
    }

    /**
     * Retrieve the files for the optional packages required by
     * the jars in ClassPath.
     *
     * @param classPath the Classpath array
     * @return the files that need to be added to ClassLoader
     */
    protected final File[] getOptionalPackagesFor( final URL[] classPath )
        throws Exception
    {
        final Manifest[] manifests = getManifests( classPath );
        final Extension[] available = Extension.getAvailable( manifests );
        final Extension[] required = Extension.getRequired( manifests );

        if( isDebugEnabled() )
        {
            final String message1 =
                REZ.getString( "available-extensions",
                               Arrays.asList( available ) );
            debug( message1 );
            final String message2 =
                REZ.getString( "required-extensions",
                               Arrays.asList( required ) );
            debug( message2 );
        }

        if( 0 == required.length )
        {
            return new File[ 0 ];
        }

        final ArrayList dependencies = new ArrayList();
        final ArrayList unsatisfied = new ArrayList();

        if( null == getManager() )
        {
            final String message =
                REZ.getString( "missing-packagemanager" );
            throw new IllegalStateException( message );
        }

        m_manager.scanDependencies( required,
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
                warn( message );
            }

            final String message =
                REZ.getString( "unsatisfied.extensions", new Integer( size ) );
            throw new Exception( message );
        }

        if( isDebugEnabled() )
        {
            final String message =
                REZ.getString( "optional-packages-added", dependencies );
            debug( message );
        }

        final OptionalPackage[] packages =
            (OptionalPackage[])dependencies.toArray( new OptionalPackage[ 0 ] );
        return OptionalPackage.toFiles( packages );
    }

    /**
     * write out a warning message. Subclasses may overide this
     * method to redirect logging as appropriate.
     *
     * @param message the warning message
     */
    protected void warn( final String message )
    {
    }

    /**
     * Determine if debug messages are turned on.
     * Subclasses should overide this method.
     *
     * @return true if debugging enabled.
     */
    protected boolean isDebugEnabled()
    {
        return false;
    }

    /**
     * write out a debug message. Subclasses may overide this
     * method to redirect logging as appropriate.
     *
     * @param message the debug message
     */
    protected void debug( final String message )
    {
    }

    /**
     * Retrieve all the Manifests from the specified Classlpath.
     *
     * @param classPath the classpath
     * @return the set of manifests on the classpath
     * @throws Exception if there is an error reading manifests
     *                   from files on classpath
     */
    private Manifest[] getManifests( final URL[] classPath )
        throws Exception
    {
        final ArrayList manifests = new ArrayList();

        for( int i = 0; i < classPath.length; i++ )
        {
            final URL element = classPath[ i ];
            if( element.getFile().endsWith( ".jar" ) )
            {
                try
                {
                    final URL url = new URL( "jar:" + element + "!/" );
                    final JarURLConnection connection =
                        (JarURLConnection)url.openConnection();
                    final Manifest manifest = connection.getManifest();
                    if( null != manifest )
                    {
                        manifests.add( manifest );
                    }
                }
                catch( final IOException ioe )
                {
                    final String message =
                        REZ.getString( "bad-classpath-entry", element );
                    throw new Exception( message );
                }
            }
        }

        return (Manifest[])manifests.toArray( new Manifest[ 0 ] );
    }
}
