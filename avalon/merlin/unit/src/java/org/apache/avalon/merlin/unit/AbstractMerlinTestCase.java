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
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.apache.avalon.merlin.kernel.impl.DefaultKernel;
import org.apache.avalon.merlin.kernel.impl.DefaultKernelContext;
import org.apache.avalon.merlin.kernel.KernelContext;
import org.apache.avalon.merlin.kernel.Kernel;
import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.ProxyContext;
import org.apache.avalon.repository.impl.DefaultFileRepository;
import org.apache.avalon.repository.impl.DefaultAuthenticator;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.framework.logger.Logger;

import junit.framework.TestCase;

/**
 * Abstract Merlin Test Case.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2003/10/11 22:13:07 $
 */
public class AbstractMerlinTestCase extends TestCase
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    public static boolean MERLIN_DEBUG_OFF = false;
    public static boolean MERLIN_DEBUG_ON = true;
    public static boolean MERLIN_INFO_OFF = false;
    public static boolean MERLIN_INFO_ON = true;

    public static final File MAVEN_TARGET_CLASSES_DIR = 
      getTargetClassesDirectory();

    public static final File MAVEN_TARGET_TEST_CLASSES_DIR = 
      getTargetTestClassesDirectory();

    public static final File MERLIN_DEFAULT_CONFIG_FILE = 
      getProjectFile( "conf/config.xml" );

    //-------------------------------------------------------------------
    // state
    //-------------------------------------------------------------------

    private final KernelContext m_context;

    private final Kernel m_kernel;

    private final ContainmentModel m_test;

    private Block m_block;

    private Logger m_logger;

    //-------------------------------------------------------------------
    // constructors
    //-------------------------------------------------------------------

   /**
    * Creation of a new test case instance using the default info and debug
    * policies and a test container path of ${basedir}/target/classes
    */
    public AbstractMerlinTestCase()
    {
        this( "testcase" );
    }

   /**
    * Creation of a new test case instance using the default info and debug
    * policies and a test container path of ${basedir}/target/classes
    *
    * @param name the name of the test case
    */
    public AbstractMerlinTestCase( String name )
    {
        this( MAVEN_TARGET_CLASSES_DIR, null, MERLIN_INFO_OFF, MERLIN_DEBUG_OFF, name );
    }

   /**
    * Creation of a new test case instance using the test container 
    * path of ${basedir}/target/classes
    *
    * @param info information summary display policy
    * @param debug internal container debug policy
    * @param name the name of the test case
    */
    public AbstractMerlinTestCase( boolean info, boolean debug, String name )
    {
        this( MAVEN_TARGET_CLASSES_DIR, null, info, debug, name );
    }

   /**
    * Creation of a new test case instance using a supplied test container 
    * path, info and debug policies and unit test name.
    *
    * @param url the test container deployment path
    * @param info information summary display policy
    * @param debug internal container debug policy
    * @param name the name of the test case
    */
    public AbstractMerlinTestCase( 
      File block, File targets, boolean info, boolean debug, String name )
    {
        super( name );

        if( block == null )
        {
            throw new NullPointerException( "block" );
        }

        if( !block.exists() )
        {
            final String error = 
              "Containment block [" + block + "] does not exist.";
            throw new IllegalStateException( error );
        }

        if( ( targets != null ) && !targets.exists() )
        {
            final String error = 
              "Configuration targets [" + targets + "] does not exist.";
            throw new IllegalStateException( error );
        }

        URL url = convertToURL( block );
        URL conf = convertToURL( targets );

        File base = 
          new File( 
            System.getProperty( 
              "basedir", 
              System.getProperty( "user.dir" ) ) );

        File repo = getSystemRepositoryDirectory();
        Repository repository = createBootstrapRepository( repo );
        File library = base;
        URL kernel = null;
        URL config = null;

        //
        // bootstrap the kernel
        //

        try
        {
            m_context = 
              new DefaultKernelContext( 
                repository, repo, library, base, kernel, new URL[0], 
                config, true, info, debug );
            m_kernel = new DefaultKernel( m_context );
            m_logger = 
              m_context.getLoggerForCategory( "testcase" ).getChildLogger( name );
        }
        catch( Throwable e )
        {
            final String message =
              "Internal error while attempting to establish the kernel.";
            final String error = ExceptionHelper.packException( message, e, true );
            throw new UnitRuntimeException( error, e );
        }

        // 
        // Programatically add the test container to the root containment 
        // model so that we get a reference to the test model enabling 
        // access to the test block following setup of the test case. This
        // allows us to directly lookup objects relative to the test container.
        //

        try
        {
            ContainmentModel root = m_kernel.getContainmentModel();
            m_test = root.addContainmentModel( url, conf );
        }
        catch( Throwable e )
        {
            final String message = 
              "Internal error while attempting to establish the test container.";
            final String error = ExceptionHelper.packException( message, e, true );
            getLogger().error( error );
            throw new UnitRuntimeException( error );
        }
    }

    //-------------------------------------------------------------------
    // testcase
    //-------------------------------------------------------------------

   /**
    * Startup the Merlin Kernel.
    */
    public void setUp() throws Exception
    {
        getKernel().startup();

        try
        {
            m_block = getKernel().getBlock( m_test );
        }
        catch( Throwable e )
        {
            final String message = 
              "Internal error while attempting to resolve test block: " 
              + m_test;
            final String error = ExceptionHelper.packException( message, e, true );
            getLogger().error( error );
            throw new UnitException( error );
        }
    }

   /**
    * Shutdown the Merlin Kernel.
    */
    public void tearDown()
    {
        m_kernel.shutdown();
    }

    //-------------------------------------------------------------------
    // service access
    //-------------------------------------------------------------------

   /**
    * Return a reference to the kernel.
    * @return the kernel
    */
    protected Kernel getKernel()
    {
        return m_kernel;
    }

   /**
    * Resolve a service relative to the test container.  The supplied
    * path may be relative or absolute.  Relative paths will be resolved
    * relative to the test container wheras absolute paths will be resolved 
    * relative to the root container.
    *
    * @param path the absolute or relative path to a component type
    * @return the object resolved from an appliance defined by the supplied path
    * @exception UnitException if a resolution error occurs
    */
    protected Object resolve( String path ) throws UnitException
    {
        if( path == null ) throw new NullPointerException( "path" );

        try
        {
            Appliance appliance = m_block.resolveAppliance( path );
            if( appliance == null )
            {
                final String problem = 
                  "Unknown appliance: " + path;
                throw new IllegalArgumentException( problem );
            }
            return appliance.resolve( this );
        }
        catch( Throwable e )
        {
            final String error = 
              "Service resolution error from path: " + path;
            final String msg = ExceptionHelper.packException( error, e, false );
            throw new UnitException( msg, e );
        }
    }

   /**
    * Return the assigned logging channel for the test case.
    * @return the logging channel
    */
    protected Logger getLogger()
    {
        return m_logger;
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

   /**
    * Return the repository from which we will build the kernel classloader.
    * @param repo the system repository directory
    * @return the repository
    */
    private Repository createBootstrapRepository( File repo )
    {
        ProxyContext proxy = createProxyContext();
        URL[] hosts = createHostsSequence();
        return new DefaultFileRepository( repo, proxy, hosts );
    }

   /**
    * Return an array of hosts based on the maven.repo.remote property value.
    * @return the array of remote hosts
    */
    private URL[] createHostsSequence()
    {
        ArrayList list = new ArrayList();
        String path = System.getProperty( "maven.repo.remote" );
        if( path == null ) return new URL[0];

        StringTokenizer tokenizer = new StringTokenizer( path, "," );
        while( tokenizer.hasMoreElements() )
        {
            String token = tokenizer.nextToken();
            appendEntry( list, token );
        }
        return (URL[]) list.toArray( new URL[0] );
    }

    private void appendEntry( List list, String token )
    {
        try
        {
            list.add( new URL( token ) );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to convert token [" + token + "] to a URL.";
            throw new UnitRuntimeException( error, e );
        }
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
            if( proxyPort == null ) 
              throw new IllegalStateException( "maven.proxy.port" );
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
    *
    * @return the system repository directory
    */
    private static File getSystemRepositoryDirectory()
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
    * Convinience method to get the ${basedir}/target/classes directory.
    * @return the deployment directory
    */
    public static File getTargetClassesDirectory()
    {
        return getProjectFile( "target/classes" );
    }

   /**
    * Convinience method to get the ${basedir}/target/test-classes directory.
    * @return the deployment url
    */
    public static File getTargetTestClassesDirectory()
    {
        return getProjectFile( "target/test-classes" );
    }

   /**
    * Convinience method to get the ${basedir}/[path] directory.
    * @return the deployment url
    */
    public static File getProjectFile( String path )
    {
        File base = getBaseDirectory();
        return new File( base, path );
    }

    private static File getBaseDirectory()
    {
        String basedir = System.getProperty( "basedir" );
        return new File( basedir );
    }

    private URL convertToURL( File file )
    {
        if( file == null ) return null;
        try
        {
            return file.toURL();
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to convert file [" + file + "] to a url.";
            throw new UnitRuntimeException( error, e );
        }
    }
}

