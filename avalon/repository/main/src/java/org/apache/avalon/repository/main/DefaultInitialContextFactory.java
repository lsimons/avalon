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
import org.apache.avalon.repository.provider.InitialContextFactory;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.util.LoaderUtils;
import org.apache.avalon.repository.util.RepositoryUtils;

import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.env.EnvAccessException;
import org.apache.avalon.util.exception.ExceptionHelper;
import org.apache.avalon.util.defaults.DefaultsBuilder;
import org.apache.avalon.util.defaults.Defaults;


/**
 * A utility class used to establish a new {@link InitialContext}
 * instance. An initial context is normally created by simply
 * instantiating the factory using a application key and a working
 * directory. 
 *
 * <pre>
 * final String key = "demo";
 * final File work = new File( System.getProperty( "user.dir" ) );
 * final InitialContextFactory factory = 
 *   new DefaultInitialContextFactory( key, work );
 * InitialContext context = factory.createInitialContext();
 * </pre>
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.3 $
 */
public class DefaultInitialContextFactory implements InitialContextFactory
{
    //------------------------------------------------------------------
    // private static 
    //------------------------------------------------------------------

   /**
    * The name of a properties resource contained within the repository 
    * bootstrap jar file.  Property values contained in this resource
    * consitute the most primative static default values.
    */
    private static final String AVALON_PROPERTIES = "avalon.properties";

    //------------------------------------------------------------------
    // immutable state 
    //------------------------------------------------------------------

    private final String m_key;

    private final File m_work;

    private final DefaultsBuilder m_defaults;

    private final Properties m_properties;

    //------------------------------------------------------------------
    // mutable state 
    //------------------------------------------------------------------

    private File m_cache;

    private Artifact m_artifact;

    private ClassLoader m_classloader;

    private String[] m_hosts;

    // ------------------------------------------------------------------------
    // constructor
    // ------------------------------------------------------------------------

    /**
     * <p>Creates an initial repository context factory.  The supplied 
     * key is used to establish the application root directory and 
     * property files at application, user and working directory 
     * levels.  A key such as 'merlin' will be transformed to the 
     * environment symbol 'MERLIN_HOME' (i.e. uppercase of key plus 
     * _HOME) and resolved to a value.  If the symbol is undefined, 
     * the application home directory defaults to a file path 
     * ${user.home}/.[key] (so for example, if MERLIN_HOME is 
     * undefined the default application home for Merlin is
     * ${user.home}/.merlin.  Based on the application root directory,
     * a set of property files with the name [key].properties are 
     * resolved from the following locations:</p>
     * 
     * <ul>
     *  <li>the current working directory</li>
     *  <li>user's home directory</li>
     *  <li>application home directory</li>
     * </ul>
     * 
     * <p>The order in which properties are evaluated in in accordance
     * the above list.  The current working directory properties take 
     * precedence over properties defined in the user's home directory
     * which in turn take precedence over properties defined under the 
     * application home directory.  System properties take precedence
     * over all properties.</p>
     *
     * @param key the application key
     * @param work the working directory
     * @throws IOException if an error occurs during establishment
     * @exception NullPointerException if tyhe supplied key or work 
     *    arguments are null
     */
    public DefaultInitialContextFactory( String key, File work ) 
      throws IOException
    {
        if( null == key ) throw new NullPointerException( "key" );
        if( null == work ) throw new NullPointerException( "work" );

        m_key = key;
        m_work = work;
        m_defaults = new DefaultsBuilder( key, work );
        Properties defaults = getDefaultProperties();
        m_properties = 
          m_defaults.getConsolidatedProperties(
            defaults, 
            KEYS );
        Defaults.macroExpand( 
            m_properties, 
            new Properties[]{ m_properties } );

        String spec = m_properties.getProperty( 
          InitialContext.IMPLEMENTATION_KEY );
        if( null != spec )
        {
            m_artifact = Artifact.createArtifact( spec );
        }
        else
        {
            final String error =
              "Required implementation key [" 
              + InitialContext.IMPLEMENTATION_KEY 
              + "] not found.";
            throw new IllegalStateException( error );
        }
    }

    // ------------------------------------------------------------------------
    // InitialContextFactory
    // ------------------------------------------------------------------------

   /**
    * Set the parent classloader.  If not defined, the default 
    * classloader is the classloader holding this class. 
    *
    * @param classloader the parent classloader
    */
    public void setParentClassLoader( ClassLoader classloader )
    {
        m_classloader = classloader;
    }

   /**
    * The initial context factory support the establishment of an 
    * initial context which is associated with a repository cache 
    * manager implementation.  A client can override the default
    * repository cache manager implementation by declaring an 
    * artifact referencing a compliant factory (not normally 
    * required).
    *
    * @param artifact the repository cache manager artifact
    */
    public void setImplementation( Artifact artifact )
    {
        m_artifact = artifact;
    }

   /**
    * The cache directory is the directory into which resources 
    * such as jar files are loaded by a repository cache manager.
    *
    * @param cache the repository cache directory
    */
    public void setCacheDirectory( File cache )
    {
        m_cache = cache;
    }

   /**
    * Set the initial hosts to be used by a repository cache manager 
    * implementation and the initial context implementation when 
    * resolving dependent resources.  If is resource is not present
    * in a local cache, remote hosts are checked in the order presented
    * in the supplied list. A host may be a file url or a http url.
    *
    * @param hosts a sequence of remote host urls
    */
    public void setHosts( String[] hosts )
    {
        m_hosts = hosts;
    }

   /**
    * Creation of an inital context based on the system and working 
    * directory, parent classloader, repository cache manager implementation
    * artifact, cache directory, and remote hosts sequence supplied to the 
    * factory.
    *
    * @return a new initial context
    */
    public InitialContext createInitialContext() 
    {
        try
        {
            return new DefaultInitialContext(
              getApplicationKey(),
              getParentClassLoader(),
              getImplementation(),
              getWorkingDirectory(),
              getCacheDirectory(),
              getHosts() );
        }
        catch( Throwable e )
        {
            final String error =
              "Could not create initial context.";
            throw new RepositoryRuntimeException( error, e );
        }
    }


   /**
    * Return the application key.
    */
    public String getApplicationKey()
    {
        return m_key;
    }

   /**
    * Return the home directory value direved from the application key.
    * @return the home directory.
    */
    public File getHomeDirectory()
    {
        return m_defaults.getHomeDirectory();
    }

   /**
    * Return the working directory value.
    * @return the working directory.
    */
    public File getWorkingDirectory()
    {
        return m_work;
    }

   /**
    * Return the parent classloader. The default classloader returned
    * from this operation is the classloader containing this class.
    * 
    * @return the parent classloader
    */
    public ClassLoader getParentClassLoader()
    {
        if( null != m_classloader ) return m_classloader;
        return DefaultInitialContext.class.getClassLoader();
    }

   /**
    * Return the implementation artifact.  If not overriden, a default
    * artifact referencing avalon-repository-impl will be returned.
    *
    * @return the implementation artifact
    */
    public Artifact getImplementation()
    {
        return m_artifact;
    }

   /** 
    * Return the assigned or default cache directory. If undefined
    * the cache directory shall default to ${avalon.home}/repository.
    *
    * @return the cache directory
    */
    public File getCacheDirectory()
    {
        if( null != m_cache ) return m_cache;
        String value = m_properties.getProperty( InitialContext.CACHE_KEY );
        if( null != value ) return new File( value );
        return new File( getHomeDirectory(), "repository" );
    }

   /**
    * Return the assigned or default host sequence.
    * @return the remote host url sequence
    */
    public String[] getHosts()
    {
        if( null != m_hosts ) return m_hosts;
        String value = m_properties.getProperty( InitialContext.HOSTS_KEY );
        if( null == value ) return new String[0];
        return expandHosts( value );
    }

    // ------------------------------------------------------------------------
    // implementation
    // ------------------------------------------------------------------------

    private Properties getDefaultProperties() throws IOException
    {
        Properties properties = new Properties();
        ClassLoader classloader = 
          DefaultInitialContextFactory.class.getClassLoader();
        InputStream input = 
          classloader.getResourceAsStream( AVALON_PROPERTIES );
        if( input == null ) 
        {
            final String error = 
              "Missing resource: [" + AVALON_PROPERTIES + "]";
            throw new Error( error );
        }
        properties.load( input );
        return properties;
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
