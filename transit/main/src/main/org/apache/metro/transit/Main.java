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
import java.util.Properties;


/**
 * CLI hander for the cache package.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
public final class Main
{
    //------------------------------------------------------------------
    // static 
    //------------------------------------------------------------------

   /**
    * The default controller artifact.
    */
    private static final String CONTROLLER_VALUE = "@CONTROLLER_ARTIFACT_URI@";

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
        // create an inital context using the metro.initial.* properties
        //

        InitialContextFactory factory = new InitialContextFactory();
        InitialContext context = factory.createInitialContext();
        Repository repository = new StandardLoader( context );

        //
        // get the artifact that references the plugin controller
        //

        Artifact artifact = getPluginArtifact();
        if( context.getDebugPolicy() )
        {
            context.getMonitor().debug( 
              InitialContext.CONTROLLER_KEY
              + " : "
              + artifact );
        }

        //
        // load the controller
        //

        try
        {
            Object[] params = new Object[]{ args };
            ClassLoader classloader = Main.class.getClassLoader();
            m_object = repository.getPlugin( classloader, artifact, params );
            if( m_object instanceof Runnable )
            {
                 Thread thread = new Thread( (Runnable) m_object );
                 thread.start();
                 setShutdownHook( thread ); 
            }
        }
        catch( IOException e )
        {
            // already handled
        }
        catch( Throwable e )
        {
            e.printStackTrace();
        }
    }

    private Artifact getPluginArtifact()
    {
        Properties properties = InitialContextFactory.PROPERTIES;
        String spec = System.getProperty( InitialContext.CONTROLLER_KEY );
        if( null != spec )
        {
            return Artifact.createArtifact( spec );
        }
        spec = properties.getProperty( InitialContext.CONTROLLER_KEY );
        if( null != spec )
        {
            return Artifact.createArtifact( spec );
        }
        return Artifact.createArtifact( CONTROLLER_VALUE );
    }

   /**
    * Create a shutdown hook that will trigger shutdown of the supplied plugin.
    * @param thread the application thread
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

