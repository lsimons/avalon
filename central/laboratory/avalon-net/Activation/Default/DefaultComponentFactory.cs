// Copyright 2004 Apache Software Foundation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

namespace Apache.Avalon.Activation.Default
{
	using System;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Meta;
	using Apache.Avalon.Composition.Model;

	/// <summary>
	/// Summary description for DefaultComponentFactory.
	/// </summary>
	public class DefaultComponentFactory : IComponentFactory
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------

		private ISystemContext m_system;

		private IComponentModel m_model;

		private ILogger m_logger;

		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------

		/// <summary>
		/// Creation of a new component factory.
		/// </summary>
		/// <param name="system">the system context</param>
		/// <param name="model">the component model</param>
		public DefaultComponentFactory( ISystemContext system, IComponentModel model )
		{
			m_system = system;
			m_model = model;
   			m_logger = model.Logger.CreateChildLogger( "lifecycle" );
		}

		#region IComponentFactory Members

		public object Incarnate()
		{
			Type type = m_model.DeploymentType;
			ILogger logger = m_model.Logger;
			IConfiguration config = m_model.Configuration;
			// Parameters params = m_model.getParameters();
			ILookupManager manager = new DefaultLookupManager( m_model );
			IContext context = TargetContext;

			// Object instance = instantiate( 
			// 	clazz, logger, config, params, context, manager );

			Object instance = Activator.CreateInstance( type );

			try
			{
				ContainerUtil.EnableLogging( instance, logger );
				ApplyContext( instance, context );
				ContainerUtil.Service( instance, manager );
				ContainerUtil.Configure( instance, config );
				// ContainerUtil.parameterize( instance, params );

				//
				// handle lifecycle extensions
				//

				ApplyCreateStage( instance, true );

				//
				// complete intialization
				//

				ContainerUtil.Initialize( instance );
				ContainerUtil.Start( instance );

				return instance;
			}
			catch( Exception e )
			{
				String error = "lifestyle.error.new " +  m_model.QualifiedName;
				throw new LifecycleException( error, e );
			}		
		}

		public void Etherialize(object instance)
		{
			if (instance == null)
			{
				return;
			}

			try
			{
				ApplyCreateStage( instance, false );
			}
			catch( Exception )
			{
				// will not happen
			}
			finally
			{
				try
				{
					ContainerUtil.Shutdown( instance );
				}
				catch( Exception e )
				{
					if( Logger.IsWarnEnabled )
					{
						String warning = "Ignoring component source shutdown error.";
						Logger.Warn( warning, e );
					}
				}
			}		
		}
		#endregion

		protected ILogger Logger
		{
			get
			{
				return m_logger;
			}
		}

		private IContext TargetContext
		{
			get
			{
				IContextModel model = m_model.ContextModel;
				
				if( null == model ) 
				{
					return null;
				}

				return model.Context;
			}
		}

		private void ApplyCreateStage( Object instance, bool flag ) 
		{
			StageDescriptor[] stages = m_model.TypeDescriptor.Stages;
			if( ( stages.Length > 0 ) && Logger.IsDebugEnabled )
			{
				Logger.Debug( "stage count: " + stages.Length );
			}

			for( int i=0; i<stages.Length; i++ )
			{
				StageDescriptor stage = stages[i];
				IComponentModel provider = GetStageProvider( stage );
				/*
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
				}*/
			}
		}

		/*
		private Creator getCreator( DeploymentModel provider ) 
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
		DeploymentModel provider ) 
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
		DeploymentModel provider ) 
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
		}*/

		private IComponentModel GetStageProvider( StageDescriptor stage ) 
		{
			String key = stage.Key;
			IStageModel model = m_model.GetStageModel( stage );
			IDeploymentModel provider = model.Provider;
			if( provider is IComponentModel )
			{
				return (IComponentModel) provider;
			}
			else
			{
				String error = "lifecycle.error.invalid-stage-provider " + key;
				throw new ApplicationException( error );
			}
		}

		private void ApplyContext( Object instance, IContext context ) 
		{
			if( null == context ) 
			{	
				return;
			}

			IContextModel model = m_model.ContextModel;
			if( model == null ) 
			{
				return;
			}

			IDeploymentModel provider = model.Provider;
			if( null == provider )
			{
				//
				// its classic avalon
				//

				try
				{
					ContainerUtil.Contextualize( instance, context );
				}
				catch( Exception e )
				{
					String error = "lifecycle.error.avalon-contextualization " + m_model.QualifiedName;
					throw new LifecycleException( error, e );
				}
			}
			else
			{
				try
				{
					IContextualizationHandler handler =
						(IContextualizationHandler) provider.Resolve();
					handler.Contextualize( instance, context );
				}
				catch( Exception e )
				{
					String error = "lifecycle.error.custom-contextualization" + m_model.QualifiedName;
					throw new LifecycleException( error, e );
				}
			}
		}
	}
}
