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

package org.apache.avalon.repository.main ;

import java.io.File;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase ;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.InitialContextFactory;
import org.apache.avalon.repository.main.DefaultInitialContextFactory;

import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.exception.ExceptionHelper;

/**
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.9 $
 */
public class DefaultInitialContextTest extends TestCase
{
    private static final String KEY = "test";

    private static final File BASEDIR = getWorkDir();

    private static File getWorkDir()
    {
        String path = System.getProperty( "project.dir" );
        if( null != path )
        {
            return new File( path );
        }
        else
        {
            path = System.getProperty( "basedir" );
            File root = new File( path );
            return new File( root, "target/test-classes" );
        }
    }

    /**
     * Constructor for DefaultInitialContextTest.
     * @param name the test name
     */
    public DefaultInitialContextTest( String name )
    {
        super( name );
    }

    public void testRepositoryBootstrap() throws Exception
    {
        DefaultInitialContextFactory factory = 
          new DefaultInitialContextFactory( KEY, BASEDIR );
        factory.setCacheDirectory( getMavenRepositoryDirectory() );
        factory.setHosts( getDefaultHosts() );

        InitialContext context = factory.createInitialContext();

        assertEquals( 
          "cache", 
          context.getInitialCacheDirectory(), 
          getMavenRepositoryDirectory() );

        String[] defaults = getDefaultHosts();
        String[] hosts = context.getInitialHosts();
        assertNotNull( "hosts", hosts );
        assertEquals( "hosts count", defaults.length, hosts.length );

        for( int i=0; i<defaults.length; i++ )
        {
            assertEquals( 
              "host", defaults[i], hosts[i] );
        }

        Factory initialFactory = context.getInitialFactory();
        assertNotNull( initialFactory );

        Repository repository = (Repository) context.getRepository() ;
        assertNotNull( repository ) ;
   
        Artifact artifact = Artifact.createArtifact( 
          "avalon-framework", "avalon-framework-api", "4.1.5" );
        URL url = repository.getResource( artifact );
        assertNotNull( "url", url );
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
        try
        {
            String local = 
              System.getProperty( 
                "maven.home.local", 
                Env.getEnvVariable( "MAVEN_HOME_LOCAL" ) );
            if( null != local ) return local;

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

    private static String[] getDefaultHosts()
    {
        return new String[]{ 
          "http://www.dpml.net/",
          "http://www.ibiblio.org/maven/"
        };
    }
}
