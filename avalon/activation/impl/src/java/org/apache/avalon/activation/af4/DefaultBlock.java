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

package org.apache.avalon.activation.af4;

import java.lang.reflect.Proxy;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.ApplianceException;
import org.apache.avalon.activation.appliance.ApplianceRuntimeException;
import org.apache.avalon.activation.appliance.AssemblyException;
import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.activation.appliance.Engine;
import org.apache.avalon.activation.appliance.Home;
import org.apache.avalon.activation.appliance.NoProviderDefinitionException;

import org.apache.avalon.composition.event.CompositionEvent;
import org.apache.avalon.composition.event.CompositionEventListener;

import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.ClassLoaderModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ServiceModel;
import org.apache.avalon.composition.model.StageModel;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
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
 * @version $Revision: 1.1.2.1 $ $Date: 2004/02/22 15:50:06 $
 */
public class DefaultBlock extends AbstractLogEnabled 
  implements Appliance, Block, Home, CompositionEventListener
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    /**
     * Create a root containment block.
     * @param model the root containment model
     * @return the appliance
     */
    public static Block createRootBlock( ContainmentModel model, Engine engine ) throws Exception
    {
        if( null == model ) 
        {
            throw new NullPointerException( "model" );
        }
        return new DefaultBlock( model, engine );
    }

    //-------------------------------------------------------------------
    // immmutable state
    //-------------------------------------------------------------------

    private final ContainmentModel m_model;

    private final Engine m_engine;
    
    private final Object m_proxy;


    //-------------------------------------------------------------------
    // mutable state
    //-------------------------------------------------------------------

    private boolean m_enabled;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

   /**
    * Creation of a new abstract block.
    *
    * @param context the block context
    * @exception ApplianceException if a block creation error occurs
    */
    DefaultBlock( ContainmentModel model, Engine engine )
    {
        super();
        ClassLoaderModel clmodel = model.getClassLoaderModel();
        
        m_model = model;
        m_engine = engine;

        setEnabled( true );
        synchronized( model )
        {
            model.addCompositionListener( this );
        }
        try
        {
            final Logger log = model.getLogger().getChildLogger( "proxy" );
            final BlockInvocationHandler handler = 
              new BlockInvocationHandler( log, this );
            final Class[] classes = getInterfaceClasses();
            
            m_proxy = Proxy.newProxyInstance( 
              model.getClassLoaderModel().getClassLoader(),
              classes,
              handler );
        }
        catch( Throwable e )
        {
            final String error = 
              "Composite service establishment failure in block: " + this;
            throw new ApplianceRuntimeException( error, e );
        }
    }

    //-------------------------------------------------------------------
    // stuff
    //-------------------------------------------------------------------

    private void setEnabled( boolean enable )
    {
        m_enabled = enable;
    }
    
    private boolean isEnabled()
    {
        return m_enabled;
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

    public DeploymentModel getModel() 
    {
        return m_model;
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
        if( !getContainmentModel().isAssembled() )
        {
            throw new IllegalStateException( "assembly" );
        }

        synchronized( this )
        {
            if( isEnabled() )
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

            setEnabled( true );
        }
    }

   /**
    * Decommission the block.  Under the decommissioning phase, 
    * all active components will be taken down.
    */
    public void decommission()
    {
        synchronized( this )
        {
            if( ! isEnabled() ) return;

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

            setEnabled( false );
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
    public Object resolve() throws Exception
    {
        return m_proxy;
    }

    /**
     * Release an object
     *
     * @param instance the object to be released
     */
    public void release( Object instance )
    {
        //
        // container proxy is a singleton reference
        //
    }

    //-------------------------------------------------------------------
    // Disposable
    //-------------------------------------------------------------------

    public void dispose()
    {
        synchronized( this )
        {
            if( ! isEnabled() ) 
                return;

            getLogger().debug( "disposal phase" );
            getContainmentModel().removeCompositionListener( this );
            getContainmentModel().setHandler( null );

            setEnabled( false );
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
            appliance = new DefaultAppliance( component, this );
        }
        else if( model instanceof ContainmentModel )
        {
            getLogger().debug( "creating block: " + path );
            ContainmentModel containment = (ContainmentModel) model;
            appliance = new DefaultBlock( containment, m_engine );
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


    private Class[] getInterfaceClasses() throws Exception
    {
        ContainmentModel model = getContainmentModel();
        ClassLoader loader = model.getClassLoaderModel().getClassLoader();
        ArrayList list = new ArrayList();
        ServiceModel[] services = model.getServiceModels();
        for( int i=0; i<services.length; i++ )
        {
            final ServiceModel service = services[i];
            list.add( service.getServiceClass() );
        }
        return (Class[]) list.toArray( new Class[0] );
    }
}
