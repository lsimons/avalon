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

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.provider.CacheManager;
import org.apache.avalon.repository.impl.DefaultRepository;
import org.apache.avalon.repository.impl.DefaultCacheManager;

/**
 * Merlin commandline bootstrap handler.
 */
public class Merlin
{
    //--------------------------------------------------------------------------
    // static
    //--------------------------------------------------------------------------

    private static final String REPOSITORY_PATH = 
      "http://dpml.net/,http://www.ibiblio.org/maven/";

    private static final String MERLIN_HOME_KEY = 
      "merlin.home";

    private static final String MERLIN_SYSTEM_REPOSITORY_KEY = 
      "merlin.system.repository";

    private static final String MERLIN_SYSTEM_REMOTE_REPOSITORY_PATH_KEY = 
      "merlin.system.remote-repository.path";

    private static final String MERLIN_BOOTSTRAP_CLASSNAME_KEY = 
      "merlin.bootstrap.class";
    private static final String MERLIN_API_CLASSPATH_KEY = 
      "merlin.api.classpath";
    private static final String MERLIN_SPI_CLASSPATH_KEY = 
      "merlin.spi.classpath";
    private static final String MERLIN_IMPL_CLASSPATH_KEY = 
      "merlin.impl.classpath";
    private static final String MERLIN_PROPERTIES_NAME = 
      "merlin.properties";

    private static final File HOME = 
      new File( System.getProperty( "user.dir" ) );

   /**
    * Command line entry point to the Merlin system.
    * The main method handles the establishment of a bootstrap repository
    * from which a classload is built that contains the classes necessary
    * to bootstrap a kernel into existance.  The implementation passes
    * all command line parameters to the CLIKernelLoader class for 
    * processing and kernel establishment.
    *
    * @param args the command-line arguments
    * @exception Exception is an error occurs
    */
    public static void main( String[] args ) throws Exception
    {
        boolean debug = isDebugEnabled( args );

        //
        // the base directory is the directory containing the 
        // the merlin jar repository
        //

        String fallback =  System.getProperty( "user.home" ) + "/.merlin";
        String home =  System.getProperty( MERLIN_HOME_KEY, fallback );
        File merlinHome = new File( home );
        File merlinDefaultSystemRepository = new File( merlinHome, "system" );
        String merlinSystemRepositoryPath = 
          System.getProperty( 
            MERLIN_SYSTEM_REPOSITORY_KEY, 
            merlinDefaultSystemRepository.toString() );

        File base = new File( merlinSystemRepositoryPath );

        if( !base.exists() )
        {
            final String info = 
              "INFO-NEW-REPOSITORY, creating a new repository at: " + base;
            System.out.println( info );
            base.mkdirs();
        }

        if( debug )
        {
            System.out.println( "System Repository: " + base );
        }

        //
        // Establish the repository.  If a system property corresponding to 
        // MERLIN_REPOSITORY_REMOTE_KEY is not null then setup the repository
        // so that it dynamcally downloads content.  If the value is null then
        // default values.
        //

        Repository repository = null;
        String remotePath = 
          System.getProperty( MERLIN_SYSTEM_REMOTE_REPOSITORY_PATH_KEY, REPOSITORY_PATH );
        if( debug )
        {
            System.out.println( "system remote repositories: " + remotePath );
        }

        String[] remote = getRemoteURLs( remotePath );
        CacheManager cache = new DefaultCacheManager( base, null );
        repository = new DefaultRepository( cache, remote );

        //
        // get the set of URLs for the bootstrap classloader from the 
        // merlin.properties file bundled with the bootstrap jar file
        //

        ClassLoader classloader = Merlin.class.getClassLoader();
        InputStream input = 
          classloader.getResourceAsStream( MERLIN_PROPERTIES_NAME  );
        Properties properties = new Properties();
        properties.load( input );

        URL[] api = null;
        try
        {
            api = getURLs( repository, properties, MERLIN_API_CLASSPATH_KEY );
        }
        catch( Throwable e )
        {
            final String error =
              "\nInternal error while attempting to build api classloader.";
            String msg = 
              BootstrapHelper.packException( error, e, true );
            System.err.println( msg );
            return;
        }

        URL[] spi = null;
        try
        {
            spi = getURLs( repository, properties, MERLIN_SPI_CLASSPATH_KEY );
        }
        catch( Throwable e )
        {
            final String error =
              "\nInternal error while attempting to build api classloader.";
            String msg = 
              BootstrapHelper.packException( error, e, true );
            System.err.println( msg );
            return;
        }

        URL[] impl = null;
        try
        {
            impl = getURLs( repository, properties, MERLIN_IMPL_CLASSPATH_KEY );
        }
        catch( Throwable e )
        {
            final String error =
              "\nInternal error while attempting to build implementation classloader.";
            String msg = 
              BootstrapHelper.packException( error, e, true );
            System.err.println( msg );
            return;
        }

        //
        // create the container bootstrap classloader using these
        // URLs
        //

        ClassLoader apiLoader = new URLClassLoader( api );
        ClassLoader spiLoader = new URLClassLoader( spi, apiLoader );
        URLClassLoader loader = new URLClassLoader( impl, spiLoader );
        Thread.currentThread().setContextClassLoader( loader );

        if( debug )
        {
            System.out.println( "Classloader dump:\n" );
            printClassLoader( loader );
            System.out.println( "\n" );
        }

        //
        // get the bootstrap kernel loader class
        //

        Class clazz;
        String classname = 
          properties.getProperty( MERLIN_BOOTSTRAP_CLASSNAME_KEY );

        try
        {
            clazz = loader.loadClass( classname );
        }
        catch( Throwable e )
        {
            final String error =
              "\nInternal error during loader class creation.";
            String msg = 
              BootstrapHelper.packException( error, e, true );
            System.err.println( msg );
            return;
        }

        //
        // instantiate the kernel loader
        //

        try
        {
            Constructor constructor = 
              clazz.getConstructor( 
                new Class[]{ Repository.class, args.getClass() } );
            Object kernelLoader = 
              constructor.newInstance( 
                new Object[]{ repository, args } );
        }
        catch( InvocationTargetException e )
        {
            Throwable target = e.getTargetException();
            boolean cliError = 
              target.getClass().getName().startsWith( "org.apache.commons.cli" );
            if( cliError )
            {
                System.err.println( "Commandline error: " + target.getMessage() );
            }
            else
            {
                final String error =
                  "\nInternal error during kernel instantiation.";
                String msg = 
                  BootstrapHelper.packException( 
                    error, target, true );
                System.err.println( msg );
            }
            return;
        }
        catch( Throwable e )
        {
            final String error =
              "\nInternal error during kernel instantiation.";
            String msg = 
              BootstrapHelper.packException( error, e, true );
            System.err.println( msg );
            return;
        }
    }

    private static String[] getRemoteURLs( String path ) throws Exception
    {
        ArrayList list = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer( path, "," );
        while( tokenizer.hasMoreElements() )
        {
            String token = tokenizer.nextToken();
            list.add( token );
        }
        return (String[]) list.toArray( new String[0] );
    }

   /**
    * Consruct an array of URLs based on the declarations provided
    * in the supplied properties object.  Each URL is specified 
    * as a property value is mapped to a numbered property key in 
    * the form [key].n.  Each value is expressed as a repository
    * entry in the form [group]:[artifact];[version]. 
    * 
    * @param repository the repository from which artifacts shall be
    *   cached
    * @param properties the properties holding the keyed artifact ids
    * @param key the property name key
    * @return the array of urls
    */ 
    private static URL[] getURLs( 
       Repository repository, Properties properties, String key )
    {
        int i = 0;
        ArrayList list = new ArrayList();
        String label = getProperty( properties, key, i );
        while( label != null )
        {
            i++;
            list.add( getURL( repository, label ) );
            label = getProperty( properties, key, i );
        }
        return (URL[]) list.toArray( new URL[0] );
    }

   /**
    * Return a property key by concatonation of the supplied
    * key, the period character and an integer.
    * @param prioperties the properties set containing the keyed entry
    * @param key the partial key
    * @param i the key index
    * @return the value of the property [key].[i]
    */
    private static String getProperty( Properties properties, String key, int i )
    {
         final String label = key + "." + i;
         return properties.getProperty( label );
    }

   /**
    * Return a URL a a local repository cached resource replative to 
    * a suplied property value in the form [group]:[artifact];[version].
    * @param repository the repository under whcih resources are cached
    * @param item the encoded artifact identifier
    * @return the URL to the locally cached artifact
    */ 
    private static URL getURL( Repository repository, String item )
    {
         try
         {
             int n = item.indexOf( ":" );
             final String group = item.substring( 0, n );
            
             String artifact = null;
             String version = null;
             int m = item.indexOf( ";" );
             if( m > -1 )
             {
                 artifact = item.substring( n+1, m );
                 version = item.substring( m+1, item.length() );
             }
             else
             {
                 artifact = item.substring( n+1, item.length() );
             }

             Artifact ref = Artifact.createArtifact( group, artifact, version );            

             return repository.getResource( ref );
         }
         catch( Throwable e )
         {
             final String error = 
              "Internal bootstrap error.  Unable to load item: " + item;
             throw new BootstrapRuntimeException( error, e );
         }
    }

   /**
    * Utilitiy method to test if the -debug flag is present.
    * @param args the command line arguments
    * @return TRUE if the -debug flag is present
    */
    private static boolean isDebugEnabled( final String[] args )
    {
        for( int i=0; i<args.length; i++ )
        {
            final String arg = args[i];
            if( arg.equals( "-debug" ) ) return true;
        }
        return false;
    }

   /**
    * Debug utility to dump a classloader url set to system.out.
    * The implementation will print url from the supplied loader 
    * following which it will print the parent recursively if the 
    * parent is a URLClassLoader.
    * 
    * @param loader the classloader to dump
    */
    private static void printClassLoader( URLClassLoader loader )
    {
        URL[] urls = loader.getURLs();
        for( int i=0; i<urls.length; i++ )
        {
            System.out.println( urls[i] );
        }
        if( loader.getParent() instanceof URLClassLoader )
        {
            printClassLoader( (URLClassLoader) loader.getParent() );
        }
    }
}
