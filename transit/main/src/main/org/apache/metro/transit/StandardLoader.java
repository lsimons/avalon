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

package org.apache.metro.transit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLClassLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.Map;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;


/**
 * Utility class supporting downloading of resources based on 
 * artifact references.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Loader.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class StandardLoader implements Repository
{
    //---------------------------------------------------------------------
    // immutable state
    //---------------------------------------------------------------------

    private final InitialContext m_context;

    //---------------------------------------------------------------------
    // constructor
    //---------------------------------------------------------------------

   /**
    * Creation of a new loader using the supplied initial context.
    * @param context the initial context
    */
    public StandardLoader() throws IOException
    {
        this( new InitialContextFactory().createInitialContext() );
    }

   /**
    * Creation of a new loader using the supplied initial context.
    * @param context the initial context
    */
    public StandardLoader( InitialContext context ) throws IOException
    {
        m_context = context;
    }
    
    //---------------------------------------------------------------------
    // Repository
    //---------------------------------------------------------------------

    /**
     * Attempts to download and cache a remote artifact using a set of remote
     * repositories.  The operation is not fail fast and so it keeps trying if
     * the first repository does not have the artifact in question.
     * 
     * @param artifact the artifact to retrieve and cache
     * @return URL a url referencing the local resource
     */
    public URL getResource( Artifact artifact ) 
        throws IOException
    {        
        File destination = 
          new File( m_context.getCacheDirectory(), artifact.getPath() );

        if( !m_context.getOnlineMode() )
        {
            if( destination.exists() )
            {
                debug( "using local copy: " + artifact );
                return getURL( destination );
            }
            else
            {
                final String error = 
                  "Artifact [" + artifact 
                  + "] does not exist in local cache.";
                throw new RepositoryException( error );
            }
        }

        //
        // continue with remote repository evaluation
        //

        debug( "checking: " + artifact );

        String[] hosts = m_context.getHosts();
        for ( int i = 0; i < hosts.length; i++ )
        {
            try
            {
                String url = artifact.getURL( hosts[i] ) ;
                URL local = loadResource( url, destination ) ;
                if( null != local )
                {
                    debug( "updated from: " + url );
                    debug( "cached as: " + local );
                }
                return local;
            }
            catch ( Exception e )
            {
                debug( "skipping: " + hosts[i] );
                debug( "error: " + e.getClass().getName() );
                debug( "message: " + e.getMessage() );
                // ignore
            }
        }
        
        if( destination.exists() )
        {
             debug( "using local: " + destination );
             return getURL( destination );
        }
        else
        {
              debug( "unresolvable: " + artifact );
        }

        final String error =
          "Unresolvable artifact: [" 
          + artifact 
          + "].";
        throw new RepositoryException( error );
    }

   /**
    * Get a plugin class relative to a supplied artifact.
    * 
    * @param parent the parent classloader
    * @param artifact the plugin artifact
    * @return the plugin class
    */
    public Class getPluginClass( ClassLoader parent, Artifact artifact ) 
       throws IOException
    {
        if( null == artifact ) throw new NullPointerException( "artifact" );
        if( null == parent ) throw new NullPointerException( "parent" );

        Plugin descriptor = getPluginDescriptor( artifact );
        return getPluginClass( parent, descriptor );
    }

   /**
    * Creates a Factory from an artifact reference.
    * 
    * @param context the initial repository context
    * @param parent the parent classloader
    * @param artifact the reference to the application
    * @param args commandline arguments
    * @exception RepositoryException if a factory creation error occurs
    */
    public Object getPlugin( ClassLoader parent, Artifact artifact, Object[] args  )
      throws IOException
    {
        if( null == artifact ) throw new NullPointerException( "artifact" );
        if( null == parent ) throw new NullPointerException( "parent" );
        if( null == args  ) throw new NullPointerException( "args" );

        debug( "building: " + artifact );

        try
        {
            Plugin descriptor = getPluginDescriptor( artifact );
            m_context.getMonitor().debug( "building classload stack" );
            ClassLoader classloader = createClassLoader( parent, descriptor );
            String classname = descriptor.getFactoryClassname();
            Class clazz = loadPluginClass( classloader, classname );

            m_context.getMonitor().debug( 
              "plugin class ["
              + clazz.getName() 
              + "] established" );

            //try
            //{
                return createPlugin( classloader, descriptor, clazz, args );
            //}
            //catch( IOException e )
            //{
//System.out.println( "#### EXIT/0 " + e );
            //    throw e;
            //}
            //catch( Throwable e )
            //{
            //    final String error = 
            //      "Unable to establish a plugin:";
            //    StringBuffer buffer = new StringBuffer( error );
            //    buffer.append( "\n " + artifact );
            //    buffer.append( "\n build: " + descriptor.getBuild() );
            //    buffer.append( "\n source: " 
            //      + clazz.getProtectionDomain().getCodeSource().getLocation() );
//System.out.println( "#### EXIT/1 " + buffer.toString() );
            //    throw new RepositoryException( buffer.toString(), e );
            //}
        }
        catch( Exception ce )
        {
            final String error = 
              "Unable to create a plugin using [" 
              + artifact
              + "].";
            throw new CacheException( error, ce );
        }
    }

    //---------------------------------------------------------------------
    // implementation
    //---------------------------------------------------------------------

   /**
    * Get a resource url relative to the supplied artifact and mime type.
    * 
    * @param artifact the artifact describing the resource
    * @param mime the mime type suffix
    * @return the mime resource url
    */
    private URL getMimeResource( Artifact artifact, String mime ) throws IOException
    {
        if( null == artifact ) 
          throw new NullPointerException( "artifact" );
        if( null == mime ) 
          throw new NullPointerException( "mime" );

        debug( "checking: " + artifact + "." + mime );

        File destination = 
          new File( m_context.getCacheDirectory(), artifact.getPath() + "." + mime );
        
        if( !m_context.getOnlineMode() )
        {
            if( destination.exists() )
            {
                return getURL( destination );
            }
            else
            {
                final String error = 
                  "Mime object [" 
                  + mime 
                  + "for artifact ["
                  + artifact 
                  + "] does not exist in local cache.";
                throw new RepositoryException( error );
            }
        }

        //
        // evaluate remote repositories
        //

        String[] hosts = m_context.getHosts();
        for ( int i = 0; i < hosts.length; i++ )
        {
            try
            {
                String url = artifact.getURL( hosts[i] ) + "." + mime;
                return loadResource( url, destination ) ;
            }
            catch ( Exception e )
            {
                // ignore
            }
        }

        if( destination.exists() ) return getURL( destination );
        
        final String error =
          "Unresolvable mime resource ["
          + mime 
          + "] for [" 
          + artifact 
          + "].";
        throw new RepositoryException( error );
    }

   /**
    * Get a plugin class relative to a supplied descriptor.
    * 
    * @param parent the parent classloader
    * @param artifact the plugin artifact
    * @return the plugin class
    */
    private Class getPluginClass( ClassLoader parent, Plugin descriptor ) 
       throws IOException
    {
        if( null == descriptor ) throw new NullPointerException( "descriptor" );
        if( null == parent ) throw new NullPointerException( "parent" );

        try
        {
            ClassLoader classloader = createClassLoader( parent, descriptor );
            String classname = descriptor.getFactoryClassname();
            return loadPluginClass( classloader, classname );
        }
        catch( CacheException ce )
        {
            final String error = 
              "Unable to load the plugin class [" 
              + descriptor.getFactoryClassname()
              + "].";
            throw new RepositoryException( error, ce );
        }
    }

   /**
    * Creates a plugin descriptor from an artifact.
    * 
    * @param artifact the reference to the application
    * @return the plugin descriptor
    * @exception RepositoryException if a factory creation error occurs
    */
    private Plugin getPluginDescriptor( Artifact artifact )
      throws IOException
    {
        if( null == artifact ) throw new NullPointerException( "artifact" );

        try
        {
            Attributes attributes = getAttributes( artifact );
            return new Plugin( attributes );
        }
        catch( CacheException ce )
        {
            final String error = 
              "Unable to resolve a plugin descriptor for [" 
              + artifact
              + "].";
            throw new RepositoryException( error, ce );
        }
    }

   /**
    * Create a factory using a supplied class and command line arguments.
    * 
    * @param descriptor the plugin descriptor
    * @param clazz the the factory class
    * @param map plugin parameters
    * @param args the command line args
    * @return the pluggable factory
    * @exception RepositoryException if a factory creation error occurs
    */
    private Object createPlugin( 
      ClassLoader classloader, Plugin descriptor, Class clazz, Object[] args )
      throws IOException
    {
        if( null == clazz ) throw new NullPointerException( "clazz" );
        if( null == classloader ) throw new NullPointerException( "classloader" );
        if( null == descriptor ) throw new NullPointerException( "descriptor" );
        if( null == args ) throw new NullPointerException( "args" );

        Constructor[] constructors = clazz.getConstructors();
        if( constructors.length < 1 ) 
        {
            final String error = 
              "Factory class ["
              + clazz.getName() 
              + "] does not declare a public constructor.";
            throw new RepositoryException( error );
        }

        Constructor constructor = constructors[0];
        Class[] classes = constructor.getParameterTypes();
        Object[] arguments = new Object[ classes.length ];

        if( classes.length == 0 )
        {
            m_context.getMonitor().debug( "class contains a null constructor" );
        }
        else
        {
            m_context.getMonitor().debug( "class contains a multi-arg constructor" );
        }

        for( int i=0; i<classes.length; i++ )
        {
            Class c = classes[i];
            m_context.getMonitor().debug( "constructor parameter (" + i + "): " + c.getName() );
            for( int j=0; j<args.length; j++ )
            {
                Object object = args[j];
                if( c.isAssignableFrom( object.getClass() )  )
                {
                     m_context.getMonitor().debug( 
                       "assigning argument [" 
                       + c.getName() 
                       + "]");
                     arguments[i] = object;
                     break;
                }
            }
        }

        for( int i=0; i<arguments.length; i++ )
        {
            if( null == arguments[i] )
            {
                Class c = classes[i];
                if( InitialContext.class.isAssignableFrom( c ) )
                {
                    arguments[i] = m_context;
                    m_context.getMonitor().debug( "assigning inital context" );
                }
                else if( Plugin.class.isAssignableFrom( c ) )
                {
                    arguments[i] = descriptor;
                    m_context.getMonitor().debug( "assigning plugin descriptor" );
                }
                else if( ClassLoader.class.isAssignableFrom( c ) )
                {
                    arguments[i] = classloader;
                    m_context.getMonitor().debug( "assigning classloader" );
                }
                else if( Repository.class.isAssignableFrom( c ) )
                {
                    arguments[i] = this;
                    m_context.getMonitor().debug( "assigning repository service" );
                }
                else
                {
                    final String error = 
                      "Unable to resolve an argument for parameter ["
                      + (i+1)
                      + "] requesting the ["
                      + classes[i].getName()
                      + "] class.";
                    throw new RepositoryException( error );
                }
            }
        }

        m_context.getMonitor().debug( "instantiating plugin" );

        try
        {
            return constructor.newInstance( arguments );
        }
        catch( InvocationTargetException e )
        {
            final String error = 
              "Cannot create plugin [" 
              + clazz.getName() 
              + "] due to an invocation failure.";
            Throwable cause = e.getTargetException();
            throw new RepositoryException( error, cause );
        }
        catch( Throwable e )
        {
            final String error = 
              "Cannot create plugin [" 
              + clazz.getName() 
              + "] due to an instantiation failure.";
            throw new RepositoryException( error, e );
        }
    }

    /**
     * Returns a classloader based on supplied plugin metadata.
     * @param parent the parent classloader
     * @param artifact the implementation artifact
     * @return the classloader
     */
    private ClassLoader createClassLoader( ClassLoader base, Plugin descriptor )
        throws IOException
    {
        if( null == descriptor ) 
          throw new NullPointerException( "descriptor" );
        if( null == base ) 
          throw new NullPointerException( "base" );

        Artifact artifact = descriptor.getArtifact();

        Artifact[] apiArtifacts = descriptor.getDependencies( Plugin.API_KEY );
        if( apiArtifacts.length > 0 )
        {
            m_context.getMonitor().debug( 
              "api classloader size: " 
              + apiArtifacts.length );
        }
        URL[] apis = getURLs( apiArtifacts  );
        ClassLoader api = buildClassLoader( base, apis );

        Artifact[] spiArtifacts = descriptor.getDependencies( Plugin.SPI_KEY );
        if( spiArtifacts.length > 0 )
        {
            m_context.getMonitor().debug( 
              "spi classloader size: " 
              + spiArtifacts.length );
        }
        URL[] spis = getURLs( spiArtifacts );
        ClassLoader spi = buildClassLoader( api, spis );

        Artifact[] impArtifacts = descriptor.getDependencies( Plugin.IMP_KEY );
        if( impArtifacts.length > 0 )
        {
            m_context.getMonitor().debug( 
              "impl classloader size: " 
              + impArtifacts.length );
        }
        URL[] imps = getURLs( artifact, impArtifacts );

        ClassLoader classloader = buildClassLoader( spi, imps );
        m_context.getMonitor().debug( "classloader created" );
        return classloader;
    }

    private URL[] getURLs( Artifact[] artifacts ) 
      throws IOException
    {
        URL[] urls = new URL[ artifacts.length ];
        for( int i=0; i<urls.length; i++ )
        {
            urls[i] = getResource( artifacts[i] );
        }
        return urls;
    }

    private URL[] getURLs( Artifact primary, Artifact[] artifacts ) 
      throws IOException
    {
        URL[] urls = new URL[ artifacts.length +1 ];
        for( int i=0; i<artifacts.length; i++ )
        {
            urls[i] = getResource( artifacts[i] );
        }
        urls[ artifacts.length ] = getResource( primary );
        return urls;
    }

    private ClassLoader buildClassLoader( ClassLoader parent, URL[] urls  )
    {
        if( 0 == urls.length ) return parent;
        return new URLClassLoader( urls, parent );
    }

   /**
    * Load a factory class using a supplied classloader and factory classname.
    * @param classloader the classloader to load the class from
    * @param factory the factory classname
    * @return the factory class
    * @exception CacheException if a factory class loading error occurs
    */
    protected Class loadPluginClass( ClassLoader classloader, String factory )
        throws IOException
    {
        try
        {
            return classloader.loadClass( factory );
        }
        catch( ClassNotFoundException e )
        {
            final String error = 
              "Could not load factory class[ " + factory + "].";
            throw new CacheException( error, e );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unexpected error while attempting to load factory class: [" 
              + factory 
              + "].";
            throw new CacheException( error, e );
        }
    }

    /**
     * Return the metadata attribututes associated with an artifact.
     * @param artifact the relative artifact from which a .meta resource will 
     *   be resolved to establish the artifact attributes
     * @return the attributes associated with the artifact
     * @exception CacheException if an error occurs while retrieving 
     *   or building the attributes
     * @exception NullPointerException if the supplied artifact is null
     */
    private Attributes getAttributes( Artifact artifact )
        throws IOException, NullPointerException
    {
        if( null == artifact )
          throw new NullPointerException( "artifact" );

        URL url = getMimeResource( artifact, "meta" );
        try
        {
            return CacheUtils.getAsAttributes( 
              CacheUtils.getProperties( url ) );
        }
        catch( IOException e )
        {
            throw e;
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to retrieve the metadata for the artifact ["
              + artifact + "].";
            throw new CacheException( error, e );
        }
    }

    /**
     * Retrieve a remote resource. 
     *
     * @param url the of the file to retrieve
     * @param destination where to store it
     * @return URL a url referencing the local resource
     */
    private URL loadResource( String url, File destination ) 
      throws IOException
    {
        boolean online = m_context.getOnlineMode();
        boolean update = destination.exists();
        Policy policy = m_context.getTimestampPolicy();
        boolean timestamping = ( policy != Policy.OVERWRITE );
        long remoteTimestamp = 0; // remote

        //
        // if we are not online so go with whatever is in the cache
        //

        if( !online )
        {
             if( update )
             {
                 return getURL( destination );
             }
             else
             {
                 final String error =
                   "Cannot retrieve [" + url + "] in off-line mode.";
                 throw new CacheException( error );
             }
        }
        else
        {
            //
            // check if the destination file needs to be downloded
            //

            if( !policy.isaCandidate( destination ) )
            {
                return getURL( destination );
            }
        }

        //
        // if timestamp is enabled and the destination file exists and 
        // the source is a file - then do a quick check using native File
        // last modification dates to see if anything needs to be done
        //

        if( timestamping && url.startsWith( "file:" )  )
        {
            try
            {
                URL sourceFileUrl = new URL( url );
                String sourcePath = sourceFileUrl.getPath();
                File sourceFile = new File( sourcePath );

                if( destination.exists() )
                {
                    if( destination.lastModified() >= sourceFile.lastModified() )
                    {
                        debug( "cached version up-to-date" );
                        return destination.toURL();
                    }
                }

                //
                // set the remote tamestamp here because the pricision
                // for a file last modification date is higher then the 
                // connection last modification date
                //

                remoteTimestamp = sourceFile.lastModified();

            }
            catch( Throwable e )
            {
                final String error = 
                  "Unexpected error while handling resource request.";
                throw new CacheRuntimeException( error, e );
            }
        }

        //
        // otherwise continue with classic processing - either its a case
        // of downloading a new resource or updating a snapshot
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
        if( destination.exists() )
        {
            timestamp = destination.lastModified() ;
            hasTimestamp = true ;
        }

        //set up the URL connection
        URLConnection connection = source.openConnection() ;

        //modify the headers
        //NB: things like user authentication could go in here too.

        if( timestamping && hasTimestamp )
        {
            connection.setIfModifiedSince( timestamp ) ;
        }

        //connect to the remote site (may take some time)

        connection.connect() ;

        //next test for a 304 result (HTTP only)

        if( connection instanceof HttpURLConnection )
        {
            HttpURLConnection httpConnection = 
              ( HttpURLConnection ) connection ;
            
            if( timestamping )
            {
                if ( httpConnection.getResponseCode() == 
                  HttpURLConnection.HTTP_NOT_MODIFIED )
                {
                    return destination.toURL() ;
                }
            }

            // test for 401 result (HTTP only)
            if ( httpConnection.getResponseCode() == 
                HttpURLConnection.HTTP_UNAUTHORIZED )
            {
                throw new IOException( "Not authorized." ) ;
            }
        }

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
            final String error = url.toString();
            throw new FileNotFoundException( error ) ;
        }

        File parent = destination.getParentFile() ;
        parent.mkdirs();

        File tempFile = File.createTempFile( "~metro", ".tmp", parent );
        tempFile.deleteOnExit(); // safety harness in case we abort abnormally
                                 // like a Ctrl-C.
        
        FileOutputStream tempOut = new FileOutputStream( tempFile );
        String title;
        if( update )
        {
            title = "updating: " + source + " ";
        }
        else
        {
            title = "downloading: " + source + " ";
        }

        int total = connection.getContentLength();
        DownloadMonitor monitor = 
          m_context.getMonitor().createDownloadMonitor( title, total );
        copyStream( monitor, in, tempOut, true );

        // An atomic operation and no risk of a corrupted
        // artifact content.

        tempFile.renameTo( destination );
        
        // if (and only if) the use file time option is set, then the
        // saved file now has its timestamp set to that of the downloaded
        // file

        if( timestamping )
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
        String name = file.getName();
        int i = name.lastIndexOf( "." );
        if( i > -1 )
        {
            String sub = name.substring( 0, i );
            return sub.endsWith( "SNAPSHOT" );
        }
        return file.getName().endsWith( "SNAPSHOT" );
    }

    private static URL getURL( File file ) throws IOException
    {
        return file.toURL();
    }
    
    private static void copyStream( 
      DownloadMonitor monitor, InputStream src, OutputStream dest, boolean closeStreams )
      throws IOException
    {
        int length ;
        int count = 0; // cumulative total read
        byte[] buffer = new byte[100 * 1024];
        try
        {
            while ( ( length = src.read( buffer ) ) >= 0 )
            {
                count = count + length;
                dest.write( buffer, 0, length ) ;
                if( null != monitor )
                {
                    monitor.notifyUpdate( count );
                }
            }
        }
        finally
        {
            if( closeStreams )
            {
                if( null != src ) try
                {
                    src.close();
                }
                catch( Throwable e )
                {
                    // ignore
                }
                if( null != dest ) try
                {
                    dest.close();
                }
                catch( Throwable e )
                {
                    // ignore
                }
            }
            if( null != monitor )
            {
                monitor.notifyCompletion();
            }
        }
    }

    private void debug( String message )
    {
        m_context.getMonitor().debug( message );
    }
}
