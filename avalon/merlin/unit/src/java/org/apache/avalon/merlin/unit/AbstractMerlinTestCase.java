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

package org.apache.avalon.merlin.unit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Enumeration;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.main.DefaultInitialContext;
import org.apache.avalon.repository.main.DefaultBuilder;

import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.exception.ExceptionHelper;

/**
 * Test case that usages the repository builder to deploy the 
 * Merlin default application factory.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.18 $
 */
public abstract class AbstractMerlinTestCase extends TestCase
{
    //----------------------------------------------------------
    // static
    //----------------------------------------------------------

    private static final String APPLICANCE_CLASSNAME = 
      "org.apache.avalon.activation.appliance.Appliance";

    //----------------------------------------------------------
    // immutable state
    //----------------------------------------------------------

    private Object m_kernel;

    private ClassLoader m_classloader;

    private Object m_root;

    private Method m_locate;
    private Method m_resolve;
    private Method m_shutdown;

    //----------------------------------------------------------
    // constructor
    //----------------------------------------------------------

    /**
     * Constructor for MerlinEmbeddedTest.
     * @param name the name of the testcase
     */
    public AbstractMerlinTestCase( String name )
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        try
        {
            Artifact artifact = 
              Artifact.createArtifact( 
                "merlin", "merlin-impl", "3.2-dev" );

            InitialContext context = 
               new DefaultInitialContext( 
                 getMavenRepositoryDirectory() );

            Builder builder = new DefaultBuilder( context, artifact );
            m_classloader = builder.getClassLoader();
            Factory factory = builder.getFactory();
            Map criteria = factory.createDefaultCriteria();

            //
            // set the defaults
            //

            criteria.put( "merlin.repository", getMavenRepositoryDirectory() );
            criteria.put( "merlin.context", new File( getBaseDirectory(), "target" ) );

            //
            // read in any properties declared under the path 
            // ${basedir}/merlin.properties
            //

            applyLocalProperties( criteria );

            //
            // if the deployment path is undefined then the best we 
            // can do is to assume ${basedir}/target/classes and/or
            // ${basedir}/target/test-classes contains a BLOCK-INF/block.xml
            // and from this, derive a merlin.deployment value
            //

            String[] deployment = (String[]) criteria.get( "merlin.deployment" );
            if( deployment.length == 0 )
            {
                String path = buildDefaultTestPath();
                if( null != path )
                {
                    criteria.put( "merlin.deployment", path );
                }
                else
                {
                    final String error = 
                      "Cannot locate a deployment objective.";
                    throw new IllegalStateException( error );
                }
            }

            //
            // if the ${merlin.override} value is undefined, check for 
            // the existance of an override file in ${basedir}/conf/config.xml
            // and if it exists assign it as the override parameter
            //

            if( null == criteria.get( "merlin.override" ) )
            {  
                String override = buildDefaultOverridePath();
                if( null != override )
                {
                    criteria.put( "merlin.override", override );
                }
            }

            //
            // go ahead with the deployment of the kernel
            //

            m_kernel = factory.create( criteria );
            m_shutdown = m_kernel.getClass().getMethod( "shutdown", new Class[0] );
            Method method = m_kernel.getClass().getMethod( "getBlock", new Class[0] );
            m_root = method.invoke( m_kernel, new Object[0] );
            m_locate = m_root.getClass().getMethod( "locate", new Class[]{ String.class } );
            Class applianceClass = m_classloader.loadClass( APPLICANCE_CLASSNAME );
            m_resolve = applianceClass.getMethod( "resolve", new Class[0] );
        }
        catch( Throwable e )
        {
            final String error = ExceptionHelper.packException( e, true );
            System.out.println( error );
            throw new Exception( error );
        }
    }

    public void tearDown()
    {
        m_classloader = null;
        m_root = null;
        m_locate = null;
        m_resolve = null;

        try
        {
            m_shutdown.invoke( m_kernel, new Object[0] );
        }
        catch( Throwable e )
        {
            // ignore
        }

        m_shutdown = null;
    }

    //----------------------------------------------------------------------
    // protected
    //----------------------------------------------------------------------

    protected Object resolve( String path ) throws Exception
    {
        if( null == m_kernel ) 
          throw new IllegalStateException( "kernel does not exist" );

        try
        {
            Object appliance =  m_locate.invoke( m_root, new Object[]{ path } );
            return m_resolve.invoke( appliance, new Object[0] );
        }
        catch( InvocationTargetException ite )
        {
            Throwable cause = ite.getTargetException();
            final String error = ExceptionHelper.packException( cause, true );
            throw new Exception( error );
        }
        catch( Throwable e )
        {
            final String error = ExceptionHelper.packException( e, true );
            throw new Exception( error );
        }
    }

    //----------------------------------------------------------------------
    // utilities
    //----------------------------------------------------------------------

    private String buildDefaultOverridePath()
    {
        File base = getBaseDirectory();
        File config = new File( base, "conf/config.xml" );
        if( config.exists() ) return "conf/config.xml";
        return null;
    }

    private String buildDefaultTestPath()
    {
        File base = getBaseDirectory();
        File classes = new File( base, "target/classes/BLOCK-INF/block.xml" );
        File tests = new File( base, "target/test-classes/BLOCK-INF/block.xml" );
        if( classes.exists() && tests.exists() )
        {
            return "target/classes,target/test-classes";
        }
        else if( classes.exists() )
        {
            return "target/classes";
        }
        else if( tests.exists() )
        {
            return "target/test-classes";
        }
        return null;
    }

    private void applyLocalProperties( Map criteria ) throws IOException
    {
        File base = getBaseDirectory();
        Properties properties = 
          getLocalProperties( base, "merlin.properties" );
        Enumeration keys = properties.keys();
        while( keys.hasMoreElements() )
        {
            final String key = (String) keys.nextElement();
            if( key.startsWith( "merlin." ) )
            {
                String value = properties.getProperty( key );
                criteria.put( key, value );
            }
        }
    }

    private Properties getLocalProperties( 
      File dir, String filename ) throws IOException
    {
        Properties properties = new Properties();
        if( null == dir ) return properties;
        File file = new File( dir, filename );
        if( !file.exists() ) return properties;
        properties.load( new FileInputStream( file ) );
        return properties;
    }


    private static File getMavenRepositoryDirectory()
    {
        return new File( getMavenHomeDirectory(), "repository" );
    }

    private static File getMavenHomeDirectory()
    {
        return new File( getMavenHome() );
    }

    private static String getMavenHome()
    {
System.out.println( 
  "### ${maven.home} == [" 
  + System.getProperty( "maven.home" ) 
  + "]" );
System.out.println( 
  "### ${maven.home.local} == [" 
  + System.getProperty( "maven.home.local" ) 
  + "]" );
System.out.println( 
  "### MAVEN_HOME == [" 
  + getEnvValue( "MAVEN_HOME" ) 
  + "]" );
System.out.println( 
  "### MAVEN_HOME_LOCAL == [" 
  + getEnvValue( "MAVEN_HOME_LOCAL" ) 
  + "]" );

        try
        {
            String local = 
              System.getProperty( 
                "maven.home.local", 
                Env.getEnvVariable( "MAVEN_HOME_LOCAL" ) );

System.out.println( 
  "### local == [" 
  + local 
  + "]" );

            if( null != local ) return local;

            String maven = 
              System.getProperty( 
                "maven.home", 
                Env.getEnvVariable( "MAVEN_HOME" ) );

System.out.println( 
  "### maven == [" 
  + maven 
  + "]" );

            if( null != maven ) return maven;

System.out.println( 
  "### fallback == [" 
  + System.getProperty( "user.home" ) + File.separator + ".maven" 
  + "]" );

            return System.getProperty( "user.home" ) + File.separator + ".maven";

        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to access environment.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

    private static String getEnvValue( String key )
    {
        try
        {
            return Env.getEnvVariable( key );
        }
        catch( Throwable e )
        {
            throw new RuntimeException( e.toString() );
        }
    }

    private File getBaseDirectory()
    {
        final String base = System.getProperty( "basedir" );
        if( null != base )
        {
            return new File( base );
        }
        return new File( System.getProperty( "user.dir" ) );
    }
}
