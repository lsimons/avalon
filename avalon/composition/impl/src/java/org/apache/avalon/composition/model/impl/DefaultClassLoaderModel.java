/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.composition.model.impl;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;

import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;

import java.security.cert.Certificate;

import org.apache.avalon.composition.data.CertsDirective;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.ClassLoaderDirective;
import org.apache.avalon.composition.data.FilesetDirective;
import org.apache.avalon.composition.data.GrantDirective;
import org.apache.avalon.composition.data.IncludeDirective;
import org.apache.avalon.composition.data.PermissionDirective;
import org.apache.avalon.composition.data.RepositoryDirective;
import org.apache.avalon.composition.data.ResourceDirective;

import org.apache.avalon.composition.model.ClassLoaderModel;
import org.apache.avalon.composition.model.TypeRepository;
import org.apache.avalon.composition.model.ServiceRepository;
import org.apache.avalon.composition.model.ModelException;

import org.apache.avalon.composition.provider.ClassLoaderContext;

import org.apache.avalon.composition.util.StringHelper;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;

import org.apache.avalon.extension.Extension;
import org.apache.avalon.extension.manager.ExtensionManager;
import org.apache.avalon.extension.manager.OptionalPackage;
import org.apache.avalon.extension.manager.PackageManager;
import org.apache.avalon.extension.manager.impl.DefaultExtensionManager;
import org.apache.avalon.extension.manager.impl.DelegatingExtensionManager;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * <p>Implementation of a classloader model within which a 
 * repository, a base directory and a classloader directive 
 * are associated together enabling the creation of a fully 
 * qualified classpath.</p>
 *
 * <p>The classpath established by this model implementation
 * applies the following logic:</p>
 * <ul>
 *  <li>establish an extensions manager relative to the 
 *      &lt;library/&gt> directives</li>
 *  <li>build an uqualifed classpath relative to the  
 *      &lt;classpath/&gt> directives</li>
 *  <li>resolve any optional jar file extension jar file
 *      entries based on the manifest declarations of 
 *      the unqualified classpath, together with recursive
 *      resolution of resolved optional extensions</li>
 *  <li>consolidate the generated classpath relative to 
 *      the optional extensions established by any parent
 *      classloader models</li>
 * </ul>
 * <p>
 * Class dependecies include the Excalibur i18n, the assembly 
 * repository package, the avalon framework and meta packages,
 * and the extensions package.
 * </p>
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.10 $ $Date: 2004/02/23 13:00:31 $
 */
public class DefaultClassLoaderModel extends AbstractLogEnabled 
    implements ClassLoaderModel
{
    //==============================================================
    // static
    //==============================================================

    private static final Resources REZ =
            ResourceManager.getPackageResources( DefaultClassLoaderModel.class );

    //private static ClassLoaderModel createClassLoaderModel( 
    //  ClassLoaderContext context ) throws ModelException
    //{
    //    return new DefaultClassLoaderModel( context );
    //}

    //==============================================================
    // state
    //==============================================================

    private final ClassLoaderContext m_context;

    private final ExtensionManager m_extension;

    private final PackageManager m_manager;

    private final String[] m_classpath;

    private final OptionalPackage[] m_packages;

    private final URL[] m_urls;

    private final URLClassLoader m_classLoader;

    private ProtectionDomain[] m_protectionDomains;
    
    private final DefaultTypeRepository m_types;

    private final DefaultServiceRepository m_services;

    private final Logger m_local;

    //==============================================================
    // constructor
    //==============================================================

   /**
    * Creation of a new classloader model.  The model associated a 
    * repository, a base directory and a classloader directive 
    * enabling the creation of a fully populated classpath.
    *
    * @param context the classloader context
    */
    public DefaultClassLoaderModel( ClassLoaderContext context ) 
      throws ModelException
    { 
        if( context == null )
        {
            throw new NullPointerException( "context" );
        }

        m_context = context;
        enableLogging( context.getLogger() );
        m_local = getLogger().getChildLogger( "classloader" );
        if( getLogger().isDebugEnabled() )
        {
            getLocalLogger().debug( 
              "base: " + 
              StringHelper.toString( context.getBaseDirectory() ) );
        }
        File base = 
          context.getBaseDirectory();
        Repository repository = 
          context.getRepository();
        ClassLoaderDirective directive = 
          context.getClassLoaderDirective();
        ExtensionManager manager = 
          context.getExtensionManager();
        URL[] implicit = context.getImplicitURLs();

        try
        {
            if( manager != null )
            {
                DefaultExtensionManager local = 
                  new DefaultExtensionManager( 
                    directive.getLibrary().getOptionalExtensionDirectories( base ) );
                m_extension = new DelegatingExtensionManager(
                  new ExtensionManager[]{ manager, local } );
            }
            else
            {
                m_extension = new DefaultExtensionManager( 
                  directive.getLibrary().getOptionalExtensionDirectories( base ) );
            }

            m_manager = new PackageManager( m_extension );
            m_classpath = createClassPath( base, repository, directive, implicit );
            
            if( getLocalLogger().isDebugEnabled() )
            {
                String str = "classpath: " 
                  + StringHelper.toString( m_classpath );
                getLocalLogger().debug( str );
            }

            m_packages = buildOptionalPackages( 
              m_classpath, context.getOptionalPackages() );
            m_urls = buildQualifiedClassPath();
            m_classLoader = 
              new URLClassLoader( m_urls, context.getClassLoader() );
            m_protectionDomains = createProtectionDomains( directive.getGrantDirective() );

            //
            // scan the classpath for component type and service
            // definitions
            //

            ArrayList types = new ArrayList();
            ArrayList services = new ArrayList();
            Logger scannerLogger = getLocalLogger().getChildLogger( "scanner" );
            Scanner scanner = new Scanner( scannerLogger, m_classLoader );
            scanner.scan( m_urls, types, services );

            //
            // create the repository supporting type and service lookup
            //

            Logger typeLogger = getLocalLogger().getChildLogger( "types" );
            m_types = new DefaultTypeRepository( 
              typeLogger, m_classLoader, context.getTypeRepository(), types );
            Logger serviceLogger = getLocalLogger().getChildLogger( "services" );
            m_services = new DefaultServiceRepository( 
              serviceLogger, context.getServiceRepository(), services );
        }
        catch( Throwable e )
        {
            final String error = "Could not create classloader.";
            throw new ModelException( error, e );
        }
    }

    //==============================================================
    // ClassLoaderModel
    //==============================================================

   /**
    * Creation of a classloader model using this model as the 
    * relative parent.
    *
    * @param logger the loggiong channel
    * @param profile the profile directive
    * @param implied a sequence of implied urls
    * @return a new classloader context
    */
    public ClassLoaderModel createClassLoaderModel( 
       Logger logger, ContainmentProfile profile, URL[] implied )
       throws ModelException
    {
        ClassLoaderContext context = 
          createChildContext( logger, profile, implied );
        return new DefaultClassLoaderModel( context );
    }

   /**
    * Return the type repository managed by this containment
    * context.
    *
    * @return the repository
    */
    public TypeRepository getTypeRepository()
    {
        return m_types;
    }

   /**
    * Return the classloader model service repository.
    *
    * @return the repository
    */
    public ServiceRepository getServiceRepository()
    {
        return m_services;
    }

   /**
    * Return the optional extensions manager.
    * @return the extension manager
    */
    public ExtensionManager getExtensionManager()
    {
        return m_extension;
    }

   /**
    * Return the set of local established optional packages.
    *
    * @return the local set of optional packages
    */
    public OptionalPackage[] getOptionalPackages()
    {
        return getOptionalPackages( false );
    }

   /**
    * Return the set of optional packages already established including
    * the optional packages established by any parent classloader model.
    *
    * @param policy if TRUE, return the local and all ancestor optional 
    *   package - if FALSE only return the local packages
    * @return the OptionalPackage instances
    */
    public OptionalPackage[] getOptionalPackages( boolean policy )
    {
        if( !policy )
        {
            return m_packages;
        }

        final ArrayList list = new ArrayList();
        OptionalPackage[] available = m_context.getOptionalPackages();
        for( int i=0; i<available.length; i++ )
        {
             list.add( available[i] );
        }
        for( int i=0; i<m_packages.length; i++ )
        {
            list.add( m_packages[i] );
        }

        return (OptionalPackage[]) list.toArray( new OptionalPackage[0] );
    }

   /**
    * Return the fully qualified classpath including extension jar files
    * resolved relative to the classpath directives in the meta-data
    * and any parent classloader models.
    *
    * @return an array of URL representing the complete classpath 
    */
    public URL[] getQualifiedClassPath()
    {
        return m_urls;
    }

   /**
    * Return the classloader established by this classloader model.
    * @return the classloader
    */
    public ClassLoader getClassLoader()
    {
        return m_classLoader;
    }

   /** 
    * Return the security ProtectionDomains defined for this ClassLoaderModel.
    * 
    * These ProtectionDomains will be enforced if code level security is enabled
    * globally. If no ProtectionDomain are returned, all the components under
    * this container will run without Permissions.
    *
    * @return A ProtectionDomain which should be enagaged if codelevel
    *         security is enabled for the Classloader.
    **/
    public ProtectionDomain[] getProtectionDomains()
    {
        return m_protectionDomains;
    }
    
    //==============================================================
    // private implementation
    //==============================================================

   /**
    * Creation of a classloader context using this model as the 
    * relative parent.
    *
    * @param logger the loggiong channel
    * @param profile the profile directive
    * @param implied a sequence of implied urls
    * @return a new classloader context
    */
    private ClassLoaderContext createChildContext( 
       Logger logger, ContainmentProfile profile, URL[] implied )
    {
        Repository repository = m_context.getRepository();
        File base = m_context.getBaseDirectory();
        OptionalPackage[] packages = getOptionalPackages();
        ClassLoaderDirective directive = 
          profile.getClassLoaderDirective();

        return new DefaultClassLoaderContext( 
          logger, repository, base, m_classLoader, packages,
          m_extension, m_types, m_services, directive, implied );
    }

    private String[] getClassPath()
    {
        return m_classpath;
    }

   /**
    * Build the fully qulalified classpath including extension jar files
    * resolved relative to the classpath directives in the meta-data.
    *
    * @return an array of URL representing the complete classpath 
    */
    private URL[] buildQualifiedClassPath()
      throws Exception
    {
        final ArrayList list = new ArrayList();
        final String[] classpath = getClassPath();
        for( int i=0; i<classpath.length; i++ )
        {
            list.add( new URL( classpath[i] ) );
        }
        File[] extensions = OptionalPackage.toFiles( getOptionalPackages() );
        for( int i=0; i<extensions.length; i++ )
        {
            list.add( extensions[i].toURL() );
        }
        return (URL[]) list.toArray( new URL[0] );
    }

    private String[] createClassPath( 
      File base, Repository repository, ClassLoaderDirective directive, URL[] implicit )
      throws Exception
    {
        ArrayList classpath = new ArrayList();

        if( implicit.length > 0 ) 
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "implicit entries: " + implicit.length );
            }

            for( int i=0; i<implicit.length; i++ )
            {
                classpath.add( implicit[i].toString() );
            }
        }

        File[] files = 
          expandFileSetDirectives( 
            base, directive.getClasspathDirective().getFilesets() );
        addToClassPath( classpath, files );

        if( files.length > 0 ) 
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "included entries: " + files.length );
            }
        }

        RepositoryDirective[] repositories = 
          directive.getClasspathDirective().getRepositoryDirectives();

        if( repositories.length > 0 ) 
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( 
                  "repository declarations: " 
                  + repositories.length );
            }
        }

        for( int i=0; i<repositories.length; i++ )
        {
            ResourceDirective[] resources = repositories[i].getResources();
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "repository " + i 
                + " contains " 
                + resources.length 
                + " entries." );
            }

            for( int j=0; j<resources.length; j++ )
            {
                ResourceDirective resource = resources[j];
                String id = resource.getId();
                String version = resource.getVersion();
                if( resource.getType().equals( "jar" ) )
                {
                  URL url = repository.getResource(
                    Artifact.createArtifact(
                      resource.getGroup(),
                      resource.getName(),
                      resource.getVersion(),
                      resource.getType() ) );
                    classpath.add( url.toString() );
                }
            }
        }

        return (String[]) classpath.toArray( new String[0] );
    }

    private ProtectionDomain[] createProtectionDomains( GrantDirective directive )
    {
        Permissions permissions = new Permissions();
        PermissionDirective[] permDirectives = directive.getPermissionDirectives();
        for( int i=0 ; i < permDirectives.length ; i++ )
        {
            permissions.add( permDirectives[i].getPermission() );
        }
        
        CertsDirective certsDirective = directive.getCertsDirective();
        Certificate[] allCerts = certsDirective.getCertificates();
            
        ProtectionDomain[] pd = new ProtectionDomain[ m_urls.length ];
        for( int i=0 ; i < m_urls.length ; i++ )
        {
            CodeSource cs = new CodeSource( m_urls[i], allCerts );
            pd[i] = new ProtectionDomain( cs, permissions );
        }                
        return pd;
    }
    
    /**
     * Retrieve the files for the optional packages required by
     * the jars in ClassPath.
     *
     * @param classPath the Classpath array
     * @return the files that need to be added to ClassLoader
     * @exception Exception if a extension error occurs
     */
    private OptionalPackage[] buildOptionalPackages( final String[] classPath )
      throws Exception
    {
        return buildOptionalPackages( classPath, new OptionalPackage[0] );
    }

    /**
     * Retrieve the files for the optional packages required by
     * the jars in the ClassPath.
     *
     * @param classPath the Classpath array
     * @return the files that need to be added to ClassLoader
     * @exception Exception if a extension error occurs
     */
    private OptionalPackage[] buildOptionalPackages( 
      final String[] classPath, final OptionalPackage[] established )
      throws Exception
    {
        final ArrayList unsatisfied = new ArrayList();
        final ArrayList dependencies = new ArrayList();
        for( int i=0; i<established.length; i++ )
        {
            dependencies.add( established[i] );
        }

        final Manifest[] manifests = getManifests( classPath );
        final Extension[] available = Extension.getAvailable( manifests );
        final Extension[] required = Extension.getRequired( manifests );

        m_manager.scanDependencies( required, available, dependencies, unsatisfied );
        if( 0 != unsatisfied.size() )
        {
            final int size = unsatisfied.size();
            final String message =
              REZ.getString( 
                "classloader.unsatisfied-extensions.error", 
                new Integer( size ) );
            StringBuffer buffer = new StringBuffer( message );
            for( int i = 0; i < size; i++ )
            {
                final Extension extension = (Extension) unsatisfied.get( i );
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
                final String entry = REZ.format( 
                  "classloader.missing.extension.error", params );
                buffer.append( "\n" + entry );
            }
            throw new ModelException( buffer.toString() );
        }

        final OptionalPackage[] packages =
                (OptionalPackage[]) dependencies.toArray( new OptionalPackage[0] );
        return consolidate( packages, established );
    }

    private OptionalPackage[] consolidate( 
      OptionalPackage[] includes, OptionalPackage[] excludes )
    {
        ArrayList list = new ArrayList();
        for( int i=0; i<includes.length; i++ )
        {
            boolean skip = false;
            OptionalPackage inc = includes[i];
            File file = inc.getFile();
            for( int j=0; j<excludes.length; j++ )
            {
                if( file.equals( excludes[j].getFile() ) );
                skip = true;
                break;
            }
            if( !skip )
            {
                list.add( inc );
            }
        }
        return (OptionalPackage[]) list.toArray( 
          new OptionalPackage[0] );
    }

    private void addToClassPath( List list, File[] files )
      throws IOException
    {
        for( int i=0; i<files.length; i++ )
        {
            addToClassPath( list, files[i] );
        }
    }

    private void addToClassPath( List list, File file )
      throws IOException
    {
        File canonical = file.getCanonicalFile();
        String uri = canonical.toURL().toString();
        list.add( uri );
    }

    private Manifest[] getManifests( final String[] classPath )
            throws ModelException
    {
        final ArrayList manifests = new ArrayList();
        for( int i = 0; i < classPath.length; i++ )
        {
            final String element = classPath[i];

            if( element.endsWith( ".jar" ) || element.startsWith( "jar:" ) )
            {
                try
                {
                    URL url = null;
                    if( element.startsWith( "jar:" ) )
                    {
                        url = new URL( element );
                    } 
                    else
                    {
                        url = new URL( "jar:" + element + "!/" );
                    }

                    final JarURLConnection connection =
                            (JarURLConnection) url.openConnection();
                    final Manifest manifest = connection.getManifest();
                    if( null != manifest )
                    {
                        manifests.add( manifest );
                    }
                } 
                catch( final IOException ioe )
                {
                    final String message =
                      REZ.getString( "classloader.bad-classpath-entry.error", element );
                    throw new ModelException( message, ioe );
                }
            }
        }
        return (Manifest[]) manifests.toArray( new Manifest[0] );
    }


   /**
    * Return an array of files corresponding to the expansion 
    * of the filesets declared within the directive.
    *
    * @param base the base directory against which relative 
    *   file references will be resolved
    * @return the classpath
    */
    public File[] expandFileSetDirectives( 
      File base, FilesetDirective[] filesets ) throws IOException
    {
        ArrayList list = new ArrayList();

        for( int i=0; i<filesets.length; i++ )
        {
            FilesetDirective fileset = filesets[i];
            File anchor = getDirectory( base, fileset.getBaseDirectory() );
            IncludeDirective[] includes = fileset.getIncludes();
            if( includes.length > 0 )
            {
                for( int j=0; j<includes.length; j++ )
                {
                    File file = new File( anchor, includes[j].getPath() );
                    list.add( file );
                }
            }
            else
            {
                list.add( anchor );
            }
        }

        return (File[]) list.toArray( new File[0] );
    }

    private File getDirectory( File base, String path ) throws IOException
    {
        File file = new File( path );
        if( file.isAbsolute() )
        {
            return verifyDirectory( file );
        }
        return verifyDirectory( new File( base, path ) );
    }

    private File verifyDirectory( File dir ) throws IOException
    {
        if( dir.isDirectory() )
        {
            return dir.getCanonicalFile();
        }

        final String error = 
          "Path does not correspond to a directory: " + dir;
        throw new IOException( error );
    }
    
    private Logger getLocalLogger()
    {
        return m_local;
    }
}
