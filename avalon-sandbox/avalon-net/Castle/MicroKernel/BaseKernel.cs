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
	using System.Collections.Specialized;

	using Apache.Avalon.DynamicProxy;

	/// <summary>
	/// Summary description for BaseKernel.
	/// </summary>
	public class BaseKernel : Kernel
	{
		protected ArrayList m_aspectBefore = new ArrayList();

		protected ArrayList m_aspectAfter = new ArrayList();

		protected ArrayList m_concerns = new ArrayList();

		protected Hashtable m_components = new Hashtable(CaseInsensitiveHashCodeProvider.Default, CaseInsensitiveComparer.Default);

		protected Hashtable m_services = new Hashtable();

		protected Hashtable m_dependencyToSatisfy = new Hashtable();

		protected IHandlerFactory m_handlerFactory = new Handlers.SimpleHandlerFactory();

		protected ILifestyleManagerFactory m_lifestyleManagerFactory = new LifestyleManagers.SimpleLifestyleManagerFactory();

		/// <summary>
		/// 
		/// </summary>
		public BaseKernel()
		{
		}

		#region Kernel Members

		/// <summary>
		/// 
		/// </summary>
		/// <param name="key"></param>
		/// <param name="service"></param>
		/// <param name="implementation"></param>
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

			IHandler handler = HandlerFactory.CreateHandler( service, implementation );

			handler.Init ( this );

			m_components[ key ] = handler;

			OnNewHandler( service, handler );
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="flags"></param>
		/// <param name="aspect"></param>
		public void AddAspect( AspectPointCutFlags flags, IAspect aspect )
		{
			AssertUtil.ArgumentNotNull( aspect, "aspect" );

			if ((AspectPointCutFlags.Before & flags) != 0)
			{
				lock(m_aspectBefore)
				{
					m_aspectBefore.Add( aspect );
				}
			}
			if ((AspectPointCutFlags.After & flags) != 0)
			{
				lock(m_aspectAfter)
				{
					m_aspectAfter.Add( aspect );
				}
			}
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

		public IAspect[] GetAspects( AspectPointCutFlags pointcut )
		{
			if (pointcut == AspectPointCutFlags.Before)
			{
				return (IAspect[]) m_aspectBefore.ToArray( typeof(IAspect) );
			}
			else if (pointcut == AspectPointCutFlags.After)
			{
				return (IAspect[]) m_aspectAfter.ToArray( typeof(IAspect) );
			}

			return new IAspect[0];
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

		#endregion

		private void OnNewHandler( Type service, IHandler handler )
		{
			m_services[ service ] = handler;

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
	}
}
