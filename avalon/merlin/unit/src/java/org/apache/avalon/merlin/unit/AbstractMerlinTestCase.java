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

package org.apache.avalon.merlin.unit;

import java.io.File;
import java.io.InputStream;
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
 * @version $Revision: 1.24 $
 */
public abstract class AbstractMerlinTestCase extends TestCase
{
    //----------------------------------------------------------
    // static
    //----------------------------------------------------------

    private static final String MERLIN_PROPERTIES = "merlin.properties";

    private static final String IMPLEMENTATION_KEY = "merlin.implementation";

    private static final String DEPLOYMENT_MODEL_CLASSNAME = 
      "org.apache.avalon.composition.model.DeploymentModel";

    //----------------------------------------------------------
    // immutable state
    //----------------------------------------------------------

    private Object m_kernel;

    private ClassLoader m_classloader;

    private Object m_root;

    private Method m_locate;
    private Method m_runtime;
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
        ClassLoader classloader = AbstractMerlinTestCase.class.getClassLoader();

        try
        {
            File repository = new File( getMavenHome(), "repository" );

            Artifact artifact = 
              DefaultBuilder.createImplementationArtifact( 
                classloader, 
                getMerlinHome(),
                getBaseDirectory(), 
                MERLIN_PROPERTIES, 
                IMPLEMENTATION_KEY );

            InitialContext context = 
               new DefaultInitialContext( repository );

            Builder builder = new DefaultBuilder( context, artifact );
            m_classloader = builder.getClassLoader();
            Factory factory = builder.getFactory();
            Map criteria = factory.createDefaultCriteria();

            //
            // set the defaults
            //

            criteria.put( "merlin.repository", repository );
            criteria.put( "merlin.context", "target" );
            criteria.put( "merlin.server", "true" );
            criteria.put( "merlin.code.security.enabled", "false" );

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
            m_shutdown =
              m_kernel.getClass().getMethod( 
                "shutdown", 
                new Class[0] );
            Method method = 
              m_kernel.getClass().getMethod( 
                "getModel", 
                new Class[0] );
            m_root = method.invoke( m_kernel, new Object[0] );
            m_locate = 
              m_root.getClass().getMethod( 
                "getModel", 
                new Class[]{ String.class } );
            Class modelClass = 
              m_classloader.loadClass( DEPLOYMENT_MODEL_CLASSNAME );
            m_resolve = 
              modelClass.getMethod( "resolve", new Class[0] );
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
            Object model =  m_locate.invoke( m_root, new Object[]{ path } );
            return m_resolve.invoke( model, new Object[0] );
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

    private static File getMavenHome()
    {
        try
        {
            String local = 
              System.getProperty( 
                "maven.home.local", 
                Env.getEnvVariable( "MAVEN_HOME_LOCAL" ) );

            if( null != local ) return new File( local ).getCanonicalFile();

            return new File(
              System.getProperty( "user.home" ) 
              + File.separator 
              + ".maven" ).getCanonicalFile();

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

    private File getBaseDirectory()
    {
        final String base = System.getProperty( "basedir" );
        if( null != base )
        {
            return new File( base );
        }
        return new File( System.getProperty( "user.dir" ) );
    }

   /**
    * Return the merlin home directory.
    * @return the merlin install directory
    */
    private static File getMerlinHome()
    {
        return new File( getMerlinHomePath() );
    }

   /**
    * Return the merlin home directory path.
    * @return the merlin install directory path
    */
    private static String getMerlinHomePath()
    {
        try
        {
            String merlin = 
              System.getProperty( 
                "merlin.home", 
                Env.getEnvVariable( "MERLIN_HOME" ) );
            if( null != merlin ) return merlin;
            return System.getProperty( "user.home" ) 
              + File.separator + ".merlin";
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while attempting to access MERLIN_HOME environment.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

}
