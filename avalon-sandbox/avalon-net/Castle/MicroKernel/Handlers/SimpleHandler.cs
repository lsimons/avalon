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
	public class SimpleHandler : AbstractHandler
	{
		private ConstructorInfo m_constructor;
		
		private ArrayList m_properties = new ArrayList();

		/// <summary>
		/// 
		/// </summary>
		/// <param name="service"></param>
		/// <param name="implementation"></param>
		public SimpleHandler(Type service, Type implementation) : base(service, implementation)
		{
		}

		#region IHandler Members

		public override void Init( Kernel kernel )
		{
			base.Init(kernel);

			InspectConstructors();
			InspectSetMethods();

			// Now we check with the kernel if 
			// we have the necessary implementations 
			// for the services requested by the constructor

			EnsureHaveRequiredImplementations();

			CreateComponentFactoryAndLifestyleManager();
		}

		#endregion

		protected void InspectConstructors()
		{
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
		}

		protected void InspectSetMethods()
		{
			PropertyInfo[] properties = m_service.GetProperties();

			foreach(PropertyInfo property in properties)
			{
				if (IsEligible( property ))
				{
					AddDependency( property.PropertyType );

					m_properties.Add( property );
				}
			}
		}

		protected bool IsEligible( PropertyInfo property )
		{
			// TODO: An attribute could say to use
			// that the property is optional.

			if (!property.CanWrite || !property.PropertyType.IsInterface)
			{
				return false;
			}

			return true;
		}

		protected bool IsEligible( ConstructorInfo constructor )
		{
			ParameterInfo[] parameters = constructor.GetParameters();

			foreach(ParameterInfo parameter in parameters)
			{
				if (parameter.ParameterType == typeof(String) &&
					!parameter.Name.Equals("contextdir")) // Just a sample
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
					AddDependency( parameter.ParameterType );
				}
			}
		}

		protected void AddDependency( Type service )
		{
			if (m_kernel.HasService( service ))
			{
				m_serv2handler[ service ] = m_kernel.GetHandlerForService( service );
			}
			else
			{
				// This is handler is considered invalid
				// until dependencies are satisfied
				m_state = State.WaitingDependency;
				m_dependencies.Add( service );
						
				// Register ourself in the kernel
				// to be notified if the dependency is satified
				m_kernel.AddDependencyListener( 
					service, 
					new DependencyListenerDelegate(DependencySatisfied) );
			}
		}

		/// <summary>
		/// Delegate implementation invoked by kernel
		/// when one of registered dependencies were satisfied by 
		/// new components registered.
		/// </summary>
		/// <param name="service"></param>
		/// <param name="handler"></param>
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
			info.Properties = (PropertyInfo[]) m_properties.ToArray( typeof(PropertyInfo) );

			IComponentFactory factory = new Factory.SimpleComponentFactory( 
				m_service, m_implementation, 
				m_kernel.GetAspects(AspectPointCutFlags.Before), 
				m_kernel.GetAspects(AspectPointCutFlags.After), info);

			m_lifestyleManager = m_kernel.LifestyleManagerFactory.Create( factory );
		}
	}
}
