
package org.apache.avalon.repository.main;

import java.util.Properties;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase ;

import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.InitialContextFactory;
import org.apache.avalon.util.exception.ExceptionHelper;
import org.apache.avalon.util.env.Env;

/**
 * DefaultsBuilderTestCase
 * 
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $
 */
public class DefaultInitialContextFactoryTestCase extends TestCase
{
    private static final String KEY = "merlin";

    private static final File BASEDIR = 
      new File( System.getProperty( "basedir" ) );

    public void testInitialContextCreation() throws Exception
    {
        DefaultInitialContextFactory factory = 
          new DefaultInitialContextFactory( KEY, BASEDIR );

        //
        // Before creating the intial context we can set the 
        // hosts, cache, parent classloader (and the repoitory
        // implementation artifact but that would be unusual).
        //
        // Before modifying anything lets just check what the 
        // default values are.
        //

        System.out.println( "" );
        System.out.println( "InitialContextFactory" );
        System.out.println( "---------------------" );
        System.out.println( "  key: " + factory.getApplicationKey() );
        System.out.println( "  home: " + factory.getHomeDirectory() );
        System.out.println( "  cache: " + factory.getCacheDirectory() );
        System.out.println( "  work: " + factory.getWorkingDirectory() );
        System.out.println( "  impl: " + factory.getImplementation() );
        String[] hosts = factory.getHosts();
        for( int i=0; i<hosts.length; i++ )
        {
            System.out.println( 
              "  host (" + (i+1) + "): " 
              + hosts[i] );
        }
        System.out.println( "" );

        //
        // Normally the default values should be ok and you should 
        // not need to modify anything, however, you have available
        // serveral set methods on the InitialContextFactory interface
        // that allow customization of the factory.  In this example
        // we will override the system cach with the local maven 
        // repository.
        //

        File repo = getMavenRepositoryDirectory();
        factory.setCacheDirectory( repo );

        // Once customized we can proceed with the instantiation
        // of the initial context. The following method invocation
        // will trigger the population of the system cache with the  
        // resources necesary to load the repository implementation.
        //

        InitialContext context = factory.createInitialContext();

        //
        // The following code simply prints out the working and cache
        // directory, and the set of initial hosts assigned to the 
        // initial context.
        //

        System.out.println( "InitialContext" );
        System.out.println( "--------------" );
        System.out.println( "  work: " + context.getInitialWorkingDirectory() );
        System.out.println( "  cache: " + context.getInitialCacheDirectory() );
        hosts = context.getInitialHosts();
        for( int i=0; i<hosts.length; i++ )
        {
            System.out.println( 
              "  host (" + (i+1) + "): " 
              + hosts[i] );
        }
        System.out.println( "" );

        //
        // With the establishment of an initial context we can load any 
        // factory based system (e.g. logging, merlin, etc.).
        //
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
        catch( IOException e )
        {
            final String error = 
              "Internal error while attempting to access environment.";
            final String message = 
              ExceptionHelper.packException( error, e, true );
            throw new RuntimeException( message );
        }
    }

}
