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
import java.lang.reflect.UndeclaredThrowableException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessControlException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Permission;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Map;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.ApplianceException;
import org.apache.avalon.activation.appliance.AssemblyException;
import org.apache.avalon.activation.appliance.Engine;
import org.apache.avalon.activation.lifecycle.ContextualizationHandler;
import org.apache.avalon.activation.lifecycle.Factory;
import org.apache.avalon.activation.lifecycle.LifecycleCreateExtension;
import org.apache.avalon.activation.lifecycle.LifecycleDestroyExtension;
import org.apache.avalon.activation.lifecycle.LifecycleException;
import org.apache.avalon.activation.lifestyle.LifestyleHandler;
import org.apache.avalon.activation.lifestyle.impl.SingletonLifestyleHandler;
import org.apache.avalon.activation.lifestyle.impl.ThreadLifestyleHandler;
import org.apache.avalon.activation.lifestyle.impl.TransientLifestyleHandler;
import org.apache.avalon.composition.data.CategoriesDirective;
import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.model.ContextModel;
import org.apache.avalon.composition.model.DependencyModel;
import org.apache.avalon.composition.model.ComponentModel;
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
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.lifecycle.Accessor;
import org.apache.avalon.lifecycle.Creator;
import org.apache.avalon.meta.info.InfoDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.util.exception.ExceptionHelper;

/**
 * DefaultAppliance is the default implementation of a local 
 * appliance instance.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.25 $ $Date: 2004/01/21 14:17:35 $
 */
public class DefaultAppliance extends AbstractAppliance implements Appliance
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
    private final ComponentModel m_model;

   /**
    * The engine from which we resolve dependent appliances during asembly.
    */
    private final Engine m_engine;

   /**
    * The deployed state of the appliance.
    */
    private final DefaultState m_deployment = new DefaultState();

   /**
    * The instance factory.
    */
    private final Factory m_factory = new StandardFactory();

    private Object m_instance;

    private AccessControlContext m_accessControlContext;
    
    private boolean              m_secured;
    
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

    public DefaultAppliance( ComponentModel model, 
                             Engine engine, 
                             AccessControlContext access,
                             boolean secured )
    {
        super( model );
        m_model = (ComponentModel) model;
        m_engine = engine;
        m_accessControlContext = access;
        m_secured = secured;
        
        // Enabled the SecurityManager if none already exists, and that
        // the kernel setting for enabling the secure execution has been
        // set. The parameter in the kernel is urn:composition.security.enabled
        if( m_secured && System.getSecurityManager() == null )
        {
            System.setSecurityManager( new SecurityManager() );
        }
    }

    public DefaultAppliance( ComponentModel model, 
                             Engine engine )
    {
        super( model );
        m_model = (ComponentModel) model;
        m_engine = engine;
        m_accessControlContext = null;
        m_secured = false;
    }

    //-------------------------------------------------------------------
    // stuff
    //-------------------------------------------------------------------

    /**
     * Return the component model assigned to this appliance.
     * @return the componentn model
     */
     private ComponentModel getComponentModel()
     {
         return m_model;
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

    private Map getServiceProviders()
    {
        Hashtable map = new Hashtable();
        DependencyModel[] deps = 
          getComponentModel().getDependencyModels();
        for( int i=0; i<deps.length; i++ )
        {
            DependencyModel dep = deps[i];
            DeploymentModel provider = dep.getProvider();
            if( null != provider )
            {
                final String key = 
                  dep.getDependency().getKey();
                map.put( key, m_engine.locate( provider ) );
            }
            else
            {
                final String error = 
                  "Null provider returned for the service: " 
                  + dep.getDependency().getKey();
                throw new IllegalStateException( error );
            }
        }
        return map;
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
            m_deployment.setEnabled( true );

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
        synchronized( m_deployment )
        {
            if( !m_deployment.isEnabled() ) return;
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
              + this + "].";
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
        release( instance, false );
    }

    /**
     * Release an object.
     *
     * @param finalized the finalized state of the object
     * @param instance the object to be released
     */
    private void release( Object instance, boolean finalized )
    {
        if( instance == null ) 
            return;
        if( !m_deployment.isEnabled() ) 
            return;
        releaseInstance( getProviderInstance( instance ) );
        m_lifestyle.release( instance, finalized );
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
        getLogger().debug( "destroy: " + id );
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

    private void applyLogger( final Object instance )
    {
        if( instance instanceof LogEnabled )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying logger to: " + id );
            }
            final Logger logger = m_model.getLogger();
            if( ! m_secured )
            {
                ((LogEnabled)instance).enableLogging( logger );
            }
            else
            {
                AccessController.doPrivileged( new PrivilegedAction()
                {
                    public Object run()
                    {
                        ((LogEnabled)instance).enableLogging( logger );
                        return null;
                    }
                }, m_accessControlContext );
            }
        }
    }

    private void applyContext( final Object instance ) 
      throws Exception
    {
        if( instance == null ) 
            throw new NullPointerException( "context" );
        final ContextModel model = m_model.getContextModel();
        if( model == null ) 
            return;

        final Context context = model.getContext();
        if( m_contextualization != null )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying contextualization strategy to: " + id );
            }
            try
            {
                if( ! m_secured )
                {
                    m_contextualization.contextualize( instance, context );
                }
                else
                {
                    AccessController.doPrivileged( new PrivilegedExceptionAction()
                    {
                        public Object run() throws Exception
                        {
                            m_contextualization.contextualize( instance, context );
                            return null;
                        }
                    }, m_accessControlContext );
                }
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
                if( ! m_secured )
                {
                    ((Contextualizable)instance).contextualize( context );
                }
                else
                {
                    AccessController.doPrivileged( new PrivilegedExceptionAction()
                    {
                        public Object run() throws Exception
                        {
                            ((Contextualizable)instance).contextualize( context );
                            return null;
                        }
                    }, m_accessControlContext );
                }
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

    private void applyServices( final Object instance ) 
      throws Exception
    {
        if( instance instanceof Serviceable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying service manager to: " + id );
            }

            Map providers = getServiceProviders();
            final ServiceManager manager = new DefaultServiceManager( getLogger(), providers );
            if( ! m_secured )
            {
                ((Serviceable)instance).service( manager );
            }
            else
            {
                AccessController.doPrivileged( new PrivilegedExceptionAction()
                {
                    public Object run() throws Exception
                    {
                        ((Serviceable)instance).service( manager );
                        return null;
                    }
                }, m_accessControlContext );
            }
        }
    }

    private void applyConfiguration( final Object instance ) 
      throws Exception
    {
        if( instance instanceof Configurable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying configuration to: " + id );
            }
            if( ! m_secured )
            {
                ((Configurable)instance).configure( m_model.getConfiguration() );
            }
            else
            {
                AccessController.doPrivileged( new PrivilegedExceptionAction()
                {
                    public Object run() throws Exception
                    {
                        ((Configurable)instance).configure( m_model.getConfiguration() );
                        return null;
                    }
                }, m_accessControlContext );
            }
        }
    }

    private void applyParameters( final Object instance ) 
      throws Exception
    {
        if( instance instanceof Parameterizable )
        {
            if( getLogger().isDebugEnabled() )
            {
                int id = System.identityHashCode( instance );
                getLogger().debug( "applying parameters to: " + id );
            }
            if( ! m_secured )
            {
                ((Parameterizable)instance).parameterize( m_model.getParameters() );
            }
            else
            {
                AccessController.doPrivileged( new PrivilegedExceptionAction()
                {
                    public Object run() throws Exception
                    {
                        ((Parameterizable)instance).parameterize( m_model.getParameters() );
                        return null;
                    }
                }, m_accessControlContext );
            }
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
 
            Class c = ((ComponentModel)provider.getModel()).getDeploymentClass();
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

    private void applyInitialization( final Object instance ) 
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
                if( ! m_secured )
                {
                    ((Initializable)instance).initialize();
                }
                else
                {
                    AccessController.doPrivileged( new PrivilegedExceptionAction()
                    {
                        public Object run() throws Exception
                        {
                            ((Initializable)instance).initialize();
                            return null;
                        }
                    }, m_accessControlContext );
                }
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "lifecycle.initialize.component.error" );
                throw new LifecycleException( error, e );
            }
        }
    }

    private void applyStart( final Object instance ) 
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
                if( ! m_secured )
                {
                    ((Startable)instance).start();
                }
                else
                {
                    AccessController.doPrivileged( new PrivilegedExceptionAction()
                    {
                        public Object run() throws Exception
                        {
                            ((Startable)instance).start();
                            return null;
                        }
                    }, m_accessControlContext );
                }
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
                if( ! m_secured )
                {
                    ((Executable)instance).execute();
                }
                else
                {
                    AccessController.doPrivileged( new PrivilegedExceptionAction()
                    {
                        public Object run() throws Exception
                        {
                            ((Executable)instance).execute();
                            return null;
                        }
                    }, m_accessControlContext );
                }
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "lifecycle.execute.component.error" );
                throw new LifecycleException( error, e );
            }
        }
    }

    private void applyStop( final Object instance ) 
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
                if( ! m_secured )
                {
                    ((Startable)instance).stop();
                }
                else
                {
                    AccessController.doPrivileged( new PrivilegedExceptionAction()
                    {
                        public Object run() throws Exception
                        {
                            ((Startable)instance).stop();
                            return null;
                        }
                    }, m_accessControlContext );
                }
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( "lifecycle.stop.component.warn" );
                getLogger().warn( error, e );
            }
        }
    }

    private void applyDispose( final Object instance ) 
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
                if( ! m_secured )
                {
                    ((Disposable)instance).dispose();
                }
                else
                {
                    AccessController.doPrivileged( new PrivilegedExceptionAction()
                    {
                        public Object run() throws Exception
                        {
                            ((Disposable)instance).dispose();
                            return null;
                        }
                    }, m_accessControlContext );
                }
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
      LoggingManager logging, ComponentModel model )
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

    private Object createProvider( Object instance ) throws ApplianceException
    {
        if( getComponentModel().
          getType().getInfo().
            getAttribute( "urn:activation:proxy", "true" ).equals( "false" ) )
        {
            return instance;
        }

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
        catch( AccessControlException e )
        {
            Permission p = e.getPermission();
            if( null != p )
            {
                final String warning = 
                  "Proxy creation disabled due to insufficient permission: [" 
                  + p.getName()
                  + "].";
                getLogger().warn( warning );
            }
            else
            {
                final String warning = 
                  "Proxy creation disabled due to access control restriction."; 
                getLogger().warn( warning );
            }
            return instance;
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
        private boolean m_destroyed = false;

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
            if( proxy == null ) 
                throw new NullPointerException( "proxy" );
            if( method == null ) 
                throw new NullPointerException( "method" );
            if( m_destroyed ) 
                throw new IllegalStateException( "destroyed" );

            try
            {
                return secureInvocation( method, m_instance, args );
            }
            catch( Throwable e )
            {
                e = handleInvocationThrowable( e );
                throw e;
            }
        }

        protected void finalize() throws Throwable
        {
            if( !m_destroyed )
            {
                final String message = 
                  "Releasing component [" 
                  + System.identityHashCode( m_instance ) + "] (" 
                  + m_model.getType().getInfo().getLifestyle()
                  + "/" 
                  + InfoDescriptor.getCollectionPolicyKey( 
                      m_model.getCollectionPolicy() ) 
                  + ").";
                getLogger().debug( message );
                release( m_instance, true );
            }
        }

        Object getInstance()
        {
            return m_instance;
        }

        void notifyDestroyed()
        {
            m_destroyed = true;
        }
        
        private Object secureInvocation( 
          final Method method, final Object object, final Object[] args )
          throws Exception
        {
            if( ! m_secured )
            {
                return method.invoke( object, args );
            }
            else
            {
                Object result = AccessController.doPrivileged( 
                new PrivilegedExceptionAction()
                {
                    public Object run() throws Exception
                    {
                        return method.invoke( object, args );
                    }
                }, m_accessControlContext );
                return result;
            }
        }
        
        private Throwable handleInvocationThrowable( Throwable e )
        {
            final String error = 
              "Delegation error raised by component: " 
              + m_model.getQualifiedName();
            while( true )
            {
                if( e instanceof UndeclaredThrowableException )
                {
                    Throwable cause = 
                      ((UndeclaredThrowableException) e).getUndeclaredThrowable();
                    if( cause == null )
                        return new ApplianceException( error, e );
                    e = cause;
                }
                else if( e instanceof InvocationTargetException )
                {
                    Throwable cause = 
                      ((InvocationTargetException) e).getTargetException();
                    if( cause == null )
                        return new ApplianceException( error, e );
                    e = cause;
                }
                else if( e instanceof PrivilegedActionException )
                {
                    Throwable cause = 
                      ((PrivilegedActionException) e).getException();
                    if( cause == null )
                        return new ApplianceException( error, e );
                    e = cause;
                }
                else
                {
                    break;
                }
            }
            return e;
        }
    }

    private class StandardFactory implements Factory
    {
       /**
        * Return the component deployment model. 
        *
        * @exception LifecycleException
        */
        public ComponentModel getComponentModel()
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
                getLogger().error( e.getMessage() );
                final String error = 
                  "Provider publication failure.";
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
            if( Proxy.isProxyClass( instance.getClass() ) )
            {
                ApplianceInvocationHandler handler = 
                  (ApplianceInvocationHandler) Proxy.getInvocationHandler( instance );
                handler.notifyDestroyed();
            }
            destroyInstance( getProviderInstance( instance ) );
        }
    }
}
