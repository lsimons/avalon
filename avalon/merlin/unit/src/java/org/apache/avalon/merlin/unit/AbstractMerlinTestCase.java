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
import java.util.Map;
import java.util.Hashtable;
import java.net.URL;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.merlin.unit.DefaultEmbeddedKernel;

import junit.framework.TestCase;

/**
 * Abstract test case suitable for execution under Maven. 
 *
 * @author mcconnell@apache.org
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
      getProjectFile( "target/classes" );

    public static final File MAVEN_TARGET_TEST_CLASSES_DIR = 
      getProjectFile( "target/test-classes" );

    public static final File MERLIN_DEFAULT_CONFIG_FILE = 
      getProjectFile( "conf/config.xml" );


    //-------------------------------------------------------------------
    // state
    //-------------------------------------------------------------------

    private final DefaultEmbeddedKernel m_kernel;

    private final ContainmentModel m_test;

    private final Thread m_thread;

    private Logger m_logger;

    private Block m_block;

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
    * @param block the test container deployment path
    * @param targets the test container deployment path
    * @param info information summary display policy
    * @param debug internal container debug policy
    * @param name the name of the test case
    */
    public AbstractMerlinTestCase(
      File block, File targets, boolean info, boolean debug, String name )
    {
        super( name );

        //
        // validate arguments
        //

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

        //
        // build the kernel
        //

        Map map = new Hashtable();
        map.put( "merlin.policy.info", new Boolean( info ) );
        map.put( "merlin.policy.debug", new Boolean( debug ) );
        map.put( "merlin.repository.dir", getMavenRepositoryDirectory() );
        map.put( "merlin.base.dir", getBaseDirectory() );

        try
        {
            m_kernel = new DefaultEmbeddedKernel( map );
            m_thread = new Thread( m_kernel );
            m_thread.start();
        }
        catch( Throwable e )
        {
            final String error = 
              "Runnable kernel establishment failure.";
            final String msg = UnitHelper.packException( error, e, true );
            throw new UnitRuntimeException( msg, null );
        }

        //
        // wait for the kernel to initialize
        //


        while( !m_kernel.established() )
        {
            try
            {
                Thread.sleep( 100 );
            }
            catch( Throwable e )
            {
                // wakeup
            }
        }

        //
        // check for kernel errors
        //

        if( m_kernel.getError() != null )
        {
            final String message = 
              "Internal error while attempting to establish the kernel.";
            final String error = 
              UnitHelper.packException( message, m_kernel.getError(), true );
            throw new UnitRuntimeException( error, null );
        }

        //
        // setup a logger for the testcase
        //

        m_logger = m_kernel.getLoggerForCategory( "testcase" );

        //
        // add a container holding the components that will
        // be used as services in the testcase
        //

        try
        {
            URL url = convertToURL( block );
            URL conf = convertToURL( targets );
            ContainmentModel root = m_kernel.getContainmentModel();
            m_test = root.addContainmentModel( url, conf );
        }
        catch( Throwable e )
        {
            final String message = 
              "Internal error while attempting to establish the test container.";
            final String error = UnitHelper.packException( message, e, true );
            throw new UnitRuntimeException( error, null );
        }
    }

    //--------------------------------------------------------
    // TestCase
    //--------------------------------------------------------

   /**
    * Startup the kernel based on the meta model established 
    * under the constructor.  The implementation will locate 
    * the test container and establish it as the reference for 
    * relative service lookups.
    */
    protected void setUp() throws Exception
    {
        m_kernel.startup();

        try
        {
            m_block = m_kernel.getBlock( m_test );
        }
        catch( Throwable e )
        {
            final String message = 
              "Internal error while attempting to resolve test block: " 
              + m_test;
            final String error = UnitHelper.packException( message, e, true );
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

    //--------------------------------------------------------
    // utilities
    //--------------------------------------------------------

   /**
    * Return the assigned logging channel for the test case.
    * @return the logging channel
    */
    protected Logger getLogger()
    {
        return m_logger;
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
    protected Object resolve( String path ) throws Exception
    {
        if( path == null ) throw new NullPointerException( "path" );

        try
        {
            Appliance appliance = m_block.locate( path );
            if( appliance == null )
            {
                final String problem = 
                  "Unknown appliance: " + path;
                throw new IllegalArgumentException( problem );
            }
            return appliance.resolve();
        }
        catch( Throwable e )
        {
            final String error = 
              "Service resolution error from path: " + path;
            final String msg = UnitHelper.packException( error, e, false );
            throw new UnitException( msg, e );
        }
    }

    //--------------------------------------------------------
    // implementation
    //--------------------------------------------------------

   /**
    * Convert a supplied file to a url.  If the file argument is
    * null return null else return file.toURL().  
    * @param file the file to convert
    * @return the equivalent url
    */
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
        if( basedir != null ) return new File( basedir );
        return new File( "." );
    }

   /**
    * Return the maven system repository directory.
    * @return the system repository directory
    */
    private File getMavenRepositoryDirectory()
    {
        //
        // get ${maven.home.local} system property - this may 
        // be null in which case to fallback to ${user.home}/.maven
        //

        final String local = System.getProperty( "maven.home.local" );
        if( local != null )
        {
            try
            {
                File sys = getDirectory( new File( local ) );
                return getDirectory( new File( sys, "repository" ) );
            }
            catch( Throwable e )
            {
                final String error = 
                  "Unable to resolve repository from ${maven.home.local}.";
                throw new UnitRuntimeException( error, e );
            }
        }
        else
        {
            //
            // try to establish the repository relative to 
            // ${user.home}/.maven/repository
            //

            final String userHome = System.getProperty( "user.home" );
            if( userHome != null )
            {
                try
                {
                    File home = getDirectory( new File( userHome ) );
                    File maven = getDirectory( new File( home, ".maven" ) );
                    return getDirectory( new File( maven, "repository" ) );
                }
                catch( Throwable e )
                {
                    final String error = 
                      "Unable to resolve the maven repository relative to ${user.home}.";
                    throw new UnitRuntimeException( error, e );
                }
            }
            else
            {
                //
                // should never happen
                //

                final String error = 
                  "Unable to resolve maven repository.";
                throw new IllegalStateException( error );
            }
        }
    }

    private File getDirectory( File file )
    {
        if( file == null ) throw new NullPointerException( "file" );
        if( file.exists() )
        {
            return file;
        }
        else
        {
            final String error =
              "Directory [" + file + "] does not exist.";
            throw new IllegalArgumentException( error );
        }
    }
}

