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
import java.util.ArrayList;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * CLI hander for the cache package.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
public final class MainTask extends Task
{
    //------------------------------------------------------------------
    // static 
    //------------------------------------------------------------------

   /**
    * The default controller artifact.
    */
    private static final String CONTROLLER_VALUE = "@CONTROLLER_ARTIFACT_URI@";

    // ------------------------------------------------------------------------
    // state
    // ------------------------------------------------------------------------

    private Controller m_controller;

    private boolean m_init = false;

    private String m_target;

    // ------------------------------------------------------------------------
    // setters
    // ------------------------------------------------------------------------

    public void setTarget( String target )
    {
        m_target = target;
    }

    // ------------------------------------------------------------------------
    // Task
    // ------------------------------------------------------------------------

    public void init() throws BuildException
    {
        if( !m_init ) try
        {
            m_init = true;
            initMain();
        }
        catch( IOException ioe )
        {
             throw new BuildException( ioe );
        }
    }

    public void execute() throws BuildException
    {
        if( null != m_target )
        {
            ClassLoader classloader = Main.class.getClassLoader();
            try
            {
                String[] args = getArgs();
                Artifact artifact = Artifact.createArtifact( m_target );
                m_controller.deploy( artifact, args );
            }
            catch( ControllerException e )
            {
                //throw new BuildException( e );
            }
            catch( Throwable e )
            {
                throw new BuildException( e );
            }
        }
    }

    // ------------------------------------------------------------------------
    // internal
    // ------------------------------------------------------------------------

    private void initMain() throws IOException
    {
        //
        // get metro properties
        //

        Properties properties = InitialContextFactory.PROPERTIES;
        InitialContext context = new InitialContextFactory().createInitialContext();
        Repository repository = new StandardLoader( context );
        Artifact artifact = getPluginArtifact( properties );

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
            Object[] params = new Object[]{ new String[0] };
            ClassLoader classloader = Main.class.getClassLoader();
            Object object = repository.getPlugin( classloader, artifact, params );
            if( object instanceof Controller )
            {
                m_controller = (Controller) object;
                Thread thread = new Thread( (Runnable) m_controller );
                thread.start();
                setShutdownHook( thread );
            }
            else
            {
                final String error = 
                  "The artifact ["
                  + artifact
                  + "] is not a controller plugin.";
                throw new ControllerException( error );
            }
        }
        catch( Throwable e )
        {
            throw new BuildException( e );
        }
    }

    private String[] getArgs()
    {
        return new String[0];
    }

    private Artifact getPluginArtifact( Properties properties )
    {
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

