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

package org.apache.avalon.repository.main;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.NoSuchMethodException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLClassLoader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.JarURLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.jar.Manifest;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.RepositoryRuntimeException;
import org.apache.avalon.repository.meta.FactoryDescriptor;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.util.LoaderUtils;
import org.apache.avalon.repository.util.RepositoryUtils;

import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.env.EnvAccessException;
import org.apache.avalon.util.exception.ExceptionHelper;


/**
 * Sets up the environment to create repositories by downloading the required 
 * jars, preparing a ClassLoader and delegating calls to repository factory 
 * methods using the newly configured ClassLoader.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.20 $
 */
public class DefaultInitialContext extends AbstractBuilder implements InitialContext
{
    //------------------------------------------------------------------
    // public static 
    //------------------------------------------------------------------

   /**
    * Group identifier manifest key.
    */
    public static final String BLOCK_GROUP_KEY = "Block-Group";

   /**
    * The name of the properties file to be searched for confiuration
    * properties.  Seaches will be conducted on the current directory and 
    * the user's home directory.
    */
    public static final String AVALON_PROPERTIES = "avalon.properties";

   /**
    * Return the Avalon system common directory.  This directory is 
    * is used as the default root directory against which the 
    * default application repository is established.  
    * 
    * @return the avalon system home directory.
    */
    public static File getAvalonHome()
    {
        try
        {
            String path = 
              System.getProperty( "avalon.home", Env.getEnvVariable( "AVALON_HOME" ) );

            if( null != path )
            {
                return new File( path ).getCanonicalFile();
            }
            else
            {
                return new File(
                  System.getProperty( "user.home" ) 
                  + File.separator 
                  + ".avalon" ).getCanonicalFile();
            }
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to access symbol AVALON_HOME.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

    //------------------------------------------------------------------
    // private static 
    //------------------------------------------------------------------

    private static final String AVALON_IMPL_PROPERTIES = 
       "avalon.properties";

    private static final File USER_HOME = 
      new File( System.getProperty( "user.home" ) );

    private static final String[] DEFAULT_INITIAL_HOSTS = 
      new String[]{
        "http://dpml.net/", 
        "http://ibiblio.org/maven" };

    //------------------------------------------------------------------
    // immutable state 
    //------------------------------------------------------------------

   /** 
    * The application key.
    */
    private final String m_key;
        
   /** 
    * The instantiated delegate repository factory.
    */
    private final Factory m_factory;

   /**
    * The initial cache directory.
    */
    private final File m_cache;

   /**
    * The initial remote host names.
    */
    private final String[] m_hosts;

   /**
    * The base working directory.
    */
    private final File m_base;

    // ------------------------------------------------------------------------
    // constructors
    // ------------------------------------------------------------------------

    /**
     * Creates an initial repository context.
     * 
     * @deprecated use {@link DefaultInitialContextFactory}
     * @throws RepositoryException if an error occurs during establishment
     */
    public DefaultInitialContext( ) 
        throws RepositoryException
    {
         this( (File) null );
    }

    /**
     * Creates an initial repository context.
     * 
     * @deprecated use {@link DefaultInitialContextFactory}
     * @param cache the cache directory
     * @throws RepositoryException if an error occurs during establishment
     */
    public DefaultInitialContext( File cache ) 
        throws RepositoryException
    {
         this( cache, null );
    }

    /**
     * Creates an initial repository context.
     * 
     * @deprecated use {@link DefaultInitialContextFactory}
     * @param hosts a set of initial remote repository addresses 
     * @throws RepositoryException if an error occurs during establishment
     */
    public DefaultInitialContext( String[] hosts ) 
        throws RepositoryException
    {
         this( (File) null, hosts );
    }

    /**
     * Creates an initial repository context.
     * 
     * @deprecated use {@link DefaultInitialContextFactory}
     * @param hosts a set of initial remote repository addresses 
     * @param cache the cache directory
     * @throws RepositoryException if an error occurs during establishment
     */
    public DefaultInitialContext( File cache, String[] hosts ) 
        throws RepositoryException
    {
         this( (Artifact) null, cache, hosts );
    }
    
    /**
     * Creates an initial repository context.
     *
     * @deprecated use {@link DefaultInitialContextFactory}
     * @param artifact an artifact referencing the default implementation
     * @param cache the cache directory
     * @param hosts a set of initial remote repository addresses 
     * @throws RepositoryException if an error occurs during establishment
     */
    public DefaultInitialContext( 
      Artifact artifact, File cache, String[] hosts ) 
      throws RepositoryException
    {
        this( 
          null, null, artifact, cache, hosts );
    }

    /**
     * Creates an initial repository context.
     *
     * @deprecated use {@link DefaultInitialContextFactory}
     * @param base the base working directory
     * @param loader the parent classloader
     * @param artifact an artifact referencing the default implementation
     * @param cache the cache directory
     * @param hosts a set of initial remote repository addresses 
     * @throws RepositoryException if an error occurs during establishment
     */
    public DefaultInitialContext( 
      File base, ClassLoader loader, Artifact artifact, File cache, String[] hosts ) 
      throws RepositoryException
    {
        m_key = "avalon";

        m_base = setupBaseDirectory( base );
        m_cache = setupCache( cache, base );
        m_hosts = setupHosts( hosts, base );

        Artifact implementation = setupImplementation( artifact );
        ClassLoader parent = setupClassLoader( loader );

        //
        // Create the temporary directory to pull down files into
        //

        if ( ! m_cache.exists() ) m_cache.mkdirs();

        //
        // Build the url to access the properties of the implementation artifact
        // which is default mechanism dependent.
        //

        Attributes attributes = loadAttributes( m_cache, m_hosts, implementation );
        FactoryDescriptor descriptor = new FactoryDescriptor( attributes );
        String factory = descriptor.getFactory();
        if( null == factory ) 
        {
            final String error = 
              "Required property 'avalon.artifact.factory' not present in artifact: "
              + implementation + " under the active cache: [" + m_cache + "] using the "
              + "attribute sequence: " + attributes;
            throw new IllegalArgumentException( error );
        }

        //
        // Grab all of the dependents in one hit because this is 
        // the implementation so we can ignore api/spi spread.
        //

        Artifact[] dependencies = descriptor.getDependencies();

        int n = dependencies.length;
        URL[] urls = new URL[ n + 1];
        for( int i=0; i<n; i++ )
        {
            urls[i] = LoaderUtils.getResource( 
              dependencies[i], m_hosts, m_cache, true );
        }

        urls[ n ] = LoaderUtils.getResource( 
            implementation, m_hosts, m_cache, true );

        //
        // create the classloader
        //
        
        ClassLoader classloader = new URLClassLoader( urls, parent );
        Class clazz = loadFactoryClass( classloader, factory );

        //
        // load the actual repository implementation 
        //

        try
        {
            m_factory = createDelegate( classloader, clazz, this );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to establish a factory for the supplied artifact:";
            StringBuffer buffer = new StringBuffer( error );
            buffer.append( "\n artifact: " + implementation );
            buffer.append( "\n build: " + descriptor.getBuild() );
            buffer.append( "\n factory: " + descriptor.getFactory() );
            buffer.append( "\n source: " 
              + clazz.getProtectionDomain().getCodeSource().getLocation() );
            buffer.append( "\n cache: " + m_cache );
            throw new RepositoryException( buffer.toString(), e );
        }
    }

    /**
     * Creates an initial repository context.
     *
     * @param parent the parent classloader
     * @param artifact an artifact referencing the default implementation
     * @param base the base working directory
     * @param cache the cache directory
     * @param hosts a set of initial remote repository addresses 
     * @throws RepositoryException if an error occurs during establishment
     */
    DefaultInitialContext( 
      String key, ClassLoader parent, Artifact artifact, File base, File cache, String[] hosts ) 
      throws RepositoryException
    {
        if( null == key ) throw new NullPointerException( "key" ); 
        if( null == base ) throw new NullPointerException( "base" ); 
        if( null == parent ) throw new NullPointerException( "parent" ); 
        if( null == artifact ) throw new NullPointerException( "artifact" ); 
        if( null == cache ) throw new NullPointerException( "cache" ); 
        if( null == hosts ) throw new NullPointerException( "hosts" ); 

        m_key = key;
        m_base = base;
        m_cache = cache;
        m_hosts = hosts;

        Attributes attributes = loadAttributes( m_cache, m_hosts, artifact );
        FactoryDescriptor descriptor = new FactoryDescriptor( attributes );
        String factory = descriptor.getFactory();
        if( null == factory ) 
        {
            final String error = 
              "Required property 'avalon.artifact.factory' not present in artifact: "
              + artifact + " under the active cache: [" + m_cache + "] using the "
              + "attribute sequence: " + attributes;
            throw new IllegalArgumentException( error );
        }

        //
        // Grab all of the dependents in one hit because this is 
        // the implementation so we can ignore api/spi spread.
        //

        Artifact[] dependencies = descriptor.getDependencies();

        int n = dependencies.length;
        URL[] urls = new URL[ n + 1];
        for( int i=0; i<n; i++ )
        {
            urls[i] = LoaderUtils.getResource( 
              dependencies[i], m_hosts, m_cache, true );
        }

        urls[ n ] = LoaderUtils.getResource( 
            artifact, m_hosts, m_cache, true );

        //
        // create the classloader
        //
        
        ClassLoader classloader = new URLClassLoader( urls, parent );
        Class clazz = loadFactoryClass( classloader, factory );

        //
        // load the actual repository implementation 
        //

        try
        {
            m_factory = createDelegate( classloader, clazz, this );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to establish a factory for the supplied artifact:";
            StringBuffer buffer = new StringBuffer( error );
            buffer.append( "\n artifact: " + artifact );
            buffer.append( "\n build: " + descriptor.getBuild() );
            buffer.append( "\n factory: " + descriptor.getFactory() );
            buffer.append( "\n source: " 
              + clazz.getProtectionDomain().getCodeSource().getLocation() );
            buffer.append( "\n cache: " + m_cache );
            throw new RepositoryException( buffer.toString(), e );
        }
    }
  
    // ------------------------------------------------------------------------
    // InitialContext
    // ------------------------------------------------------------------------

    /**
     * Return the application key.  The value of the key may be used 
     * to resolve property files by using the convention 
     * [key].properties.
     * 
     * @return the application key.
     */
    public String getApplicationKey()
    {
        return m_key;
    }

    /**
     * Return the base working directory.
     * 
     * @return the base directory
     */
    public File getInitialWorkingDirectory()
    {
        return m_base;
    }
    
    /**
     * Return cache root directory.
     * 
     * @return the cache directory
     */
    public File getInitialCacheDirectory()
    {
        return m_cache;
    }
    
    /**
     * Return the initial set of host names.
     * @return the host names sequence
     */
    public String[] getInitialHosts()
    {
        return m_hosts;
    }

   /**
    * Return the initial repository factory.
    * @return the initial repository factory
    */
    public Factory getInitialFactory()
    {
        return m_factory;
    }

   /**
    * Create a factory builder using a supplied artifact.
    * @param artifact the factory artifact
    * @return the factory builder
    * @exception Exception if a builder creation error occurs
    */
    public Builder newBuilder( Artifact artifact )
      throws Exception
    {
        return new DefaultBuilder( this, artifact );
    }

   /**
    * Create a factory builder using a supplied artifact.
    * @param classloader the parent classloader
    * @param artifact the factory artifact
    * @return the factory
    * @exception Exception if a factory creation error occurs
    */
    public Builder newBuilder( ClassLoader classloader, Artifact artifact )
      throws Exception
    {
        return new DefaultBuilder( this, classloader, artifact );
    }

   /**
    * Install a block archive into the repository cache.
    * @param url the block archive url
    * @return the block manifest
    */
    public Manifest install( URL url ) throws RepositoryException
    {
        String path = url.getFile();

        try
        {
            File temp = File.createTempFile( "avalon-", "-bar" );
            temp.delete();
            LoaderUtils.getResource( url.toString(), temp, true );
            temp.deleteOnExit();
            StringBuffer buffer = new StringBuffer();
            Manifest manifest = expand( temp.toURL(), buffer );

            //
            // need a logging solution
            //

            System.out.println( buffer.toString() );
            return manifest;
        }
        catch( RepositoryException e )
        {
            throw e;
        }
        catch( Throwable e )
        {
            final String error = 
              "Cannot install target: " + url;
            throw new RepositoryException( error, e );
        }
    }

    // ------------------------------------------------------------------------
    // implementation
    // ------------------------------------------------------------------------

   /**
    * Expand a block archive into the repository.
    * @param url the block archive url
    * @param buffer a string buffer against which messages may be logged
    * @return the block manifest
    */
    private Manifest expand( URL url, StringBuffer buffer ) throws RepositoryException
    {
        try
        {
            URL jurl = new URL( "jar:" + url.toString() + "!/" );
            JarURLConnection connection = (JarURLConnection) jurl.openConnection();
            Manifest manifest = connection.getManifest();

            final String group = getBlockGroup( manifest );

            buffer.append( "\nBlock Group: " + group );
            final File root = new File( m_cache, group );
            buffer.append( "\nLocal target: " + root );
            JarFile jar = connection.getJarFile();
            Enumeration entries = jar.entries();
            while( entries.hasMoreElements() )
            {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                if( !entry.getName().startsWith( "META-INF" ) )
                {
                    installEntry( buffer, root, jar, entry );
                }
            }
            buffer.append( "\nInstall successful." );
            return manifest;
        }
        catch( Throwable e )
        {
            final String error = 
              "Could not install block: " + url;
            throw new RepositoryException( error, e );
        }
    }

    private String getBlockGroup( Manifest manifest )
    {
        return (String) manifest.getMainAttributes().getValue( BLOCK_GROUP_KEY );
    }

   /**
    * Internal utility to install a entry from a jar file into the local repository.
    * @param buffer the buffer to log messages to
    * @param root the root directory corresponding to the bar group
    * @param jar the block archive
    * @param entry the entry from the archive to install
    */
    private void installEntry( 
      StringBuffer buffer, File root, JarFile jar, ZipEntry entry ) throws Exception
    {
        if( entry.isDirectory() ) return;
        
        final String name = entry.getName();
        File file = new File( root, name );

        long timestamp = entry.getTime();
        if( file.exists() )
        {
            if( file.lastModified() == timestamp )
            {
                buffer.append( "\nEntry: " + name + " (already exists)" );
                return;
            }
            else if( file.lastModified() > timestamp )
            {
                buffer.append( "\nEntry: " + name + " (local version is more recent)" );
                return;
            }
            else
            {
                buffer.append( "\nEntry: " + name + " (updating local version)" );
            }
        }
        else
        {
            buffer.append( "\nEntry: " + name );
        }

        InputStream is = jar.getInputStream( entry );
        if ( is == null )
        {
            final String error = 
              "Entry returned a null input stream: " + name;
            buffer.append( "\n  " + error );
            throw new IOException( error );
        }

        file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream( file );
        byte[] buf = new byte[100 * 1024];
        int length;
        while ( ( length = is.read( buf ) ) >= 0 )
        {
            fos.write( buf, 0, length );
        }
        fos.close();
        is.close();

        if ( timestamp < 0 )
        {
            file.setLastModified( System.currentTimeMillis() );
        }
        else
        {
            file.setLastModified( timestamp );
        }
    }

    private Attributes loadAttributes( File cache, String[] hosts, Artifact artifact )
      throws RepositoryException
    {
        try
        {
             return RepositoryUtils.getAttributes( cache, artifact );
        }
        catch( RepositoryException re )
        {
             return RepositoryUtils.getAttributes( hosts, artifact );
        }
    }

    private ClassLoader setupClassLoader( ClassLoader classloader )
    {
        if( null != classloader ) return classloader;
        return DefaultInitialContext.class.getClassLoader();
    }

    private File setupCache( File cache, File base )
    {
        if( null != cache ) return cache;
        return setupDefaultCache( base );
    }

    private String[] setupHosts( String[] hosts, File base )
    {
        if( null != hosts ) return RepositoryUtils.getCleanPaths( hosts );
        return setupDefaultHosts( base );
    }

    private Artifact setupImplementation( Artifact artifact )
    {
        if( null != artifact ) return artifact;
        return getDefaultImplementation( );
    }

   /**
    * Build the properties that declare the default repository
    * implementation that was assigned at build time.
    */
    private static Properties createDefaultProperties()
    {
        final String path = AVALON_IMPL_PROPERTIES;
        try
        {
            Properties properties = new Properties();
            ClassLoader classloader = DefaultInitialContext.class.getClassLoader();
            InputStream input = classloader.getResourceAsStream( path );
            if( input == null ) 
            {
                final String error = 
                  "Missing resource: [" + path + "]";
                throw new Error( error );
            }
            properties.load( input );
            return properties;
        }
        catch ( Throwable e )
        {
            final String error = 
              "Internal error. " 
              + "Unable to locate the standard repository implementation directive.";
            RepositoryException re = new RepositoryException( error, e );
            re.printStackTrace( System.err );
            return null;
        }
    }

    private static Artifact getDefaultImplementation()
    {
        Properties properties = createDefaultProperties();
        String spec = properties.getProperty( 
          InitialContext.IMPLEMENTATION_KEY );
        if( null == spec )
        {
            final String error =
              "Missing avalon.properties resource.";
            throw new IllegalStateException( error );
        }
        return Artifact.createArtifact( spec );
    }

    private File setupBaseDirectory( File base )
    {
        if( null != base ) return base;
        return getBaseDirectory();
    }

    private String[] setupDefaultHosts( File base )
    {
        String homeValue = getUserProperties().getProperty( HOSTS_KEY );
        String workValue = getWorkProperties( base ).getProperty( HOSTS_KEY, homeValue );
        String value = System.getProperty( HOSTS_KEY , workValue );
        if( null == value ) return DEFAULT_INITIAL_HOSTS;
        return expandHosts( value );
    }

    private static File setupDefaultCache( File base )
    {
        String homeValue = getUserProperties().getProperty( CACHE_KEY );
        String workValue = getWorkProperties( base ).getProperty( CACHE_KEY, homeValue );
        String value = System.getProperty( CACHE_KEY , workValue );
        if( null != value ) return new File( value  );
        return getDefaultCache();
    }

    private static File getDefaultCache()
    {
        return new File( getAvalonHome(), "repository" );
    }

    private static File getBaseDirectory()
    {
        String base = System.getProperty( "basedir" );
        if( null != base )
        {
            return new File( base );
        }
        return new File( System.getProperty( "user.dir" ) );
    }

    private static Properties getLocalProperties( 
      File dir, String filename ) 
    {
        Properties properties = new Properties();
        if( null == dir ) return properties;
        File file = new File( dir, filename );
        if( !file.exists() ) return properties;
        try
        {
            properties.load( new FileInputStream( file ) );
            return properties;
        }
        catch( Throwable e )
        {
            final String error = 
              "Unexpected exception while attempting to read properties from: " 
              + file;
            throw new RepositoryRuntimeException( error, e );
        }
    }

    private static String[] expandHosts( String arg )
    {
        ArrayList list = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer( arg, "," );
        while( tokenizer.hasMoreTokens() )
        {
            list.add( tokenizer.nextToken() );
        }
        return (String[]) list.toArray( new String[0] );
    }

    private static Properties getUserProperties()
    {
        return getLocalProperties( USER_HOME, AVALON_PROPERTIES );
    }

    private static Properties getWorkProperties( File base )
    {
        return getLocalProperties( base, AVALON_PROPERTIES );
    }
}
