// Copyright 2004 The Apache Software Foundation
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

namespace Apache.Avalon.Castle.MicroKernel
{
	using System;
	using System.Collections;

	using Apache.Avalon.Castle.MicroKernel.Model;
	using Apache.Avalon.Castle.MicroKernel.Subsystems.Lookup.Default;
	using Apache.Avalon.Castle.MicroKernel.Subsystems.Events;
	using Apache.Avalon.Castle.MicroKernel.Subsystems.Events.Default;

	/// <summary>
	/// Base implementation of <see cref="IKernel"/>
	/// </summary>
	public class BaseKernel : IKernel
	{
		protected Hashtable m_components;

		protected Hashtable m_services;

		protected Hashtable m_subsystems;

		protected Hashtable m_dependencyToSatisfy;

		protected IHandlerFactory m_handlerFactory;

		protected IComponentModelBuilder m_componentModelBuilder;

		protected ILifestyleManagerFactory m_lifestyleManagerFactory;

		/// <summary>
		/// 
		/// </summary>
		public BaseKernel()
		{
			m_services = new Hashtable();
			m_components = new Hashtable(CaseInsensitiveHashCodeProvider.Default, CaseInsensitiveComparer.Default);
			m_subsystems = new Hashtable();
			m_handlerFactory = new Handler.Default.SimpleHandlerFactory();
			m_dependencyToSatisfy = new Hashtable();
			m_componentModelBuilder = new Model.Default.DefaultComponentModelBuilder( this );
			m_lifestyleManagerFactory = new Lifestyle.Default.SimpleLifestyleManagerFactory();

			InitializeSubsystems();
		}

		#region Kernel Members

		/// <summary>
		/// Adds a component to kernel.
		/// </summary>
		/// <param name="key">The unique key that identifies the component</param>
		/// <param name="service">The service exposed by this component</param>
		/// <param name="implementation">The actual implementation</param>
		public void AddComponent( String key, Type service, Type implementation )
		{
			AssertUtil.ArgumentNotNull( key, "key" );
			AssertUtil.ArgumentNotNull( service, "service" );
			AssertUtil.ArgumentNotNull( implementation, "implementation" );
			
			if (!service.IsInterface)
			{
				throw new ArgumentException("service must be an interface");
			}
			if (implementation.IsInterface)
			{
				throw new ArgumentException("implementation can't be an interface");
			}
			if (!service.IsAssignableFrom(implementation))
			{
				throw new ArgumentException("The specified implementation does not implement the service interface");
			}

			IComponentModel model = ModelBuilder.BuildModel( key, service, implementation );

			IHandler handler = HandlerFactory.CreateHandler( model );
			handler.Init ( this );

			m_components[ key ] = handler;

			OnNewHandler( key, service, implementation, handler );
		}

		/// <summary>
		/// Adds a subsystem.
		/// </summary>
		/// <param name="key">Name of this subsystem</param>
		/// <param name="system">Subsystem implementation</param>
		public void AddSubsystem( String key, IKernelSubsystem system )
		{
			AssertUtil.ArgumentNotNull( key, "key" );
			AssertUtil.ArgumentNotNull( system, "system" );

			system.Init( this );

			m_subsystems[ key ] = system;
		}

		/// <summary>
		/// 
		/// </summary>
		public IHandler this [ String key ]
		{
			get
			{
				return (IHandler) m_components[ key ];
			}
		}

		public IHandler GetHandler( String key, object criteria )
		{
			// TODO: IHandler GetHandler( String key, object criteria )
			return null;
		}

		public IHandlerFactory HandlerFactory
		{
			get
			{
				return m_handlerFactory;
			}
			set
			{
				AssertUtil.ArgumentNotNull( value, "value" );
				m_handlerFactory = value;
			}
		}

		/// <summary>
		/// 
		/// </summary>
		public ILifestyleManagerFactory LifestyleManagerFactory
		{
			get
			{
				return m_lifestyleManagerFactory;
			}
			set
			{
				AssertUtil.ArgumentNotNull( value, "value" );
				m_lifestyleManagerFactory = value;
			}
		}

		public IComponentModelBuilder ModelBuilder
		{
			get
			{
				return m_componentModelBuilder;
			}
			set
			{
				AssertUtil.ArgumentNotNull( value, "value" );
				m_componentModelBuilder = value;
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="service"></param>
		/// <returns></returns>
		public bool HasService( Type service )
		{
			return m_services.Contains( service );
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="service"></param>
		/// <param name="depDelegate"></param>
		public void AddDependencyListener( Type service, DependencyListenerDelegate depDelegate )
		{
			lock(m_dependencyToSatisfy)
			{
				Delegate del = m_dependencyToSatisfy[ service ] as Delegate;

				if (del == null)
				{
					m_dependencyToSatisfy[ service ] = depDelegate;
				}
				else
				{
					del = Delegate.Combine( del, depDelegate );
					m_dependencyToSatisfy[ service ] = del;
				}
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="service"></param>
		/// <returns></returns>
		public IHandler GetHandlerForService( Type service )
		{
			return (IHandler) m_services[ service ];
		}

		/// <summary>
		/// Returns a registered subsystem;
		/// </summary>
		/// <param name="key">Key used when registered subsystem</param>
		/// <returns>Subsystem implementation</returns>
		public IKernelSubsystem GetSubsystem( String key )
		{
			return (IKernelSubsystem) m_subsystems[ key ];
		}

		#endregion

		/// <summary>
		/// 
		/// </summary>
		protected virtual void InitializeSubsystems()
		{
			AddSubsystem( KernelConstants.LOOKUP, new LookupCriteriaMatcher() );
			AddSubsystem( KernelConstants.EVENTS, new EventManager() );
		}

		private void OnNewHandler( String key, Type service, Type implementation, IHandler handler )
		{
			m_services[ service ] = handler;
			
			RaiseDependencyEvent( service, handler );
			RaiseSubsystemNewComponentEvent( key, service, implementation );
		}

		private void RaiseDependencyEvent( Type service, IHandler handler )
		{
			lock(m_dependencyToSatisfy)
			{
				if (!m_dependencyToSatisfy.Contains( service ))
				{
					return;
				}

				DependencyListenerDelegate del = (DependencyListenerDelegate) m_dependencyToSatisfy[ service ];
				del( service, handler );

				m_dependencyToSatisfy.Remove( service );
			}
		}

		private void RaiseSubsystemNewComponentEvent( String key, Type service, Type implementation )
		{
			IEventManager eventManager = (IEventManager) GetSubsystem( KernelConstants.EVENTS );

			if (eventManager == null)
			{
				return;
			}

			eventManager.OnComponentAdded( new EventManagerData( key, service, implementation ) );
		}
	}
}
