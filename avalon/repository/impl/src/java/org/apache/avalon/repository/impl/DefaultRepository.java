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

package org.apache.avalon.repository.impl;
 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.Authenticator;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

import javax.naming.directory.Attributes;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.RepositoryRuntimeException;
import org.apache.avalon.repository.meta.FactoryDescriptor;
import org.apache.avalon.repository.meta.MetaException;
import org.apache.avalon.repository.util.LoaderUtils;
import org.apache.avalon.repository.util.RepositoryUtils;

/**
 * A component that provides access to versioned resources based on 
 * an underlying file system.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.8 $ $Date: 2004/02/23 01:29:05 $
 */
public class DefaultRepository implements Repository
{
    //------------------------------------------------------------------
    // immutable state 
    //------------------------------------------------------------------

   /**
    * The cache directory.
    */
    private final File m_cache;

    private final LoaderUtils m_loader;

    //------------------------------------------------------------------
    // mutable state 
    //------------------------------------------------------------------

   /**
    * Sequence of remote hosts.
    */
    private URL[] m_hosts;

   /**
    * Sequence of remote hosts.
    */
    private String[] m_roots;

   /**
    * Sequence of remote hosts.
    */
    private boolean m_online;
    
    //------------------------------------------------------------------
    // constructor 
    //------------------------------------------------------------------

   /**
    * Creation of a new instance of the default repository.
    * @param cache the cache manager assigned to the repository
    * @param hosts the set of remote hosts
    * @exception NullPointerException if the cache or hosts argument
    * is null
    */
    DefaultRepository( File cache, String[] hosts, boolean online )
    {
        if( cache == null ) throw new NullPointerException( "cache" );
        if( hosts == null ) throw new NullPointerException( "hosts" );

        m_cache = cache;
        m_roots = RepositoryUtils.getCleanPaths( hosts );
        m_hosts = getHosts( m_roots );
        m_online = online;
        m_loader = new LoaderUtils( online );
    }

    //------------------------------------------------------------------
    // Repository 
    //------------------------------------------------------------------
    
    /**
     * Return the metadata attribututes associated with an artifact.
     * @param artifact the relative artifact from which a .meta resource will 
     *   be resolved to establish the artifact attributes
     * @return the attributes associated with the artifact
     * @exception RepositoryException if an error occurs while retrieving 
     *   or building the attributes
     * @exception NullPointerException if the supplied artifact is null
     */
    public Attributes getAttributes( Artifact artifact )
        throws RepositoryException
    {
        if( null == artifact )
          throw new NullPointerException( "artifact" );

        try
        {
            return RepositoryUtils.getAsAttributes( 
              RepositoryUtils.getProperties( 
                getResource( artifact, "meta" ) ) );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to retrieve the metadata for the artifact :"
              + artifact;
            throw new RepositoryException( error, e );
        }
    }

    /**
     * Get a resource url relative to the supplied artifact.
     * 
     * @param artifact the artifact describing the resource
     * @return the resource url
     */
    public URL getResource( Artifact artifact )
        throws RepositoryException
    {
        return m_loader.getResource( 
          artifact, m_roots, m_cache, true );
    }

    /**
     * Get a resource url relative to the supplied artifact.
     * 
     * @param artifact the artifact describing the resource
     * @param mime a mime type relative to the artifact address
     * @return the mime instance url
     */
    private URL getResource( Artifact artifact, String mime )
        throws RepositoryException
    {
        return m_loader.getResource( 
          artifact, mime, m_roots, m_cache, true );
    }
        
    /**
     * Returns a classloader based on the metadata associated with 
     * a supplied artifact.  The classloader is created relative to 
     * the system classloader.
     * 
     * @param artifact the artifact fro which dependency metadata 
     *   will be resolved
     * @return the classloader
     */
    public ClassLoader getClassLoader( Artifact artifact )
        throws RepositoryException
    {
        return getClassLoader( 
          ClassLoader.getSystemClassLoader(), artifact ); 
    }

    /**
     * Returns a classloader based on the metadata associated with 
     * a supplied artifact.  The classloader is created relative to 
     * the supplied parent classloader.
     * 
     * @param parent the parent classloader
     * @param artifact the implementation artifact
     * @return the classloader
     */
    public ClassLoader getClassLoader( ClassLoader parent, Artifact artifact )
        throws RepositoryException
    {
        if( null == parent ) 
          throw new NullPointerException( "parent" );
        if( null == artifact ) 
          throw new NullPointerException( "artifact" );

        Attributes attributes = getAttributes( artifact );
        FactoryDescriptor relational = null;
        try
        {
            relational = new FactoryDescriptor( attributes );
        }
        catch( MetaException me )
        {
            final String error = 
              "Could not create a relational descriptor from the artifact: " 
              + artifact; 
            throw new RepositoryException( error, me ); 
        }
       
        URL[] apis = 
          getURLs( 
            relational.getDependencies( 
              FactoryDescriptor.API_KEY ) );
        ClassLoader api = buildClassLoader( apis, parent );

        URL[] spis = 
          getURLs( 
            relational.getDependencies( 
              FactoryDescriptor.SPI_KEY ) );
        ClassLoader spi = buildClassLoader( spis, api );

        URL[] imps = 
          getURLs( artifact, 
            relational.getDependencies( 
              FactoryDescriptor.IMP_KEY ) );
        return buildClassLoader( imps, spi );
    }

   /**
    * Return a string representation of this repository.
    * @return the string representation
    */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer( m_cache.toString() );
        for( int i=0; i<m_hosts.length; i++ )
        {
            buffer.append( ", " );
            buffer.append( m_hosts[i] );
        }
        return buffer.toString();
    }

    //------------------------------------------------------------------
    // implementation 
    //------------------------------------------------------------------

    private URL[] getHosts( String[] paths )
    {
        URL[] hosts = new URL[ paths.length ];
        for( int i=0; i<paths.length; i++ )
        {
            String path = paths[i];
            try
            {
                hosts[i] = new URL( path );
            }
            catch( Throwable e )
            {
                final String error = 
                  "Internal error while attempting to construct url for host: " 
                  + path;
                throw new RepositoryRuntimeException( error, e );
            }
        }
        return hosts;
    }

    private ClassLoader buildClassLoader( 
      URL[] urls, ClassLoader parent )
    {
        if( 0 == urls.length ) return parent;
        return new URLClassLoader( urls, parent );
    }

    private URL[] getURLs( Artifact[] artifacts ) 
      throws RepositoryException
    {
        URL[] urls = new URL[ artifacts.length ];
        for( int i=0; i<urls.length; i++ )
        {
            urls[i] = getResource( artifacts[i] );
        }
        return urls;
    }

    private URL[] getURLs( Artifact primary, Artifact[] artifacts ) 
      throws RepositoryException
    {
        URL[] urls = new URL[ artifacts.length +1 ];
        for( int i=0; i<artifacts.length; i++ )
        {
            urls[i] = getResource( artifacts[i] );
        }
        urls[ artifacts.length ] = getResource( primary );
        return urls;
    }
}
