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

package org.apache.avalon.activation.appliance.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.ApplianceException;
import org.apache.avalon.activation.appliance.ApplianceRuntimeException;
import org.apache.avalon.activation.appliance.AssemblyException;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.activation.appliance.BlockContext;
import org.apache.avalon.activation.appliance.Engine;
import org.apache.avalon.activation.appliance.NoProviderDefinitionException;

import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.logging.LoggingManager;

import org.apache.avalon.composition.event.CompositionEvent;
import org.apache.avalon.composition.event.CompositionEventListener;

import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.StageModel;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

/**
 * The AbstractBlock is responsible for the management 
 * of the assembly of the subsidiary appliances, the coordination
 * of the deployment, decommissioning and eventual dissassembly of 
 * contained appliances, and the overall management of a containment 
 * context.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.10.2.4 $ $Date: 2004/01/07 16:07:17 $
 */
public abstract class AbstractBlock extends AbstractAppliance 
  implements Block, CompositionEventListener
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    /**
     * Create a root containment block.
     * @param services the service context
     * @param model the root containment model
     * @return the appliance
     */
    public static Block createRootBlock( 
      SystemContext system, ContainmentModel model ) throws Exception
    {
        if( null == system ) 
        {
            throw new NullPointerException( "system" );
        }
        if( null == model ) 
        {
            throw new NullPointerException( "model" );
        }

        Logger logger = 
          system.getLoggingManager().getLoggerForCategory( "" );
        BlockContext context = new DefaultBlockContext(
          logger, model, system, null );
        return new CompositeBlock( context );
    }

    //-------------------------------------------------------------------
    // immmutable state
    //-------------------------------------------------------------------

    private final BlockContext m_context;

    private final DefaultState m_deployment = new DefaultState();

    private final DefaultState m_self = new DefaultState();

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

   /**
    * Creation of a new abstract block.
    *
    * @param context the block context
    * @exception ApplianceException if a block creation error occurs
    */
    AbstractBlock( BlockContext context )
    {
        super( context.getLogger(), context.getContainmentModel() );

        m_context = context;

        m_self.setEnabled( true );

        ContainmentModel model = m_context.getContainmentModel();
        synchronized( model )
        {
            model.addCompositionListener( this );
        }
    }

    //-------------------------------------------------------------------
    // Block
    //-------------------------------------------------------------------

   /**
    * Return the containment metamodel associated with the block.
    * @return the containment model
    */
    public ContainmentModel getContainmentModel() 
    {
        return m_context.getContainmentModel();
    }

    //-------------------------------------------------------------------
    // CompositionEventListener
    //-------------------------------------------------------------------

    /**
     * Notify the listener that a model has been added to 
     * a source containment model.
     *
     * @param event the containment event raised by the 
     *    source containment model
     */
    public void modelAdded( CompositionEvent event )
    {
        getLogger().debug( "event/addition: " + event );
    }

    /**
     * Notify the listener that a model has been removed from 
     * a source containment model.
     *
     * @param event the containment event raised by the 
     *    source containment model
     */
    public void modelRemoved( CompositionEvent event )
    {
        getLogger().debug( "event/removal: " + event );
    }

    //-------------------------------------------------------------------
    // Engine
    //-------------------------------------------------------------------

   /**
    * Return an appliance relative to a supplied model.
    * @param model the meta model
    * @return the appliance
    * @exception ApplianceException if an error occurs during appliance
    *    resolution
    */
    public Appliance locate( DeploymentModel model )
    {
        return getAppliance( model );
    }

    private Appliance getAppliance( final DeploymentModel model )
    {
        return getAppliance( model, true );
    }

    private Appliance getAppliance( final DeploymentModel model, boolean create )
    {
        Appliance appliance = (Appliance) model.getHandler();
        if( null != appliance )
        {
            return appliance;
        }
        else if( create )
        {
            appliance = createAppliance( model );
            model.setHandler( appliance );
            return appliance;
        }
        else
        {
            return null;
        }
    }

   /**
    * Return an appliance relative to a specific path.
    * @param source the appliance path
    * @return the appliance
    * @exception IllegalArgumentException if the supplied path is invalid
    * @exception ApplianceException if an error occurs during appliance
    *    resolution
    */
    public Appliance locate( String source )
    {
        DeploymentModel model =
          getContainmentModel().getModel( source );
        return getAppliance( model );
    }

    //-------------------------------------------------------------------
    // Deployable
    //-------------------------------------------------------------------

   /**
    * Deploy the appliance. If the deployment policy is startup
    * an initial instance of a component will be deployed.
    *
    * @exception Exception if a deployment error occurs
    */
    public void deploy() throws Exception
    {
        if( !m_context.getContainmentModel().isAssembled() )
        {
            throw new IllegalStateException( "assembly" );
        }

        synchronized( m_deployment )
        {
            if( m_deployment.isEnabled() )
            {
                return;
            }

            //
            // get the model's startup sequence and from this
            // we locate the appliances within the scope of this container
            // and deploy them
            //

            ContainmentModel model = getContainmentModel();
            DeploymentModel[] startup = model.getStartupGraph();
            long timeout = model.getDeploymentTimeout();

            ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
            ClassLoader classloader = model.getClassLoaderModel().getClassLoader();
            Thread.currentThread().setContextClassLoader( classloader );

            Deployer deployer = 
              new Deployer( getLogger().getChildLogger( "deployer" ) );

            try
            {
                for( int i=0; i<startup.length; i++ )
                {
                    final DeploymentModel child = startup[i];
                    final Appliance appliance = getAppliance( child );
                    deployer.deploy( appliance, timeout );
                }
            }
            finally
            {
                deployer.dispose();
                // restore the Old ContextClassloader.
                Thread.currentThread().setContextClassLoader( oldCL );
            }

            m_deployment.setEnabled( true );
        }
    }

   /**
    * Decommission the block.  Under the decommissioning phase, 
    * all active components will be taken down.
    */
    public void decommission()
    {
        synchronized( m_deployment )
        {
            if( !m_deployment.isEnabled() ) return;

            ContainmentModel model = getContainmentModel();
            DeploymentModel[] shutdown = model.getShutdownGraph();
            long timeout = model.getDeploymentTimeout();

            if( getLogger().isDebugEnabled() )
            {
                String message = "decommissioning";
                getLogger().debug( message );
            }

            for( int i=0; i<shutdown.length; i++ )
            {
                final DeploymentModel child = shutdown[i];
                final Appliance appliance = getAppliance( child, false );
                if( null != appliance )
                {
                    appliance.decommission();
                }
            }

            m_deployment.setEnabled( false );
        }
    }

    //-------------------------------------------------------------------
    // Home
    //-------------------------------------------------------------------

    /**
     * Resolve a object to a value.
     *
     * @return the resolved object
     * @throws Exception if an error occurs
     */
    public abstract Object resolve() throws Exception;

    /**
     * Release an object
     *
     * @param instance the object to be released
     */
    public abstract void release( Object instance );

    //-------------------------------------------------------------------
    // Disposable
    //-------------------------------------------------------------------

    public void dispose()
    {
        synchronized( m_self )
        {
            if( !m_self.isEnabled() ) return;

            getLogger().debug( "disposal phase" );
            getContainmentModel().removeCompositionListener( this );
            getContainmentModel().setHandler( null );

            m_self.setEnabled( false );
        }
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

    /**
     * Create a new appliance.
     * @param model the component model
     * @return the appliance
     */
    public Appliance createAppliance( DeploymentModel model )
    {
        Appliance appliance = null;

        final String path = model.getPath() + model.getName();
        final SystemContext services = m_context.getSystemContext();
        final LoggingManager logging = services.getLoggingManager();

        if( model instanceof ComponentModel )
        {
            getLogger().debug( "creating appliance: " + path );
            ComponentModel deployment = (ComponentModel) model;
            CategoriesDirective categories = deployment.getCategories();
            if( categories != null )
            {
                logging.addCategories( path, categories );
            }
            Logger logger = logging.getLoggerForCategory( path );
            appliance = new DefaultAppliance( logger, deployment, this );
        }
        else if( model instanceof ContainmentModel )
        {
            getLogger().debug( "creating block: " + path );
            ContainmentModel containment = (ContainmentModel) model;
            CategoriesDirective categories = containment.getCategories();
            if( categories != null )
            {
                logging.addCategories( path, categories );
            }

            Logger logger = logging.getLoggerForCategory( path );

            BlockContext context = 
              new DefaultBlockContext(
                logger, containment, services, this );

            appliance = new CompositeBlock( context );
        }
        else
        {
            final String error =
              "Unrecognized model: " + model.getClass().getName();
            throw new IllegalArgumentException( error );
        }

        return appliance;
    }

    //-------------------------------------------------------------------
    // Object
    //-------------------------------------------------------------------

    public String toString()
    {
        return "block:" + getModel().getQualifiedName();
    }
}
