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

namespace Apache.Avalon.Castle.MicroKernel.Handlers
{
	using System;
	using System.Collections;
	using System.Reflection;

	/// <summary>
	/// Summary description for SimpleHandler.
	/// </summary>
	public class SimpleHandler : IHandler
	{
		protected Type m_service;

		protected Type m_implementation;

		protected Kernel m_kernel;

		private State m_state = State.Valid;

		private ConstructorInfo m_constructor;

		private ArrayList m_dependencies = new ArrayList();

		private Hashtable m_serv2handler = new Hashtable();

		private ILifestyleManager m_lifestyleManager;

		/// <summary>
		/// 
		/// </summary>
		/// <param name="service"></param>
		/// <param name="implementation"></param>
		public SimpleHandler(Type service, Type implementation)
		{
			AssertUtil.ArgumentNotNull( service, "service" );
			AssertUtil.ArgumentNotNull( implementation, "implementation" );

			m_service = service;
			m_implementation = implementation;
		}

		#region IHandler Members

		public void Init( Kernel kernel )
		{
			m_kernel = kernel;

			ConstructorInfo[] constructors = m_implementation.GetConstructors();

			// TODO: Try to sort the array 
			// by the arguments lenght in descendent order

			// TODO: Should also support SetMethods

			foreach(ConstructorInfo constructor in constructors)
			{
				if (IsEligible( constructor ))
				{
					m_constructor = constructor;
					break;
				}
			}

			if ( m_constructor == null )
			{
				throw new HandlerException( 
					String.Format("Handler could not find an eligible constructor for type {0}", m_implementation.FullName) );
			}

			// Now we check with the kernel if 
			// we have the necessary implementations 
			// for the services requested by the constructor

			EnsureHaveRequiredImplementations();

			CreateComponentFactoryAndLifestyleManager();
		}

		public object Resolve()
		{
			if (m_state == State.WaitingDependency)
			{
				throw new HandlerException("Can't Resolve component. It has dependencies to be satisfied.");
			}

			try
			{
				return m_lifestyleManager.Resolve();
			}
			catch(Exception ex)
			{
				throw new HandlerException("Exception while attempting to instantiate type", ex);
			}
		}

		public void Release()
		{
			m_lifestyleManager.Release();
		}

		/// <summary>
		/// 
		/// </summary>
		public State ActualState
		{
			get
			{
				return m_state;
			}
		}

		#endregion

		protected bool IsEligible( ConstructorInfo constructor )
		{
			ParameterInfo[] parameters = constructor.GetParameters();

			foreach(ParameterInfo parameter in parameters)
			{
				if (parameter.ParameterType == typeof(String) &&
					!parameter.Name.Equals("contextdir")) // Just as sample
				{
					return false;
				}

				if (!parameter.ParameterType.IsInterface)
				{
					return false;
				}
			}

			return true;
		}

		protected void EnsureHaveRequiredImplementations()
		{
			ParameterInfo[] parameters = m_constructor.GetParameters();

			foreach(ParameterInfo parameter in parameters)
			{
				if (parameter.ParameterType.IsInterface)
				{
					if (m_kernel.HasService( parameter.ParameterType ))
					{
						m_serv2handler[parameter.ParameterType] = 
							m_kernel.GetHandlerForService( parameter.ParameterType );
					}
					else
					{
						m_kernel.AddDependencyListener( parameter.ParameterType, new DependencyListenerDelegate(DependencySatisfied) );

						m_state = State.WaitingDependency;
						m_dependencies.Add( parameter.ParameterType );
					}
				}
			}
		}

		private void DependencySatisfied( Type service, IHandler handler )
		{
			m_serv2handler[ service ] = handler;

			m_dependencies.Remove( service );

			if (m_dependencies.Count == 0)
			{
				m_state = State.Valid;
			}
		}

		private void CreateComponentFactoryAndLifestyleManager()
		{
			ConstructionInfo info = new ConstructionInfo( m_constructor, m_serv2handler );

			IComponentFactory factory = new Factory.SimpleComponentFactory( 
				m_service, m_implementation, 
				m_kernel.GetAspects(AspectPointCutFlags.Before), 
				m_kernel.GetAspects(AspectPointCutFlags.After), info);

			m_lifestyleManager = m_kernel.LifestyleManagerFactory.Create( factory );
		}
	}
}
