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

package org.apache.avalon.activation.csi;

import java.util.Map;
import java.util.Hashtable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException ;
import java.lang.reflect.Method ;

import org.apache.avalon.activation.ComponentFactory;
import org.apache.avalon.activation.RuntimeFactory;
import org.apache.avalon.activation.LifecycleException;

import org.apache.avalon.composition.model.ModelException;
import org.apache.avalon.composition.model.ModelRuntimeException;
import org.apache.avalon.composition.model.DeploymentModel;
import org.apache.avalon.composition.model.ComponentModel;
import org.apache.avalon.composition.model.ContextModel;
import org.apache.avalon.composition.model.ContainmentModel;
import org.apache.avalon.composition.model.StageModel;
import org.apache.avalon.composition.model.LifecycleCreateExtension;
import org.apache.avalon.composition.model.LifecycleDestroyExtension;
import org.apache.avalon.composition.model.ContextualizationHandler;
import org.apache.avalon.composition.provider.SystemContext;

import org.apache.avalon.meta.info.StageDescriptor;

import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Executable;

import org.apache.avalon.lifecycle.Creator;

import org.apache.avalon.repository.Artifact;


/**
 * A factory enabling the establishment of runtime handlers.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/02/14 21:33:55 $
 */
public class SecureComponentFactory implements ComponentFactory
{
    //-------------------------------------------------------------------
    // static
    //-------------------------------------------------------------------

    private static final Resources REZ =
      ResourceManager.getPackageResources( 
        SecureComponentFactory.class );

    //-------------------------------------------------------------------
    // immutable state
    //-------------------------------------------------------------------

    private final SystemContext m_system;

    private final ComponentModel m_model;

    private Logger m_logger;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

    public SecureComponentFactory( SystemContext system, ComponentModel model )
    {
        m_system = system;
        m_model = model;
   
        m_logger = model.getLogger().getChildLogger( "lifecycle" );
    }

    //-------------------------------------------------------------------
    // ComponentFactory
    //-------------------------------------------------------------------

    private Context getTargetContext()
    {
       ContextModel model = m_model.getContextModel();
       if( null == model ) return null;
       return model.getContext();
    }

   /**
    * Creation of a new instance including all deployment stage handling.
    * @return the new instance
    */
    public Object incarnate() throws LifecycleException
    {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        ClassLoader classloader = 
          m_model.getDeploymentClass().getClassLoader();
        Thread.currentThread().setContextClassLoader( classloader );

        try
        {
            return doIncarnation();
        }
        finally 
        {
            Thread.currentThread().setContextClassLoader( current );
        }
    }

   /**
    * Termination of the instance including all end-of-life processing.
    * @param instance the component instance
    */
    public void etherialize( Object instance )
    {
        try
        {
            applyCreateStage( instance, false );
        }
        catch( Throwable e )
        {
            // will not happen
        }
        finally
        {
            try
            {
                ContainerUtil.shutdown( instance );
            }
            catch( Throwable e )
            {
                if( getLogger().isWarnEnabled() )
                {
                    final String warning = 
                      "Ignoring component source shutdown error.";
                    getLogger().warn( warning, e );
                }
            }
        }
    }

    //-------------------------------------------------------------------
    // protected implementation
    //-------------------------------------------------------------------

    protected Logger getLogger()
    {
        return m_logger;
    }

    //-------------------------------------------------------------------
    // private implementation
    //-------------------------------------------------------------------

   /**
    * Creation of a new instance including all deployment stage handling.
    * @return the new instance
    */
    private Object doIncarnation() throws LifecycleException
    {
        Class clazz = m_model.getDeploymentClass();
        Logger logger = m_model.getLogger();
        Configuration config = m_model.getConfiguration();
        Parameters params = m_model.getParameters();
        ServiceManager manager = new SecureServiceManager( m_model );
        Context context = getTargetContext();

        Object instance = instantiate( clazz, logger, config, params, context, manager );

        try
        {
            ContainerUtil.enableLogging( instance, logger );
            applyContext( instance, context );
            ContainerUtil.service( instance, manager );
            ContainerUtil.configure( instance, config );
            ContainerUtil.parameterize( instance, params );

            //
            // handle lifecycle extensions
            //

            applyCreateStage( instance, true );

            //
            // complete intialization
            //

            ContainerUtil.initialize( instance );
            if( Startable.class.isAssignableFrom( clazz ) )
            {
                ContainerUtil.start( instance );
            }
            else if( Executable.class.isAssignableFrom( clazz ) )
            {
                ContainerUtil.execute( instance );
            }
            return instance;
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "lifestyle.error.new", 
                m_model.getQualifiedName() );
             throw new LifecycleException( error, e );
        }
    }

    private Object instantiate( 
      Class clazz, Logger logger, Configuration config, Parameters params, 
      Context context, ServiceManager manager )
      throws LifecycleException
    {
        Constructor[] constructors = clazz.getConstructors();
        if( constructors.length < 1 ) 
        {
            final String error = 
              REZ.getString( 
                "lifecycle.error.no-constructor", 
                clazz.getName() );
            throw new LifecycleException( error );
        }

        //
        // assume components have only one constructor for now
        //

        Constructor constructor = constructors[0];
        Class[] classes = constructor.getParameterTypes();
        Object[] args = new Object[ classes.length ];
        for( int i=0; i<classes.length; i++ )
        {
            Class c = classes[i];
            if( Logger.class.isAssignableFrom( c ) )
            {
                if( null == logger )
                {
                    throw new NullPointerException( "logger" );
                }
                args[i] = logger;
            }
            else if( Context.class.isAssignableFrom( c ) )
            {
                if( null == context )
                {
                    throw new NullPointerException( "context" );
                }
                args[i] = context;
            }
            else if( Configuration.class.isAssignableFrom( c ) )
            {
                if( null == config )
                {
                    throw new NullPointerException( "config" );
                }
                args[i] = config;
            }
            else if( Parameters.class.isAssignableFrom( c ) )
            {
                if( null == params )
                {
                    throw new NullPointerException( "params" );
                }
                args[i] = params;
            }
            else if( ServiceManager.class.isAssignableFrom( c ) )
            {
                if( null == manager )
                {
                    throw new NullPointerException( "manager" );
                }
                args[i] = manager;
            }
            else
            {
                final String error = 
                  REZ.getString( 
                    "lifecycle.error.unrecognized-parameter", 
                    c.getName(),
                    clazz.getName() );
                throw new LifecycleException( error );
            }
        }

        //
        // instantiate the factory
        //

        return instantiateComponent( constructor, args );
    }

   /**
    * Instantiation of a component instance using a supplied constructor 
    * and arguments.
    * 
    * @param constructor the component constructor
    * @param args the constructor arguments
    * @return the component instance
    * @exception LifecycleException if an instantiation error occurs
    */
    private Object instantiateComponent( 
      Constructor constructor, Object[] args ) 
      throws LifecycleException
    {
        try
        {
            return constructor.newInstance( args );
        }
        catch( Throwable e )
        {
            Class clazz = constructor.getDeclaringClass();
            final String error = 
              REZ.getString( 
                "lifecycle.error.instantiation", 
                clazz.getName() );
            throw new LifecycleException( error, e );
        }
    }

    private void applyCreateStage( Object instance, boolean flag ) 
      throws LifecycleException
    {
        StageDescriptor[] stages = m_model.getType().getStages();
        if( ( stages.length > 0 ) && getLogger().isDebugEnabled() )
        {
            getLogger().debug( "stage count: " + stages.length );
        }

        for( int i=0; i<stages.length; i++ )
        {
            StageDescriptor stage = stages[i];
            ComponentModel provider = getStageProvider( stage );
            Class c = provider.getDeploymentClass();
            if( Creator.class.isAssignableFrom( c ) )
            {
                getLogger().debug( "processing create: " + c.getName() );

                Creator handler = getCreator( provider );
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
                        try
                        {
                            handler.create( instance, context );
                        }
                        catch( Throwable e )
                        {
                            final String error =
                              "Create stage error raised by extension.";
                            throw new LifecycleException( error, e );
                        }
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
                        "lifecycle.error.stage.creator", stage.getKey() );
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
                  getLifecycleCreateExtension( provider );

                try
                {
                    if( getLogger().isDebugEnabled() )
                    {
                        int id = System.identityHashCode( instance );
                        getLogger().debug( "applying model create stage to: " + id );
                    }
                    handler.create( m_model, stage, instance );
                }
                catch( Throwable e )
                {
                    final String error =
                      "Create stage extension error.";
                    throw new LifecycleException( error, e );
                }
                finally
                {
                    provider.release( handler );
                }
            }
            else if( !flag && LifecycleDestroyExtension.class.isAssignableFrom( c ) )
            {
                LifecycleDestroyExtension handler = 
                  getLifecycleDestroyExtension( provider );

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

    private Creator getCreator( DeploymentModel provider ) 
      throws LifecycleException
    {
        try
        {
            return (Creator) provider.resolve();
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to resolve creation stage provider.";
            throw new LifecycleException( error, e );
        }
    }

    private LifecycleCreateExtension getLifecycleCreateExtension( 
      DeploymentModel provider ) throws LifecycleException
    {
        try
        {
            return (LifecycleCreateExtension) provider.resolve();
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to resolve lifecycle creation extension provider.";
            throw new LifecycleException( error, e );
        }
    }

    private LifecycleDestroyExtension getLifecycleDestroyExtension( 
      DeploymentModel provider ) throws LifecycleException
    {
        try
        {
            return (LifecycleDestroyExtension) provider.resolve();
        }
        catch( Throwable e )
        {
            final String error = 
              "Unable to resolve lifecycle destroy extension provider.";
            throw new LifecycleException( error, e );
        }
    }

    private ComponentModel getStageProvider( StageDescriptor stage ) 
      throws IllegalStateException
    {
        final String key = stage.getKey();
        StageModel model = m_model.getStageModel( stage );
        DeploymentModel provider = model.getProvider();
        if( provider instanceof ComponentModel )
        {
            return (ComponentModel) provider;
        }
        else
        {
            final String error = 
              REZ.getString( 
                "lifecycle.error.invalid-stage-provider", 
                key );
            throw new IllegalStateException( error );
        }
    }

    private void applyContext( final Object instance, Context context ) 
      throws LifecycleException
    {
        if( null == context ) return;

        final ContextModel model = m_model.getContextModel();
        if( model == null ) return;

        final DeploymentModel provider = model.getProvider();
        if( null == provider )
        {
            //
            // its classic avalon
            //

            try
            {
                ContainerUtil.contextualize( instance, context );
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( 
                    "lifecycle.error.avalon-contextualization", 
                    m_model.getQualifiedName() );
                throw new LifecycleException( error, e );
            }
        }
        else
        {
            try
            {
                ContextualizationHandler handler =
                  (ContextualizationHandler) provider.resolve();
                handler.contextualize( instance, context );
            }
            catch( Throwable e )
            {
                final String error = 
                  REZ.getString( 
                    "lifecycle.error.custom-contextualization", 
                    m_model.getQualifiedName(),
                    provider.toString() );
                throw new LifecycleException( error, e );
            }
        }
    }

    private ContextualizationHandler getContextualizationHandler()
      throws LifecycleException
    {
        ContextModel model = m_model.getContextModel();
        if( null == model ) return null;
        DeploymentModel provider = model.getProvider();
        try
        {
            return (ContextualizationHandler) provider.resolve();
        }
        catch( Throwable e )
        {
            final String error = 
              REZ.getString( 
                "lifestyle.error.contextualization", 
                provider.toString() );
            throw new LifecycleException( error, e );
        }
    }
}