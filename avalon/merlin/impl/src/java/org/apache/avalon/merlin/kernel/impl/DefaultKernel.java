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

package org.apache.avalon.merlin.kernel.impl;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import javax.management.NotificationBroadcasterSupport;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.avalon.merlin.kernel.Kernel;
import org.apache.avalon.merlin.kernel.KernelContext;
import org.apache.avalon.merlin.kernel.KernelException;
import org.apache.avalon.merlin.kernel.KernelRuntimeException;
import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.activation.appliance.Engine;
import org.apache.avalon.activation.appliance.Composite;
import org.apache.avalon.activation.appliance.ServiceContext;
import org.apache.avalon.activation.appliance.UnknownServiceException;
import org.apache.avalon.activation.appliance.impl.DefaultBlock;
import org.apache.avalon.activation.appliance.impl.DefaultServiceContext;
import org.apache.avalon.composition.data.TargetDirective;
import org.apache.avalon.composition.data.CategoryDirective;
import org.apache.avalon.composition.data.ContainmentProfile;
import org.apache.avalon.composition.data.builder.XMLContainmentProfileCreator;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.logging.LoggingDescriptor;
import org.apache.avalon.composition.logging.TargetDescriptor;
import org.apache.avalon.composition.logging.TargetProvider;
import org.apache.avalon.composition.logging.impl.DefaultLoggingManager;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.ModelFactory;
import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.model.ContainmentContext;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.impl.DefaultSystemContext;
import org.apache.avalon.composition.model.impl.DefaultModelFactory;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.util.ExceptionHelper;
import org.apache.avalon.composition.util.StringHelper;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.Version;
import org.apache.excalibur.mpool.PoolManager;

/**
 * Implementation of the default Merlin Kernel.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2003/10/12 17:12:45 $
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

    private final KernelContext m_context;

    private final ContainmentModel m_model;

    private final DefaultState m_self = new DefaultState();

    private final DefaultState m_start = new DefaultState();

    private Block m_block;

    private String m_stateString = INITIALIZING;

    private long m_stateChangeSequenceId = 0;

    private final MBeanServer m_server;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new Merlin Kernel.
    * @param context the kernel context
    * @exception KKernelException if a kernel creation error occurs
    */
    public DefaultKernel( final KernelContext context ) 
      throws KernelException
    {
        this( null, context );
    }

   /**
    * Creation of a new Merlin Kernel.
    * @param context the kernel context
    * @exception KernelException if a kernel creation error occurs
    */
    public DefaultKernel( final MBeanServer server, final KernelContext context ) 
      throws KernelException
    {
        if( context == null ) throw new NullPointerException( "context" );

        m_context = context;
        m_server = server;

        if( m_server != null ) try
        {
            ObjectName name = new ObjectName( "merlin.kernel:type=kernel" );
            m_server.registerMBean( this, name );
        }
        catch( Throwable e )
        {
            final String error =
              "\nInternal error during kernel registration.";
            throw new KernelException( error, e );
        }

        //
        // create the root block into which we install application blocks
        //

        getLogger().debug( "creating root containment context" );
        ContainmentContext contaiment = context.getContainmentContext();
        Thread.currentThread().setContextClassLoader( contaiment.getClassLoader() );

        getLogger().debug( "construction phase" );

        try
        {
            m_model = context.getModelFactory().createContainmentModel( contaiment );
        }
        catch( Throwable e )
        {
            final String error = 
              "Internal error while build default containment model.";
            throw new KernelException( error, e );
        }

        //
        // install any block declared within the kernel context
        //

        getLogger().debug( "install phase" );
        URL[] urls = context.getInstallSequence();
        for( int i=0; i<urls.length; i++ )
        {
            URL url = urls[i];
            if( getLogger().isInfoEnabled() )
            {
                getLogger().info( 
                  "installing: " 
                  + StringHelper.toString( url ) );
            }
            try
            {
                m_model.addModel( url );
            }
            catch( Throwable e )
            {
                final String error = 
                  "Block install failure: " + url;
                throw new KernelException( error, e );
            }
        }

        //
        // apply any customization to the models using a supplied 
        // targets directive
        //

        getLogger().debug( "customization phase" );
        TargetDirective[] targets = context.getTargetDirectives();
        for( int i=0; i<targets.length; i++ )
        {
            TargetDirective target = targets[i];
            final String path = target.getPath();
            Object model = m_model.getModel( path );
            if( model != null )
            {
                if( model instanceof DeploymentModel )
                {
                    DeploymentModel deployment = (DeploymentModel) model;
                    if( target.getConfiguration() != null )
                    {
                        deployment.setConfiguration( target.getConfiguration() );
                    }
                    if( target.getCategoriesDirective() != null )
                    {
                        deployment.setCategories( target.getCategoriesDirective() );
                    }
                }
                else if( model instanceof ContainmentModel )
                {
                    ContainmentModel containment = (ContainmentModel) model;
                    if( target.getCategoriesDirective() != null )
                    {
                        containment.setCategories( target.getCategoriesDirective() );
                    }
                }
            }
            else
            {
                final String warning = 
                  "Ignoring target directive as the path does not refer to a known component: " 
                  + path;
                getLogger().warn( warning );
            }
        }

        setState( INITIALIZED );
        getLogger().debug( "kernel established" );
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
    * Return the runtime repository path.
    * @return the repository path
    */
    public String getRepositoryDirectoryPath()
    {
        return m_context.getRepository().getLocation();
    }

   /**
    * Return the home path
    * @return the home path (possibly null)
    */
    public String getHomeDirectoryPath()
    {
        return m_context.getHomePath().toString();
    }

   /**
    * Return the temporary directory path
    * @return the path (possibly null)
    */
    public String getTempDirectoryPath()
    {
        if( m_context.getTempPath() != null )
        {
            return m_context.getTempPath().toString();
        }
        return "";
    }

   /**
    * Return the library path
    * @return the path (possibly null)
    */
    public String getLibraryDirectoryPath()
    {
        if( m_context.getLibraryPath() == null )
        {
            return getHomeDirectoryPath();
        }
        else
        {
            return m_context.getLibraryPath().toString();
        }
    }

    //--------------------------------------------------------------
    // Kernel
    //--------------------------------------------------------------

   /**
    * Return the root containment model.
    * @return the containment model
    */
    public ContainmentModel getContainmentModel()
    {
        return m_model;
    }

   /**
    * Return the block matching the supplied model.
    * @return the containment block
    */
    public Block getBlock( ContainmentModel model ) throws KernelException
    {
        if( !m_self.isEnabled() ) throw new IllegalStateException( "kernel" );
        try
        {
            return (Block) m_block.resolveAppliance( model.getQualifiedName() );
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to resolve block relative to the containment path: [" 
              + model.getPath() + "].";
            throw new KernelRuntimeException( error, e );
        }
    }

   /**
    * Return the root block.
    * @return the containment block
    */
    public Block getRootBlock()
    {
        if( m_block == null ) throw new IllegalStateException( "not-started" );
        return m_block;
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
        synchronized( m_self )
        {
            if( m_self.isEnabled() ) return;

            setState( STARTING );

            //
            // we have a model established and we now need to go though the process
            // of appliance establishment
            //

            setState( COMPOSITION );

            try
            {
                DefaultServiceContext services = new DefaultServiceContext();
                services.put( PoolManager.ROLE, m_context.getPoolManager() );
                services.put( LoggingManager.KEY, m_context.getLoggingManager() );
                if( m_server != null )
                {
                    services.put( Appliance.MBEAN_SERVER_KEY, m_server );
                }
                m_block = DefaultBlock.createRootBlock( services, m_model );
            }
            catch( Throwable e )
            {
                setState( INITIALIZED );
                final String error = 
                  "Composition failure.";
                throw new KernelException( error, e );
            }

            if( m_block instanceof Composite )
            {
                setState( ASSEMBLY );
                try
                {
                    getLogger().debug( "assembly phase" );
                    ((Composite)m_block).assemble();
                }
                catch( Throwable e )
                {
                    setState( INITIALIZED );
                    final String error = 
                      "Assembly failure.";
                    throw new KernelException( error, e );
                }
            }

            Throwable cause = null;
            setState( DEPLOYMENT );
            try
            {
                getLogger().debug( "deployment phase" );
                m_block.deploy();
                m_self.setEnabled( true );
            }
            catch( Throwable e )
            {
                setState( INITIALIZED );
                cause = e;
                final String error = 
                  "Deployment failure.";
                throw new KernelException( error, e );
            }
            finally
            {
                if( cause != null )
                {
                    shutdown();
                }
                else if( !m_context.getServerFlag() )
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


            if( m_block != null )
            {
                try
                {
                    setState( DECOMMISSIONING );
                    m_block.decommission();
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
                    if( m_block instanceof Composite )
                    {
                        setState( DISSASSEMBLY );
                        getLogger().info( "dissassembly phase" );
                        ((Composite)m_block).disassemble();
                    }
                }
                catch( Throwable e )
                {
                    if( getLogger().isWarnEnabled() )
                    {
                        final String error =
                          "Ignoring block dissassembly error.";
                        getLogger().warn( error, e );
                    }
                }

                setState( BLOCK_DISPOSAL );
                try
                {
                    if( m_block instanceof Disposable )
                    {
                        getLogger().info( "disposal phase" );
                        ((Disposable)m_block).dispose();
                    }
                }
                catch( Throwable e )
                {
                    if( getLogger().isWarnEnabled() )
                    {
                        final String error =
                          "Ignoring block disposal error.";
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
        return m_context.getKernelLogger();
    }
}
