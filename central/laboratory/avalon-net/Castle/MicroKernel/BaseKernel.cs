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
    using System.ComponentModel;

	using Apache.Avalon.Castle.MicroKernel.Model;
	using Apache.Avalon.Castle.MicroKernel.Subsystems.Lookup.Default;

	/// <summary>
	/// Base implementation of <see cref="IKernel"/>
	/// </summary>
	public class BaseKernel : IKernel, IDisposable
	{
        private static readonly object ComponentAddedEvent = new object();
        private static readonly object ComponentCreatedEvent = new object();
        private static readonly object ComponentDestroyedEvent = new object();

        protected EventHandlerList m_events;

        protected IList m_componentsInstances = new ArrayList();

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
            m_events = new EventHandlerList();
            m_services = new Hashtable();
			m_components = new Hashtable(CaseInsensitiveHashCodeProvider.Default, CaseInsensitiveComparer.Default);
			m_subsystems = new Hashtable();
			m_handlerFactory = new Handler.Default.SimpleHandlerFactory();
			m_dependencyToSatisfy = new Hashtable();
			m_componentModelBuilder = new Model.Default.DefaultComponentModelBuilder( this );
			m_lifestyleManagerFactory = new Lifestyle.Default.SimpleLifestyleManagerFactory();

			InitializeSubsystems();
		}

		#region IKernel Members

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
            if (implementation.IsAbstract)
            {
                throw new ArgumentException("implementation can't be abstract");
            }
            if (!service.IsAssignableFrom(implementation))
            {
				throw new ArgumentException("The specified implementation does not implement the service interface");
			}

			IComponentModel model = ModelBuilder.BuildModel( key, service, implementation );

			IHandler handler = HandlerFactory.CreateHandler( model );
			handler.Init ( this );

			m_components[ key ] = handler;

            OnNewHandler( model, key, service, implementation, handler);
        }

        public event ComponentDataDelegate ComponentAdded
        {
            add { m_events.AddHandler(ComponentAddedEvent, value); }
            remove { m_events.RemoveHandler(ComponentAddedEvent, value); }
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

        #region IDisposable Members

        public void Dispose()
        {
            foreach (PairHandlerComponent pair in m_componentsInstances)
            {
                pair.Handler.Release(pair.Instance);
            }
            m_componentsInstances.Clear();
        }

        #endregion

        /// <summary>
        /// 
		/// </summary>
		protected virtual void InitializeSubsystems()
		{
            // Examples:
			// AddSubsystem( KernelConstants.LOOKUP, new LookupCriteriaMatcher() );
			// AddSubsystem( KernelConstants.EVENTS, new EventManager() );
		}

        protected virtual void RaiseComponentAdded(IComponentModel model, String key, Type service, Type implementation, IHandler handler)
        {
            ComponentDataDelegate eventDelegate = (ComponentDataDelegate) m_events[ComponentAddedEvent];
            
            if (eventDelegate != null)
            {
                eventDelegate(model, key, service, implementation, handler);
            }
        }

        private void OnNewHandler( IComponentModel model, String key, Type service, Type implementation, IHandler handler )
		{
			m_services[ service ] = handler;

            RaiseComponentAdded( model, key, service, implementation, handler );

            if (model.ActivationPolicy == Apache.Avalon.Framework.Activation.Start)
            {
                object instance = handler.Resolve();
                
                m_componentsInstances.Add( new PairHandlerComponent(handler, instance) );
            }
        }
	}

    internal class PairHandlerComponent
    {
        private IHandler m_handler;
        private object m_instance;

        public PairHandlerComponent( IHandler handler, object instance )
        {
            m_handler = handler;
            m_instance = instance;
        }

        public IHandler Handler
        {
            get { return m_handler; }
        }

        public object Instance
        {
            get { return m_instance; }
        }
    }
}
