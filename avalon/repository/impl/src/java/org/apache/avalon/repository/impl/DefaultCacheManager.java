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
import org.apache.avalon.repository.provider.BlockManifest;
import org.apache.avalon.repository.provider.CacheManager;
import org.apache.avalon.repository.util.LoaderUtils;
import org.apache.avalon.repository.util.RepositoryUtils;

/**
 * A component that provides access to versioned resources based on 
 * an underlying file system.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2004/01/24 23:20:04 $
 */
public class DefaultCacheManager implements CacheManager
{
    //------------------------------------------------------------------
    // immutable state 
    //------------------------------------------------------------------

   /**
    * The directory referencing the local repository cache.
    */
    private final File m_base;
    
   /**
    * The default hosts.
    */
    private final String[] m_hosts;
    
    //------------------------------------------------------------------
    // constructor 
    //------------------------------------------------------------------

   /**
    * Creation of a new cache manager.
    *
    * @param base the base directory for the repository cache
    * @param context the proxy context
    * @param hosts the default hosts
    */
    public DefaultCacheManager( File base, ProxyContext context, String[] hosts )
    {
        if( null == base ) 
         throw new NullPointerException( "base" );

        m_base = base;
        m_hosts = hosts;

        if( context != null )
        {
            System.getProperties().put( "proxySet", "true" );
            System.getProperties().put( "proxyHost", context.getHost() );
            System.getProperties().put( "proxyPort", context.getPort() );
            if( context.getAuthenticator() != null )
            {
                Authenticator.setDefault( context.getAuthenticator() );
            }
        }
    }

    //------------------------------------------------------------------
    // implementation 
    //------------------------------------------------------------------

    /**
     * Return cache root directory.
     * 
     * @return the cache directory
     */
    public File getCacheDirectory()
    {
        return m_base;
    }

    /**
     * Return the default hosts.
     * 
     * @return the host names
     */
    public String[] getDefaultHosts()
    {
        return m_hosts;
    }

   /**
    * Creation of a new repository handler using teh default hosts.
    * @return the repository
    */
    public Repository createRepository()
    {
        return createRepository( m_hosts );
    }

   /**
    * Creation of a new repository handler.
    * @param hosts the set of hosts to assign to the repository
    * @return the repository
    */
    public Repository createRepository( String[] hosts )
    {
        return new DefaultRepository( this, hosts );
    }

    /**
     * Install a block archive into the repository.
     * @param url the block archive url
     * @param buffer a string buffer against which messages may be logged
     * @return the block manifest
     */
     public BlockManifest install( URL url, StringBuffer buffer ) 
       throws RepositoryException
     {
         String path = url.getFile();

         try
         {
             File temp = File.createTempFile( "avalon-", "-bar" );
             temp.delete();
             LoaderUtils.getResource( url.toString(), temp, true );
             temp.deleteOnExit();
             return expand( temp.toURL(), buffer );
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

   /**
    * Return the repository address.
    * 
    * @return the address
    */
    public String toString()
    {
        return "cache:" + m_base.toString();
    }


    /**
     * Expand a block archive into the repository.
     * @param url the block archive url
     * @param buffer a string buffer against which messages may be logged
     * @return the block manifest
     */
     private BlockManifest expand( URL url, StringBuffer buffer ) 
       throws RepositoryException
     {
         try
         {
             URL jurl = new URL( "jar:" + url.toString() + "!/" );
             JarURLConnection connection = 
               (JarURLConnection) jurl.openConnection();
             BlockManifest manifest = 
               new DefaultBlockManifest( connection.getManifest() );
             final String group = manifest.getBlockGroup();

             buffer.append( "\nBlock Group: " + group );
             final File root = new File( m_base, group );
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


    /**
     * Internal utility to install a entry from a jar file into the local repository.
     * @param buffer the buffer to log messages to
     * @param root the root directory corresponding to the bar group
     * @param jar the block archive
     * @param entry the entry from the archive to install
     */
     private void installEntry( 
       StringBuffer buffer, File root, JarFile jar, ZipEntry entry )
       throws Exception
     {
         if( entry.isDirectory() ) return;
        
         final String name = entry.getName();
         File file = new File( root, name );

         long timestamp = entry.getTime();
         if( file.exists() )
         {
             if( file.lastModified() == timestamp )
             {
                 buffer.append( 
                   "\nEntry: " + name 
                   + " (already exists)" );
                 return;
             }
             else if( file.lastModified() > timestamp )
             {
                 buffer.append( 
                   "\nEntry: " + name 
                   + " (local version is more recent)" );
                 return;
             }
             else
             {
                 buffer.append( 
                   "\nEntry: " + name 
                   + " (updating local version)" );
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
}
