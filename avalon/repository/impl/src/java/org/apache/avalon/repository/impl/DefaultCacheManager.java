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
import org.apache.avalon.repository.provider.BlockManifest;
import org.apache.avalon.repository.provider.CacheManager;
import org.apache.avalon.repository.util.LOADER;
import org.apache.avalon.repository.util.RepositoryUtils;

/**
 * A component that provides access to versioned resources based on 
 * an underlying file system.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/12/07 03:15:16 $
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
             LOADER.getResource( url.toString(), temp, true );
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
