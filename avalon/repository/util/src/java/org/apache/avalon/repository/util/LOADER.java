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

package org.apache.avalon.repository.util;


import java.io.File ;
import java.io.IOException ;
import java.io.InputStream ;
import java.io.FileOutputStream ;

import java.util.ArrayList ;
import java.util.Properties ;
import java.text.ParseException ;

import java.net.URL ;
import java.net.URLConnection ;
import java.net.URLClassLoader ;
import java.net.HttpURLConnection ;
import java.net.MalformedURLException ;

import javax.naming.NamingException ;
import javax.naming.NamingEnumeration ;
import javax.naming.directory.Attributes ;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.RepositoryException;


/**
 * Utility class supporting downloading of resources based on 
 * artifact references.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $
 */
public class LOADER
{
    /**
     * Attempts to download and cache a remote artifact trying a set of remote
     * repositories.  The operation is not fail fast and so it keeps trying if
     * the first repository does not have the artifact in question.
     * 
     * @param artifact the artifact to retrieve and cache
     * @param repositories the remote repositories to try to download from 
     * @param root the root cache directory
     * @param timestamping whether to check the modified timestamp on the
     *      <code>destinationFile</code> against the remote <code>source</code>
     * @return URL a url referencing the local resource
     */
    public static URL getResource( Artifact artifact, 
        String [] repositories, File root, boolean timestamping ) 
        throws RepositoryException
    {
        Exception cause = null;

        File destination = new File( root, artifact.getPath() );
        
        for ( int i = 0; i < repositories.length; i++ )
        {
            try
            {
                String url = artifact.getURL( repositories[i] ) ;
                return getResource( url, destination, timestamping ) ;
            }
            catch ( Exception e )
            {
                cause = e ;
            }
        }

        if( destination.exists() ) return getURL( destination );
        
        StringBuffer buffer = new StringBuffer();
        buffer.append(
          "Failed to download artifact to local cache file " 
          + destination.getAbsolutePath() 
          + " from hosts: " );
        for( int i=0; i<repositories.length; i++ )
        {
            buffer.append( "\n  " + repositories[i] );
        }
        throw new RepositoryException( buffer.toString(), cause );
    }

    /**
     * Attempts to download and cache a remote artifact trying a set of remote
     * repositories.  The operation is not fail fast and so it keeps trying if
     * the first repository does not have the artifact in question.
     * 
     * @param artifact the artifact to retrieve and cache
     * @param mime the mime type
     * @param repositories the remote repositories to try to download from 
     * @param root the root cache directory
     * @param timestamping whether to check the modified timestamp on the
     *      <code>destinationFile</code> against the remote <code>source</code>
     * @return URL a url referencing the local resource
     */
    public static URL getResource( Artifact artifact, String mime,
        String [] repositories, File root, boolean timestamping ) 
        throws RepositoryException
    {
        if( null == artifact ) 
          throw new NullPointerException( "artifact" );

        if( null == mime ) 
          throw new NullPointerException( "mime" );

        if( null == root ) 
          throw new NullPointerException( "root" );

        if( null == repositories ) 
          throw new NullPointerException( "repositories" );

        Exception cause = null;

        File destination = new File( root, artifact.getPath() + "." + mime );
        
        for ( int i = 0; i < repositories.length; i++ )
        {
            try
            {
                String url = artifact.getURL( repositories[i] ) + "." + mime;
                return getResource( url, destination, timestamping ) ;
            }
            catch ( Exception e )
            {
                cause = e ;
            }
        }

        if( destination.exists() ) return getURL( destination );
        
        StringBuffer buffer = new StringBuffer();
        buffer.append(
          "Failed to download mime artifact to local cache file " 
          + destination.getAbsolutePath() 
          + " from hosts: " );
        for( int i=0; i<repositories.length; i++ )
        {
            buffer.append( "\n  " + repositories[i] );
        }
        throw new RepositoryException( buffer.toString(), cause );
    }


    
    /**
     * Retrieve a remote file. 
     *
     * @param url the of the file to retrieve
     * @param destination where to store it
     * @param timestamping whether to check the modified timestamp on the
     *      <code>destinationFile</code> against the remote <code>source</code>
     * @return URL a url referencing the local resource
     */
    public static URL getResource( 
      String url, File destination, boolean timestamping ) 
      throws Exception
    {

        boolean update = destination.exists();
        long remoteTimestamp = 0; // remote

        //
        // if timestamp is enabled and the destination file exists and 
        // the source is a file - then do a quick check using native File
        // last modification dates to see if anything needs to be done
        // 

        if( timestamping && destination.exists() && url.startsWith( "file:" ) )
        {
            try
            {
                URL sourceFileUrl = new URL( url );
                String sourcePath = sourceFileUrl.getPath();
                File sourceFile = new File( sourcePath );
                if( destination.lastModified() >= sourceFile.lastModified()  )
                {
                    return destination.toURL();
                }
                else
                {
                    //
                    // set the remote tamestamp here because the pricision
                    // for a file last modification date is higher then the 
                    // connection last mosification date
                    //

                    remoteTimestamp = sourceFile.lastModified();
                }
            }
            catch( Throwable e )
            {
                e.printStackTrace();
            }
        }

        if( destination.exists() && !isSnapshot( destination ) )
        {
            return getURL( destination );
        }

        //
        // otherwise continue with classic processing
        //

        URL source = null ; 
        String username = null ;
        String password = null ;

        // We want to be able to deal with Basic Auth where the username
        // and password are part of the URL. An example of the URL string
        // we would like to be able to parse is like the following:
        //
        // http://username:password@repository.mycompany.com

        int atIdx = url.indexOf( "@" ) ;
        if ( atIdx > 0 )
        {
            String s = url.substring( 7, atIdx ) ;
            int colon = s.indexOf( ":" ) ;
            username = s.substring( 0, colon ) ;
            password = s.substring( colon + 1 ) ;
            source = new URL( "http://" + url.substring( atIdx + 1 ) ) ;
        }
        else
        {
            source = new URL( url ) ;
        }

        //set the timestamp to the file date.
        long timestamp = 0 ;
        boolean hasTimestamp = false ;
        if ( timestamping && destination.exists() )
        {
            timestamp = destination.lastModified() ;
            hasTimestamp = true ;
        }

        //set up the URL connection
        URLConnection connection = source.openConnection() ;

        //modify the headers
        //NB: things like user authentication could go in here too.

        if ( timestamping && hasTimestamp )
        {
            connection.setIfModifiedSince( timestamp ) ;
        }

        //connect to the remote site (may take some time)

        connection.connect() ;

        //next test for a 304 result (HTTP only)

        if ( connection instanceof HttpURLConnection )
        {
            HttpURLConnection httpConnection = 
              ( HttpURLConnection ) connection ;
            
            if ( httpConnection.getResponseCode() == 
              HttpURLConnection.HTTP_NOT_MODIFIED )
            {
                return destination.toURL() ;
            }
            
            // test for 401 result (HTTP only)
            if ( httpConnection.getResponseCode() == 
                    HttpURLConnection.HTTP_UNAUTHORIZED )
            {
                throw new Exception( "Not authorized." ) ;
            }
        }

        // REVISIT: at this point even non HTTP connections may support the
        // if-modified-since behaviour - we just check the date of the
        // content and skip the write if it is not newer.
        // Some protocols (FTP) dont include dates, of course.

        InputStream in = null ;
        for ( int ii = 0; ii < 3; ii++ )
        {
            try
            {
                in = connection.getInputStream() ;
                break ;
            }
            catch ( IOException ex )
            {
                // do nothing
            }
        }
        if ( in == null )
        {
            final String error = 
              "Connection returned a null input stream: " + url ;
            throw new IOException( error ) ;
        }

        File parent = destination.getParentFile() ;
        parent.mkdirs() ;

        FileOutputStream out = new FileOutputStream( destination ) ;

        byte[] buffer = new byte[100 * 1024] ;
        int length ;

        if( update )
        {
            System.out.print( "Update from: [" + source + "] ") ;
        }
        else
        {
            System.out.print( "Download from: [" + source + "] ") ;
        }
        while ( ( length = in.read( buffer ) ) >= 0 )
        {
            out.write( buffer, 0, length ) ;
            System.out.print( "." ) ;
        }

        System.out.println( "" ) ;
        out.close() ;
        in.close() ;

        // if (and only if) the use file time option is set, then the
        // saved file now has its timestamp set to that of the downloaded
        // file

        if ( timestamping )
        {
            if( remoteTimestamp == 0 )
            {
                remoteTimestamp = connection.getLastModified() ;
            }

            if( remoteTimestamp  < 0 )
            {
                destination.setLastModified( System.currentTimeMillis() ) ;
            }
            else
            {
                destination.setLastModified( remoteTimestamp ) ;
            }
        }
        return destination.toURL();
    }

    private static boolean isSnapshot( File file )
    {
        return file.getName().endsWith( "SNAPSHOT" );
    }

    private static URL getURL( File file ) throws RepositoryException
    {
        try
        {
            return file.toURL();
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to create a url from the file: " 
              + file;
            throw new RepositoryException( error, e );
        }
    }
}
