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

package org.apache.avalon.repository.util;


import java.io.File ;
import java.io.FileInputStream ;
import java.io.FileOutputStream ;
import java.io.InputStream ;
import java.io.IOException ;
import java.io.OutputStream ;

import java.util.ArrayList ;
import java.text.ParseException ;
import java.util.Properties ;

import java.net.HttpURLConnection ;
import java.net.MalformedURLException ;
import java.net.URL ;
import java.net.URLClassLoader ;
import java.net.URLConnection ;

import javax.naming.NamingEnumeration ;
import javax.naming.NamingException ;
import javax.naming.directory.Attributes ;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.RepositoryException;


/**
 * Utility class supporting downloading of resources based on 
 * artifact references.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.4 $
 */
public class LoaderUtils
{
     private boolean m_online;

     public LoaderUtils( boolean online )
     {
         m_online = online;
     }

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
    public URL getResource( Artifact artifact, 
        String [] repositories, File root, boolean timestamping ) 
        throws RepositoryException
    {
        Exception cause = null;

        File destination = new File( root, artifact.getPath() );

        if( !m_online )
        {
            if( destination.exists() )
            {
                return getURL( destination );
            }
            else
            {
                final String error = 
                  "Artifact [" + artifact + "] does not exist in local cache (repository offline).";
                throw new RepositoryException( error );
            }
        }

        //
        // continue with remote repository evaluation
        //
 
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
    public URL getResource( Artifact artifact, String mime,
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
        
        if( !m_online )
        {
            if( destination.exists() )
            {
                return getURL( destination );
            }
            else
            {
                final String error = 
                  "Artifact ["
                  + artifact.getPath() + "." + mime 
                  + "] does not exist in local cache (repository offline).";
                throw new RepositoryException( error );
            }
        }

        //
        // evaluate remote repositories
        //

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
    public URL getResource( 
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
                    // connection last modification date
                    //

                    remoteTimestamp = sourceFile.lastModified();
                }
            }
            catch( Throwable e )
            {
                e.printStackTrace();
            }
        }

        if( !m_online )
        {
             if( destination.exists() )
             {
                 return getURL( destination );
             }
             else
             {
                 final String error =
                   "Cannot retrieve url [" + url + "] while disconnected.";
                 throw new RepositoryException( error );
             }
        }
        else
        {
            if( destination.exists() && !isSnapshot( destination ) )
            {
                return getURL( destination );
            }
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

        File tempFile = File.createTempFile( "~avalon", ".tmp", parent );
        tempFile.deleteOnExit(); // safety harness in case we abort abnormally
                                 // like a Ctrl-C.
        
        FileOutputStream tempOut = new FileOutputStream( tempFile );
        String title;
        if( update )
        {
            title = "Update from: [" + source + "] ";
        }
        else
        {
            title = "Download from: [" + source + "] ";
        }
        copyStream( in, tempOut, true, title );

        // An atomic operation and no risk of a corrupted
        // artifact content.

        tempFile.renameTo( destination );
        
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
    
    private static void copyStream( 
      InputStream src, OutputStream dest, boolean closeStreams, 
      String title  )
      throws IOException
    {
        boolean progress = title != null;
        byte[] buffer = new byte[100 * 1024] ;
        int length ;
        if( title != null )
            System.out.println( title );        
        try
        {        
            while ( ( length = src.read( buffer ) ) >= 0 )
            {
                dest.write( buffer, 0, length ) ;
                if( progress )
                    System.out.print( "." ) ;
            }
        } 
        finally
        {
            if( closeStreams )
            {
                if( src != null )
                    src.close();
                if( dest != null )
                    dest.close();
            }
            if( progress )
                System.out.println( "" ) ;
        }
    }
}
