/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.

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

package org.apache.avalon.merlin.unit;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.merlin.kernel.Kernel;
import org.apache.avalon.merlin.kernel.KernelException;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.ProxyContext;
import org.apache.avalon.repository.impl.DefaultFileRepository;
import org.apache.avalon.repository.impl.DefaultAuthenticator;

/**
 * Embedded kernel implementation.
 *
 * @author mcconnell@apache.org
 */
public class DefaultEmbeddedKernel implements Runnable, Kernel
{
    //--------------------------------------------------------
    // static
    //--------------------------------------------------------

    private static final String MERLIN_PROPERTIES_NAME = 
      "merlin.properties";

    private static final String MERLIN_API_CLASSPATH_KEY = 
      "merlin.api.classpath";

    private static final String MERLIN_SPI_CLASSPATH_KEY = 
      "merlin.spi.classpath";

    private static final String MERLIN_IMPL_CLASSPATH_KEY = 
      "merlin.impl.classpath";

    private static final String MERLIN_LOADER_CLASS =
      "org.apache.avalon.merlin.kernel.impl.DefaultLoader";

    private static final String STARTUP = "startup";
    private static final String SHUTDOWN = "shutdown";
    private static final String CONTINUE = "continue";
    private static final String EXIT = "exit";

    private static final URL DPML = createURL( "http://dpml.net/" );
    private static final URL IBIBLIO = createURL( "http://www.ibiblio.org/maven/" );
    private static final URL[] DEFAULT_REMOTE_URLS = new URL[]{ DPML, IBIBLIO };

    private static URL createURL( String path )
    {
        try
        {
            return new URL( path );
        }
        catch( Throwable e )
        {
            // will not happen
            final String error =  
              "Unexpect error while building url: " + path;
            throw new UnitRuntimeException( error, e );
        }
    }

    //--------------------------------------------------------
    // state
    //--------------------------------------------------------

    private final URLClassLoader m_classloader;
    private final Map m_map;
    private Repository m_repository;
    private Object m_loader;
    private String m_command = CONTINUE;
    private Throwable m_error;
    private Kernel m_kernel;

    private boolean m_started = false;

    //--------------------------------------------------------
    // constructors
    //--------------------------------------------------------

   /**
    * Creation of a new kernel loader using a set of supplied arguments.  
    * The supplied arguments are used to construct an embedded kernel
    * during thread iniitialization. The implementation established
    * a classloader containing the full merlin implementation in preparation
    * for execution under a seperate thread.  During thread execution the 
    * classloader is bound as the threads context classloader.
    * 
    * @param map the embedded kernel loader arguments
    * @see org.apache.avalon.merlin.kernel.impl.DefaultLoader
    */
    public DefaultEmbeddedKernel( Map map )
    {
        m_map = map;

        try
        {
            m_repository = createBootstrapRepository();
            Properties properties = loadBootstrapProperties();
            ClassLoader current = Thread.currentThread().getContextClassLoader();
            m_classloader = createClassLoader( current, m_repository, properties );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to build the loader.";
            throw new UnitRuntimeException( error, e );
        }
    }

   /**
    * Thread initialization during which a classloader holding the 
    * Merlin set of classes is assigned as the context classloader.  Using
    * this classloader a merlin kernel loader is created and the thread
    * listens for startup and shutdown requests.
    */
    public void run()
    {
        Thread.currentThread().setContextClassLoader( m_classloader );

        //
        // bootstrap the kernel loader
        //

        try
        {
            Class clazz = getLoaderClass();
            Constructor constructor = 
              clazz.getConstructor( new Class[]{ Repository.class, Map.class } );
            m_loader = constructor.newInstance( new Object[] { m_repository, m_map } );

            Method method = 
              clazz.getMethod( "getKernel", new Class[0] );
            m_kernel = (Kernel) method.invoke( m_loader, new Object[0] );
            setShutdownHook( this );
            m_started = true;
        }
        catch( Throwable e )
        {
            m_error = e;
            m_started = true;
        }

        while( m_command != EXIT )
        {
            if( m_error != null ) break;
            if( m_command == STARTUP )
            {
                handleStartup();
            }
            else if( m_command == SHUTDOWN )
            {
                handleShutdown();
            }
            else
            {
                try
                {
                    Thread.currentThread().sleep( 100 );
                }
                catch( Throwable e )
                {
                    // wakeup
                }
            }
        }
    }

   /**
    * Utility method to test is the thread is fully established.
    * @return true if the thread has completed establishment
    */
    public boolean established()
    {
        return m_started;
    }

   /**
    * Utility method to return a throwable instance that may be established
    * during kernel startup as a result of a invlid block definition.
    * @return the error condition or null no error has occured
    */
    public Throwable getError()
    {
        return m_error;
    }

    //--------------------------------------------------------
    // Kernel
    //--------------------------------------------------------

   /**
    * Return the root containment model.
    * @return the containment model
    */
    public ContainmentModel getContainmentModel()
    {
        return m_kernel.getContainmentModel();
    }

   /**
    * Return the block matching the supplied model.
    * @return the containment block
    */
    public Block getBlock( ContainmentModel model ) throws KernelException
    {
        return m_kernel.getBlock( model );
    }

   /**
    * Return the root block.
    * @return the containment block
    */
    public Block getRootBlock()
    {
        return m_kernel.getRootBlock();
    }

   /**
    * Initiate the establishment of the root container.
    */
    public void startup()
    {
        if( m_command == EXIT ) throw new IllegalStateException("trminated");
        synchronized( m_command )
        {
            m_command = STARTUP;
            while( m_command.equals( STARTUP ) )
            {
                try
                {
                    Thread.currentThread().sleep( 100 );
                }
                catch( Throwable e )
                {
                    // wakeup
                }
            }
            if( m_error != null )
            {
                final String error = 
                  "Startup failure due to kernel error.";
                throw new UnitRuntimeException( error, m_error );
            }
        }
    }

   /**
    * Initiate an orderly shutdown of the kernel.
    */
    public void shutdown()
    {
        if( m_error != null ) return;
        if( m_command == EXIT ) throw new IllegalStateException("trminated");
        synchronized( m_command )
        {
            m_command = SHUTDOWN;
            while( m_command.equals( SHUTDOWN ) )
            {
                try
                {
                    Thread.currentThread().sleep( 100 );
                }
                catch( Throwable e )
                {
                    // wakeup
                }
            }
        }
    }

   /**
    * Return the Logger for the specified category.
    * @param category the category path
    * @return the logging channel
    */
    public Logger getLoggerForCategory( final String category )
    {
        return m_kernel.getLoggerForCategory( category );
    }

    //--------------------------------------------------------
    // implementation
    //--------------------------------------------------------

    private void handleStartup()
    {
        try
        {
            Class clazz = getLoaderClass();
            Method method = 
              clazz.getMethod( STARTUP, new Class[0] );
            method.invoke( m_loader, new Object[0] );
            m_command = CONTINUE;
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to start the kernel.";
            m_error = new UnitRuntimeException( error, e );
            m_command = EXIT;
        }
    }

    private void handleShutdown()
    {
        try
        {
            Class clazz = getLoaderClass();
            Method method = 
              clazz.getMethod( SHUTDOWN, new Class[]{} );
            method.invoke( m_loader, new Class[0] );
            m_command = EXIT;
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to shutdown the kernel.";
            m_error = new UnitRuntimeException( error, e );
            m_command = EXIT;
        }
    }

    private Class getLoaderClass()
    {
        try
        {
            return m_classloader.loadClass( MERLIN_LOADER_CLASS );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to load the kernel loader class.";
            throw new UnitRuntimeException( error, e );
        }
    }

   /**
    * Create the classloader holding the kernel.
    */
    private URLClassLoader createClassLoader( 
       ClassLoader loader, Repository repository, Properties properties ) throws Exception
    {
        URL[] api = getURLs( repository, properties, MERLIN_API_CLASSPATH_KEY );
        URL[] spi = getURLs( repository, properties, MERLIN_SPI_CLASSPATH_KEY );
        URL[] impl = getURLs( repository, properties, MERLIN_IMPL_CLASSPATH_KEY );

        URLClassLoader apiLoader = new URLClassLoader( api, loader );
        URLClassLoader spiLoader = new URLClassLoader( spi, apiLoader );
        URLClassLoader implLoader = new URLClassLoader( impl, spiLoader );
        return implLoader;
    }

   /**
    * Load the bootstrap properties.
    */
    private Properties loadBootstrapProperties()
    {
        try
        {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream input = 
              classloader.getResourceAsStream( MERLIN_PROPERTIES_NAME  );
            Properties properties = new Properties();
            properties.load( input );
            return properties;
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to create the load bootstrap properties.";
            throw new UnitRuntimeException( error, e );
        }
    }

   /**
    * Return the repository from which we will build the kernel classloader.
    *
    * @return the repository
    */
    private Repository createBootstrapRepository() 
    {
        try
        {
            File repo = getSystemRepositoryDirectory();
            ProxyContext proxy = createProxyContext();
            URL[] hosts = createHostsSequence();
            return new DefaultFileRepository( repo, proxy, hosts );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to create the bootstrap repository.";
            throw new UnitRuntimeException( error, e );
        }
    }

   /**
    * Return an array of hosts based on the maven.repo.remote property value.
    * @return the array of remote hosts
    */
    private URL[] createHostsSequence() throws Exception
    {
        ArrayList list = new ArrayList();
        String path = System.getProperty( "maven.repo.remote" );
        if( path == null ) 
        {
            return DEFAULT_REMOTE_URLS;
        }

        StringTokenizer tokenizer = new StringTokenizer( path, "," );
        while( tokenizer.hasMoreElements() )
        {
            String token = tokenizer.nextToken();
            list.add( new URL( token ) );
        }
        return (URL[]) list.toArray( new URL[0] );
    }

   /**
    * Create of the proxy context.  If no proxy properties are declared a null
    * value is returned.  Proxy values are based assessment of  
    * properties maven.proxy.host, maven.proxy.port, maven.proxy.username and 
    * maven.proxy.password.
    *
    * @return the proxy context or null if not required
    */
    private ProxyContext createProxyContext()
    {
        String host = System.getProperty( "maven.proxy.host" );
        if( host != null )
        {
            String proxyPort = System.getProperty( "maven.proxy.port" );
            if( proxyPort == null ) throw new IllegalStateException( "maven.proxy.port" );
            int port = new Integer( proxyPort ).intValue();
            String username = System.getProperty( "maven.proxy.username" );
            DefaultAuthenticator authenticator = null;
            if( username != null )
            {
                String password = System.getProperty( "maven.proxy.password" );
                authenticator = new DefaultAuthenticator( username, password );
            }
            return new ProxyContext( host, port, authenticator );
        }
        else
        {
            return null;
        }
    }

   /**
    * Return the file corresponding  to the merlin system repository.
    * Currently hardwired to use the maven repository until we get the 
    * Merlin environment stuff in place.
    *
    * @return the system repository directory
    */
    public static File getSystemRepositoryDirectory()
    {
        final String system = System.getProperty( "maven.repo.local" );
        if( system != null )
        {
            return new File( new File( system ), "repository" );
        }
        else
        {
            final String home = System.getProperty( "maven.home" );
            if( home != null )
            {
                return new File( new File( home ), "repository" );
            }
            else
            {
                File user = new File( System.getProperty( "user.dir" ) );
                return new File( user, ".merlin/system" );
            }
        }
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
            
             return repository.getArtifact( group, artifact, version, "jar" );
         }
         catch( Throwable e )
         {
             final String error = 
              "Internal bootstrap error.  Unable to load item: " + item;
             throw new UnitRuntimeException( error, e );
         }
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
            System.out.println( "" );
            printClassLoader( (URLClassLoader) loader.getParent() );
        }
    }

   /**
    * Create a shutdown hook that will trigger shutdown of the supplied kernel.
    * @param kernel the kernel to be shutdown
    */
    private void setShutdownHook( final DefaultEmbeddedKernel kernel )
    {
        //
        // Create a shutdown hook to trigger clean disposal of the
        // Merlin kernel
        //

        Runtime.getRuntime().addShutdownHook(
          new Thread()
          {
              public void run()
              {
                  try
                  {
                      kernel.shutdown();
                  }
                  catch( Throwable e )
                  {
                      // ignore it
                  }
              }
          }
        );
    }
}

