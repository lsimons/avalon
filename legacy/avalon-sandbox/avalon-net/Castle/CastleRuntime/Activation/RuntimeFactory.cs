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

namespace Apache.Avalon.Castle.Runtime.Extended.Activation
{
	using System;
	using System.Collections;

	using Apache.Avalon.Castle;
	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Util;
	using Apache.Avalon.Activation;
	using Apache.Avalon.Activation.Default;
	using Apache.Avalon.Composition.Model;

	/// <summary>
	/// Summary description for RuntimeFactory.
	/// </summary>
	[ManagedComponent]
	public class RuntimeFactory : ManagedService, IRuntimeFactory
	{
		protected ISystemContext m_system;

		protected ILifestyleFactory m_lifestyleFactory;

		protected Hashtable m_cache = new Hashtable();

		public RuntimeFactory()
		{
		}

		#region ManagedService Overrides

		public override void Start()
		{
			base.Start();

			ObtainSystemContext();
		}

		#endregion

		#region IRuntimeFactory Members

		public IAppliance GetRuntime(IDeploymentModel model)
		{
			IAppliance appliance = GetCachedAppliance( model );

			if ( appliance == null )
			{
				if ( model is IContainmentModel )
				{
					appliance = new DefaultBlock( m_system, model as IContainmentModel );
				}
				else if ( model is IComponentModel )
				{
					IComponentModel compModel = model as IComponentModel;

					ILifestyleManager manager =	m_lifestyleFactory.CreateLifestyleManager( compModel );
					
					appliance = new DefaultAppliance( compModel, manager );
				}
			}

			return appliance;
		}

		#endregion

		private IAppliance GetCachedAppliance(IDeploymentModel model)
		{
			return m_cache[ model.QualifiedName ] as IAppliance;
		}

		private void ObtainSystemContext()
		{
			m_system = (ISystemContext) MXUtil.GetAttribute( 
				Server, CastleConstants.ORCHESTRATOR_NAME, "SystemContext" );
		}
	}
}
