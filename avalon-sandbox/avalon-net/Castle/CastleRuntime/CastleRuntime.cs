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

namespace Apache.Avalon.Castle.Runtime.Extended
{
	using System;

	using Apache.Avalon.Castle.ManagementExtensions;
	using Apache.Avalon.Castle.Util;
	using Apache.Avalon.Castle.Util.Proxies;
	using Apache.Avalon.Composition.Model;
	using Apache.Avalon.Activation;

	/// <summary>
	/// Summary description for CastleRuntime.
	/// </summary>
	[ManagedComponent]
	public class CastleRuntime : ManagedService, IRuntime
	{
		private ManagedObjectName m_runtimeFactoryName = Constants.DEFAULT_RUNTIME_FACTORY_NAME;

		private IRuntimeFactory m_runtimeFactory;

		public CastleRuntime()
		{
		}

		#region CastleRuntime Attributes

		[ManagedAttribute]
		public ManagedObjectName RuntimeFactory 
		{
			get
			{
				return m_runtimeFactoryName;
			}
			set
			{
				m_runtimeFactoryName = value;
			}
		}

		#endregion

		#region IRuntime Members

		[ManagedOperation]
		public void Decommission(IDeploymentModel model)
		{
			m_runtimeFactory.GetRuntime( model ).Decommission();
		}

		[ManagedOperation]
		public void Commission(IDeploymentModel model)
		{
			m_runtimeFactory.GetRuntime( model ).Commission();
		}

		[ManagedOperation]
		public void Release(IDeploymentModel model, object instance)
		{
			m_runtimeFactory.GetRuntime( model ).Release( instance );
		}

		[ManagedOperation]
		public object Resolve(IDeploymentModel model)
		{
			return m_runtimeFactory.GetRuntime( model ).Resolve();;
		}

		#endregion
	
		#region ManagedService Overrides

		public override void Start()
		{
			base.Start();

			ObtainRuntimeFactory();
		}

		#endregion

		private void ObtainRuntimeFactory()
		{
			if (m_runtimeFactoryName == Constants.DEFAULT_RUNTIME_FACTORY_NAME)
			{
				MXUtil.InvokeOn( Server, 
					CastleConstants.CONTROLLER_NAME, 
					"AddService", 
					Constants.DEFAULT_RUNTIME_FACTORY_TYPE, 
					m_runtimeFactoryName, ManagedObjectName );
			}

			// Generates a proxy to runtimefactory implementation

			m_runtimeFactory = (IRuntimeFactory) ManagedObjectProxyGenerator.CreateProxy( 
				m_runtimeFactoryName, Server, typeof(IRuntimeFactory) );
		} 

		
	}
}
