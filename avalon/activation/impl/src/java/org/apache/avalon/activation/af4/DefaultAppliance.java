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
import java.util.HashMap;

import org.apache.avalon.lifecycle.Accessor;
import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.ApplianceException;
import org.apache.avalon.activation.appliance.Engine;
import org.apache.avalon.activation.lifecycle.ContextualizationHandler;
import org.apache.avalon.activation.lifecycle.Factory;
import org.apache.avalon.activation.lifecycle.LifecycleException;
import org.apache.avalon.activation.lifestyle.LifestyleHandler;
import org.apache.avalon.activation.lifestyle.impl.SingletonLifestyleHandler;
import org.apache.avalon.activation.lifestyle.impl.ThreadLifestyleHandler;
import org.apache.avalon.activation.lifestyle.impl.TransientLifestyleHandler;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContextModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.StageModel;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.meta.info.InfoDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

/**
 * DefaultAppliance is the default implementation of a local 
 * appliance instance.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1.2.1 $ $Date: 2004/02/22 15:50:06 $
 */
public class DefaultAppliance extends AbstractLogEnabled 
    implements Appliance, Disposable
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( DefaultAppliance.class );

    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

   /**
    * A map of appliance instances associated and keyed by the role
    * that the provider appliance plays relative to this appliance.
    */
    private final HashMap m_providers = new HashMap();

   /**
    * The stage extension providers keyed by stage key.
    */
    private final HashMap m_stages = new HashMap();

   /**
    * The deployment model characterizing this appliance instance.
    */
    private final ComponentModel m_model;

   /**
    * The engine from which we resolve dependent appliances during asembly.
    */
    private final Engine m_engine;

   /**
    * The instance factory.
    */
    private final Factory m_factory;

    private Object m_instance;

    //-------------------------------------------------------------------
    // mutable state
    //-------------------------------------------------------------------

   /**
    * The lifestyle handler.
    */
    private LifestyleHandler m_lifestyle = null;

   /**
    * The appliance assigned to handler contextualization (only
    * required in the case of a custom contextualization strategy).
    */
    private Appliance m_contextProvider = null;

   /**
    * The contextualization handler (possibly null) that will apply
    * the context to the component - established during the commissioning
    * phase.
    */
    private ContextualizationHandler m_contextualization = null;
   
   /**
    * Logging channel for the appliance.
    */
    private Logger m_logger;

    private boolean m_enabled;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    public DefaultAppliance( ComponentModel model, 
                             Engine engine )
        throws ApplianceException
    {
        super();
        m_model = (ComponentModel) model;
        m_engine = engine;
        m_factory = createFactory();
    }

    //-------------------------------------------------------------------
    // stuff
    //-------------------------------------------------------------------

    /**
     * Return the component model assigned to this appliance.
     * @return the componentn model
     */
     public DeploymentModel getModel()
     {
         return m_model;
     }

     public ComponentModel getComponentModel()
     {
         return m_model;
     }

     public Engine getEngine()
     {
        return m_engine;
     }
     
     private boolean isEnabled()
     {
        return m_enabled;
     }
     
     private void setEnabled( boolean enable )
     {
        m_enabled = enable;
     }
     
    /**
     * Return the context provider.  This is a component that
     * will be used to apply the contextualization strategy.
     *
     * @return the appliance mapped as provider of the context (may be null)
     */
    private Appliance getContextProvider()
    {
        ContextModel context = getComponentModel().getContextModel();
        if( null != context )
        {
            DeploymentModel provider = context.getProvider();
            if( null != provider )
            {
                return m_engine.locate( provider );
            }
        }
        return null;
    }

    /**
     * Returns the appliance assigned to handle the components lifecycle stage.
     * @param stage the lifecycle stage specification
     * @return the stage extension
     * @exception IllegalStateException if the stage provider is unresolvable
     */
    private Appliance getStageProvider( StageDescriptor stage ) 
      throws IllegalArgumentException
    {
        final String key = stage.getKey();
        StageModel model = getComponentModel().getStageModel( stage );
        if( null != model )
        {
            DeploymentModel provider = model.getProvider();
            if( null != provider )
            {
                return m_engine.locate( provider );
            }
            else
            {
                final String error = 
                  "Null provider returned for the stage: " + stage;
                throw new IllegalStateException( error );
            }
        }
        else
        {
            final String error = 
              REZ.getString( 
                "lifecycle.stage.key.unknown.error",
                getComponentModel().getQualifiedName(), key );
            throw new IllegalStateException( error );
        }
    }

    //-------------------------------------------------------------------
    // Deployable
    //-------------------------------------------------------------------

   /**
    * Deploy the appliance during which deployment phase artifacts 
    * such as logging channels, contextualization handlers and so on are 
    * established in preparation for component creation requests.
    * If the deployment policy is startup, an initial instance of a 
    * component will be deployed.
    *
    * @exception Exception if a deployment error occurs
    */
    public void deploy() throws Exception
    {
        synchronized( this )
        {
            if( isEnabled() ) return;
            final String lifestyle = m_model.getType().getInfo().getLifestyle();
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( 
                  "deployment (" 
                  + lifestyle
                  + "/"
                  + InfoDescriptor.getCollectionPolicyKey( 
                      m_model.getCollectionPolicy() ) 
                  + ") [" 
                  + m_model.getActivationPolicy() + "]" );
            }

            //
            // setup custom contextualizer if required
            //

            m_contextProvider = getContextProvider();
            if( m_contextProvider != null )
            {
                m_contextualization = 
                  setupContextualizationHandler( m_contextProvider );
            }

            //
            // setup a lifestyle handler that we use to 
            // abstract out lifecycle specific semantics - invocations 
            // against resolve will be redirected to the handler and 
            // the handler will call the newInstance method on this
            // appliance as needed
            //

            m_lifestyle = createLifestyleHandler( lifestyle );
            setEnabled( true );

            //
            // if the deployment policy is on startup then trigger 
            // component access now
            //

            if( getComponentModel().getActivationPolicy() )
            {
                m_instance = resolve();
                if( getLogger().isDebugEnabled() )
                {
                    int id = System.identityHashCode( m_instance );
                    getLogger().debug( "activated instance: " + id );
                }
            }
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
            getLogger().debug( "decommissioning phase" );

            if( m_model.getActivationPolicy() )
            {
                m_instance = null;
            }

            if( m_lifestyle != null )
            {
                if( m_lifestyle instanceof Disposable )
                {
                    ((Disposable)m_lifestyle).dispose();
                }
            }
            if( m_contextProvider != null )
            {
                m_contextProvider.release( m_contextualization );
                m_contextProvider = null;
                m_contextualization = null;
            }
            setEnabled( false );
            m_lifestyle = null;
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
        if( ! isEnabled() )
        {
            final String error = 
              "Illegal attempt to resolve an service from a non-deployed appliance ["
              + this + "].";
            getLogger().error( error );
            throw new IllegalStateException( error );
        }

        Object provider = m_lifestyle.resolve();
        accessInstance( getProviderInstance( provider ) );
        return provider;
    }

    private void accessInstance( Object instance ) throws Exception
    {
        applyAccessStages( instance, true );
    }

    private Object getProviderInstance( Object instance )
    {
        if( Proxy.isProxyClass( instance.getClass() ) )
        {
            ApplianceInvocationHandler handler = 
              (ApplianceInvocationHandler) Proxy.getInvocationHandler( instance );
            return handler.getInstance();
        }
        else
        {
            return instance;
        }
    }

    /**
     * Release an object.
     *
     * @param instance the object to be released
     */
    public void release( Object instance )
    {
        release( instance, false );
    }

    /**
     * Release an object.
     *
     * @param finalized the finalized state of the object
     * @param instance the object to be released
     */
    public void release( Object instance, boolean finalized )
    {
        if( instance == null ) 
            return;
        if( ! isEnabled() ) 
            return;
        releaseInstance( getProviderInstance( instance ) );
        m_lifestyle.release( instance, finalized );
    }

    private void releaseInstance( Object instance )
    {
        try
        {
            applyAccessStages( instance, false );
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( "lifestyle.release.warning" );
            getLogger().warn( error, e );
        }
    }
    
    //-------------------------------------------------------------------
    // Disposable
    //-------------------------------------------------------------------

    public void dispose()
    {
        getLogger().debug( "disposal" );
    }

    //-------------------------------------------------------------------
    // implementation
    //-------------------------------------------------------------------

    
    private Object applyAccessStages( Object instance, boolean flag ) 
      throws Exception
    {
        StageDescriptor[] stages = m_model.getType().getStages();
        for( int i=0; i<stages.length; i++ )
        {
            StageDescriptor stage = stages[i];
            Appliance provider = getStageProvider( stage );

            //
            // TODO: add operation to Appliance interface so that we can 
            // verify assignability
            //

            Class c = ((ComponentModel)provider.getModel()).getDeploymentClass();
            if( Accessor.class.isAssignableFrom( c ) )
            {
                Accessor handler = (Accessor) provider.resolve();
                try
                {
                    Context context = m_model.getContextModel().getContext();
                    if( flag )
                    {
                        if( getLogger().isDebugEnabled() )
                        {
                            int id = System.identityHashCode( instance );
                            getLogger().debug( "applying access stage to: " + id );
                        }
                        handler.access( instance, context );
                    }
                    else
                    {
                        if( getLogger().isDebugEnabled() )
                        {
                            int id = System.identityHashCode( instance );
                            getLogger().debug( "applying release stage to: " + id );
                        }
                        handler.release( instance, context );
                    }
                }
                catch( Throwable e )
                {
                    final String error = 
                      REZ.getString( 
                        "lifecycle.stage.accessor.error",
                        stage.getKey() );
                    if( flag )
                    {
                        throw new LifecycleException( error, e );
                    }
                    else
                    {
                        getLogger().warn( error, e );
                    }
                }
                finally
                {
                    provider.release( handler );
                }
            }
        }
        return instance;
    }

    private ContextualizationHandler setupContextualizationHandler( 
      Appliance appliance )
      throws Exception
    {
        if( appliance == null ) return null;

        try
        {
            return (ContextualizationHandler) appliance.resolve();
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "deployment.contextualization.custom-resolve.error", 
                appliance.getModel().getQualifiedName() );
            throw new LifecycleException( error, e );
        }
    }

    private LifestyleHandler createLifestyleHandler( final String lifestyle ) 
    {
        Logger log = getLogger();
        if( lifestyle.equals( InfoDescriptor.SINGLETON ) )
        {
            return new SingletonLifestyleHandler( log, m_factory );
        }
        else if( lifestyle.equals( InfoDescriptor.THREAD ) )
        {
            return new ThreadLifestyleHandler( log, m_factory );
        }
        else if( lifestyle.equals( InfoDescriptor.TRANSIENT ) )
        {
            return new TransientLifestyleHandler( log, m_factory );
        }
        else
        {
            final String error = 
              "Unsupported lifestyle [" + lifestyle + "].";
            throw new IllegalArgumentException( error );
        }
    }

   /**
    * Register this appliance as a listener on a supplied provider 
    * appliance.
    * 
    * @param appliance the provider appliance we want to listen to
    */
    private void registerListener( Appliance appliance )
    {
        //
        // once we have an listener/event model in 
        // place, consumer appliances will be able
        // to register themselves with providers
        // and act responsibly with respect to 
        // provider state changes
        //
    }

    /**
     * Creates the component factory.
     *
     * @throws ClassCastException if the FactoryClass in the ComponentModel
     *         does not implement Factory.
     * @throws IllegalArgumentException if the implementation does not have
     *         a no argument public constructor.
     */
    private Factory createFactory()
        throws ApplianceException
    {
        Class cls = m_model.getFactoryClass();
        try
        {
            Object factory = cls.newInstance();
            if( factory instanceof LogEnabled )
                ((LogEnabled) factory).enableLogging( getLogger() );
            return (Factory) factory;            
        } catch( InstantiationException e )
        {
            // TODO;  A better description of which factory.
            final String errorMessage = 
              "Not a valid class. Component Factory can not be created:" + cls.getName();
            throw new ApplianceException( errorMessage, e );
        } catch( IllegalAccessException e )
        {
            // TODO;  A better description of which factory.
            final String errorMessage = 
              "No public constructor. Component Factory can not be created:" + cls.getName();
            throw new ApplianceException( errorMessage, e );
        }
    }
}
