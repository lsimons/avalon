/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.repository.main;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.lang.reflect.Constructor;
import java.lang.NoSuchMethodException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.text.ParseException;
import java.util.StringTokenizer;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLClassLoader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

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
 * @version $Revision: 1.13 $
 */
public class DefaultInitialContext extends AbstractBuilder implements InitialContext
{
    //------------------------------------------------------------------
    // public static 
    //------------------------------------------------------------------

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
    * the instantiated delegate repository factory
    */
    private final Factory m_delegate;

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
     * @param artifact an artifact referencing the default implementation
     * @param cache the cache directory
     * @param hosts a set of initial remote repository addresses 
     * @throws RepositoryException if an error occurs during establishment
     */
    public DefaultInitialContext( 
      File base, ClassLoader loader, Artifact artifact, File cache, String[] hosts ) 
      throws RepositoryException
    {
        m_base = setupBaseDirectory( base );
        Properties avalonSystem = 
          getLocalProperties( getAvalonHome(), AVALON_PROPERTIES );
        Properties avalonHome = 
          getLocalProperties( USER_HOME, AVALON_PROPERTIES );
        Properties avalonWork = 
          getLocalProperties( m_base, AVALON_PROPERTIES );
        m_cache = setupCache( cache, avalonSystem, avalonHome, avalonWork );
        m_hosts = setupHosts( hosts, avalonSystem, avalonHome, avalonWork );

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
            m_delegate = createDelegate( classloader, factory, this );
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
  
    // ------------------------------------------------------------------------
    // InitialContext
    // ------------------------------------------------------------------------

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
        return m_delegate;
    }

    // ------------------------------------------------------------------------
    // implementation
    // ------------------------------------------------------------------------

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

    private File setupCache( 
      File file, Properties system, Properties home, Properties work )
    {
        if( null != file ) return file;
        return setupDefaultCache( system, home, work );
    }

    private String[] setupHosts( 
      String[] hosts, Properties system, Properties home, Properties work )
    {
        if( null != hosts ) return RepositoryUtils.getCleanPaths( hosts );
        return setupDefaultHosts( system, home, work );
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
        String spec = properties.getProperty( "avalon.repository.implementation" );
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

    private String[] setupDefaultHosts(
      Properties system, Properties home, Properties work )
    {
        String systemValue = system.getProperty( HOSTS_KEY );
        String homeValue = home.getProperty( HOSTS_KEY, systemValue );
        String workValue = work.getProperty( HOSTS_KEY, homeValue );
        String value = System.getProperty( HOSTS_KEY , workValue );
        if( null == value ) return DEFAULT_INITIAL_HOSTS;
        return expandHosts( value );
    }

    private static File setupDefaultCache( 
      Properties system, Properties home, Properties work )
    {
        String systemValue = system.getProperty( CACHE_KEY );
        String homeValue = home.getProperty( CACHE_KEY, systemValue );
        String workValue = work.getProperty( CACHE_KEY, homeValue );
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

    private Properties getLocalProperties( 
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
}
