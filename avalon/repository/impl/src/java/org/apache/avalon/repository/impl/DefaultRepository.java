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
import org.apache.avalon.repository.provider.CacheManager;
import org.apache.avalon.repository.util.LOADER;
import org.apache.avalon.repository.util.RepositoryUtils;

/**
 * A component that provides access to versioned resources based on 
 * an underlying file system.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/12/04 19:34:33 $
 */
public class DefaultRepository implements Repository
{
    //------------------------------------------------------------------
    // static 
    //------------------------------------------------------------------

    private static final String[] DEFAULT_HOSTS = 
      new String[]{ "http://dpml.net", "http://ibiblio.org/maven" };

    //------------------------------------------------------------------
    // immutable state 
    //------------------------------------------------------------------

   /**
    * Sequence of remote hosts.
    */
    private final URL[] m_hosts;

   /**
    * Sequence of remote hosts.
    */
    private final String[] m_roots;

    private final CacheManager m_cache;
    
    //------------------------------------------------------------------
    // constructor 
    //------------------------------------------------------------------

   /**
    * Creation of a new instance of the default repository using the 
    * default hosts.
    *
    * @param cache the cache manager assigned to the repository
    * @exception NullPointerException if the cache or hosts argument
    * is null
    */
    public DefaultRepository( CacheManager cache )
    {
        this( cache, DEFAULT_HOSTS );
    }

   /**
    * Creation of a new instance of the default repository.
    * @param cache the cache manager assigned to the repository
    * @param hosts the set of remote hosts
    * @exception NullPointerException if the cache or hosts argument
    * is null
    */
    public DefaultRepository( CacheManager cache, String[] hosts )
    {
        if( cache == null ) throw new NullPointerException( "cache" );
        if( hosts == null ) throw new NullPointerException( "hosts" );

        m_cache = cache;
        m_roots = RepositoryUtils.getCleanPaths( hosts );
        m_hosts = getHosts( m_roots );
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
        return LOADER.getResource( 
          artifact, m_roots, m_cache.getCacheDirectory(), true );
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
        return LOADER.getResource( 
          artifact, mime, m_roots, m_cache.getCacheDirectory(), true );
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
        return m_cache.toString();
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
