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

namespace Apache.Avalon.Activation.Default.Lifestyles
{
	using System;

	using Apache.Avalon.Framework;
	using Apache.Avalon.Meta;
	using Apache.Avalon.Composition.Model;

	/// <summary>
	/// Summary description for AbstractLifestyleManager.
	/// </summary>
	public abstract class AbstractLifestyleManager : ILifestyleManager
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------

		private IComponentModel m_model;

		private IComponentFactory m_factory;

		private ILogger m_logger;

		// private final ReferenceQueue m_liberals = new ReferenceQueue();

		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------

		/// <summary>
		/// Creation of a new instance.
		/// </summary>
		/// <param name="model">the component model</param>
		/// <param name="factory">the component factory</param>
		public AbstractLifestyleManager( IComponentModel model, IComponentFactory factory )
		{
			m_factory = factory;
			m_model = model;
			m_logger = model.Logger;
		}

		~AbstractLifestyleManager()
		{
			Decommission();
		}

		#region ILifestyleManager Members

		public virtual void Finalize(object instance)
		{
			lock( m_factory )
			{
				if( instance != null )
				{
					m_factory.Etherialize( instance );
				}
			}
		}

		#endregion

		#region ICommissionable Members

		public abstract void Commission();
		
		public abstract void Decommission();

		#endregion

		#region IResolver Members

		public void Release( object instance )
		{
			try
			{
				ApplyExtensionStages( instance, false );
			}
			catch( Exception e )
			{
				String error = "Ignoring error returned from release extension.";
				Logger.Error( error, e );
			}
			HandleRelease( instance );
		}

		public object Resolve()
		{
			Object instance = HandleResolve();
			return ApplyExtensionStages( instance, true );
		}

		#endregion

		//-------------------------------------------------------------------
		// implementation
		//-------------------------------------------------------------------

		protected abstract Object HandleResolve();

		protected abstract void HandleRelease( Object instance );

		protected ILogger Logger
		{
			get
			{
				return m_logger;
			}
		}

		protected IComponentModel ComponentModel
		{ 
			get
			{
				return m_model;
			}
		}

		protected IComponentFactory ComponentFactory
		{ 
			get
			{
				return m_factory;
			}
		}

		private Object ApplyExtensionStages( object instance, bool flag ) 
		{
			StageDescriptor[] stages = m_model.TypeDescriptor.Stages;

			for( int i=0; i<stages.Length; i++ )
			{
				StageDescriptor descriptor = stages[i];
				IStageModel stage = m_model.GetStageModel( descriptor );

				IComponentModel provider = GetStageProvider( stage );
				// Class c = provider.getDeploymentClass();

				/*
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
							stage.getStage().getKey() );
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
				}*/
			}
			return instance;
		}

		private IComponentModel GetStageProvider( IStageModel stage )
		{
			try
			{
				return (IComponentModel) stage.Provider;
			}
			catch( Exception e )
			{
				String error = 
					"Unable to resolve access stage provider.";
				throw new LifecycleException( error, e );
			}
		}
	}
}
