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

package org.apache.avalon.merlin.impl;

import java.net.URL;

import javax.management.NotificationBroadcasterSupport;
import javax.management.AttributeChangeNotification;

import org.apache.avalon.merlin.Kernel;
import org.apache.avalon.merlin.KernelCriteria;
import org.apache.avalon.merlin.KernelException;
import org.apache.avalon.merlin.KernelRuntimeException;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.activation.appliance.Composite;
import org.apache.avalon.activation.appliance.impl.AbstractBlock;

import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.util.StringHelper;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

/**
 * Implementation of the default Merlin Kernel.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1.2.2 $ $Date: 2004/01/07 16:07:17 $
 */
public class DefaultKernel extends NotificationBroadcasterSupport 
  implements Kernel, DefaultKernelMBean
{
    //--------------------------------------------------------------
    // static
    //--------------------------------------------------------------

    private static final String INITIALIZING = "initializing";
    private static final String INITIALIZED = "initialized";
    private static final String STARTING = "starting";
    private static final String COMPOSITION = "model composition";
    private static final String ASSEMBLY = "model assembly";
    private static final String DEPLOYMENT = "block deployment";
    private static final String STARTED = "started";
    private static final String STOPPING = "stopping";
    private static final String DECOMMISSIONING = "decommissioning";
    private static final String DISSASSEMBLY = "dissassembly";
    private static final String BLOCK_DISPOSAL = "block disposal";
    private static final String STOPPED = "stopped";

    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final Logger m_logger;

    private final KernelCriteria m_criteria;

    private final SystemContext m_context;

    private final ContainmentModel m_model;

    private final DefaultState m_self = new DefaultState();

    private final DefaultState m_start = new DefaultState();

    //--------------------------------------------------------------
    // mutable state
    //--------------------------------------------------------------

    private String m_stateString = INITIALIZING;

    private long m_stateChangeSequenceId = 0;

    private Block m_application;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new Merlin Kernel.
    * @param logger the assigned logging channel
    * @param criteria the kernel creation criteria
    * @param system the system block
    * @param mosdel the application model
    * @exception KernelException if a kernel creation error occurs
    */
    public DefaultKernel( 
      final Logger logger,
      final KernelCriteria criteria, 
      final SystemContext context,
      final ContainmentModel model ) throws KernelException
    {
        if( logger == null ) 
          throw new NullPointerException( "logger" );
        if( criteria == null ) 
          throw new NullPointerException( "criteria" );
        if( context == null ) 
          throw new NullPointerException( "context" );
        if( model == null ) 
          throw new NullPointerException( "model" );

        m_criteria = criteria;
        m_context = context;
        m_model = model;
        m_logger = logger;

        setState( INITIALIZED );

        if( getLogger().isDebugEnabled() )
        {
            int count = 
              m_model.getModels().length;
            if( count == 0 )
            {
                getLogger().debug( "kernel established" );
            }
            else
            {
                getLogger().debug( "kernel established (" + count + ")" );
            }
        }
    }

    //--------------------------------------------------------------
    // DefaultKernelMBean
    //--------------------------------------------------------------

    /**
     * Return the state of the kernel.
     * @return a string representing the kernel state
     */
     public String getKernelState()
     {
         return m_stateString;
     }

    /**
     * Return an approximation to the total amount of memory currently 
     * available for future allocated objects, measured in bytes.
     * @return the number of bytes of estimated free memory
     */
    public long getMemoryFree()
    {
        return Runtime.getRuntime().freeMemory();
    }

   /**
    * Returns the total amount of memory in the Java virtual machine. The value 
    * returned by this method may vary over time, depending on the host environment. 
    *
    * @return the total amount of memory currently available for current and future 
    *    objects, measured in bytes.
    */
    public long getMemoryTotal()
    {
        return Runtime.getRuntime().totalMemory();
    }

   /**
    * Return the percentage of free memory available.
    * @return the free memory percentage
    */
    public int getMemoryVariableRatio()
    {
        return (int) ((Runtime.getRuntime().freeMemory() * 100) / 
          Runtime.getRuntime().totalMemory());
    }

   /**
    * Return the number of active threads.
    * @return the active thread count
    */
    public int getThreadCount()
    {
        return Thread.activeCount();
    }

   /**
    * Return the root directory to the shared repository.
    * @return the avalon home root repository directory
    */
    public String getRepositoryDirectory()
    {
        return m_criteria.getRepositoryDirectory().toString();
    }

   /**
    * Return the root directory to the merlin installation
    * @return the merlin home directory
    */
    public String getHomePath()
    {
        return m_criteria.getHomeDirectory().toString();
    }

   /**
    * Return the root directory to the merlin system repository
    * @return the merlin system repository directory
    */
    public String getSystemPath()
    {
        return m_criteria.getSystemDirectory().toString();
    }

   /**
    * Return the root directory to the merlin configurations
    * @return the merlin configuration directory
    */
    public String getConfigPath()
    {
        return m_criteria.getConfigDirectory().toString();
    }

   /**
    * Return the url to the kernel confiuration
    * @return the kernel configuration url
    */
    public String getKernelPath()
    {
        return m_criteria.getKernelURL().toString();
    }

   /**
    * Return the working client directory.
    * @return the working directory
    */
    public String getWorkingPath()
    {
        return m_criteria.getWorkingDirectory().toString();
    }

   /**
    * Return the temporary directory.
    * @return the temp directory
    */
    public String getTempPath()
    {
        return m_criteria.getTempDirectory().toString();
    }

   /**
    * Return the context directory from which relative 
    * runtime home directories will be established for 
    * components referencing urn:avalon:home
    *
    * @return the working directory
    */
    public String getContextPath()
    {
        return m_criteria.getContextDirectory().toString();
    }

   /**
    * Return the anchor directory to be used when resolving 
    * library declarations in classload specifications.
    *
    * @return the anchor directory
    */
    public String getAnchorPath()
    {
        return m_criteria.getAnchorDirectory().toString();
    }

   /**
    * Return info generation policy.  If TRUE the parameters 
    * related to deployment will be listed on startup. 
    *
    * @return the info policy
    */
    public boolean isInfoEnabled()
    {
        return m_criteria.isInfoEnabled();
    }

   /**
    * Return debug policy.  If TRUE all logging channels will be 
    * set to debug level (useful for debugging).
    *
    * @return the debug policy
    */
    public boolean isDebugEnabled()
    {
        return m_criteria.isDebugEnabled();
    }

    //--------------------------------------------------------------
    // Kernel
    //--------------------------------------------------------------

   /**
    * Return the block matching the supplied path.
    * @param path an appliance path
    * @return the corresponding appliance
    */
    public Appliance locate( String path ) throws KernelException
    {
        if( null == m_application )
        {
            throw new IllegalStateException( "not-started" );
        }

        try
        {
            return m_application.locate( path );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to resolve appliance relative to the path: [" 
              + path + "].";
            throw new KernelException( error, e );
        }
    }

   /**
    * Return the root application block.
    * @return the application containment block
    */
    public Block getBlock()
    {
        return m_application;
    }

    //--------------------------------------------------------------
    // DefaultKernel
    //--------------------------------------------------------------

   /**
    * Initiate the establishment of the root container.
    * @exception Exception if a startup error occurs
    */
    public void startup() throws Exception
    {
        //
        // instantiate the runtime root application block
        //

        synchronized( m_self )
        {
            if( m_self.isEnabled() ) return;
            setState( ASSEMBLY );
            try
            {
                getLogger().debug( "application assembly" );
                m_model.assemble();
            }
            catch( Throwable e )
            {
                final String error = 
                  "Application assembly failure.";
                throw new KernelException( error, e );
            }

            try
            {
                m_application = 
                  AbstractBlock.createRootBlock( m_context, m_model );
                setState( INITIALIZED );
            }
            catch( Throwable e )
            {
                final String error = 
                  "Application establishment failure.";
                throw new KernelException( error, e );
            }

            Throwable cause = null;
            setState( DEPLOYMENT );
            try
            {
                getLogger().debug( "application deployment" );
                m_application.deploy();
                m_self.setEnabled( true );
            }
            catch( Throwable e )
            {
                setState( INITIALIZED );
                cause = e;
                final String error = 
                  "Application deployment failure.";
                throw new KernelException( error, e );
            }
            finally
            {
                if( cause != null )
                {
                    shutdown();
                }
                else if( !m_criteria.isServerEnabled() )
                {
                    setState( STARTED );
                    // TODO: add pause parameter
                    shutdown();
                }
                else
                {
                    setState( STARTED );
                }
            }
        }
    }

   /**
    * Shutdown the kernel during which orderly shutdown of all
    * installed blocks is undertaken.
    */
    public void shutdown()
    {
        synchronized( m_self )
        {
            if( !m_self.isEnabled() ) return;

            setState( STOPPING );


            if( m_application != null )
            {
                try
                {
                    setState( DECOMMISSIONING );
                    m_application.decommission();
                }
                catch( Throwable e )
                {
                    if( getLogger().isWarnEnabled() )
                    {
                        final String error =
                          "Ignoring block decommissioning error.";
                        getLogger().warn( error, e );
                    }
                }

                try
                {
                    setState( DISSASSEMBLY );
                    getLogger().info( "dissassembly phase" );
                    m_model.disassemble();
                }
                catch( Throwable e )
                {
                    if( getLogger().isWarnEnabled() )
                    {
                        final String error =
                          "Ignoring application dissassembly error.";
                        getLogger().warn( error, e );
                    }
                }
            }

            if( getLogger().isDebugEnabled() )
            {
                int n = Thread.activeCount();
                getLogger().debug( "active threads (" + n + ")" );
            }

            setState( STOPPED );
            m_self.setEnabled( false );
        }
    }

    //--------------------------------------------------------------
    // internal
    //--------------------------------------------------------------

    /**
     * Set the state of the kernel.  The method also triggers the 
     * emmission of a attribute change notification containing the 
     * old and new state value.
     *
     * @param state a string representing the new kernel state
     */
     private void setState( String state )
     {
         if( m_stateString.equals( state ) ) return;
         getLogger().debug( "state: " + state );
         String old = m_stateString;
         m_stateString = state;
         long id = m_stateChangeSequenceId++;
         AttributeChangeNotification notification = 
           new AttributeChangeNotification( 
             this, id, System.currentTimeMillis(),
             "State change", "state", "string", old, state );
         sendNotification( notification );
     }

    private class DefaultState
    {
        private boolean m_enabled = false;

       /**
        * Return the enabled state of the state.
        * @return TRUE if the state has been enabled else FALSE
        */
        public boolean isEnabled()
        {
            return m_enabled;
        }

       /**
        * Set the enabled state of the state.
        * @param enabled the enabled state to assign
        */
        public void setEnabled( boolean enabled )
        {
            m_enabled = enabled;
        }
    }

    private Logger getLogger()
    {
        return m_logger;
    }
}
