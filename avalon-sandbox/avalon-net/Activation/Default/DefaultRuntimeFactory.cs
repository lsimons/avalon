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
	using System.Collections;

	using Apache.Avalon.Activation;
	using Apache.Avalon.Composition.Model;

	/// <summary>
	/// Summary description for DefaultRuntimeFactory.
	/// </summary>
	public class DefaultRuntimeFactory : IRuntimeFactory
	{
		//-------------------------------------------------------------------
		// immutable state
		//-------------------------------------------------------------------

		private ISystemContext m_system;

		private Hashtable m_map = new Hashtable();

		private ILifestyleFactory m_factory;

		//-------------------------------------------------------------------
		// constructor
		//-------------------------------------------------------------------

		public DefaultRuntimeFactory( ISystemContext system )
		{
			m_system = system;
			m_factory = new DefaultLifestyleFactory( m_system );
		}

		#region IRuntimeFactory Members

		public IAppliance GetRuntime(Apache.Avalon.Composition.Model.IDeploymentModel model)
		{
			lock(m_map)
			{
				IAppliance runtime = GetRegisteredRuntime( model );

				if( null != runtime ) 
					return runtime;

				//
				// create the runtime
				// check the model for an overriding runtime using the 
				// standard runtime as the default (not implemented
				// yet)
				//

				if( model is IComponentModel )
				{
					IComponentModel component = (IComponentModel) model;
					ILifestyleManager manager = 
						m_factory.CreateLifestyleManager( component );
					runtime = NewComponentRuntime( component, manager );
				}
				else if( model is IContainmentModel )
				{
					IContainmentModel containment = (IContainmentModel) model;
					runtime = NewContainmentRuntime( containment );
				}
				else
				{
					String error = "runtime.error.unknown-model " + model.ToString();
					throw new ModelRuntimeException( error );
				}

				RegisterRuntime( model, runtime );
				return runtime;
			}
		}

		#endregion

		/// <summary>
		/// Resolve a runtime handler for a component model.
		/// </summary>
		/// <param name="model">the containment model</param>
		/// <param name="manager"></param>
		/// <returns>the runtime handler</returns>
		protected IAppliance NewComponentRuntime( IComponentModel model, ILifestyleManager manager )
		{
			return new DefaultAppliance( model, manager );
		}

		/// <summary>
		/// Resolve a runtime handler for a containment model.
		/// </summary>
		/// <param name="model">the containment model</param>
		/// <returns> runtime handler</returns>
		protected IAppliance NewContainmentRuntime( IContainmentModel model )
		{
			return new DefaultBlock( m_system, model );
		}

		/// <summary>
		/// Lookup a runtime relative to the model name.
		/// </summary>
		/// <param name="model">the deployment model</param>
		/// <returns>matching runtime (possibly null)</returns>
		private IAppliance GetRegisteredRuntime( IDeploymentModel model )
		{
			return (IAppliance) m_map[ model.QualifiedName ];
		}

		private void RegisterRuntime( IDeploymentModel model, IAppliance runtime )
		{
			m_map[ model.QualifiedName ] = runtime;
		}
	}
}
