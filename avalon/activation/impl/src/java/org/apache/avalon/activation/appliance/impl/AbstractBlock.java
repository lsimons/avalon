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

package org.apache.avalon.activation.appliance.impl;

import java.net.URL;
import java.net.URLClassLoader;

import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.Permission;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.ApplianceException;
import org.apache.avalon.activation.appliance.ApplianceRuntimeException;
import org.apache.avalon.activation.appliance.AssemblyException;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.activation.appliance.Engine;
import org.apache.avalon.activation.appliance.NoProviderDefinitionException;

import org.apache.avalon.composition.event.CompositionEvent;
import org.apache.avalon.composition.event.CompositionListener;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ClassLoaderModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.StageModel;
import org.apache.avalon.composition.runtime.Commissionable;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.logging.data.CategoriesDirective;
import org.apache.avalon.logging.provider.LoggingManager;

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
 * @version $Revision: 1.20 $ $Date: 2004/02/07 06:06:30 $
 */
public abstract class AbstractBlock extends AbstractAppliance 
  implements Block, CompositionListener, Commissionable
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    /**
     * Create a root containment block.
     * @param model the root containment model
     * @return the appliance
     */
    public static Block createRootBlock( ContainmentModel model ) throws Exception
    {
        if( null == model ) 
        {
            throw new NullPointerException( "model" );
        }
        return new DefaultBlock( model );
    }

    //-------------------------------------------------------------------
    // immmutable state
    //-------------------------------------------------------------------

    private final ContainmentModel m_model;

    private final DefaultState m_deployment = new DefaultState();

    private final DefaultState m_self = new DefaultState();

    private final Engine m_engine;
    private final AccessControlContext  m_accessControlContext;
    private final boolean               m_secured;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

   /**
    * Creation of a new abstract block.
    *
    * @param context the block context
    * @exception ApplianceException if a block creation error occurs
    */
    AbstractBlock( ContainmentModel model, Engine engine )
    {
        super( model );
        ClassLoaderModel clmodel = model.getClassLoaderModel();
        if( model.isSecureExecutionEnabled() )
        {
            m_accessControlContext = createAccessControlContext( clmodel );
            m_secured = true;
            if( getLogger().isDebugEnabled() )
                getLogger().debug( 
                  "AccessControlContext created: " 
                  + m_accessControlContext );
        }
        else
        {
            m_accessControlContext = null;
            m_secured = false;
            if( getLogger().isDebugEnabled() )
                getLogger().debug( "Non-Secure Execution!" );
        }
        
        m_model = model;
        m_engine = engine;

        m_self.setEnabled( true );
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
        return m_model;
    }

    //-------------------------------------------------------------------
    // CompositionListener
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
    * Return an appliance relative to a specific path.
    * @param source the appliance path
    * @return the appliance
    * @exception IllegalArgumentException if the supplied does
    *    not refer to a know appliance
    * @exception ApplianceException if an error occurs during appliance
    *    resolution
    */
    public Appliance locate( String source )
    {
        DeploymentModel model =
          getContainmentModel().getModel( source );
        if( model == null )
        {
            final String error = 
              "Path [" + source + "] does not refer to a known appliance.";
            throw new IllegalArgumentException( error );
        }
        return locate( model );
    }

   /**
    * Return an appliance relative to a supplied model.
    * @param model the meta model
    * @return the appliance
    * @exception ApplianceException if an error occurs during appliance
    *    resolution
    */
    public Appliance locate( DeploymentModel model )
    {
        return getAppliance( model, true );
    }

    private Appliance getAppliance( final DeploymentModel model, boolean create )
    {
        if( null == model )
        {
            throw new NullPointerException( "model" );
        }

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

    protected AccessControlContext getAccessControlContext()
    {
        return m_accessControlContext;
    }

    /**
     * Creates the AccessControlContext based on the Permissons granted
     * in the ContainmentModel and the ClassLoader used to load the class.
     **/
    private AccessControlContext createAccessControlContext( ClassLoaderModel model )
    {
        ClassLoader classloader = model.getClassLoader();
        if( classloader instanceof URLClassLoader )
        {
            Permissions permissionGroup = new Permissions();
            Permission[] permissions = model.getSecurityPermissions();
            for( int i=0 ; i < permissions.length ; i++ )
                permissionGroup.add( permissions[i] );
            
            Certificate[] certs = model.getCertificates();
            URL[] jars = ((URLClassLoader) classloader).getURLs();
            ProtectionDomain[] domains = new ProtectionDomain[ jars.length ];
            for( int i=0 ; i < jars.length ; i++ )
            {
                CodeSource cs = new CodeSource( jars[i], certs );
                domains[i] = new ProtectionDomain( cs, permissionGroup );
            }
            return new AccessControlContext( domains );    
        }
        else
        {
            // TODO: No other idea on how to handle this at the moment.
            throw new SecurityException( "ClassLoader's must inherit from URLClassLoader." );
        }
    }
    
    //-------------------------------------------------------------------
    // Commissionable
    //-------------------------------------------------------------------

   /**
    * Deploy the appliance. If the deployment policy is startup
    * an initial instance of a component will be deployed.
    *
    * @exception Exception if a deployment error occurs
    */
    public void commission() throws Exception
    {
        if( !getContainmentModel().isAssembled() )
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
              new Deployer( getLogger() );

            try
            {
                for( int i=0; i<startup.length; i++ )
                {
                    final DeploymentModel child = startup[i];
                    final Appliance appliance = locate( child );
                    deployer.deploy( child );
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
                final Commissionable appliance = child.getHandler();
                if( null != appliance )
                {
                    appliance.decommission();
                }
            }

            m_deployment.setEnabled( false );
        }
    }

    //-------------------------------------------------------------------
    // Resolver
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
        Logger logger = model.getLogger();

        if( model instanceof ComponentModel )
        {
            getLogger().debug( "creating appliance: " + path );
            ComponentModel component = (ComponentModel) model;
            appliance = new DefaultAppliance( component, this, m_accessControlContext, m_secured );
        }
        else if( model instanceof ContainmentModel )
        {
            getLogger().debug( "creating block: " + path );
            ContainmentModel containment = (ContainmentModel) model;
            appliance = new DefaultBlock( containment, this );
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
