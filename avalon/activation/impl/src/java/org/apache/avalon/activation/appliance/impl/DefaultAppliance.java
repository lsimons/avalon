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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.util.ArrayList;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.ApplianceException;
import org.apache.avalon.activation.appliance.AssemblyException;
import org.apache.avalon.activation.appliance.Composite;
import org.apache.avalon.activation.appliance.Engine;
import org.apache.avalon.activation.appliance.ServiceContext;
import org.apache.avalon.activation.lifecycle.ContextualizationHandler;
import org.apache.avalon.activation.lifecycle.Factory;
import org.apache.avalon.activation.lifecycle.LifecycleCreateExtension;
import org.apache.avalon.activation.lifecycle.LifecycleDestroyExtension;
import org.apache.avalon.activation.lifecycle.LifecycleException;
import org.apache.avalon.activation.lifestyle.LifestyleHandler;
import org.apache.avalon.activation.lifestyle.impl.PooledLifestyleHandler;
import org.apache.avalon.activation.lifestyle.impl.SingletonLifestyleHandler;
import org.apache.avalon.activation.lifestyle.impl.ThreadLifestyleHandler;
import org.apache.avalon.activation.lifestyle.impl.TransientLifestyleHandler;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.model.ContextModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.StageModel;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.lifecycle.Accessor;
import org.apache.avalon.lifecycle.Creator;
import org.apache.avalon.meta.info.InfoDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;

/**
 * DefaultAppliance is the default implementation of a local 
 * appliance instance.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2003/10/18 00:34:19 $
 */
public class DefaultAppliance extends AbstractAppliance
  implements Composite, DefaultApplianceMBean
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
    private final Hashtable m_providers = new Hashtable();

   /**
    * The stage extension providers keyed by stage key.
    */
    private final Hashtable m_stages = new Hashtable();

   /**
    * The deployment model characterizing this appliance instance.
    */
    private final DeploymentModel m_model;

   /**
    * The engine from which we resolve dependent appliances during asembly.
    */
    private final Engine m_engine;

   /**
    * The assembled state of the appliance.
    */
    private final DefaultState m_assembly = new DefaultState();

   /**
    * The deployed state of the appliance.
    */
    private final DefaultState m_deployment = new DefaultState();

   /**
    * The system service context. 
    */
    private final ServiceContext m_context;

   /**
    * The instance factory.
    */
    private final Factory m_factory = new StandardFactory();

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

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    public DefaultAppliance( 
      Logger logger, ServiceContext context, DeploymentModel model, Engine engine )
    {
        super( logger.getChildLogger( "appliance" ), model );

        m_logger = logger;
        m_context = context;
        m_model = (DeploymentModel) model;
        m_engine = engine;
    }

    //-------------------------------------------------------------------
    // stuff
    //-------------------------------------------------------------------

    /**
     * Return the context provider.  This is a component that
     * will be used to apply the contextualization strategy.
     *
     * @return the appliance mapped as provider of the context (may be null)
     */
    private Appliance getContextProvider()
    {
        return m_contextProvider;
    }

    /**
     * Returns the appliace assigned to handle the components lifecycle stage.
     * @param stage the lifecycle stage specification
     * @return the stage extension
     * @exception IllegalStateException if the stage provider is unresolvable
     */
    private Appliance getStageProvider( StageDescriptor stage ) throws IllegalArgumentException
    {
        final String key = stage.getKey();
        Appliance appliance = (Appliance) m_stages.get( key );
        if( appliance != null ) return appliance;

        final String error = 
          REZ.getString( 
            "lifecycle.stage.key.unknown.error",
            m_model.getQualifiedName(), key );
        getLogger().error( error );

        throw new IllegalStateException( error );
    }

    /**
     * Return the assigned extension providers.
     *
     * @return the set of extension provider appliances.
     */
    private Appliance[] getStageProviders()
    {
        return (Appliance[]) m_stages.values().toArray( new Appliance[0] );
    }

    //-------------------------------------------------------------------
    // Composite
    //-------------------------------------------------------------------

    /**
     * Returns the assembled state of the appliance.
     * @return true if this appliance is assembled
     */
    public boolean isAssembled()
    {
        return m_assembly.isEnabled();
    }

    /**
     * Assemble the appliance.
     * @exception ApplianceException if an error occurs during appliance assembly
     */
    public void assemble() throws AssemblyException
    {
        synchronized( m_assembly )
        {
            if( isAssembled() )
            {
                return;
            }

            getLogger().debug( "assembly phase" );

            //
            // check if we need a contextualization handler
            //

            if( m_model.getContextModel() != null )
            {
                Class clazz = m_model.getContextModel().getStrategyClass();
                if( !clazz.getName().equals( ContextModel.DEFAULT_STRATEGY_CLASSNAME ) )
                {
                    //
                    // we need to load a deployment phase context strategy handler
                    // using the strategy interface as the extension handler key
                    //

                    try
                    {
                        StageDescriptor stage = 
                          new StageDescriptor( clazz.getName() );
                        m_contextProvider = 
                          m_engine.locate( stage );
                    }
                    catch( Throwable e )
                    {
                        final String error = 
                          REZ.getString( 
                            "assembly.context.error",
                            getModel().getQualifiedName(), clazz.getName() );
                        throw new AssemblyException( error, e );
                    }

                    //
                    // verify that the stage is a context handler
                    //

                    //
                    // TODO - instead of casting we should be able 
                    // test the appliance if it supports assignment
                    // from a class via an operation on the Home
                    // interface
                    //

                    DeploymentModel model = 
                       (DeploymentModel) m_contextProvider.getModel();
                    Class handler = 
                      model.getDeploymentClass();

                    if( !ContextualizationHandler.class.isAssignableFrom( handler ) )
                    {
                        final String error = 
                          REZ.getString( 
                            "assembly.context-strategy.bad-class.error",
                            handler.getName() );
                        throw new AssemblyException( error );
                    }

                    registerListener( m_contextProvider );
                    if( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( 
                          "assigning context provider: " + m_contextProvider );
                    }
                }
            }

            //
            // get the dependency models for the type and resolve providers
            //

            DependencyModel[] dependencies = m_model.getDependencyModels();
            for( int i=0; i<dependencies.length; i++ )
            {
                DependencyModel dependency = dependencies[i];
                final String key = dependency.getDependency().getKey();
                try
                {
                    final Appliance appliance = 
                      m_engine.locate( dependency );
                    registerListener( appliance );
                    m_providers.put( key, appliance );
                    if( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( 
                          "assigning service provider for key (" 
                          + key + "): " + appliance );
                    }
                }
                catch( Throwable e )
                {
                    final String error = 
                      REZ.getString( 
                        "assembly.dependency.error",
                        getModel().getQualifiedName(), key );
                    throw new AssemblyException( error, e );
                }
            }

            //
            // get the stage models for the type and resolve providers
            //

            StageModel[] stages = m_model.getStageModels();
            for( int i=0; i<stages.length; i++ )
            {
                StageModel stage = stages[i];
                final String key = stage.getStage().getKey();
                try
                {
                    final Appliance appliance = 
                      m_engine.locate( stage );
                    registerListener( appliance );
                    m_stages.put( key, appliance );
                    if( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( 
                          "assigning stage provider (" + key + "): " + appliance );
                    }
                }
                catch( Throwable e )
                {
                    final String error = 
                      REZ.getString( 
                        "assembly.stage.error",
                        getModel().getQualifiedName(), key );
                    throw new AssemblyException( error, e );
                }
            }
            m_assembly.setEnabled( true );
        }
    }

    /**
     * Disassemble the appliance.
     */
    public void disassemble()
    {
        synchronized( m_assembly )
        {
           if( !m_assembly.isEnabled() )
           {
               final String error = 
                 REZ.getString( 
                   "assembly.dissassembly.state.error", 
                   getModel().getQualifiedName() );
               throw new IllegalStateException( error );
           }

           getLogger().debug( "dissassembly phase" );

           m_contextProvider = null;
           m_stages.clear();
           m_providers.clear();
           m_assembly.setEnabled( false );
        }
    }

    public Appliance[] getProviders()
    {
        final ArrayList list = new ArrayList();
        if( m_contextProvider != null )
        {
            list.add( m_contextProvider );
        }
        list.addAll( m_stages.values() );
        list.addAll( m_providers.values() );
        return (Appliance[]) list.toArray( new Appliance[0] );
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
        synchronized( m_deployment )
        {
            if( m_deployment.isEnabled() ) return;
            final String lifestyle = m_model.getType().getInfo().getLifestyle();
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( 
                  "deployment (" 
                  + lifestyle + ") [" 
                  + m_model.getActivationPolicy() + "]" );
            }

            //
            // setup custom contextualizer if required
            //

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
            m_deployment.setEnabled( true );

            //
            // if the deployment policy is on startup then trigger 
            // component access now
            //

            if( m_model.getActivationPolicy() )
            {
                Object instance = resolve();
                if( getLogger().isDebugEnabled() )
                {
                    int id = System.identityHashCode( instance );
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
        synchronized( m_deployment )
        {
            if( !m_deployment.isEnabled() ) return;
            getLogger().debug( "decommissioning phase" );
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
                m_contextualization = null;
            }
            m_deployment.setEnabled( false );
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
        if( !m_deployment.isEnabled() )
        {
            final String error = 
              "Illegal attempt to resolve an service from a non-deployed appliance ["
              + this + "]. A possible cause is a recursive dependency declaration.";
            getLogger().error( error );
            throw new IllegalStateException( error );
        }

        Object provider = m_lifestyle.resolve();
        accessInstance( getProviderInstance( provider ) );
        return provider;
    }

    /**
     * Release an object.
     *
     * @param instance the object to be released
     */
    public void release( Object instance )
    {
        if( instance == null ) return;
        if( !m_deployment.isEnabled() ) return;
        releaseInstance( getProviderInstance( instance ) );
        m_lifestyle.release( instance );
    }

    private void accessInstance( Object instance ) throws Exception
    {
        applyAccessStages( instance, true );
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

    private void destroyInstance( Object instance )
    {
        final int id = System.identityHashCode( instance );
        getLogger().debug( "component disposal: " + id );
        try
        {
            applyStop( instance );
            applyCreateStages( instance, false );
            applyDispose( instance );
        }
        catch( Throwable e )
        {
            getLogger().warn( "ignoring release stage error", e );
        }
        getLogger().debug( "destroyed instance: " + id );
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

    private Object createNewInstance( Class clazz )
      throws LifecycleException
    {
        try
        {
            return clazz.newInstance();
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "lifecycle.instantiation.error", clazz.getName() );
            throw new LifecycleException( error, e );
        }
    }

    private void applyLogger( Object instance )
    {
        if( instance instanceof LogEnabled )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying logger to: " + id );
            }
            ((LogEnabled)instance).enableLogging( m_logger );
        }
    }

    private void applyContext( Object instance ) 
      throws Exception
    {
        if( instance == null ) throw new NullPointerException( "context" );

        final ContextModel model = m_model.getContextModel();

        if( model == null ) return;

        Context context = model.getContext();
        if( m_contextualization != null )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying contextualization strategy to: " + id );
            }
            try
            {
                m_contextualization.contextualize( instance, context );
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( 
                    "lifecycle.contextualization.custom.error", 
                    m_model.getQualifiedName() );
                throw new LifecycleException( error, e );
            }
        }
        else if( instance instanceof Contextualizable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying contextualization to: " + id );
            }

            try
            {
                ((Contextualizable)instance).contextualize( context );
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( 
                    "lifecycle.contextualization.component.error", 
                    m_model.getQualifiedName() );
                throw new LifecycleException( error, e );
            }
        }
    }

    private void applyServices( Object instance ) 
      throws Exception
    {
        if( instance instanceof Serviceable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying service manager to: " + id );
            }
            ServiceManager manager = new DefaultServiceManager( getLogger(), m_providers );
            ((Serviceable)instance).service( manager );
        }
    }

    private void applyConfiguration( Object instance ) 
      throws Exception
    {
        if( instance instanceof Configurable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying configuration to: " + id );
            }
            ((Configurable)instance).configure( m_model.getConfiguration() );
        }
    }

    private void applyParameters( Object instance ) 
      throws Exception
    {
        if( instance instanceof Parameterizable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying parameters to: " + id );
            }
            ((Parameterizable)instance).parameterize( m_model.getParameters() );
        }
    }

    private void applyCreateStages( Object instance, boolean flag ) 
      throws Exception
    {
        StageDescriptor[] stages = m_model.getType().getStages();
        if( ( stages.length > 0 ) && getLogger().isDebugEnabled() )
        {
            getLogger().debug( "stage count: " + stages.length );
        }

        for( int i=0; i<stages.length; i++ )
        {
            StageDescriptor stage = stages[i];
            Appliance provider = getStageProvider( stage );

            //
            // TODO: add operation to Appliance interface so that we can 
            // verify assignability
 
            Class c = ((DeploymentModel)provider.getModel()).getDeploymentClass();
            getLogger().debug( "processing create: " + c.getName() 
              + ", [" +  Creator.class.isAssignableFrom( c ) + "]" );

            if( Creator.class.isAssignableFrom( c ) )
            {
                Creator handler = (Creator) provider.resolve();
                Context context = m_model.getContextModel().getContext();
                try
                {
                    if( flag )
                    {
                        if( getLogger().isDebugEnabled() )
                        {
                            int id = System.identityHashCode( instance );
                            getLogger().debug( "applying create stage to: " + id );
                        }
                        handler.create( instance, context );
                    }
                    else
                    {
                        if( getLogger().isDebugEnabled() )
                        {
                            int id = System.identityHashCode( instance );
                            getLogger().debug( "applying destroy stage to: " + id );
                        }
                        handler.destroy( instance, context );
                    }
                }
                catch( Throwable e )
                {
                    final String error = 
                      REZ.getString( 
                        "lifecycle.stage.creator.error", stage.getKey() );
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

            if( flag && LifecycleCreateExtension.class.isAssignableFrom( c ) )
            {
                LifecycleCreateExtension handler = 
                  (LifecycleCreateExtension) provider.resolve();
                try
                {
                    if( getLogger().isDebugEnabled() )
                    {
                        int id = System.identityHashCode( instance );
                        getLogger().debug( "applying model create stage to: " + id );
                    }
                    handler.create( m_model, stage, instance );
                }
                finally
                {
                    provider.release( handler );
                }
            }
            else if( !flag && LifecycleDestroyExtension.class.isAssignableFrom( c ) )
            {
                LifecycleDestroyExtension handler = 
                  (LifecycleDestroyExtension) provider.resolve();

                try
                {
                    if( getLogger().isDebugEnabled() )
                    {
                        int id = System.identityHashCode( instance );
                        getLogger().debug( "applying model destroy stage to: " + id );
                    }
                    handler.destroy( m_model, stage, instance );
                }
                catch( Throwable e )
                {
                    if( getLogger().isWarnEnabled() )
                    {
                        final String error = 
                          "Ignoring destroy stage error";
                        getLogger().warn( error, e );
                    }
                }
                finally
                {
                    provider.release( handler );
                }
            }
        }
    }

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

            Class c = ((DeploymentModel)provider.getModel()).getDeploymentClass();
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

    private void applyInitialization( Object instance ) 
      throws LifecycleException
    {
        if( instance instanceof Initializable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying initialization to: " + id );
            }
            try
            {
                ((Initializable)instance).initialize();
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "lifecycle.initialize.component.error" );
                throw new LifecycleException( error, e );
            }
        }
    }


    private void applyStart( Object instance ) 
      throws LifecycleException
    {
        if( instance instanceof Startable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "starting: " + id );
            }
            try
            {
                ((Startable)instance).start();
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "lifecycle.start.component.error" );
                throw new LifecycleException( error, e );
            }
        }
        else if( instance instanceof Executable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "executing: " + id );
            }
            try
            {
                ((Executable)instance).execute();
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "lifecycle.execute.component.error" );
                throw new LifecycleException( error, e );
            }
        }
    }

    private void applyStop( Object instance ) 
    {
        if( instance instanceof Startable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "stopping: " + id );
            }
            try
            {
                ((Startable)instance).stop();
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "lifecycle.stop.component.warn" );
                getLogger().warn( error, e );
            }
        }
    }

    private void applyDispose( Object instance ) 
    {
        if( instance instanceof Disposable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "disposing of: " + id );
            }
            try
            {
                ((Disposable)instance).dispose();
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "lifecycle.dispose.component.warn" );
                getLogger().warn( error, e );
            }
        }
    }

    private Logger getTargetLogger( 
      LoggingManager logging, DeploymentModel model )
    {
        if( LogEnabled.class.isAssignableFrom( model.getDeploymentClass() ) )
        {
            final String name = model.getQualifiedName();
            CategoriesDirective categories = model.getCategories();
            if( categories != null )
            {
                logging.addCategories( name, categories );
            }
            return logging.getLoggerForCategory( name );
        }
        else
        {
            return null;
        }
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
        else if( lifestyle.equals( InfoDescriptor.POOLED ) )
        {
            return new PooledLifestyleHandler( log, m_factory );
        }
        else
        {
            return new TransientLifestyleHandler( log, m_factory );
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

    private Object createProvider( Object instance ) throws ApplianceException
    {
        Class[] classes = m_model.getInterfaces();
        try
        {
            ApplianceInvocationHandler handler = 
              new ApplianceInvocationHandler( instance );
            return Proxy.newProxyInstance( 
              m_model.getDeploymentClass().getClassLoader(),
              classes,
              handler );
        }
        catch( Throwable e )
        {
            final String error = 
              "Proxy establishment failure in block: " + this;
            throw new ApplianceException( error, e );
        }
    }

   /**
    * This makes a dynamic proxy for an object.  The object can be represented
    * by one, some or all of it's interfaces.
    *
    */
    private final class ApplianceInvocationHandler
        implements InvocationHandler
    {
        private final Object m_instance;
        private boolean m_disposed = false;

       /**
        * Create a proxy invocation handler.
        *
        * @param instance the underlying provider 
        */
        protected ApplianceInvocationHandler( Object instance )
        {
            m_instance = instance;
        }

        /**
         * Invoke the specified method on underlying object.
         * This is called by the proxy object.
         *
         * @param proxy the proxy object
         * @param method the method invoked on proxy object
         * @param args the arguments supplied to method
         * @return the return value of method
         * @throws Throwable if an error occurs
         */
        public Object invoke( final Object proxy,
                final Method method,
                final Object[] args )
                throws Throwable
        {
            if( proxy == null ) throw new NullPointerException( "proxy" );
            if( method == null ) throw new NullPointerException( "method" );
            if( m_disposed ) throw new IllegalStateException( "disposed" );

            try
            {
                return method.invoke( m_instance, args );
            }
            catch( InvocationTargetException e )
            {
                final String error = 
                  "Delegation error raised by component: " + m_model.getQualifiedName();
                throw new ApplianceException( error, e.getTargetException() );
            }
            catch( Throwable e )
            {
                final String error =
                  "Service resolution failure for the component: '" 
                  + method.getDeclaringClass()
                  + "' for operation: '" + method.getName()
                  + "' in appliance: " + m_model.getQualifiedName();
                throw new ApplianceException( error, e );
            }
        }

        protected void finalize() throws Throwable
        {
            if( !m_disposed )
            {
                release( m_instance );
            }
        }

        Object getInstance()
        {
            return m_instance;
        }
    }

    private class StandardFactory implements Factory
    {
       /**
        * Return the component deployment model. 
        *
        * @exception LifecycleException
        */
        public DeploymentModel getDeploymentModel()
        {
            return m_model;
        }

       /**
        * Create a new instance of a component. 
        *
        * @exception LifecycleException
        */
        public Object newInstance() throws LifecycleException
        {
            Class clazz = m_model.getDeploymentClass();
            Object instance = null;
            try
            {
                instance = createNewInstance( clazz );
                if( getLogger().isDebugEnabled() )
                {
                    int id = System.identityHashCode( instance );
                    getLogger().debug( "new instance: " + id );
                }

                applyLogger( instance );
                applyContext( instance );
                applyServices( instance );
                applyConfiguration( instance );
                applyParameters( instance );
                applyCreateStages( instance, true );
                applyInitialization( instance );
                applyStart( instance );

                if( getLogger().isDebugEnabled() )
                {
                    int id = System.identityHashCode( instance );
                    getLogger().debug( "established: " + id );
                }
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "lifestyle.new.error", m_model.getQualifiedName() );
                throw new LifecycleException( error, e );
            }

            try
            {
                return createProvider( instance );
            }
            catch( Throwable e )
            {
                final String error = 
                  "Proxy establishment failure.";
                getLogger().error( error );
                throw new LifecycleException( error, e );
            }
        }

       /**
        * Decommission and dispose of the supplied component. 
        *
        * @param instance the object to decommission
        */
        public void destroy( Object instance )
        {
            if( instance == null ) throw new NullPointerException( "instance" );
            destroyInstance( getProviderInstance( instance ) );
        }
    }
}
