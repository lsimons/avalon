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

namespace Apache.Avalon.Castle.MicroKernel.Handler.Default
{
	using System;

	using Apache.Avalon.Castle.MicroKernel.Model;

	/// <summary>
	/// Summary description for SimpleHandler.
	/// </summary>
	public class SimpleHandler : AbstractHandler
	{
		/// <summary>
		/// 
		/// </summary>
		/// <param name="model"></param>
		public SimpleHandler( IComponentModel model ) : base( model )
		{
		}

		#region IHandler Members

		public override void Init( IKernel kernel )
		{
			base.Init(kernel);

			// Now we check with the kernel if 
			// we have the necessary implementations 
			// for the services requested by the constructor

			EnsureDependenciesCanBeSatisfied();
			CreateComponentFactoryAndLifestyleManager();
		}

		#endregion

		protected virtual void EnsureDependenciesCanBeSatisfied()
		{
			foreach(IDependencyModel dependency in m_componentModel.Dependencies )
			{
				AddDependency( dependency.Service );
			}
		}

		protected virtual void AddDependency( Type service )
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

		protected virtual void CreateComponentFactoryAndLifestyleManager()
		{
			IComponentFactory factory = new Factory.Default.SimpleComponentFactory( 
				m_componentModel, m_serv2handler);

			m_lifestyleManager = m_kernel.LifestyleManagerFactory.Create( 
				factory, m_componentModel );
		}
	}
}
