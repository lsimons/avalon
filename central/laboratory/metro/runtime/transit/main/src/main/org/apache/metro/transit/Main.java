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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * CLI hander for the cache package.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
public class Main
{
    //------------------------------------------------------------------
    // static 
    //------------------------------------------------------------------

    static final String CONTROLLER_VALUE =   "@CONTROLLER_ARTIFACT_URI@";

   /**
    * Command line entry point for the cache implementation that handles 
    * the establishment of an initial repository from which an application
    * profile can be loaded.
    *
    * @param args the command line arguments to be supplied to the target controller
    * @exception Exception if an unchecked error occurs during application deployment
    */
    public static final void main( final String[] args ) throws Exception
    {
        new Main( args );
    }

    // ------------------------------------------------------------------------
    // state
    // ------------------------------------------------------------------------

    private Object m_object;

    // ------------------------------------------------------------------------
    // constructor
    // ------------------------------------------------------------------------

    private Main( final String[] args ) throws Exception
    {
        //
        // get metro properties
        //

        File home = getApplicationHome();
        Properties properties = getApplicationProperties( home );
        DefaultInitialContext context = new DefaultInitialContext( home, properties );
        Repository repository = new StandardLoader( context );
        Artifact artifact = getPluginArtifact( properties );

        //
        // load property files to resolve the repository artifact
        // uri that we will load that will do the real on-line 
        // repository management
        //

        try
        {
            Object[] params = new Object[]{ args };
            ClassLoader classloader = Main.class.getClassLoader();
            Object object = repository.getPlugin( classloader, artifact, params );
            if( object instanceof Runnable )
            {
                 Thread thread = new Thread( (Runnable) object );
                 thread.start();
                 setShutdownHook( thread ); 
            }
        }
        catch( RepositoryException e )
        {
            // already handled
        }
        catch( Throwable e )
        {
            e.printStackTrace();
        }
    }

    private File getApplicationHome()
    {
        String metro = System.getProperty( InitialContext.HOME_KEY );
        if( null != metro )
           return new File( metro );
        File user = getUserDirectory();
        return new File( user, "." + InitialContext.GROUP );
    }

    private File getUserDirectory()
    {
        return new File( System.getProperty( "user.home" ) );
    }

    private Artifact getPluginArtifact( Properties properties )
    {
        String spec = System.getProperty( InitialContext.CONTROLLER_KEY );
        if( null != spec )
           return Artifact.createArtifact( spec );
        spec = properties.getProperty( InitialContext.CONTROLLER_KEY );
        if( null != spec )
           return Artifact.createArtifact( spec );
        return Artifact.createArtifact( CONTROLLER_VALUE );
    }

    private File getWorkingDirectory()
    {
        return new File( System.getProperty( "user.dir" ) );
    }

   /**
    * Load all properties file from the standard locations.  Standard 
    * locations in priority order include:
    * <ul>
    * <li>${user.dir}/metro.properties</li>
    * <li>${user.home}/metro.properties</li>
    * <li>${metro.home}/metro.properties</li>
    * </ul>
    * @param home the metro home directory
    * @return the aggregated properties
    */
    private Properties getApplicationProperties( File home ) throws IOException
    {
        //
        // get ${metro.home}/metro.properties
        //

        Properties properties = new Properties();
        File homePreferenceFile = new File( home, InitialContext.PROPERTY_FILENAME );
        if( homePreferenceFile.exists() )
        {
            InputStream input = new FileInputStream( homePreferenceFile );
            properties.load( input );
        }

        //
        // get ${user.home}/metro.properties
        //

        File user = getUserDirectory();
        File userPreferenceFile = new File( user, InitialContext.PROPERTY_FILENAME );
        if( userPreferenceFile.exists() )
        {
            InputStream input = new FileInputStream( userPreferenceFile );
            properties.load( input );
        }

        //
        // get ${user.dir}/metro.properties
        //

        File dir = getWorkingDirectory();
        File dirPreferenceFile = new File( dir, InitialContext.PROPERTY_FILENAME );
        if( dirPreferenceFile.exists() )
        {
            InputStream input = new FileInputStream( dirPreferenceFile );
            properties.load( input );
        }

        return properties;
    }

   /**
    * Create a shutdown hook that will trigger shutdown of the supplied kernel.
    * @param kernel the kernel to be shutdown
    */
    private void setShutdownHook( final Thread thread )
    {
        //
        // Create a shutdown hook to trigger clean disposal of the
        // controller 
        //

        Runtime.getRuntime().addShutdownHook(
          new Thread()
          {
              public void run()
              {
                  try
                  {
                      thread.interrupt();
                  }
                  catch( Throwable e )
                  {
                      // ignore it
                  }
                  finally
                  {
                      System.runFinalization();
                  }
              }
          }
        );
    }
}

