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

package org.apache.avalon.merlin.tools;

import java.io.File;
import java.net.URL;

import org.apache.avalon.repository.Repository;
import org.apache.avalon.repository.impl.DefaultFileRepository;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.merlin.kernel.Kernel;
import org.apache.avalon.merlin.kernel.KernelException;
import org.apache.avalon.merlin.kernel.impl.DefaultKernel;
import org.apache.avalon.merlin.kernel.impl.DefaultKernelContext;


/**
 * Deploy a set of components using Merlin.
 *
 * @author mcconnell@apache.org
 */

public class MerlinBean
{
    //-----------------------------------------------------
    // state
    //-----------------------------------------------------

   /**
    * The merlin system directory.
    */
    private File m_system;

   /**
    * The target jar file, simulated deployment directory or 
    * block file to be deployed.
    */
    private File m_target;

   /**
    * The local repository directory from which the resource
    * repository will be created.  Typically this will refer to 
    * Maven local repository.
    */
    private File m_repository;

   /**
    * The configuration targets file to be used.
    */
    private File m_conf;

   /**
    * The kernel configuration file.
    */
    private File m_kernel;

   /**
    * The directory against which extension directories are resolved
    * if extension direcctories are declared in the classloader.
    */
    private File m_anchor;

   /**
    * The local working directory.  Typically corresponds to the 
    * Maven ${badedir} variable.
    */
    private File m_home;

   /**
    * The debug flag.
    */
    private String m_debug;

   /**
    * Execution model flag.
    */
    private boolean m_execute = true;

   /**
    * Info flag.
    */
    private boolean m_info = false;

   /**
    * Shutdown pause.
    */
    private int m_wait = 0; // not used

    //-----------------------------------------------------
    // bean pattern setters
    //-----------------------------------------------------

   /**
    * Set the repository.
    * @param repository the repository directory
    */
    public void setRepository( File repository ) 
    {
        m_repository = repository;
    }

   /**
    * Set the pause between deployment and decommissioning when 
    * executing in non-server model.
    */
    public void setWait( int value ) // not used
    {
        m_wait = value;
    }

    public void setConfig( File conf ) 
    {
        m_conf = conf;
    }

    public void setKernel( File kernel ) 
    {
        m_kernel = kernel;
    }

   /**
    * Set the target jar file.
    * @param target the target jar file to execute
    */
    public void setTarget( File target ) 
    {
        m_target = target;
    }

   /**
    * Set the working directory.
    */
    public void setHome( File home ) 
    {
        m_home = home;
    }

   /**
    * Set the debug flag.
    */
    public void setDebug( String priority ) 
    {
        m_debug = priority;
    }

   /**
    * Set the option jar file extensions directory anchor.
    */
    public void setAnchor( File anchor ) 
    {
        m_anchor = anchor;
    }

   /**
    * Set the deployment policy.
    */
    public void setDeploy( boolean value ) 
    {
        m_execute = value;
    }

   /**
    * Set the deployment policy.
    */
    public void setInfo( boolean value ) 
    {
        m_info = value;
    }

    //-----------------------------------------------------
    // implementation
    //-----------------------------------------------------

   /**
    * Get the target jar file as a URL
    * @return the target url
    */
    private URL getTarget() throws Exception
    {
        return m_target.toURL();
    }

   /**
    * Get the repository directory file.
    * @return the repository directory
    */
    private File getRepositoryDirectory()
    {
        return m_repository;
    }

   /**
    * Get the configuration source as a URL
    * @return the config url
    */
    private URL getConfigurationURL() throws Exception
    {
        if( m_conf != null )
        {
            if( m_conf.exists() ) return m_conf.toURL();
        }
        return null;
    }

   /**
    * The intival of time to wait before shutting down the kernel.  
    */
    public int getWait() // not used
    {
        return m_wait;
    }

   /**
    * Get the kernel source as a URL or null if not declared.
    * @return the kernel url
    */
    private URL getKernelURL() throws Exception
    {
        if( m_kernel != null )
        {
            if( m_kernel.exists() ) return m_kernel.toURL();
        }
        return null;
    }

   /**
    * Get the base directory.
    * @return the base directory
    */
    private File getHomeDirectory()
    {
        return m_home;
    }

   /**
    * Return the execute mode flag.
    * @return the flag
    */
    public boolean getExecuteFlag( ) 
    {
        return m_execute;
    }

   /**
    * Return the optional extensions anchor directory.
    * @return the flag
    */
    public File getAnchorDirectory() 
    {
        return m_anchor;
    }

   /**
    * Return the debug flag.
    * @return the flag
    */
    public boolean getInfoFlag()
    {
        return m_info;
    }

   /**
    * Return the debug flag.
    * @return the flag
    */
    public boolean getDebugFlag()
    {
        return m_debug.equalsIgnoreCase( "true" );
    }

   /**
    * Establish the merlin kernel.
    */
    public void doExecute() throws Exception
    {
        DefaultKernelContext context = null;

        try
        {
            Repository repository = new DefaultFileRepository( m_repository );
            context = 
              new DefaultKernelContext( 
                repository, 
                getRepositoryDirectory(), 
                getAnchorDirectory(), 
                getHomeDirectory(), 
                getKernelURL(), 
                new URL[]{ getTarget() },
                getConfigurationURL(), 
                getExecuteFlag(), 
                getInfoFlag(),
                getDebugFlag() 
              );
        }
        catch( Throwable e )
        {
            final String error = 
              "Could not establish the kernel context.";
            String message = ExceptionHelper.packException( error, e );
            System.err.println( message );
            throw new KernelException( message, e );
        }

        Kernel kernel = null;
        try
        {
            kernel = new DefaultKernel( context );
            setShutdownHook( kernel );
        }
        catch( Throwable e )
        {
            final String error = 
              "Could not establish the kernel.";
            String message = ExceptionHelper.packException( error, e );
            System.err.println( message );
            throw new KernelException( message, e );
        }

        try
        {
            kernel.startup();
        }
        catch( Throwable e )
        {
            final String error = 
              "Kernel startup failure.";
            String message = ExceptionHelper.packException( error, e );
            System.err.println( message );
            throw new KernelException( message, e );
        }
    }

   /**
    * Create a shutdown hook that will trigger shutdown of the supplied kernel.
    * @param kernel the kernel to be shutdown
    */
    private void setShutdownHook( final Kernel kernel )
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

