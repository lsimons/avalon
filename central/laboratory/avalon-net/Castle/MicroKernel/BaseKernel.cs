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

    using Apache.Avalon.Framework;
    using Apache.Avalon.Castle.MicroKernel.Model;
    using Apache.Avalon.Castle.MicroKernel.Handler.Default;
    using Apache.Avalon.Castle.MicroKernel.Lifestyle.Default;
    using Apache.Avalon.Castle.MicroKernel.Model.Default;
    using Apache.Avalon.Castle.MicroKernel.Interceptor;
    using Apache.Avalon.Castle.MicroKernel.Interceptor.Default;

    /// <summary>
    /// Base implementation of <see cref="IKernel"/>
    /// </summary>
    public class BaseKernel : IKernel, IDisposable
    {
        private static readonly object ComponentRegisteredEvent = new object();
        private static readonly object ComponentUnregisteredEvent = new object();
        private static readonly object ComponentWrapEvent = new object();
        private static readonly object ComponentUnWrapEvent = new object();
        private static readonly object ComponentReadyEvent = new object();
        private static readonly object ComponentReleasedEvent = new object();
        private static readonly object ComponentModelConstructedEvent = new object();

        protected EventHandlerList m_events;

        protected IList m_componentsInstances = new ArrayList();

        protected Hashtable m_key2Handler;

        protected Hashtable m_service2Key;

        protected Hashtable m_subsystems;

        protected Hashtable m_dependencyToSatisfy;

		protected Hashtable m_proxy2ComponentWrapper;

        protected IHandlerFactory m_handlerFactory;

        protected IComponentModelBuilder m_componentModelBuilder;

        protected ILifestyleManagerFactory m_lifestyleManagerFactory;

        protected IInterceptedComponentBuilder m_interceptedComponentBuilder;

        /// <summary>
        /// 
        /// </summary>
        public BaseKernel()
        {
            m_events = new EventHandlerList();
            m_key2Handler = new Hashtable(CaseInsensitiveHashCodeProvider.Default, CaseInsensitiveComparer.Default);
            m_service2Key = new Hashtable();
            m_subsystems = new Hashtable();
			m_proxy2ComponentWrapper = new Hashtable();
            m_handlerFactory = new SimpleHandlerFactory();
            m_dependencyToSatisfy = new Hashtable();
            m_componentModelBuilder = new DefaultComponentModelBuilder(this);
            m_lifestyleManagerFactory = new SimpleLifestyleManagerFactory();
            m_interceptedComponentBuilder = new DefaultInterceptedComponentBuilder();

            InitializeSubsystems();
        }

        #region IKernel Members

        /// <summary>
        /// Adds a component to kernel.
        /// </summary>
        /// <param name="key">The unique key that identifies the component</param>
        /// <param name="service">The service exposed by this component</param>
        /// <param name="implementation">The actual implementation</param>
        public void AddComponent(String key, Type service, Type implementation)
        {
            AssertUtil.ArgumentNotNull(key, "key");
            AssertUtil.ArgumentNotNull(service, "service");
            AssertUtil.ArgumentNotNull(implementation, "implementation");
            AssertUtil.ArgumentMustBeInterface(service, "service");
            AssertUtil.ArgumentMustNotBeInterface(implementation, "implementation");
            AssertUtil.ArgumentMustNotBeAbstract(implementation, "implementation");

            if (!service.IsAssignableFrom(implementation))
            {
                throw new ArgumentException("The specified implementation does not implement the service interface");
            }

            IComponentModel model = ModelBuilder.BuildModel(key, service, implementation);
            OnModelConstructed( model, key );

            IHandler handler = HandlerFactory.CreateHandler(model);
            handler.Init(this);

            m_key2Handler[ key ] = handler;
            OnComponentRegistered(model, key, handler);
        }

        /// <summary>
        /// Pending.
        /// </summary>
        /// <param name="key"></param>
        public void RemoveComponent(String key)
        {
            AssertUtil.ArgumentNotNull(key, "key");

            IHandler handler = this[key];

            if ( handler != null )
            {
                OnComponentUnregistered(handler.ComponentModel, key, handler);

                m_key2Handler.Remove( key );

                HandlerFactory.ReleaseHandler( handler );
            }
        }

        /// <summary>
        /// Pending
        /// </summary>
        /// <value></value>
        public event ComponentDataDelegate ComponentRegistered
        {
            add { m_events.AddHandler(ComponentRegisteredEvent, value); }
            remove { m_events.RemoveHandler(ComponentRegisteredEvent, value); }
        }

        /// <summary>
        /// Pending
        /// </summary>
        /// <value></value>
        public event ComponentDataDelegate ComponentUnregistered
        {
            add { m_events.AddHandler(ComponentUnregisteredEvent, value); }
            remove { m_events.RemoveHandler(ComponentUnregisteredEvent, value); }
        }

        /// <summary>
        /// Pending
        /// </summary>
        /// <value></value>
        public event WrapDelegate ComponentWrap
        {
            add { m_events.AddHandler(ComponentWrapEvent, value); }
            remove { m_events.RemoveHandler(ComponentWrapEvent, value); }
        }

        /// <summary>
        /// Pending
        /// </summary>
        /// <value></value>
        public event UnWrapDelegate ComponentUnWrap
        {
            add { m_events.AddHandler(ComponentUnWrapEvent, value); }
            remove { m_events.RemoveHandler(ComponentUnWrapEvent, value); }
        }

        /// <summary>
        /// Pending
        /// </summary>
        /// <value></value>
        public event ComponentInstanceDelegate ComponentReady
        {
            add { m_events.AddHandler(ComponentReadyEvent, value); }
            remove { m_events.RemoveHandler(ComponentReadyEvent, value); }
        }

        /// <summary>
        /// Pending
        /// </summary>
        /// <value></value>
        public event ComponentInstanceDelegate ComponentReleased
        {
            add { m_events.AddHandler(ComponentReleasedEvent, value); }
            remove { m_events.RemoveHandler(ComponentReleasedEvent, value); }
        }

        /// <summary>
        /// Pending
        /// </summary>
        /// <value></value>
        public event ComponentModelDelegate ComponentModelConstructed
        {
            add { m_events.AddHandler(ComponentModelConstructedEvent, value); }
            remove { m_events.RemoveHandler(ComponentModelConstructedEvent, value); }
        }

        /// <summary>
        /// Adds a subsystem.
        /// </summary>
        /// <param name="key">Name of this subsystem</param>
        /// <param name="system">Subsystem implementation</param>
        public void AddSubsystem(String key, IKernelSubsystem system)
        {
            AssertUtil.ArgumentNotNull(key, "key");
            AssertUtil.ArgumentNotNull(system, "system");

            system.Init(this);

            m_subsystems[ key ] = system;
        }

        /// <summary>
        /// 
        /// </summary>
        public IHandler this[String key]
        {
            get { return (IHandler) m_key2Handler[ key ]; }
        }

        public IHandler GetHandler(String key, object criteria)
        {
            // TODO: IHandler GetHandler( String key, object criteria )
            return null;
        }

        public IHandlerFactory HandlerFactory
        {
            get { return m_handlerFactory; }
            set
            {
                AssertUtil.ArgumentNotNull(value, "value");
                m_handlerFactory = value;
            }
        }

        /// <summary>
        /// 
        /// </summary>
        public ILifestyleManagerFactory LifestyleManagerFactory
        {
            get { return m_lifestyleManagerFactory; }
            set
            {
                AssertUtil.ArgumentNotNull(value, "value");
                m_lifestyleManagerFactory = value;
            }
        }

        /// <summary>
        /// 
        /// </summary>
        public IInterceptedComponentBuilder InterceptedComponentBuilder
        {
            get { return m_interceptedComponentBuilder; }
            set
            {
                AssertUtil.ArgumentNotNull(value, "value");
                m_interceptedComponentBuilder = value;
            }
        }

        /// <summary>
        /// 
        /// </summary>
        public IComponentModelBuilder ModelBuilder
        {
            get { return m_componentModelBuilder; }
            set
            {
                AssertUtil.ArgumentNotNull(value, "value");
                m_componentModelBuilder = value;
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="service"></param>
        /// <returns></returns>
        public bool HasService(Type service)
        {
            return m_service2Key.Contains(service);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="service"></param>
        /// <param name="depDelegate"></param>
        public void AddDependencyListener(Type service, DependencyListenerDelegate depDelegate)
        {
            lock (m_dependencyToSatisfy)
            {
                Delegate del = m_dependencyToSatisfy[ service ] as Delegate;

                if (del == null)
                {
                    m_dependencyToSatisfy[ service ] = depDelegate;
                }
                else
                {
                    del = Delegate.Combine(del, depDelegate);
                    m_dependencyToSatisfy[ service ] = del;
                }
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="service"></param>
        /// <returns></returns>
        public IHandler GetHandlerForService(Type service)
        {
            String key = (String) m_service2Key[ service ];
            return key == null ? null : (IHandler) m_key2Handler[ key ];
        }

        /// <summary>
        /// Returns a registered subsystem;
        /// </summary>
        /// <param name="key">Key used when registered subsystem</param>
        /// <returns>Subsystem implementation</returns>
        public IKernelSubsystem GetSubsystem(String key)
        {
            return (IKernelSubsystem) m_subsystems[ key ];
        }

        #endregion

        #region IDisposable Members

        public void Dispose()
        {
            foreach(PairHandlerComponent pair in m_componentsInstances)
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

        #region RaiseEvents

		protected virtual void RaiseDependencyEvent( Type service, IHandler handler )
		{
			DependencyListenerDelegate del = (DependencyListenerDelegate) m_dependencyToSatisfy[ service ];
			
			if ( del != null )
			{
				del( service, handler );
			}
		}

        protected virtual void RaiseComponentRegistered(IComponentModel model, String key, IHandler handler)
        {
            ComponentDataDelegate eventDelegate = (ComponentDataDelegate) m_events[ComponentRegisteredEvent];

            if (eventDelegate != null)
            {
                eventDelegate(model, key, handler);
            }
        }

        protected virtual void RaiseComponentUnregistered(IComponentModel model, String key, IHandler handler)
        {
            ComponentDataDelegate eventDelegate = (ComponentDataDelegate) m_events[ComponentUnregisteredEvent];

            if (eventDelegate != null)
            {
                eventDelegate(model, key, handler);
            }
        }

        protected virtual void RaiseModelConstructed(IComponentModel model, String key)
        {
            ComponentModelDelegate eventDelegate = (ComponentModelDelegate) m_events[ComponentModelConstructedEvent];

            if (eventDelegate != null)
            {
                eventDelegate(model, key);
            }
        }

        public virtual void RaiseComponentReadyEvent( IHandler handler, object instance )
        {
            ComponentInstanceDelegate eventDelegate = (ComponentInstanceDelegate) m_events[ComponentReadyEvent];

            if (eventDelegate != null)
            {
                IComponentModel model = handler.ComponentModel;
                String key = (String) m_service2Key[ model.Service ];

                eventDelegate(model, key, handler, instance);
            }
        }

        public virtual void RaiseComponentReleasedEvent( IHandler handler, object instance )
        {
            ComponentInstanceDelegate eventDelegate = (ComponentInstanceDelegate) m_events[ComponentReleasedEvent];

            if (eventDelegate != null)
            {
                IComponentModel model = handler.ComponentModel;
                String key = (String) m_service2Key[ model.Service ];

                eventDelegate(model, key, handler, instance);
            }
        }

        public virtual object RaiseWrapEvent( IHandler handler, object instance )
        {
            WrapDelegate eventDelegate = (WrapDelegate) m_events[ComponentWrapEvent];

            if (eventDelegate != null)
            {
                IComponentModel model = handler.ComponentModel;
                String key = (String) m_service2Key[ model.Service ];
                InterceptedComponentWrapper wrapper = 
					new InterceptedComponentWrapper( m_interceptedComponentBuilder, instance, model.Service );

                eventDelegate(model, key, handler, wrapper);

				if (wrapper.IsProxiedCreated)
				{
					object proxy = wrapper.ProxiedInstance;
					m_proxy2ComponentWrapper[ proxy ] = wrapper;

					// From now on, the outside world will have 
					// a proxy pointer, not the instance anymore.
					instance = proxy;
				}
            }

            return instance;
        }

        public virtual object RaiseUnWrapEvent( IHandler handler, object instance )
        {
            UnWrapDelegate eventDelegate = (UnWrapDelegate) m_events[ComponentUnWrapEvent];

			// We can have a null wrapper here
			InterceptedComponentWrapper wrapper = m_proxy2ComponentWrapper[ instance ] as InterceptedComponentWrapper;

			if (wrapper != null)
			{
				m_proxy2ComponentWrapper.Remove( instance );
			}

            if (eventDelegate != null)
            {
                IComponentModel model = handler.ComponentModel;
                String key = (String) m_service2Key[ model.Service ];

                eventDelegate(model, key, handler, wrapper);
            }

            return wrapper != null ? wrapper.Instance : instance;
        }

        /// <summary>
        /// 
        /// </summary>
        internal class InterceptedComponentWrapper : IInterceptedComponent
        {
            private IInterceptedComponentBuilder m_interceptedComponentBuilder;
			private IInterceptedComponent m_delegate;
			private object m_instance;
        	private Type m_service;

        	public InterceptedComponentWrapper( IInterceptedComponentBuilder interceptedComponentBuilder, 
				object instance, Type service )
            {
                m_interceptedComponentBuilder = interceptedComponentBuilder;
				m_instance = instance;
				m_service = service;
            }

			public object Instance
			{
				get { return m_instance; }
			}

            public object ProxiedInstance
            {
                get
                {
					EnsureDelegate();
                	return m_delegate.ProxiedInstance;
                }
            }

            public void Add(IInterceptor interceptor)
            {
				EnsureDelegate();
				m_delegate.Add(interceptor);
			}

            public IInterceptor InterceptorChain
            {
				get
				{
					EnsureDelegate();
					return m_delegate.InterceptorChain;
				}
			}

			public bool IsProxiedCreated
			{
				get { return m_delegate != null; }
			}

			private void EnsureDelegate()
			{
				if (m_delegate == null)
				{
					m_delegate = m_interceptedComponentBuilder.CreateInterceptedComponent( 
						m_instance, m_service );
				}
			}
        }

        #endregion

        /// <summary>
        /// Starts the component if the activation policy for 
        /// the component is 'Start' and if the component's dependencies are satisfied.
        /// </summary>
        /// <param name="model">Component model</param>
        /// <param name="handler">Handler responsible for the component</param>
        protected virtual void StartComponentIfPossible(IComponentModel model, IHandler handler)
        {
            if (model.ActivationPolicy == Activation.Start)
            {
				if (handler.ActualState == State.Valid)
				{
					StartComponent( handler );
				}
				else if (handler.ActualState == State.WaitingDependency)
				{
					handler.AddChangeStateListener( new ChangeStateListenerDelegate(StartComponent) );
				}
            }
        }

		protected virtual void StartComponent( IHandler handler )
		{
			object instance = handler.Resolve();
			m_componentsInstances.Add(new PairHandlerComponent(handler, instance));
		}

        private void OnModelConstructed(IComponentModel model, String key)
        {
            RaiseModelConstructed(model, key);
        }

        private void OnComponentRegistered(IComponentModel model, String key, IHandler handler)
        {
            m_service2Key[ model.Service ] = key;

			RaiseDependencyEvent( model.Service, handler );

            RaiseComponentRegistered(model, key, handler);

            StartComponentIfPossible( model, handler );
        }

        private void OnComponentUnregistered(IComponentModel model, String key, IHandler handler)
        {
            m_service2Key.Remove( model.Service );

            RaiseComponentUnregistered(model, key, handler);
        }
    }

    /// <summary>
    /// 
    /// </summary>
    internal class PairHandlerComponent
    {
        private IHandler m_handler;
        private object m_instance;

        public PairHandlerComponent(IHandler handler, object instance)
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